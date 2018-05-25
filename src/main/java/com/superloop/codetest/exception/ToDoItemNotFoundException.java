package com.superloop.codetest.exception;

import com.superloop.codetest.representation.ToDoItemNotFoundErrorRep;
import lombok.Data;

/**
 * Created by ryan.zhu on 24/05/2018.
 */

@Data
public class ToDoItemNotFoundException extends Exception {

    private ToDoItemNotFoundErrorRep notFoundError;

    public ToDoItemNotFoundException(ToDoItemNotFoundErrorRep notFoundError) {
        super();

        this.notFoundError = notFoundError;
    }

}
