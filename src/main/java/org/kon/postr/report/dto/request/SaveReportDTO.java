package org.kon.postr.report.dto.request;

import org.kon.postr.report.Report;

import java.util.UUID;

public record SaveReportDTO(

        UUID userId,

        UUID postId,

        Report.Reason reason

) {
}
