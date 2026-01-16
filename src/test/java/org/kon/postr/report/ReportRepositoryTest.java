package org.kon.postr.report;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kon.postr.post.Post;
import org.kon.postr.post.PostRepository;
import org.kon.postr.report.dto.response.ReasonCountDTO;
import org.kon.postr.report.dto.response.ReportCardDTO;
import org.kon.postr.report.dto.response.ReportUserCardDTO;
import org.kon.postr.user.User;
import org.kon.postr.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ReportRepositoryTest {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final EntityManager entityManager;

    @Autowired
    public ReportRepositoryTest(PostRepository postRepository, UserRepository userRepository,
                                ReportRepository reportRepository, EntityManager entityManager) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.entityManager = entityManager;
    }

//    @Test
//    @Transactional
//    public void saveReportTest() {
//        LocalDateTime baseTime = LocalDateTime.now();
//
//        User mike = userRepository.findByUsername("mike").get();
//
//        Report report = new Report(
//                Timestamp.valueOf(baseTime.plusMinutes(0)),
//                Report.Reason.HATE_SPEECH
//        );
//        report.setId(
//                new ReportKey(
//                        mike.getId(),
//                        UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b")
//                ));
//
//        report.setUser(
//                userRepository.getReferenceByUsername("mike")
//        );
//
//        report.setPost(
//                postRepository.getReferenceById(
//                        UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b")
//                )
//        );
//
//        reportRepository.save(report);
//
//        postRepository.flush();
//        entityManager.clear();
//
//        List<Report> reports = reportRepository.findAll();
//
//        assertEquals(1, reports.size());
//        assertEquals("mike", reports.get(0).getUser().getUsername());
//    }
//
//    @Test
//    @Transactional
//    public void getReports() {
//        LocalDateTime baseTime = LocalDateTime.now();
//
//        User mike = userRepository.findByUsername("mike").get();
//        User emily = userRepository.findByUsername("emily").get();
//
//        Report report1 = new Report(
//                new ReportKey(
//                        mike.getId(),
//                        UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b")
//                ),
//                Timestamp.valueOf(baseTime.plusMinutes(0)),
//                Report.Reason.HATE_SPEECH,
//                postRepository.getReferenceById(
//                        UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b")
//                ),
//                userRepository.getReferenceByUsername("mike")
//        );
//
//        Report report2 = new Report(
//                new ReportKey(
//                        emily.getId(),
//                        UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b")
//                ),
//                Timestamp.valueOf(baseTime.plusMinutes(5)),
//                Report.Reason.HARASSMENT,
//                postRepository.getReferenceById(
//                        UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b")
//                ),
//                userRepository.getReferenceByUsername("emily")
//        );
//
//        reportRepository.saveAll(List.of(report1, report2));
//
//        postRepository.flush();
//        entityManager.clear();
//
////        List<Report> reports = reportRepository.findAll();
//        System.out.println("getting reports!");
//        Page<ReportCardDTO> reports = reportRepository.findReportsOrderByReportCountDesc(
//                PageRequest.of(0, 10));
//
//        System.out.println(reports.getContent().get(0));
//        assertEquals(1, reports.getTotalElements());
//
//        List<ReasonCountDTO> reasonCounts = reportRepository.findReasonsCountByPostId(
//                UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b"),
//                PageRequest.of(0, 10)
//        );
//
//        System.out.println(reasonCounts);
//        assertEquals(2, reasonCounts.size());
//
//
//
//        Slice<ReportUserCardDTO> userReporters = reportRepository.findUsersByReport(
//                UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b"),
//                PageRequest.of(0, 10)
//        );
//
//        System.out.println(userReporters.getContent());
//        assertEquals(2, userReporters.getNumberOfElements());
//
//
//
//        Slice<ReportUserCardDTO> userReportersByReason = reportRepository.findUsersByReportAndReason(
//                UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b"),
//                Set.of(Report.Reason.HATE_SPEECH.name(), Report.Reason.VIOLENCE.name()),
//                PageRequest.of(0, 10)
//        );
//
//        System.out.println(userReportersByReason.getContent());
//        assertEquals(1, userReportersByReason.getNumberOfElements());
//
//    }
//
//    @Test
//    @Transactional
//    public void getReportsByReason() {
//        LocalDateTime baseTime = LocalDateTime.now();
//
//        User mike = userRepository.findByUsername("mike").get();
//        User nick = userRepository.findByUsername("nick").get();
//
//        Report report1 = new Report(
//                new ReportKey(
//                        mike.getId(),
//                        UUID.fromString("fb606c0d-34a9-46fb-a9ea-1721ac20cd59")
//                ),
//                Timestamp.valueOf(baseTime.plusMinutes(0)),
//                Report.Reason.INAPPROPRIATE_CONTENT,
//                postRepository.getReferenceById(
//                        UUID.fromString("fb606c0d-34a9-46fb-a9ea-1721ac20cd59")
//                ),
//                userRepository.getReferenceByUsername("mike")
//        );
//
//        Report report2 = new Report(
//                new ReportKey(
//                        nick.getId(),
//                        UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b")
//                ),
//                Timestamp.valueOf(baseTime.plusMinutes(5)),
//                Report.Reason.HATE_SPEECH,
//                postRepository.getReferenceById(
//                        UUID.fromString("d17374cf-1e2e-4dfb-ae84-1763a14b6d0b")
//                ),
//                userRepository.getReferenceByUsername("nick")
//        );
//
//        reportRepository.saveAll(List.of(report1, report2));
//
//        postRepository.flush();
//        entityManager.clear();
//
////        List<Report> reports = reportRepository.findAll();
//
//        Page<ReportCardDTO> reports = reportRepository.findReportsOrderByReportCountDesc(
//                PageRequest.of(0, 10));
//
//        System.out.println(reports.getContent().get(0));
//        assertEquals(2, reports.getTotalElements());
//
//        Page<ReportCardDTO> reportsByReason = reportRepository.findReportsByReasonsOrderByReportCountDesc(
//                Set.of(Report.Reason.INAPPROPRIATE_CONTENT.name()), PageRequest.of(0, 10));
//
//        System.out.println(reportsByReason.getContent().get(0));
//        assertEquals(1, reportsByReason.getTotalElements());
//
//        Page<ReportCardDTO> reportsByTwoReason = reportRepository.findReportsByReasonsOrderByReportCountDesc(
//                Set.of(Report.Reason.INAPPROPRIATE_CONTENT.name(), Report.Reason.HATE_SPEECH.name()),
//                PageRequest.of(0, 10));
//
//        assertEquals(2, reportsByTwoReason.getTotalElements());
//
//    }
//
//    @Test
//    @Transactional
//    public void checkIfReportExists() {
//        LocalDateTime baseTime = LocalDateTime.now();
//
//        User mike = userRepository.findByUsername("mike").get();
//        User nick = userRepository.findByUsername("nick").get();
//
//        Report report = new Report(
//                new ReportKey(
//                        mike.getId(),
//                        UUID.fromString("fb606c0d-34a9-46fb-a9ea-1721ac20cd59")
//                ),
//                Timestamp.valueOf(baseTime.plusMinutes(0)),
//                Report.Reason.INAPPROPRIATE_CONTENT,
//                postRepository.getReferenceById(
//                        UUID.fromString("fb606c0d-34a9-46fb-a9ea-1721ac20cd59")
//                ),
//                userRepository.getReferenceByUsername("mike")
//        );
//
//        reportRepository.save(report);
//
//        postRepository.flush();
//        entityManager.clear();
//
//        boolean hasReported = reportRepository.existsById(
//                new ReportKey(mike.getId(), UUID.fromString("fb606c0d-34a9-46fb-a9ea-1721ac20cd59"))
//        );
//
//        assertTrue(hasReported);
//
//    }

}
