package org.kon.postr.report.dto.response;

import java.util.UUID;

public record ReportCardDTO(

        String picture,

        String username,

        UUID postId,

        String content,

        String media,

        long reportCount

) {
}
