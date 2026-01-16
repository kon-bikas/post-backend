package org.kon.postr.user;

import org.kon.postr.response.ApiResponse;
import org.kon.postr.response.PaginatedResponse;
import org.kon.postr.response.SimpleBodyResponse;
import org.kon.postr.response.SliceMetadata;
import org.kon.postr.user.dto.UserCardDTO;
import org.kon.postr.user.dto.UserInfoDTO;
import org.kon.postr.user.dto.UserProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/all")
    public List<UserCardDTO> getAllUsers() {
        return userService.getAll();
    }

//    @PostMapping(value = "/add/{username}/{userPicture}")
//    public ResponseEntity<ApiResponse> addUser(
//                                    @PathVariable String username, @PathVariable String userPicture) {
//        ApiResponse response = userService.save(new User(username, userPicture));
//
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }

//    @PostMapping(value = "/update", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
//    public ResponseEntity<ApiResponse> updateUser(@ModelAttribute @Valid UpdateUserDTO) {
//
//    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        User user = userService.getUserById(userId);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/username/{username}")
    public ResponseEntity<SimpleBodyResponse<UserProfileDTO>> getUserByUsername(
                            @AuthenticationPrincipal Jwt userPrincipal, @PathVariable String username) {
        SimpleBodyResponse<UserProfileDTO> userResponse = userService
                .getUserByUsername(username, userPrincipal);

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/liked/{postId}/{pageNumber}")
    public ResponseEntity<PaginatedResponse<UserCardDTO, SliceMetadata>> getPostLikeUsers(
                                                @PathVariable UUID postId, @PathVariable int pageNumber) {
        PaginatedResponse<UserCardDTO, SliceMetadata> response = userService
                .getUsersThatLikedPost(postId, pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/follow/{followedId}")
    public ResponseEntity<ApiResponse> followUser(@AuthenticationPrincipal Jwt principal,
                                                  @PathVariable UUID followedId) {
        ApiResponse response = userService.followUser(principal, followedId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/unfollow/{followedId}")
    public ResponseEntity<ApiResponse> unfollowUser(@AuthenticationPrincipal Jwt principal,
                                                    @PathVariable UUID followedId) {
        ApiResponse response = userService.unfollowUser(principal, followedId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/following/{userId}/{pageNumber}")
    public ResponseEntity<PaginatedResponse<UserCardDTO, SliceMetadata>> getUserFollowing(
                                        @PathVariable UUID userId, @PathVariable int pageNumber) {
        PaginatedResponse<UserCardDTO, SliceMetadata> response =
                userService.getUserFollowingById(userId, pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/followers/{userId}/{pageNumber}")
    public ResponseEntity<PaginatedResponse<UserCardDTO, SliceMetadata>> getUserFollowers(
                                        @PathVariable UUID userId, @PathVariable int pageNumber) {
        PaginatedResponse<UserCardDTO, SliceMetadata> response =
                userService.getUserFollowersById(userId, pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/profile/info")
    public ResponseEntity<SimpleBodyResponse<UserInfoDTO>> getProfileInfo(
                                                        @AuthenticationPrincipal Jwt principal) {
        SimpleBodyResponse<UserInfoDTO> response = userService.getProfileInfo(principal);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/update/picture", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse> updatePicture(
                                                @RequestPart(name = "picture") MultipartFile picture,
                                                @AuthenticationPrincipal Jwt principal) {
        ApiResponse response = userService.updateUserPicture(picture, principal);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/me")
    public Jwt getMe(@AuthenticationPrincipal Jwt principal) {
        return principal;
    }

}
