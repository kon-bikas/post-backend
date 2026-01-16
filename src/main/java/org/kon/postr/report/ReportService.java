package org.kon.postr.report;

import org.kon.postr.report.dto.request.SaveReportDTO;
import org.kon.postr.report.dto.response.ReasonCountDTO;
import org.kon.postr.report.dto.response.ReportCardDTO;
import org.kon.postr.report.dto.response.ReportUserCardDTO;
import org.kon.postr.response.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ReportService {

    PaginatedResponse<ReportCardDTO, PageMetadata> getReportsDesc(int pageNumber);

    ApiResponse save(SaveReportDTO saveReportDTO);

    PaginatedResponse<ReportCardDTO, PageMetadata> getReportsByReasons(Set<Report.Reason> reasons,
                                                                       int pageNumber);

    SimpleBodyResponse<List<ReasonCountDTO>> getReasonCounts(UUID postId, int pageNumber);

    PaginatedResponse<ReportUserCardDTO, SliceMetadata> getReportUsers(UUID postId,
                                                                       int pageNumber);

    PaginatedResponse<ReportUserCardDTO, SliceMetadata> getReportUsersByReasons(UUID postId, Set<Report.Reason> reasons,
                                                                                int pageNumber);

}
