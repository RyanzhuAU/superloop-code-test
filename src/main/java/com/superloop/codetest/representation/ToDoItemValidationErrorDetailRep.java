package com.superloop.codetest.representation;

import lombok.Data;

/**
 * Created by ryan.zhu on 24/05/2018.
 */

@Data
public class ToDoItemValidationErrorDetailRep {

    private String location;

    private String param;

    private String msg;

    private String value;

    public ToDoItemValidationErrorDetailRep(String location, String param, String msg, String value) {
        this.location = location;
        this.param = param;
        this.msg = msg;
        this.value = value;
    }

    public ToDoItemValidationErrorDetailRep() {}

}
