package com.superloop.codetest.controller;

import com.superloop.codetest.Constants;
import com.superloop.codetest.exception.ToDoItemNotFoundException;
import com.superloop.codetest.exception.ToDoItemValidationErrorException;
import com.superloop.codetest.model.ToDoItem;
import com.superloop.codetest.service.ToDoItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by ryan.zhu on 24/05/2018.
 */

@RestController
@RequestMapping("/todo")
public class ToDoItemController {

    @Autowired
    private ToDoItemService toDoItemService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * GET /todo/{id} - returns the todo item
     *
     * @param itemId
     * @return todo item and status 200, 400, 404
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity getToDoItem(@PathVariable("id") String itemId) {
        try {
            ToDoItem item = toDoItemService.getToDoItem(Long.valueOf(itemId));

            return new ResponseEntity(item, HttpStatus.OK);
        } catch (ToDoItemNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getNotFoundError(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/listAllItems", method = RequestMethod.GET)
    public ResponseEntity getAllToDoItems() {
        try {
            List<ToDoItem> itemList = toDoItemService.listToDoItems();

            return new ResponseEntity(itemList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /todo - create the new todo item
     * e.g. with JSON input {"name": "Uulwi ifis halahs gag erh'ongg w'ssh."}
     * create a todo item with the name "Uulwi ifis halahs gag erh'ongg w'ssh." and set complete to false as default
     *
     * @param json
     * @return JSON - ToDoItem object and status 200, or status 400
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createToDoItem(@RequestBody String json) {
        try {
            ToDoItem item = toDoItemService.createToDoItem(json);

            return new ResponseEntity(item, HttpStatus.OK);
        } catch (ToDoItemValidationErrorException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getValidationError(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(Constants.INPUT_STRING_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT /todo/{id} - Update the selected todo item.
     * @param json
     * @param itemId
     * @return status 200, or 400, 404 with error message for different exceptions.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateToDoItem(@RequestBody String json, @PathVariable("id") String itemId) {
        try {
            toDoItemService.updateToDoItem(Long.valueOf(itemId), json);

            return new ResponseEntity(HttpStatus.OK);
        } catch (ToDoItemValidationErrorException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getValidationError(), HttpStatus.BAD_REQUEST);
        } catch (ToDoItemNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getNotFoundError(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(Constants.INPUT_STRING_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }



    /**
     * Delete the selected todo item.
     * @param itemId
     * @return status 200, or 400, 404 with error message for different exceptions.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteToDoItem(@PathVariable("id") String itemId) {
        try {
            toDoItemService.deleteToDoItem(Long.valueOf(itemId));

            return new ResponseEntity(HttpStatus.OK);
        } catch (ToDoItemNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getNotFoundError(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete all todo items.
     * @return status 200, or 400.
     */
    @RequestMapping(value = "/deleteAll", method = RequestMethod.DELETE)
    public ResponseEntity deleteAllToDoItems() {
        try {
            this.toDoItemService.deleteAllToDoItems();

            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
