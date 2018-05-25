package com.superloop.codetest.exception;

import com.superloop.codetest.representation.ToDoItemValidationErrorRep;
import lombok.Data;

/**
 * Created by ryan.zhu on 24/05/2018.
 */

@Data
public class ToDoItemValidationErrorException extends Exception {

    private ToDoItemValidationErrorRep validationError;

    public ToDoItemValidationErrorException(ToDoItemValidationErrorRep validationError) {
        super();

        this.validationError = validationError;
    }

}
