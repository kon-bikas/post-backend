package org.kon.postr.user;

import org.kon.postr.user.dto.UserCardDTO;
import org.kon.postr.user.dto.UserProfileDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsername(String username);

    User getReferenceByUsername(String username);
    /*
     * The query will be like:
     * select ... from users as u where u.username = ?
     * CAUTION! No joins due to the fact that all relationship collections,
     * are lazy fetched
     */
    Optional<User> findByUsername(String username);

    List<UserCardDTO> findAllBy();
    /*
     * This is a Derived Query Method
     * Get the users that follow the user with id userId, as a Slice
     */
    Slice<UserCardDTO> findByFollowing_Id(UUID userId, Pageable pageable);

    /*
     * Get the users that user with id userId is following, as a Slice
     */
    Slice<UserCardDTO> findByFollowers_Id(UUID userId, Pageable pageable);

    /*
     * Get all the users that have liked a given post.
     */
    Slice<UserCardDTO> findByLikedPosts_Id(UUID postId, Pageable pageable);

    @Modifying
    @Query( value = """
                        UPDATE users u SET following_count = following_count + 1
                        WHERE u.id = :userId
                    """,
            nativeQuery = true)
    void incrementFollowingCount(@Param("userId") UUID userId);

    @Modifying
    @Query( value = """
                        UPDATE users u SET following_count = following_count - 1
                        WHERE u.id = :userId
                    """,
            nativeQuery = true)
    void decrementFollowingCount(@Param("userId") UUID userId);

    @Modifying
    @Query( value = """
                        UPDATE users u SET followers_count = followers_count + 1
                        WHERE u.id = :userId
                    """,
            nativeQuery = true)
    void incrementFollowersCount(@Param("userId") UUID userId);

    @Modifying
    @Query( value = """
                        UPDATE users u SET followers_count = followers_count - 1
                        WHERE u.id = :userId
                    """,
            nativeQuery = true)
    void decrementFollowersCount(@Param("userId") UUID userId);

    @Modifying
    @Query( value = """
                        UPDATE users u SET post_count = post_count + 1
                        WHERE u.id = :userId
                    """,
            nativeQuery = true)
    void incrementPostCount(@Param("userId") UUID userId);

    @Modifying
    @Query( value = """
                        UPDATE users u SET post_count = post_count - 1
                        WHERE u.id = :userId
                    """,
            nativeQuery = true)
    void decrementPostCount(@Param("userId") UUID userId);

    /*
     * Check if user is following the other user, if yes then true, otherwise false.
     */
    @Query( value = """
                        SELECT EXISTS(
                            SELECT 1 FROM user_following
                            WHERE follower_id = :followerId AND followed_id = :followedId
                        )
                    """,
            nativeQuery = true)
    boolean existsFollowing(@Param("followerId") UUID followerId, @Param("followedId") UUID followedId);

}
