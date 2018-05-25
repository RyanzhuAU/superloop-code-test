package com.superloop.codetest.component;

import com.superloop.codetest.model.ToDoItem;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by ryan.zhu on 24/05/2018
 **/
@Component
public class ToDoItemMapBean {
    private ConcurrentHashMap<Long, ToDoItem> toDoItemMap = new ConcurrentHashMap<>();

    private long maxItemId = 0;

    private synchronized void setMaxItemId(long itemId) {
        this.maxItemId = itemId;
    }

    public ConcurrentHashMap<Long, ToDoItem> getToDoItemMap() {
        return this.toDoItemMap;
    }

    public ToDoItem getToDoItem(long itemId) {
        return this.toDoItemMap.get(itemId);
    }

    public ToDoItem addToDoItem(ToDoItem item) {
        long newItemId = this.maxItemId + 1;
        item.setItemId(newItemId);
        this.setMaxItemId(newItemId);

        this.toDoItemMap.put(newItemId, item);

        return item;
    }

    public void updateToDoItem(ToDoItem item, long itemId) {
        this.toDoItemMap.put(itemId, item);
    }

    public void deleteToDoItem(long itemId) {
        this.toDoItemMap.remove(itemId);
    }

    public void deleteAllToDoItems() {
        this.setMaxItemId(0);
        this.toDoItemMap = new ConcurrentHashMap<>();
    }

    public boolean checkIfExist(long itemId) {
        if (this.toDoItemMap.get(itemId) == null) {
            return false;
        } else {
            return true;
        }
    }
}