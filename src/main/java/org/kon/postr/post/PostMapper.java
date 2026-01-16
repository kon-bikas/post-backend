package org.kon.postr.post;

import org.kon.postr.storage.S3Service;
import org.kon.postr.post.dto.response.PostCardDTO;
import org.kon.postr.user.dto.UserCardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class PostMapper {

    private final S3Service s3Service;
    private final PostRepository postRepository;

    @Autowired
    public PostMapper(S3Service s3Service, PostRepository postRepository) {
        this.s3Service = s3Service;
        this.postRepository = postRepository;
    }

    @Transactional
    public PostCardDTO toPostCardDTO(Post post, Jwt userPrincipal) {
        return new PostCardDTO(
                post.getId(),
                null,
                new UserCardDTO(
                        post.getUser().getId(),
                        post.getUser().getUsername(),
                        post.getUser().getPicture()),
                post.getCreatedAt(),
                post.getContent(),
                this.mediaToPresignedUrl(post.getMedia()),
                post.getLikeCount(),
                post.getReplyCount(),
                postRepository.existsLikeByUserAndPost(
                        post.getId(), UUID.fromString(userPrincipal.getSubject())
                ),
                postRepository.existsRepostByUserAndPost(
                        post.getId(), UUID.fromString(userPrincipal.getSubject())
                ),
                post.getUser().getId().equals(
                        UUID.fromString(userPrincipal.getSubject())
                )
        );
    }

    @Transactional
    public PostCardDTO toReplyCardDTO(Post post, Jwt userPrincipal) {
        if (post.getReplyParent() == null) {
            return this.toPostCardDTO(post, userPrincipal);
        }
        return new PostCardDTO(
                post.getId(),
                this.toPostCardDTO(post.getReplyParent(), userPrincipal),
                new UserCardDTO(
                        post.getUser().getId(),
                        post.getUser().getUsername(),
                        post.getUser().getPicture()),
                post.getCreatedAt(),
                post.getContent(),
                post.getMedia(),
                post.getLikeCount(),
                post.getReplyCount(),
                postRepository.existsLikeByUserAndPost(
                        post.getId(), UUID.fromString(userPrincipal.getSubject())
                ),
                postRepository.existsRepostByUserAndPost(
                        post.getId(), UUID.fromString(userPrincipal.getSubject())
                ),
                post.getUser().getId().equals(
                        UUID.fromString(userPrincipal.getSubject())
                )
        );
    }

    @Transactional
    public PostCardDTO toParentCardDTO(Post post, Jwt userPrincipal) {
        if (post == null) return null;
        return new PostCardDTO(
                post.getId(),
                this.toParentCardDTO(post.getReplyParent(), userPrincipal),
                new UserCardDTO(
                        post.getUser().getId(),
                        post.getUser().getUsername(),
                        post.getUser().getPicture()),
                post.getCreatedAt(),
                post.getContent(),
                this.mediaToPresignedUrl(post.getMedia()),
                post.getLikeCount(),
                post.getReplyCount(),
                postRepository.existsLikeByUserAndPost(
                        post.getId(), UUID.fromString(userPrincipal.getSubject())
                ),
                postRepository.existsRepostByUserAndPost(
                        post.getId(), UUID.fromString(userPrincipal.getSubject())
                ),
                post.getUser().getId().equals(
                        UUID.fromString(userPrincipal.getSubject())
                )
        );

    }

    private String mediaToPresignedUrl(String media) {
        return s3Service.getPresignedUrl(media);
    }

}
