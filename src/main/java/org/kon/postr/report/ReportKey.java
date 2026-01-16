package org.kon.postr.report;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReportKey implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "post_id")
    private UUID postId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if(o == null || getClass() != o.getClass())
            return false;

        ReportKey otherReportKey = (ReportKey) o;
        return Objects.equals(userId, otherReportKey.userId) &&
                Objects.equals(postId, otherReportKey.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }

}
