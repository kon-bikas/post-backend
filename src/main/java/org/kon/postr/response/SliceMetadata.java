package org.kon.postr.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class SliceMetadata {

    private int currentNumber;

    private boolean hasNext;

}
