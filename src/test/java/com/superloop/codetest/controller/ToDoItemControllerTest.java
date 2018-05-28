package com.superloop.codetest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.superloop.codetest.Application;
import com.superloop.codetest.Constants;
import com.superloop.codetest.model.ToDoItem;
import com.superloop.codetest.representation.ToDoItemAddRequestRep;
import com.superloop.codetest.representation.ToDoItemNotFoundErrorRep;
import com.superloop.codetest.representation.ToDoItemValidationErrorRep;
import com.superloop.codetest.util.ItemStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ryan.zhu on 24/05/2018.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@AutoConfigureMockMvc
public class ToDoItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long testItemId;

    @Before
    public void setup() throws Exception {

    }

    @Test
    public void getToDoItemTest() throws Exception {
        this.prepareTestData();

        // test normal scenario
        MvcResult result = this.mockMvc.perform(get("/todo/" + this.testItemId))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ToDoItem item = objectMapper.readValue(content, ToDoItem.class);

        assertThat(item.getName(), is("test 1"));
        assertThat(item.getDescription(), is("test details"));
        assertThat(item.getItemStatus(), is(ItemStatus.DONE));
        assertThat(item.getItemId(), is(this.testItemId));

        // test not found scenario
        long itemId = this.testItemId + 100;

        result = this.mockMvc.perform(get("/todo/" + itemId))
                .andExpect(status().isNotFound())
                .andReturn();

        content = result.getResponse().getContentAsString();

        ToDoItemNotFoundErrorRep notFoundErrorRep = objectMapper.readValue(content, ToDoItemNotFoundErrorRep.class);

        assertThat(notFoundErrorRep.getName(), is(Constants.TODO_ITEM_NOT_FOUND_ERROR_NAME));
        assertThat(notFoundErrorRep.getDetails().get(0).getMessage(), is(String.format(Constants.TODO_ITEM_NOT_FOUND_ERROR_MESSAGE, itemId)));

    }

    @Test
    public void createToDoItemTest() throws Exception {
        // test normal scenario
        ToDoItemAddRequestRep addRequestRep = new ToDoItemAddRequestRep("test abcd", "test details 2", null, null);

        MvcResult result = this.mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequestRep)))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ToDoItem item = objectMapper.readValue(content, ToDoItem.class);

        assertThat(item.getName(), is("test abcd"));
        assertThat(item.getDescription(), is("test details 2"));
        assertThat(item.getItemStatus(), is(ItemStatus.PENDING));

        // test the scenario with "DONE" item status.
        addRequestRep = new ToDoItemAddRequestRep("test qwer", "test details 3", null, "DONE");

        result = this.mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequestRep)))
                .andReturn();

        content = result.getResponse().getContentAsString();
        item = objectMapper.readValue(content, ToDoItem.class);

        assertThat(item.getName(), is("test qwer"));
        assertThat(item.getDescription(), is("test details 3"));
        assertThat(item.getItemStatus(), is(ItemStatus.DONE));

        // test empty name validation error scenario
        addRequestRep.setName("");

        result = this.mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequestRep)))
                .andExpect(status().isBadRequest())
                .andReturn();

        content = result.getResponse().getContentAsString();

        ToDoItemValidationErrorRep validationErrorRep = objectMapper.readValue(content, ToDoItemValidationErrorRep.class);

        assertThat(validationErrorRep.getName(), is(Constants.VALIDATION_ERROR_NAME));
        assertThat(validationErrorRep.getDetails().get(0).getMsg(), is(Constants.NAME_FIELD_NOT_DEFINED_ERROR_MESSAGE));

    }

    @Test
    public void updateToDoItemTest() throws Exception {
        this.prepareTestData();

        // test normal scenario
        MvcResult result = this.mockMvc.perform(get("/todo/" + this.testItemId))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ToDoItem item = objectMapper.readValue(content, ToDoItem.class);

        item.setName("abcd");
        item.setDescription("test abcd details");


        this.mockMvc.perform(put("/todo/" + this.testItemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andReturn();

        result = this.mockMvc.perform(get("/todo/" + this.testItemId))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();

        ToDoItem updatedItem = objectMapper.readValue(content, ToDoItem.class);

        assertThat(updatedItem.getName(), is("abcd"));
        assertThat(updatedItem.getDescription(), is("test abcd details"));
        assertThat(updatedItem.getItemId(), is(this.testItemId));

        // test the scenario with updating ItemStatus only
        item.setItemStatus(ItemStatus.DONE);

        this.mockMvc.perform(put("/todo/" + this.testItemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andReturn();

        result = this.mockMvc.perform(get("/todo/" + this.testItemId))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();

        item = objectMapper.readValue(content, ToDoItem.class);

        assertThat(item.getItemStatus(), is(ItemStatus.DONE));
        assertThat(item.getItemId(), is(this.testItemId));

        // test not found scenario
        long itemId = item.getItemId() + 100;

        result = this.mockMvc.perform(put("/todo/" + itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isNotFound())
                .andReturn();

        content = result.getResponse().getContentAsString();

        ToDoItemNotFoundErrorRep notFoundErrorRep = objectMapper.readValue(content, ToDoItemNotFoundErrorRep.class);

        assertThat(notFoundErrorRep.getName(), is(Constants.TODO_ITEM_NOT_FOUND_ERROR_NAME));
        assertThat(notFoundErrorRep.getDetails().get(0).getMessage(), is(String.format(Constants.TODO_ITEM_NOT_FOUND_ERROR_MESSAGE, itemId)));

        // test null name validation error scenario
        item.setName("");

        result = this.mockMvc.perform(put("/todo/" + this.testItemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest())
                .andReturn();

        content = result.getResponse().getContentAsString();

        ToDoItemValidationErrorRep validationErrorRep = objectMapper.readValue(content, ToDoItemValidationErrorRep.class);

        assertThat(validationErrorRep.getName(), is(Constants.VALIDATION_ERROR_NAME));
        assertThat(validationErrorRep.getDetails().get(0).getMsg(), is(Constants.NAME_FIELD_NOT_DEFINED_ERROR_MESSAGE));

    }

    @Test
    public void deleteToDoItemTest() throws Exception {
        this.prepareTestData();

        // test normal scenario
        this.mockMvc.perform(delete("/todo/" + this.testItemId))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = this.mockMvc.perform(get("/todo/" + this.testItemId))
                .andExpect(status().isNotFound())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ToDoItemNotFoundErrorRep notFoundErrorRep = objectMapper.readValue(content, ToDoItemNotFoundErrorRep.class);

        assertThat(notFoundErrorRep.getName(), is(Constants.TODO_ITEM_NOT_FOUND_ERROR_NAME));
        assertThat(notFoundErrorRep.getDetails().get(0).getMessage(), is(String.format(Constants.TODO_ITEM_NOT_FOUND_ERROR_MESSAGE, this.testItemId)));

        // test not found scenario
        result = this.mockMvc.perform(delete("/todo/" + this.testItemId))
                .andExpect(status().isNotFound())
                .andReturn();

        content = result.getResponse().getContentAsString();

        notFoundErrorRep = objectMapper.readValue(content, ToDoItemNotFoundErrorRep.class);

        assertThat(notFoundErrorRep.getName(), is(Constants.TODO_ITEM_NOT_FOUND_ERROR_NAME));
        assertThat(notFoundErrorRep.getDetails().get(0).getMessage(), is(String.format(Constants.TODO_ITEM_NOT_FOUND_ERROR_MESSAGE, this.testItemId)));

    }

    @Test
    public void deleteAllItemsTest() throws Exception {
        this.prepareTestData();

        this.mockMvc.perform(delete("/todo/deleteAll"))
                .andReturn();

        MvcResult result = this.mockMvc.perform(get("/todo/listAllItems"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        List<ToDoItem> itemList = objectMapper.readValue(content, new TypeReference<List<ToDoItem>>(){});

        assertThat(itemList.size(), is(0));
    }

    @Test
    public void listAllToDoItemsTest() throws Exception {
        this.prepareTestData();

        MvcResult result = this.mockMvc.perform(get("/todo/listAllItems"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        List<ToDoItem> itemList = objectMapper.readValue(content, new TypeReference<List<ToDoItem>>(){});

        assertThat(itemList.size(), is(3));

        itemList.forEach(item -> {
            if (item.getItemId() == (long) 1) {
                assertThat(item.getName(), is("test 1"));
                assertThat(item.getDescription(), is("test details"));
                assertThat(item.getItemStatus(), is(ItemStatus.DONE));
            } else if (item.getItemId() == (long) 2) {
                assertThat(item.getName(), is("test 2"));
                assertThat(item.getDescription(), isEmptyOrNullString());
                assertThat(item.getItemStatus(), is(ItemStatus.PENDING));
            } else if (item.getItemId() == (long) 1) {
                assertThat(item.getName(), is("test 3"));
                assertThat(item.getDescription(), isEmptyOrNullString());
                assertThat(item.getItemStatus(), is(ItemStatus.PENDING));
            }
        });
    }

    @Test
    public void getFilteredToDoItemsTest() throws Exception {
        this.prepareTestData();

        MvcResult result = this.mockMvc.perform(get("/todo/getFilteredItemList/PENDING"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        List<ToDoItem> itemList = objectMapper.readValue(content, new TypeReference<List<ToDoItem>>(){});

        assertThat(itemList.size(), is(2));

        itemList.forEach(item -> {
            if (item.getItemId() == (long) 2) {
                assertThat(item.getName(), is("test 2"));
                assertThat(item.getDescription(), isEmptyOrNullString());
                assertThat(item.getItemStatus(), is(ItemStatus.PENDING));
            } else if (item.getItemId() == (long) 1) {
                assertThat(item.getName(), is("test 3"));
                assertThat(item.getDescription(), isEmptyOrNullString());
                assertThat(item.getItemStatus(), is(ItemStatus.PENDING));
            }
        });

        result = this.mockMvc.perform(get("/todo/getFilteredItemList/DONE"))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();

        itemList = objectMapper.readValue(content, new TypeReference<List<ToDoItem>>(){});

        assertThat(itemList.size(), is(1));

        itemList.forEach(item -> {
            if (item.getItemId() == (long) 1) {
                assertThat(item.getName(), is("test 1"));
                assertThat(item.getDescription(), is("test details"));
                assertThat(item.getItemStatus(), is(ItemStatus.DONE));
            }
        });
    }

    private void prepareTestData() throws Exception {

        this.mockMvc.perform(delete("/todo/deleteAll"))
                .andReturn();

        ToDoItemAddRequestRep addRequestRep = new ToDoItemAddRequestRep("test 1", "test details", null, "DONE");

        MvcResult result = this.mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequestRep)))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ToDoItem item = objectMapper.readValue(content, ToDoItem.class);
        this.testItemId = item.getItemId();

        addRequestRep = new ToDoItemAddRequestRep("test 2", null, null, null);

        this.mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequestRep)));

        addRequestRep = new ToDoItemAddRequestRep("test 3", null, null, null);

        this.mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequestRep)));
    }
}
