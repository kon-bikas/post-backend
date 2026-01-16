package org.kon.postr.report;

import org.kon.postr.report.dto.response.ReasonCountDTO;
import org.kon.postr.report.dto.response.ReportCardDTO;
import org.kon.postr.report.dto.response.ReportUserCardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, ReportKey> {

    /*
     * Get reports of all reasons ordered by report count in desc order
     */
    @Query( value = """
                        SELECT u.picture_url AS picture,
                               u.username AS username,
                               p.id AS postId,
                               p.content AS content,
                               p.media_url AS media,
                               COUNT(*) AS reportCount
                        FROM reports r
                        JOIN posts p ON r.post_id = p.id
                        JOIN users u ON u.id = p.user_id
                        GROUP BY u.picture_url, u.username, p.id, p.content, p.media_url
                        ORDER BY reportCount DESC
                    """
    , nativeQuery = true)
    Page<ReportCardDTO> findReportsOrderByReportCountDesc(Pageable pageable);

    /*
     * Get reports of all reasons ordered by report count in desc order
     */
    @Query( value = """
                        SELECT u.picture_url AS picture,
                               u.username AS username,
                               p.id AS postId,
                               p.content AS content,
                               p.media_url AS media,
                               COUNT(*) AS reportCount
                        FROM reports r
                        JOIN posts p ON r.post_id = p.id
                        JOIN users u ON u.id = p.user_id
                        WHERE r.reason IN (:reasons)
                        GROUP BY u.picture_url, u.username, p.id, p.content, p.media_url
                        ORDER BY reportCount DESC
                    """
            , nativeQuery = true)
    Page<ReportCardDTO> findReportsByReasonsOrderByReportCountDesc(
                                            @Param("reasons") Set<String> reasons, Pageable pageable);

    /*
     * Get all the report reasons and the report count for each one for a given post.
     * Return to a list since the biggest size can be the number of different reasons
     */
    @Query( value = """
                        SELECT reason, COUNT(*) AS reportCount FROM reports
                        WHERE post_id = :postId
                        GROUP BY reason
                    """
    , nativeQuery = true)
    List<ReasonCountDTO> findReasonsCountByPostId(@Param("postId") UUID postId, Pageable pageable);

    /*
     * Get all the users that have reported post with id postId
     */
    @Query( value = """
                        SELECT u.id AS userId,
                               u.username AS username,
                               u.picture_url AS picture,
                               r.reason FROM reports r
                        JOIN users u ON u.id = r.user_id
                        WHERE r.post_id = :postId
                    """
            , nativeQuery = true)
    Slice<ReportUserCardDTO> findUsersByReport(@Param("postId") UUID postId, Pageable pageable);

    /*
     * Get all users that have reported post with id postId for reason inside the reasons list
     */
    @Query( value = """
                        SELECT u.id, u.username, u.picture_url, r.reason FROM reports r
                        JOIN users u ON u.id = r.user_id
                        WHERE r.post_id = :postId AND r.reason IN (:reasons)
                    """
            , nativeQuery = true)
    Slice<ReportUserCardDTO> findUsersByReportAndReason(@Param("postId") UUID postId,
                                                        @Param("reasons") Set<String> reasons,
                                                        Pageable pageable);

}
