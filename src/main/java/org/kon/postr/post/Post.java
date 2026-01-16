package org.kon.postr.post;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kon.postr.user.User;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Post {

    public enum Type {
        POST,
        REPLY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Type type;

    @Column(
            name = "creation_timestamp",
            updatable = false
    )
    @CreationTimestamp
    private Timestamp createdAt;

    @Column
    private String content;

    @Column(name = "media_url")
    private String media;


    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "reposts"
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<User> usersReposters = new HashSet<>();


    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "post_user_likes",
            joinColumns = @JoinColumn(name = "post_id", nullable = false ),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"})
    )
    private Set<User> likes = new HashSet<>();

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "parent_id"
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Post replyParent;

    @OneToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "replyParent"
    )
    private Set<Post> replies = new HashSet<>();

    @Column(name = "reply_count")
    private Long replyCount = 0L;

    public Post(Type type, String content, String media, User user) {
        this.type = type;
        this.content = content;
        this.media = media;
        this.user = user;
    }

    public Post(Type type, String content, String media, User user, Post replyParent) {
        this(type, content, media, user);
        this.replyParent = replyParent;
    }

    public void addReply(Post post) {
        replies.add(post);
    }

    /*
     * consider when deleting a post or reply, the content is null, display to the user that the post/reply
     * is deleted by the user owner, but the replies are listed below anyway.
     */

    public void addLike(User user) {
        likes.add(user);
    }

    // check if after removal the record in the join table is also removed
    public void removeLike(User user) {
        likes.remove(user);
    }

}
