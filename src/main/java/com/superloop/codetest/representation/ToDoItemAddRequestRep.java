package com.superloop.codetest.representation;

import lombok.Data;

import java.util.Date;

/**
 * Created by ryan.zhu on 24/05/2018.
 */

@Data
public class ToDoItemAddRequestRep {

    private String name;

    private String desc;

    private Date dueDate;

    private String itemStatus;

    public ToDoItemAddRequestRep(String name, String desc, Date dueDate, String itemStatus) {
        this.name = name;
        this.desc = desc;
        this.dueDate = dueDate;
        this.itemStatus = itemStatus;
    }

    public ToDoItemAddRequestRep() {}

}
