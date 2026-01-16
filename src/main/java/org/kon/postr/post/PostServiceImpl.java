package org.kon.postr.post;

import org.kon.postr.exception.ResourceNotFoundException;
import org.kon.postr.storage.S3Service;
import org.kon.postr.post.dto.request.SavePostDTO;
import org.kon.postr.post.dto.request.SaveReplyDTO;
import org.kon.postr.post.dto.response.PostCardDTO;
import org.kon.postr.response.*;
import org.kon.postr.user.User;
import org.kon.postr.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.kon.postr.response.ApiResponse.Status;

import java.io.IOException;
import java.util.UUID;


@Service
public class PostServiceImpl implements PostService{

    @Value(value = "${app.pageable.page-size}")
    private int pageSize;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PostMapper postMapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository,
                           PostMapper postMapper, S3Service s3Service) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional
    public ApiResponse save(SavePostDTO savePostDTO, Jwt userPrincipal) {

        String mediaName = this.handleMediaUpload(
                userPrincipal.getSubject(), savePostDTO.media()
        );

        postRepository.save(
                new Post(
                        Post.Type.POST,
                        savePostDTO.content(),
                        mediaName,
                        userRepository.findById(UUID.fromString(userPrincipal.getSubject()))
                                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                )
        );

        userRepository.incrementPostCount(
                UUID.fromString(userPrincipal.getSubject())
        );

        return new ApiResponse(Status.SUCCESS, "Post saved");
    }

    @Override
    @Transactional
    public ApiResponse delete(UUID id) {

        Post post = this.getPostById(id);

        postRepository.deleteById(id);

        if (post.getType() == Post.Type.POST) {
            userRepository.decrementPostCount(
                    post.getUser().getId()
            );
        }

        return new ApiResponse(Status.SUCCESS, "Post deleted successfully");
    }

    @Transactional
    protected Post getPostById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("post does not exist")
                );
    }

    @Override
    @Transactional
    public SimpleBodyResponse<PostCardDTO> getPost(UUID id, Jwt userPrincipal) {
        Post post = this.getPostById(id);

        return new SimpleBodyResponse<>(
                Status.SUCCESS, "user retrieved successfully.",
                postMapper.toParentCardDTO(post, userPrincipal)
        );

    }

    @Override
    @Transactional
    public PaginatedResponse<PostCardDTO, SliceMetadata> getPostsByUser(
                                            String username, Jwt userPrincipal, int pageNumber) {
        Slice<Post> posts = postRepository.findByUser_UsernameAndType(
                username, Post.Type.POST,
                PageRequest.of(
                        pageNumber, pageSize,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        );

        return new PaginatedResponse<>(
                Status.SUCCESS,
                "Posts were successfully retrieved",
                posts.map(post -> postMapper
                        .toPostCardDTO(post, userPrincipal)
                ).getContent(),
                new SliceMetadata(
                        posts.getNumber(), posts.hasNext()
                )
        );
    }

    @Override
    @Transactional
    public PaginatedResponse<PostCardDTO, SliceMetadata> getRepliesByUser(
                                            String username, Jwt userPrincipal, int pageNumber) {
        Slice<Post> posts = postRepository.findByUser_UsernameAndType(
                username, Post.Type.REPLY,
                PageRequest.of(
                        pageNumber, pageSize,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        );

        return new PaginatedResponse<>(
                Status.SUCCESS,
                "Posts were successfully retrieved",
                posts.map(post -> postMapper
                        .toReplyCardDTO(post, userPrincipal)
                ).getContent(),
                new SliceMetadata(
                        posts.getNumber(), posts.hasNext()
                )
        );
    }

    /*
     * gets parent posts up to the first original post
     */
    @Override
    @Transactional
    public SimpleBodyResponse<PostCardDTO> getParentPost(UUID id, Jwt userPrincipal) {
        Post reply = this.getPostById(id);

        return new SimpleBodyResponse<>(
                Status.SUCCESS, "Parent post retrieved successfully",
                postMapper.toParentCardDTO(reply.getReplyParent(), userPrincipal)
        );
    }

    @Override
    @Transactional
    public PaginatedResponse<PostCardDTO, SliceMetadata> getPostsLikedByUser(
                                                UUID userId, Jwt userPrincipal, int pageNumber) {
        Slice<Post> posts = postRepository
                .findByLikes_Id(userId, PageRequest.of(pageNumber, pageSize));

        return new PaginatedResponse<>(
                Status.SUCCESS, "posts liked by user retrieved successfully.",
                posts.map(post -> postMapper
                        .toPostCardDTO(post, userPrincipal)
                ).getContent(),
                new SliceMetadata(
                        posts.getNumber(), posts.hasNext()
                )
        );
    }

    @Override
    @Transactional
    public PaginatedResponse<PostCardDTO, SliceMetadata> getPostReplies(
                                                    UUID id, Jwt userPrincipal, int pageNumber) {
        Slice<Post> replies = postRepository
                .findByReplyParent_Id(
                        id, PageRequest.of(
                                pageNumber, pageSize,
                                Sort.by(Sort.Direction.DESC, "likeCount")
                        )
                );

        return new PaginatedResponse<>(
                Status.SUCCESS, "replies were retrieved successfully.",
                replies.map(post -> postMapper
                        .toPostCardDTO(post, userPrincipal)
                ).getContent(),
                new SliceMetadata(
                        replies.getNumber(), replies.hasNext()
                )
        );
    }

    @Override
    @Transactional
    public PaginatedResponse<PostCardDTO, SliceMetadata> getPostsRepostedByUser(
                                                UUID userId, Jwt userPrincipal, int pageNumber) {
        Slice<Post> reposts = postRepository
                .findRepostsByUser_Id(userId, PageRequest.of(pageNumber, pageSize));

        return new PaginatedResponse<>(
                Status.SUCCESS, "replies were retrieved successfully.",
                reposts.map(post -> postMapper
                        .toPostCardDTO(post, userPrincipal)
                ).getContent(),
                new SliceMetadata(
                        reposts.getNumber(), reposts.hasNext()
                )
        );
    }

    @Override
    @Transactional
    public ApiResponse replyToPost(SaveReplyDTO saveReplyDTO, Jwt userPrincipal) {

        if (!postRepository.existsById(UUID.fromString(saveReplyDTO.parentId()))) {
            throw new ResourceNotFoundException(
                    "Post with the id " + saveReplyDTO.parentId() + " does not exist."
            );
        }

        String mediaName = this.handleMediaUpload(
                userPrincipal.getSubject(), saveReplyDTO.media()
        );

        postRepository.save(
                new Post(
                        Post.Type.REPLY,
                        saveReplyDTO.content(),
                        mediaName,
                        userRepository.findById(UUID.fromString(userPrincipal.getSubject()))
                                .orElseThrow(() -> new IllegalArgumentException("User not found")),
                        postRepository.getReferenceById(UUID.fromString(saveReplyDTO.parentId()))
                )
        );

        return new ApiResponse(Status.SUCCESS, "successfully replied to post.");
    }

    @Override
    @Transactional
    public ApiResponse likePost(UUID id, Jwt userPrincipal) {
        Post post = this.getPostById(id);
        User user = userRepository.findById(UUID.fromString(userPrincipal.getSubject()))
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );

        post.addLike(user);
        postRepository.save(post);
        postRepository.incrementLikeCount(id);

        return new ApiResponse(Status.SUCCESS, "successfully liked post.");
    }

    @Override
    @Transactional
    public ApiResponse unlikePost(UUID id, Jwt userPrincipal) {
        Post post = this.getPostById(id);
        User user = userRepository.findById(UUID.fromString(userPrincipal.getSubject()))
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );

        post.removeLike(user);
        postRepository.save(post);
        postRepository.decrementLikeCount(id);

        return new ApiResponse(Status.SUCCESS, "successfully unliked post.");
    }

    @Override
    @Transactional
    public ApiResponse repostPost(UUID id, Jwt userPrincipal) {
        User user = userRepository.findById(UUID.fromString(userPrincipal.getSubject()))
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );

        user.addRepost(postRepository.getReferenceById(id));
        userRepository.save(user);

        return new ApiResponse(Status.SUCCESS, "successfully reposted post.");
    }

    @Override
    @Transactional
    public ApiResponse unrepostPost(UUID id, Jwt userPrincipal) {
        User user = userRepository.findById(UUID.fromString(userPrincipal.getSubject()))
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );

        user.removeRepost(postRepository.getReferenceById(id));
        userRepository.save(user);

        return new ApiResponse(Status.SUCCESS, "successfully removed repost.");
    }

    private String handleMediaUpload(String userId, MultipartFile mediaFile) {
        if (mediaFile == null) return null;

        String mediaName = userId + "-" + UUID.randomUUID();

        try {
            s3Service.upload(
                    mediaName, mediaFile.getBytes(), mediaFile.getContentType()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read fine. Please try again.");
        }

        return mediaName;
    }

    @Override
    public void saveTest(SavePostDTO savePostDTO) {
        MultipartFile mediaFile = savePostDTO.media();

        String mediaName = "test" + "-" + UUID.randomUUID();

        try {
            s3Service.upload(
                    mediaName, mediaFile.getBytes(), mediaFile.getContentType()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read fine. Please try again.");
        }
    }

    @Override
    public String presignedTest(String media) {
        return s3Service.getPresignedUrl(media);
    }

}
