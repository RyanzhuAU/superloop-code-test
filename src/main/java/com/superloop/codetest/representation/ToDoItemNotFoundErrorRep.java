package com.superloop.codetest.representation;

import lombok.Data;

import java.util.List;

/**
 * Created by ryan.zhu on 24/05/2018.
 */

@Data
public class ToDoItemNotFoundErrorRep {

    private List<ToDoItemNotFoundErrorDetailRep> details;

    private String name;

    public ToDoItemNotFoundErrorRep(String name, List<ToDoItemNotFoundErrorDetailRep> details) {
        this.name = name;
        this.details = details;
    }

    public ToDoItemNotFoundErrorRep() {}

}
