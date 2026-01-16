package org.kon.postr.response;

import lombok.*;

import java.util.List;

@Getter @Setter
public class PaginatedResponse<T, M extends SliceMetadata> extends ApiResponse {

    private List<T> data;

    private M metadata;

    public PaginatedResponse(Status status, String message, List<T> data, M metadata) {
        super(status, message);
        this.data = data;
        this.metadata = metadata;
    }

    public PaginatedResponse(List<T> data, M metadata) {
        this.data = data;
        this.metadata = metadata;
    }

}
