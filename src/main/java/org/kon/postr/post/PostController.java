package org.kon.postr.post;

import jakarta.validation.Valid;
import org.kon.postr.post.dto.request.SavePostDTO;
import org.kon.postr.post.dto.request.SaveReplyDTO;
import org.kon.postr.post.dto.response.PostCardDTO;
import org.kon.postr.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(value = "/id/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SimpleBodyResponse<PostCardDTO>> getPost(
            @PathVariable UUID postId, @AuthenticationPrincipal Jwt userPrincipal) {
        SimpleBodyResponse<PostCardDTO> response = postService.getPost(postId, userPrincipal);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> addPost(
                                        @ModelAttribute @Valid SavePostDTO savePostDTO,
                                        @AuthenticationPrincipal Jwt userPrincipal) {
        ApiResponse response = postService.save(savePostDTO, userPrincipal);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{postId}")
    @PreAuthorize("@authz.isPostOwner(#postId) || hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable UUID postId) {
        ApiResponse response = postService.delete(postId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{username}/{pageNumber}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaginatedResponse<PostCardDTO, SliceMetadata>> getPostsByUser(
                                                                         @PathVariable String username,
                                                                         @AuthenticationPrincipal Jwt userPrincipal,
                                                                         @PathVariable int pageNumber) {
        PaginatedResponse<PostCardDTO, SliceMetadata> pageResponse = postService
                .getPostsByUser(username, userPrincipal, pageNumber);

        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/replies/user/{username}/{pageNumber}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaginatedResponse<PostCardDTO, SliceMetadata>> getRepliesByUser(
                                                                        @PathVariable String username,
                                                                        @AuthenticationPrincipal Jwt userPrincipal,
                                                                        @PathVariable int pageNumber) {
        PaginatedResponse<PostCardDTO, SliceMetadata> sliceResponse = postService
                .getRepliesByUser(username, userPrincipal, pageNumber);

        return new ResponseEntity<>(sliceResponse, HttpStatus.OK);
    }

    @GetMapping("/parent/{replyId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SimpleBodyResponse<PostCardDTO>> getParentPost(
                                                                    @PathVariable UUID replyId,
                                                                    @AuthenticationPrincipal Jwt userPrincipal) {
        SimpleBodyResponse<PostCardDTO> response = postService.getParentPost(replyId, userPrincipal);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/likes/{userId}/{pageNumber}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaginatedResponse<PostCardDTO, SliceMetadata>> getLikedPostsByUser(
                                                                        @PathVariable UUID userId,
                                                                        @AuthenticationPrincipal Jwt userPrincipal,
                                                                        @PathVariable int pageNumber) {
        PaginatedResponse<PostCardDTO, SliceMetadata> response =
                postService.getPostsLikedByUser(userId, userPrincipal, pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/replies/{postId}/{pageNumber}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaginatedResponse<PostCardDTO, SliceMetadata>> getPostReplies(
                                                                        @PathVariable UUID postId,
                                                                        @AuthenticationPrincipal Jwt userPrincipal,
                                                                        @PathVariable int pageNumber) {
        PaginatedResponse<PostCardDTO, SliceMetadata> response =
                postService.getPostReplies(postId, userPrincipal, pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/reposts/{userId}/{pageNumber}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaginatedResponse<PostCardDTO, SliceMetadata>> getPostsReposted(
                                                                        @PathVariable UUID userId,
                                                                        @AuthenticationPrincipal Jwt userPrincipal,
                                                                        @PathVariable int pageNumber) {
        PaginatedResponse<PostCardDTO, SliceMetadata> response =
                postService.getPostsRepostedByUser(userId, userPrincipal, pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/add/reply", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> addReply(
                                        @ModelAttribute @Valid SaveReplyDTO saveReplyDTO,
                                        @AuthenticationPrincipal Jwt userPrincipal) {
        ApiResponse response = postService.replyToPost(saveReplyDTO, userPrincipal);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/like/{postId}/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> likePost(
                                        @PathVariable UUID postId, @PathVariable UUID userId,
                                        @AuthenticationPrincipal Jwt userPrincipal) {
        ApiResponse response = postService.likePost(postId, userPrincipal);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/unlike/{postId}/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> unlikePost(
                                        @PathVariable UUID postId, @PathVariable UUID userId,
                                        @AuthenticationPrincipal Jwt userPrincipal) {
        ApiResponse response = postService.unlikePost(postId, userPrincipal);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/repost/{postId}/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> repostPost(
                                        @PathVariable UUID postId, @PathVariable UUID userId,
                                        @AuthenticationPrincipal Jwt userPrincipal) {
        ApiResponse response = postService.repostPost(postId, userPrincipal);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/unrepost/{postId}/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> unrepostPost(
                                        @PathVariable UUID postId, @PathVariable UUID userId,
                                        @AuthenticationPrincipal Jwt userPrincipal) {
        ApiResponse response = postService.unrepostPost(postId, userPrincipal);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/add/test", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> uploadTest(
            @ModelAttribute @Valid SavePostDTO savePostDTO) {
        postService.saveTest(savePostDTO);

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @PostMapping(value = "/presigned/test")
    public ResponseEntity<String> presignedTest(@RequestParam String media) {
        String presignedUrl = postService.presignedTest(media);

        return new ResponseEntity<>(presignedUrl, HttpStatus.OK);
    }

}
