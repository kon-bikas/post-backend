package org.kon.postr.post;

import org.kon.postr.post.dto.response.PostCardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    /*
     * Get the posts of a user given the user's username and also post type:
     * - POST for original user posts
     * - REPLY for user's replies in other posts (or replies)
     *
     * has problem with N + 1 query for replies
     * problem with eagerly fetched data resulting in redundant queries
     */
    Slice<Post> findByUser_UsernameAndType(String username, Post.Type type, Pageable pageable);

    /*
     * Get the liked posts of a specific user by user's id
     */
    Page<Post> findByLikes_Id(UUID userId, Pageable pageable);

    /*
     * Get the Replies of a Post (or reply) based on the post's id
     */
    Slice<Post> findByReplyParent_Id(UUID parentId, Pageable pageable);

    /*
     * Get the reposted posts of a specific user by user's id.
     * We use native query because the relationship is unidirectional and
     * exists only in the User side.
     */
    @Query( value = """
                        SELECT p.* FROM posts p
                        JOIN user_reposts u_r ON p.id = u_r.post_id
                        WHERE u_r.user_id = :userId
                    """,
            nativeQuery = true)
    Slice<Post> findRepostsByUser_Id(@Param("userId") UUID userId, Pageable pageable);

    /*
     * Check if given user has liked a given post, if yes then true, otherwise false.
     */
    @Query( value = """
                        SELECT EXISTS(
                            SELECT 1 FROM post_user_likes
                            WHERE post_id = :postId AND user_id = :userId
                        )
                    """,
            nativeQuery = true)
    boolean existsLikeByUserAndPost(UUID postId, UUID userId);

    /*
     * Check if given user has reposted a given post, if yes then true, otherwise false.
     */
    @Query( value = """
                        SELECT EXISTS(
                            SELECT 1 FROM user_reposts
                            WHERE post_id = :postId AND user_id = :userId
                        )
                    """,
            nativeQuery = true)
    boolean existsRepostByUserAndPost(UUID postId, UUID userId);

    @Modifying
    @Query( value = """
                        UPDATE posts p SET like_count = like_count + 1
                        WHERE p.id = :postId
                    """,
            nativeQuery = true)
    void incrementLikeCount(@Param("postId") UUID postId);

    @Modifying
    @Query( value = """
                        UPDATE posts p SET like_count = like_count - 1
                        WHERE p.id = :postId
                    """,
            nativeQuery = true)
    void decrementLikeCount(@Param("postId") UUID postId);

    @Modifying
    @Query( value = """
                        UPDATE posts p SET reply_count = reply_count + 1
                        WHERE p.id = :postId
                    """,
            nativeQuery = true)
    void incrementReplyCount(@Param("postId") UUID postId);

    @Modifying
    @Query( value = """
                        UPDATE posts p SET reply_count = reply_count - 1
                        WHERE p.id = :postId
                    """,
            nativeQuery = true)
    void decrementReplyCount(@Param("postId") UUID postId);

}
