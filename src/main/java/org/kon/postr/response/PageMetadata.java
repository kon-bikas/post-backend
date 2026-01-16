package org.kon.postr.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PageMetadata extends SliceMetadata {

    private long totalElements;

    private int totalPages;

    public PageMetadata(int currentNumber, boolean hasNextPage, long totalElements, int totalPages) {
        super(currentNumber, hasNextPage);
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

}
