package org.kon.postr.report;

import org.kon.postr.post.PostRepository;
import org.kon.postr.report.dto.request.SaveReportDTO;
import org.kon.postr.report.dto.response.ReasonCountDTO;
import org.kon.postr.report.dto.response.ReportCardDTO;
import org.kon.postr.report.dto.response.ReportUserCardDTO;
import org.kon.postr.response.*;
import org.kon.postr.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.kon.postr.report.Report.Reason;
import static org.kon.postr.response.ApiResponse.Status;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Value(value = "${app.pageable.page-size}")
    private int pageSize;

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository,
                             PostRepository postRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PaginatedResponse<ReportCardDTO, PageMetadata> getReportsDesc(int pageNumber) {
        Page<ReportCardDTO> reports = reportRepository.findReportsOrderByReportCountDesc(
                PageRequest.of(pageNumber, pageSize)
        );

        return new PaginatedResponse<>(
                Status.SUCCESS, "reports retrieved successfully",
                reports.getContent(),
                new PageMetadata(
                        reports.getNumber(), reports.hasNext(),
                        reports.getTotalElements(), reports.getTotalPages()
                )
        );

    }

    @Override
    @Transactional
    public ApiResponse save(SaveReportDTO saveReportDTO) {
        if (!postRepository.existsById(saveReportDTO.postId()))
            throw new RuntimeException("post not found");

        if (reportRepository.existsById(
                new ReportKey(saveReportDTO.userId(), saveReportDTO.postId()))) {
            throw new RuntimeException("report already exists");
        }

        reportRepository.save(
                new Report(
                        new ReportKey(saveReportDTO.userId(), saveReportDTO.postId()),
                        postRepository.getReferenceById(saveReportDTO.postId()),
                        userRepository.getReferenceById(saveReportDTO.userId()),
                        saveReportDTO.reason()
                )
        );

        return new ApiResponse(
                Status.SUCCESS,
                "report was saved successfully."
        );

    }

    @Override
    @Transactional
    public PaginatedResponse<ReportCardDTO, PageMetadata> getReportsByReasons(
                                                                Set<Reason> reasons, int pageNumber) {

        Page<ReportCardDTO> reports = reportRepository.findReportsByReasonsOrderByReportCountDesc(
                reasons.stream().map(Enum::name).collect(Collectors.toSet()),
                PageRequest.of(pageNumber, pageSize)
        );

        return new PaginatedResponse<>(
                Status.SUCCESS, "reports retrieved successfully",
                reports.getContent(),
                new PageMetadata(
                        reports.getNumber(), reports.hasNext(),
                        reports.getTotalElements(), reports.getTotalPages()
                )
        );
    }

    @Override
    @Transactional
    public SimpleBodyResponse<List<ReasonCountDTO>> getReasonCounts(UUID postId, int pageNumber) {
        if (!postRepository.existsById(postId))
            throw new RuntimeException("post not found");

        List<ReasonCountDTO> reasonCount = reportRepository.findReasonsCountByPostId(
                postId, PageRequest.of(pageNumber, pageSize)
        );

        return new SimpleBodyResponse<>(
                Status.SUCCESS, "reason counts retrieved successfully.",
                reasonCount
        );
    }

    @Override
    @Transactional
    public PaginatedResponse<ReportUserCardDTO, SliceMetadata> getReportUsers(UUID postId, int pageNumber) {
        if (!postRepository.existsById(postId))
            throw new RuntimeException("post not found");

        Slice<ReportUserCardDTO> reportUsers = reportRepository.findUsersByReport(
                postId, PageRequest.of(pageNumber, pageSize)
        );

        return new PaginatedResponse<>(
                Status.SUCCESS, "user reporters retrieved successfully.",
                reportUsers.getContent(),
                new SliceMetadata(
                        reportUsers.getNumber(), reportUsers.hasNext()
                )
        );
    }

    @Override
    @Transactional
    public PaginatedResponse<ReportUserCardDTO, SliceMetadata> getReportUsersByReasons(
                                                                UUID postId, Set<Reason> reasons,
                                                                int pageNumber) {
        if (!postRepository.existsById(postId))
            throw new RuntimeException("post not found");

        Slice<ReportUserCardDTO> reportUsers = reportRepository.findUsersByReportAndReason(
                postId, reasons.stream().map(Enum::name).collect(Collectors.toSet()),
                PageRequest.of(pageNumber, pageSize)
        );

        return new PaginatedResponse<>(
                Status.SUCCESS, "user reporters retrieved successfully.",
                reportUsers.getContent(),
                new SliceMetadata(
                        reportUsers.getNumber(), reportUsers.hasNext()
                )
        );

    }

}
