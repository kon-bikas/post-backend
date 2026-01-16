package org.kon.postr.report;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kon.postr.post.Post;
import org.kon.postr.user.User;

import java.sql.Timestamp;

@Entity
@Table(name = "reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Report {

    public enum Reason {
        INAPPROPRIATE_CONTENT,
        HATE_SPEECH,
        VIOLENCE,
        HARASSMENT,
        OTHER
    }

    @EmbeddedId
    private ReportKey id;

    @Column(
            name = "reported_timestamp",
            updatable = false
    )
    @CreationTimestamp
    private Timestamp reportedAt;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Reason reason;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            optional = false
    )
    @MapsId("postId")
    @JoinColumn(
            name = "post_id",
            nullable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            optional = false
    )
    @MapsId("userId")
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    public Report(ReportKey id, Post post, User user, Reason reason) {
        this.id = id;
        this.post = post;
        this.user = user;
        this.reason = reason;
    }

    public Report(Timestamp reportedAt, Reason reason) {
        this.reportedAt = reportedAt;
        this.reason = reason;
    }

}
