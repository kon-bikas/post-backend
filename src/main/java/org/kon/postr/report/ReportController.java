package org.kon.postr.report;

import org.kon.postr.report.dto.request.SaveReportDTO;
import org.kon.postr.report.dto.response.ReasonCountDTO;
import org.kon.postr.report.dto.response.ReportCardDTO;
import org.kon.postr.report.dto.response.ReportUserCardDTO;
import org.kon.postr.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.kon.postr.report.Report.Reason;

import java.util.*;

@RestController
@RequestMapping(value = "/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(value = "/{pageNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponse<ReportCardDTO, PageMetadata>> getReports(
                                                                    @PathVariable int pageNumber) {
        PaginatedResponse<ReportCardDTO, PageMetadata> response = reportService
                .getReportsDesc(pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> addReport(@RequestBody SaveReportDTO saveReportDTO) {
        ApiResponse response = reportService.save(saveReportDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/get/reason/{pageNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponse<ReportCardDTO, PageMetadata>> getReportsByReason(
                                                                    @RequestBody Set<Reason> reasons,
                                                                    @PathVariable int pageNumber) {
        PaginatedResponse<ReportCardDTO, PageMetadata> response = reportService
                .getReportsByReasons(reasons, pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/reason-count/{postId}/{pageNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SimpleBodyResponse<List<ReasonCountDTO>>> getReportCount(
                                                                    @PathVariable UUID postId,
                                                                    @PathVariable int pageNumber) {
        SimpleBodyResponse<List<ReasonCountDTO>> response = reportService
                .getReasonCounts(postId, pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/users/{postId}/{pageNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponse<ReportUserCardDTO, SliceMetadata>> getReportUsers(
                                                                    @PathVariable UUID postId,
                                                                    @PathVariable int pageNumber) {
        PaginatedResponse<ReportUserCardDTO, SliceMetadata> response = reportService
                .getReportUsers(postId, pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/users/reason/{postId}/{pageNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponse<ReportUserCardDTO, SliceMetadata>> getReportUsersByReasons(
                                                                    @PathVariable UUID postId,
                                                                    @PathVariable int pageNumber,
                                                                    @RequestBody Set<Reason> reasons) {
        PaginatedResponse<ReportUserCardDTO, SliceMetadata> response = reportService
                .getReportUsersByReasons(postId, reasons, pageNumber);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/test/{postId}")
    @PreAuthorize("@authz.isPostOwner(#postId)")
    public String test(@PathVariable UUID postId) {
        return "You access the resource!!!";
    }

}
