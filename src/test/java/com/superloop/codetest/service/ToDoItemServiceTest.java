package com.superloop.codetest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.superloop.codetest.Application;
import com.superloop.codetest.Constants;
import com.superloop.codetest.component.ToDoItemMapBean;
import com.superloop.codetest.exception.ToDoItemNotFoundException;
import com.superloop.codetest.exception.ToDoItemValidationErrorException;
import com.superloop.codetest.model.ToDoItem;
import com.superloop.codetest.representation.ToDoItemAddRequestRep;
import com.superloop.codetest.representation.ToDoItemNotFoundErrorRep;
import com.superloop.codetest.representation.ToDoItemValidationErrorRep;
import com.superloop.codetest.util.ItemStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by ryan.zhu on 24/05/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ToDoItemServiceTest {
    @Autowired
    private ToDoItemMapBean toDoItemMapBean;

    @Autowired
    private ObjectMapper objectMapper;

    private ToDoItemService toDoItemService;

    @Before
    public void setup() {
        toDoItemService = new ToDoItemServiceImpl(this.toDoItemMapBean);
    }

    @Test
    public void getToDoItemTest() throws Exception {
        ToDoItem toDoItem = new ToDoItem("test1", "test detail", new Date(), null);
        toDoItem = toDoItemMapBean.addToDoItem(toDoItem);
        long itemId = toDoItem.getItemId();

        // test normal scenario
        ToDoItem item = toDoItemService.getToDoItem(itemId);

        assertThat(item.getName(), is("test1"));
        assertThat(item.getItemStatus(), is(ItemStatus.PENDING));
        assertThat(item.getItemId(), is(itemId));

        // test not found scenario
        itemId += 100;

        try {
            toDoItemService.getToDoItem(itemId);
        } catch (ToDoItemNotFoundException e) {
            ToDoItemNotFoundErrorRep notFoundError = e.getNotFoundError();
            assertThat(notFoundError.getName(), is(Constants.TODO_ITEM_NOT_FOUND_ERROR_NAME));
            assertThat(notFoundError.getDetails().get(0).getMessage(), is(String.format(Constants.TODO_ITEM_NOT_FOUND_ERROR_MESSAGE, itemId)));
        }
    }

    @Test
    public void createToDoItemTest() throws Exception {
        this.toDoItemService.deleteAllToDoItems();

        // test normal scenario
        ToDoItemAddRequestRep addRequestRep = new ToDoItemAddRequestRep("test abcd", "test details", null, null);
        String json = objectMapper.writeValueAsString(addRequestRep);
        ToDoItem item = toDoItemService.createToDoItem(json);

        Integer itemId = 1;

        assertThat(item.getItemId(), is(itemId.longValue()));
        assertThat(item.getName(), is("test abcd"));
        assertThat(item.getItemStatus(), is(ItemStatus.PENDING));

        // test scenario to create the item with DONE item status.
        addRequestRep = new ToDoItemAddRequestRep("test 2", null, null, "DONE");
        json = objectMapper.writeValueAsString(addRequestRep);
        item = toDoItemService.createToDoItem(json);

        itemId = 2;

        assertThat(item.getItemId(), is(itemId.longValue()));
        assertThat(item.getName(), is("test 2"));
        assertNull(item.getDescription());
        assertThat(item.getItemStatus(), is(ItemStatus.DONE));

        // test name validation error scenario - left boundary
        addRequestRep.setName("");
        json = objectMapper.writeValueAsString(addRequestRep);

        try {
            toDoItemService.createToDoItem(json);
        } catch (ToDoItemValidationErrorException e) {
            ToDoItemValidationErrorRep validationErrorRep = e.getValidationError();
            assertThat(validationErrorRep.getName(), is(Constants.VALIDATION_ERROR_NAME));
            assertThat(validationErrorRep.getDetails().get(0).getMsg(), is(Constants.NAME_FIELD_NOT_DEFINED_ERROR_MESSAGE));
        }
    }

    @Test
    public void listToDoItemsTest() throws Exception {
        this.toDoItemService.deleteAllToDoItems();

        // test the empty list scenario
        List<ToDoItem> toDoItemList = this.toDoItemService.listToDoItems();
        assertThat(toDoItemList.size(), is(0));

        // test normal scenario
        ToDoItem item = new ToDoItem("test1", "test detail 1", new Date(), null);
        ToDoItem item1 = toDoItemMapBean.addToDoItem(item);

        item = new ToDoItem("test2", "test detail 2", new Date(), null);
        ToDoItem item2 = toDoItemMapBean.addToDoItem(item);

        toDoItemList = this.toDoItemService.listToDoItems();

        assertThat(toDoItemList.size(), is(2));

        toDoItemList.forEach(toDoItem -> {
            if (toDoItem.getItemId() == item1.getItemId()) {
                assertThat(toDoItem.getName(), is(item1.getName()));
                assertThat(toDoItem.getDescription(), is(item1.getDescription()));
                assertThat(toDoItem.getItemStatus(), is(item1.getItemStatus()));
                assertThat(toDoItem.getCreateAt(), is(item1.getCreateAt()));
                assertThat(toDoItem.getDueDate(), is(item1.getDueDate()));
            } else if (toDoItem.getItemId() == item2.getItemId()) {
                assertThat(toDoItem.getName(), is(item2.getName()));
                assertThat(toDoItem.getDescription(), is(item2.getDescription()));
                assertThat(toDoItem.getItemStatus(), is(item2.getItemStatus()));
                assertThat(toDoItem.getCreateAt(), is(item2.getCreateAt()));
                assertThat(toDoItem.getDueDate(), is(item2.getDueDate()));
            } else {
                fail("List todo list item function error. The todo item list contains wrong todo item.");
            }
        });
    }

    @Test
    public void updateToDoItemTest() throws Exception {
        this.toDoItemService.deleteAllToDoItems();

        // test normal scenario
        ToDoItem item = new ToDoItem("test1", "test detail 1", null, null);
        item = toDoItemMapBean.addToDoItem(item);

        item.setName("test 2");
        item.setDescription(null);
        item.setItemStatus(ItemStatus.DONE);

        String json = objectMapper.writeValueAsString(item);
        toDoItemService.updateToDoItem(item.getItemId(), json);

        ToDoItem updatedItem = this.toDoItemMapBean.getToDoItem(item.getItemId());

        assertThat(updatedItem.getName(), is("test 2"));
        assertThat(updatedItem.getDescription(), isEmptyOrNullString());
        assertThat(updatedItem.getItemStatus(), is(ItemStatus.DONE));
        assertThat(updatedItem.getCreateAt(), is(item.getCreateAt()));
        assertThat(updatedItem.getDueDate(), is(item.getDueDate()));

        // test not found scenario
        long itemId = item.getItemId() + 100;

        try {
            toDoItemService.updateToDoItem(itemId, json);
        } catch (ToDoItemNotFoundException e) {
            ToDoItemNotFoundErrorRep notFoundError = e.getNotFoundError();
            assertThat(notFoundError.getName(), is(Constants.TODO_ITEM_NOT_FOUND_ERROR_NAME));
            assertThat(notFoundError.getDetails().get(0).getMessage(), is(String.format(Constants.TODO_ITEM_NOT_FOUND_ERROR_MESSAGE, itemId)));
        }

    }

    @Test
    public void deleteToDoItemTest() throws Exception {
        this.toDoItemService.deleteAllToDoItems();

        // test normal scenario
        ToDoItem item = new ToDoItem("test 1", "test details", new Date(), null);
        toDoItemMapBean.addToDoItem(item);

        item = new ToDoItem("test 2", null, null, null);
        ToDoItem item2 = toDoItemMapBean.addToDoItem(item);

        this.toDoItemService.deleteToDoItem((long) 1);

        List<ToDoItem> toDoItemList = this.toDoItemService.listToDoItems();
        assertThat(toDoItemList.size(), is(1));

        toDoItemList.forEach(toDoItem -> {
            if (toDoItem.getItemId() == item2.getItemId()) {
                assertThat(toDoItem.getName(), is(item2.getName()));
                assertThat(toDoItem.getDescription(), is(item2.getDescription()));
                assertThat(toDoItem.getItemStatus(), is(item2.getItemStatus()));
                assertThat(toDoItem.getCreateAt(), is(item2.getCreateAt()));
                assertThat(toDoItem.getDueDate(), is(item2.getDueDate()));
            } else {
                fail("Delete todo list function error. The todo item list contains wrong todo item.");
            }
        });

        // test not found scenario
        long itemId = item.getItemId() + 100;
        try {
            toDoItemService.deleteToDoItem(itemId);
        } catch (ToDoItemNotFoundException e) {
            ToDoItemNotFoundErrorRep notFoundError = e.getNotFoundError();
            assertThat(notFoundError.getName(), is(Constants.TODO_ITEM_NOT_FOUND_ERROR_NAME));
            assertThat(notFoundError.getDetails().get(0).getMessage(), is(String.format(Constants.TODO_ITEM_NOT_FOUND_ERROR_MESSAGE, itemId)));
        }
    }

    @Test
    public void deleteAllToDoItemsTest() throws Exception {
        ToDoItem item = new ToDoItem("test 1", "test details", new Date(), null);
        toDoItemMapBean.addToDoItem(item);

        this.toDoItemService.deleteAllToDoItems();

        List<ToDoItem> toDoItemList = this.toDoItemService.listToDoItems();
        assertThat(toDoItemList.size(), is(0));

    }

}
