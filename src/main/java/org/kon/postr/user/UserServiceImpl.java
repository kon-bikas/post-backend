package org.kon.postr.user;

import org.kon.postr.exception.ResourceNotFoundException;
import org.kon.postr.keycloak.KeycloakService;
import org.kon.postr.keycloak.dto.KeycloakEventUser;
import org.kon.postr.storage.S3Service;
import org.kon.postr.response.ApiResponse;
import org.kon.postr.response.PaginatedResponse;
import org.kon.postr.response.SimpleBodyResponse;
import org.kon.postr.response.SliceMetadata;
import org.kon.postr.user.dto.UpdateUserDTO;
import org.kon.postr.user.dto.UserCardDTO;
import org.kon.postr.user.dto.UserInfoDTO;
import org.kon.postr.user.dto.UserProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static org.kon.postr.response.ApiResponse.Status;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Value(value = "${app.pageable.page-size}")
    private int pageSize;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final S3Service s3Service;
    private final KeycloakService keycloakService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           S3Service s3Service, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.s3Service = s3Service;
        this.keycloakService = keycloakService;
    }

//    @Override
//    @Transactional
//    public ApiResponse save(User user) {
//        if (userRepository.existsByUsername(user.getUsername())) {
//            throw new IllegalArgumentException("User already exists");
//        }
//
//        user.setPicture(
//                user.getUsername().substring(0, 1).toUpperCase() + "-profile"
//        );
//        userRepository.save(user);
//
//        return new ApiResponse(
//                Status.SUCCESS,
//                "user created and saved successfully."
//        );
//
//    }

    @Override
    @Transactional
    public ApiResponse save(KeycloakEventUser keycloakEventUser) {
        // Note: keycloak already makes that check
        if (userRepository.existsByUsername(keycloakEventUser.username())) {
//            throw new IllegalArgumentException("User already exists");
            return new ApiResponse(Status.ERROR, "user already exist");
        }

        userRepository.save(
                new User(
                        keycloakEventUser.userId(),
                        keycloakEventUser.username(),
                        keycloakEventUser.username().substring(0, 1).toUpperCase() + "-profile"
                )
        );

        return new ApiResponse(
                Status.SUCCESS,
                "user created and saved successfully."
        );

    }


    @Override
    @Transactional
    public ApiResponse update(UpdateUserDTO updateUserDTO) {

        // do not really know if this is needed since only the authentication principle can do that
//        if (!userRepository.existsById(user.getId())) {
//            throw new IllegalArgumentException("User does not exist");
//        }



//        userRepository.save(user);

        return new ApiResponse(
                Status.SUCCESS,
                "user updated successfully."
        );
    }

    @Override
    @Transactional
    public SimpleBodyResponse<UserInfoDTO> getProfileInfo(Jwt userPrincipal) {
        return new SimpleBodyResponse<>(
                Status.SUCCESS, "profile info retrieved successfully.",
                keycloakService.getUserInfo(
                        userPrincipal.getClaimAsString("preferred_username")
                )
        );
    }

    @Override
    @Transactional
    public List<UserCardDTO> getAll() {
        return userRepository.findAllBy();
    }

    @Override
    @Transactional
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User does not exist")
                );
    }

    @Override
    @Transactional
    public SimpleBodyResponse<UserProfileDTO> getUserByUsername(String username, Jwt userPrincipal) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User does not exist")
                );

        boolean isFollowing = userRepository.existsFollowing(
                UUID.fromString(userPrincipal.getSubject()), user.getId()
        );

        return new SimpleBodyResponse<>(
            ApiResponse.Status.SUCCESS, "User profile retrieved successfully.",
                    userMapper.toUserProfileDTO(user, isFollowing)
        );
    }

    @Override
    @Transactional
    public PaginatedResponse<UserCardDTO, SliceMetadata> getUserFollowingById(UUID id, int pageNumber) {
        Slice<UserCardDTO> followingUsers = userRepository.findByFollowing_Id(
                id, PageRequest.of(pageNumber, pageSize));

        return new PaginatedResponse<>(
                Status.SUCCESS, "Following users retrieved successfully.",
                followingUsers.getContent(),
                new SliceMetadata(
                        followingUsers.getNumber(), followingUsers.hasNext()
                )
        );
    }

    @Override
    @Transactional
    public PaginatedResponse<UserCardDTO, SliceMetadata> getUserFollowersById(UUID id, int pageNumber) {
        Slice<UserCardDTO> followersUsers = userRepository.findByFollowers_Id(
                id, PageRequest.of(pageNumber, pageSize));

        return new PaginatedResponse<>(
                Status.SUCCESS, "Followers retrieved successfully.",
                followersUsers.getContent(),
                new SliceMetadata(
                        followersUsers.getNumber(), followersUsers.hasNext()
                )
        );

    }

    @Override
    @Transactional
    public PaginatedResponse<UserCardDTO, SliceMetadata> getUsersThatLikedPost(UUID postId, int pageNumber) {
        Slice<UserCardDTO> likedPostUsers = userRepository.findByLikedPosts_Id(
                postId, PageRequest.of(pageNumber, pageSize));

        return new PaginatedResponse<>(
                Status.SUCCESS, "Users that likes post retrieved successfully.",
                likedPostUsers.getContent(),
                new SliceMetadata(
                        likedPostUsers.getNumber(), likedPostUsers.hasNext()
                )
        );
    }

    @Override
    @Transactional
    public ApiResponse updateUserPicture(MultipartFile pictureFile, Jwt userPrincipal) {
        User user = userRepository.findById(UUID.fromString(userPrincipal.getSubject()))
                .orElseThrow(() ->
                        new ResourceNotFoundException("User does not exist")
                );

        String mediaName = user.getPicture();

        // check if to change the name or override the existing one
        if (mediaName.equals(user.getUsername().substring(0, 1).toUpperCase() + "-profile")) {
            mediaName =  userPrincipal.getSubject() + "-" + UUID.randomUUID();
        }

        try {
            s3Service.upload(
                    mediaName, pictureFile.getBytes(), pictureFile.getContentType()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read fine. Please try again.");
        }

        return new ApiResponse(
                Status.SUCCESS, "user's profile picture updated successfully."
        );

    }

    @Override
    @Transactional
    public ApiResponse followUser(Jwt token, UUID followedUserId) {
        User followerUser = userRepository.getReferenceById(
                UUID.fromString(token.getSubject())
        );
        User followedUser = userRepository.getReferenceById(followedUserId);

        followerUser.addFollowing(followedUser);
        userRepository.save(followerUser);

        userRepository.incrementFollowingCount(UUID.fromString(token.getSubject()));
        userRepository.incrementFollowersCount(followedUserId);

        return new ApiResponse(
                Status.SUCCESS,
                "User followed successfully."
        );
    }

    @Override
    @Transactional
    public ApiResponse unfollowUser(Jwt token, UUID followedUserId) {
        User followerUser = userRepository.getReferenceById(
                UUID.fromString(token.getSubject())
        );
        User followedUser = userRepository.getReferenceById(followedUserId);

        followerUser.removeFollowing(followedUser);
        userRepository.save(followerUser);

        userRepository.decrementFollowingCount(UUID.fromString(token.getSubject()));
        userRepository.decrementFollowersCount(followedUserId);

        return new ApiResponse(
                Status.SUCCESS,
                "User unfollowed successfully."
        );
    }

    @Transactional
    protected User getAuthenticatedUser(Jwt principal) {
        return userRepository.findByUsername(
                principal.getClaimAsString("preferred_username")
        ).orElseThrow(() ->
                new ResourceNotFoundException("User does not exist")
        );
    }

}
