package com.superloop.codetest.service;

import com.superloop.codetest.exception.ToDoItemNotFoundException;
import com.superloop.codetest.exception.ToDoItemValidationErrorException;
import com.superloop.codetest.model.ToDoItem;

import java.io.IOException;
import java.util.List;

/**
 * Created by ryan.zhu on 24/05/2018.
 */

public interface ToDoItemService {

    ToDoItem createToDoItem(String json) throws ToDoItemValidationErrorException, IOException;

    ToDoItem getToDoItem(long itemId) throws ToDoItemNotFoundException;

    void updateToDoItem(long itemId, String json) throws ToDoItemValidationErrorException, ToDoItemNotFoundException, IOException;

    List<ToDoItem> listToDoItems();

    void deleteToDoItem(long itemId) throws ToDoItemNotFoundException;

    void deleteAllToDoItems();

}
