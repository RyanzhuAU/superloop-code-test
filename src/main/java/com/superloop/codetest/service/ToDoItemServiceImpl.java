package com.superloop.codetest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.superloop.codetest.Constants;
import com.superloop.codetest.component.ToDoItemMapBean;
import com.superloop.codetest.exception.ToDoItemNotFoundException;
import com.superloop.codetest.exception.ToDoItemValidationErrorException;
import com.superloop.codetest.model.ToDoItem;
import com.superloop.codetest.representation.ToDoItemAddRequestRep;
import com.superloop.codetest.representation.ToDoItemNotFoundErrorDetailRep;
import com.superloop.codetest.representation.ToDoItemNotFoundErrorRep;
import com.superloop.codetest.util.ItemStatus;
import com.superloop.codetest.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by ryan.zhu on 24/05/2018.
 */

@Service
public class ToDoItemServiceImpl implements ToDoItemService {

    @Autowired
    private ToDoItemMapBean toDoItemMapBean;

    private ObjectMapper mapper = new ObjectMapper();

    public ToDoItemServiceImpl(ToDoItemMapBean toDoItemMapBean) {
        this.toDoItemMapBean = toDoItemMapBean;
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Get the todo item by item id.
     *
     * @param itemId
     * @return the todo item
     * @throws ToDoItemNotFoundException
     */
    public ToDoItem getToDoItem(long itemId) throws ToDoItemNotFoundException {
        logger.info("Start getting selected todo item");

        checkItemExist(itemId);

        ToDoItem item = this.toDoItemMapBean.getToDoItem(itemId);

        return item;
    }

    /**
     * create the new todo item.
     *
     * @param json
     * @return the new todo item with Id
     * @throws ToDoItemValidationErrorException, IOException
     */
    public ToDoItem createToDoItem(String json) throws ToDoItemValidationErrorException, IOException {
        logger.info("Start creating the new todo item.");

        ToDoItemAddRequestRep addRequest = this.mapper.readValue(json, ToDoItemAddRequestRep.class);
        String name = addRequest.getName();
        String desc = addRequest.getDesc();
        Date dueDate = addRequest.getDueDate();

        ItemStatus itemStatus = null;
        if (!StringUtils.isEmpty(addRequest.getItemStatus())) {
            itemStatus = ItemStatus.valueOf(addRequest.getItemStatus());
        }

        ToDoItem item = new ToDoItem(name, desc, dueDate, itemStatus);

        if (Utils.inputTextValidation(name)) {
            item = this.toDoItemMapBean.addToDoItem(item);
        }

        return item;
    }

    /**
     * Update the selected todo item.
     *
     * @param itemId
     * @param json
     * @throws ToDoItemValidationErrorException
     * @throws IOException
     */
    public void updateToDoItem(long itemId, String json) throws ToDoItemValidationErrorException, ToDoItemNotFoundException, IOException {
        ToDoItem toDoItem = this.mapper.readValue(json, ToDoItem.class);

        if (checkItemExist(itemId) && Utils.inputTextValidation(toDoItem.getName())) {
            this.toDoItemMapBean.updateToDoItem(toDoItem, itemId);
        }
    }

    /**
     * Delete the selected todo item.
     *
     * @param itemId
     */
    public void deleteToDoItem(long itemId) throws ToDoItemNotFoundException {

        if (checkItemExist(itemId)) {
            this.toDoItemMapBean.deleteToDoItem(itemId);
        }
    }

    /**
     * List existing todo items.
     *
     * @return
     */
    public List<ToDoItem> listToDoItems() {
        ConcurrentHashMap<Long, ToDoItem> toDoItemMap = this.toDoItemMapBean.getToDoItemMap();
        List<ToDoItem> toDoItemList = toDoItemMap.values().stream()
                .collect(Collectors.toList());

        return toDoItemList;
    }

    /**
     * This function is used to check if the item exist and handle the exception.
     *
     * @param itemId
     * @return true if the item is existed, or throw ToDoItemNotFoundException.
     * @throws ToDoItemNotFoundException
     */
    private Boolean checkItemExist(long itemId) throws ToDoItemNotFoundException {
        if (!this.toDoItemMapBean.checkIfExist(itemId)) {
            String errorMsg = String.format(Constants.TODO_ITEM_NOT_FOUND_ERROR_MESSAGE, itemId);
            ToDoItemNotFoundErrorDetailRep errorDetailRep = new ToDoItemNotFoundErrorDetailRep(errorMsg);

            ToDoItemNotFoundErrorRep notFoundErrorRep = new ToDoItemNotFoundErrorRep(Constants.TODO_ITEM_NOT_FOUND_ERROR_NAME, Arrays.asList(errorDetailRep));

            throw new ToDoItemNotFoundException(notFoundErrorRep);
        }

        return true;
    }

    /**
     * Delete all todo items saved in the map.
     */
    public void deleteAllToDoItems() {
        this.toDoItemMapBean.deleteAllToDoItems();
    }

}
