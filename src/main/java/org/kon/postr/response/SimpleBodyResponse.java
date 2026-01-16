package org.kon.postr.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SimpleBodyResponse<T> extends ApiResponse {

    private T data;

    public SimpleBodyResponse(Status status, String message, T data) {
        super(status, message);
        this.data = data;
    }

}
