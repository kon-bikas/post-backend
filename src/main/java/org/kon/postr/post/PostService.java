package org.kon.postr.post;

import org.kon.postr.post.dto.request.SavePostDTO;
import org.kon.postr.post.dto.request.SaveReplyDTO;
import org.kon.postr.post.dto.response.PostCardDTO;
import org.kon.postr.response.*;
import org.springframework.data.domain.Slice;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

public interface PostService {

    ApiResponse save(SavePostDTO savePostDTO, Jwt userPrincipal);

    ApiResponse delete(UUID id);

    SimpleBodyResponse<PostCardDTO> getPost(UUID id, Jwt userPrincipal);

    PaginatedResponse<PostCardDTO, SliceMetadata> getPostsByUser(String username, Jwt userPrincipal, int pageNumber);

    PaginatedResponse<PostCardDTO, SliceMetadata> getRepliesByUser(String username, Jwt userPrincipal, int pageNumber);

    SimpleBodyResponse<PostCardDTO> getParentPost(UUID id, Jwt userPrincipal);

    PaginatedResponse<PostCardDTO, SliceMetadata> getPostsLikedByUser(UUID userId, Jwt userPrincipal, int pageNumber);

    PaginatedResponse<PostCardDTO, SliceMetadata> getPostReplies(UUID id, Jwt userPrincipal, int pageNumber);

    PaginatedResponse<PostCardDTO, SliceMetadata> getPostsRepostedByUser(UUID userId, Jwt userPrincipal, int pageNumber);

    ApiResponse replyToPost(SaveReplyDTO saveReplyDTO, Jwt userPrincipal);

    ApiResponse likePost(UUID id, Jwt userPrincipal);

    ApiResponse unlikePost(UUID id, Jwt userPrincipal);

    ApiResponse repostPost(UUID id, Jwt userPrincipal);

    ApiResponse unrepostPost(UUID id, Jwt userPrincipal);



    void saveTest(SavePostDTO savePostDTO);

    String presignedTest(String media);
}
