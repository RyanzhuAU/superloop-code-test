package com.superloop.codetest.model;

import com.superloop.codetest.util.ItemStatus;
import lombok.Data;

import java.util.Date;


/**
 * Created by ryan.zhu on 24/5/2018.
 */

@Data
public class ToDoItem {
    private long itemId;

    private String name;

    private String description;

    private Date createAt;

    private Date dueDate;

    private ItemStatus itemStatus;

    public ToDoItem() {

    }

    public ToDoItem(String name, String desc, Date dueDate, ItemStatus itemStatus) {
        this(name, desc, dueDate, itemStatus, null);
    }

    public ToDoItem(String name, String desc, Date dueDate, ItemStatus itemStatus, Date createAt) {
        this.name = name;
        this.description = desc;
        this.dueDate = dueDate;

        if (itemStatus != null) {
            this.itemStatus = itemStatus;
        }
        else {
            this.itemStatus = ItemStatus.PENDING;
        }

        if (createAt == null) {
            this.createAt = new Date();
        }
    }
}
