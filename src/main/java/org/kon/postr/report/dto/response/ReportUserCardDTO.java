package org.kon.postr.report.dto.response;

import java.util.UUID;

public record ReportUserCardDTO(

        UUID userId,

        String username,

        String picture,

        String reason

) {
}
