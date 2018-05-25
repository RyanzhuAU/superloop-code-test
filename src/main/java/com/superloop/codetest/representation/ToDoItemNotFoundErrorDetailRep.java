package com.superloop.codetest.representation;

import lombok.Data;

/**
 * Created by ryan.zhu on 24/05/2018.
 */

@Data
public class ToDoItemNotFoundErrorDetailRep {

    private String message;

    public ToDoItemNotFoundErrorDetailRep(String message) {
        this.message = message;
    }

    public ToDoItemNotFoundErrorDetailRep() {}

}
