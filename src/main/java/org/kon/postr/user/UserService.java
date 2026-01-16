package org.kon.postr.user;

import org.hibernate.sql.Update;
import org.kon.postr.keycloak.dto.KeycloakEventUser;
import org.kon.postr.response.ApiResponse;
import org.kon.postr.response.PaginatedResponse;
import org.kon.postr.response.SimpleBodyResponse;
import org.kon.postr.response.SliceMetadata;
import org.kon.postr.user.dto.UpdateUserDTO;
import org.kon.postr.user.dto.UserCardDTO;
import org.kon.postr.user.dto.UserInfoDTO;
import org.kon.postr.user.dto.UserProfileDTO;
import org.springframework.data.domain.Slice;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {

//    ApiResponse save(User user);
    ApiResponse save(KeycloakEventUser user);

    ApiResponse update(UpdateUserDTO updateUserDTO);

    SimpleBodyResponse<UserInfoDTO> getProfileInfo(Jwt userPrincipal);

    List<UserCardDTO> getAll();

    User getUserById(UUID id);

    SimpleBodyResponse<UserProfileDTO> getUserByUsername(String username, Jwt userPrincipal);

    PaginatedResponse<UserCardDTO, SliceMetadata> getUserFollowingById(UUID id, int pageNumber);

    PaginatedResponse<UserCardDTO, SliceMetadata> getUserFollowersById(UUID id, int pageNumber);

    PaginatedResponse<UserCardDTO, SliceMetadata> getUsersThatLikedPost(UUID postId, int pageNumber);

    ApiResponse updateUserPicture(MultipartFile picture, Jwt userPrincipal);

    ApiResponse followUser(Jwt userPrincipal, UUID followedUserId);

    ApiResponse unfollowUser(Jwt userPrincipal, UUID followedUserId);

}
