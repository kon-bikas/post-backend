package org.kon.postr.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ApiResponse {

    public enum Status {
        SUCCESS,
        ERROR
    }

    private Status status;

    private String message;

}
