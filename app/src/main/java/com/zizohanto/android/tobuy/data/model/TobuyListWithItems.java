package com.zizohanto.android.tobuy.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class TobuyListWithItems {

    @Embedded
    public TobuyList tobuyList = null;

    @Relation(parentColumn = "id", entityColumn = "tobuy_list_id")
    public List<Item> items = new ArrayList<>();

    public TobuyList getTobuyList() {
        return tobuyList;
    }

    public void setTobuyList(TobuyList tobuyList) {
        this.tobuyList = tobuyList;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
