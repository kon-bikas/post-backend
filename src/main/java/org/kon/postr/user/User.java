package org.kon.postr.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kon.postr.post.Post;

import java.util.*;

@Entity
@Table( name = "users",
        uniqueConstraints = {
            @UniqueConstraint(name = "uniqueUsername", columnNames = "username")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    // @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String username;

    @Column(name = "picture_url")
    private String picture;

    @Column
    private String description;

    /*
     * will take the user post's from the repository on demand, like:
     * for post:
     *  get post where user is {user_id} and post type is post
     * for replies of user:
     *  get post where user is {user_id} and post type is reply
     *
     * consider index in post type and user (publisher)
     */
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Post> posts = new LinkedList<>();

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "user_reposts",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "post_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"})
    )
    private Set<Post> reposts = new HashSet<>();

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "likes"
    )
    private Set<Post> likedPosts = new HashSet<>();

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"})
    )
    private Set<User> following = new HashSet<>();

    @Column(name = "following_count")
    private Long followingCount = 0L;

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "following"
    )
    private Set<User> followers = new HashSet<>();

    @Column(name = "followers_count")
    private Long followersCount = 0L;

    @Column(name = "post_count")
    private Long postCount = 0L;

    public User(UUID id, String username, String picture) {
        this.id = id;
        this.username = username;
        this.picture = picture;
    }

    public User(String username, String picture) {
        this.username = username;
        this.picture = picture;
    }

    public User(String username) {
        this.username = username;
    }

    public void addRepost(Post post) {
        reposts.add(post);
    }

    public void removeRepost(Post post) {
        reposts.remove(post);
    }

    public void addFollowing(User user) {
        following.add(user);
    }

    public void removeFollowing(User user) {
        following.remove(user);
    }

    public void addFollower(User user) {
        followers.add(user);
    }

    public void removeFollower(User user) {
        followers.remove(user);
    }

    public void addLikedPost(Post post) {
        likedPosts.add(post);
    }

    public void removeLikedPost(Post post) {
        likedPosts.remove(post);
    }
}

