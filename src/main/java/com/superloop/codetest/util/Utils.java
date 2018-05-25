package com.superloop.codetest.util;

import com.superloop.codetest.Constants;
import com.superloop.codetest.exception.ToDoItemValidationErrorException;
import com.superloop.codetest.representation.ToDoItemValidationErrorDetailRep;
import com.superloop.codetest.representation.ToDoItemValidationErrorRep;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * Create by ryan.zhu on 24/05/2018
 **/

public class Utils {
    public static Boolean inputTextValidation(String text) throws ToDoItemValidationErrorException {
        if (StringUtils.isEmpty(text)) {
            ToDoItemValidationErrorDetailRep validationErrorDetail = new ToDoItemValidationErrorDetailRep("params", "name", Constants.NAME_FIELD_NOT_DEFINED_ERROR_MESSAGE, "");
            ToDoItemValidationErrorRep validationError = new ToDoItemValidationErrorRep(Constants.VALIDATION_ERROR_NAME, Arrays.asList(validationErrorDetail));

            throw new ToDoItemValidationErrorException(validationError);
        }

        return true;
    }
}
