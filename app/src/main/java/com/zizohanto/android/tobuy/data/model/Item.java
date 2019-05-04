package com.zizohanto.android.tobuy.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.common.base.Strings;

import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "item",
        foreignKeys = @ForeignKey(
                entity = TobuyList.class,
                parentColumns = "id",
                childColumns = "tobuy_list_id",
                onDelete = CASCADE,
                onUpdate = CASCADE
        ),
        indices = {
                @Index(value = {"tobuy_list_id"})
        }
)
public class Item {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    @ColumnInfo(name = "tobuy_list_id")
    private String tobuyListId;

    @Nullable
    private String name;

    @Nullable
    private String store;

    private double price;
    private int placedIn;

    @Ignore
    public Item(String tobuyListId, String name, double price, String store, int placedIn) {
        this(UUID.randomUUID().toString(), tobuyListId, name, price, store, placedIn);
    }

    public Item(String id, String tobuyListId, String name, double price, String store, int placedIn) {
        this.id = id;
        this.tobuyListId = tobuyListId;
        this.name = name;
        this.price = price;
        this.store = store;
        this.placedIn = placedIn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTobuyListId() {
        return tobuyListId;
    }

    public void setTobuyListId(String tobuyListId) {
        this.tobuyListId = tobuyListId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public int getPlacedIn() {
        return placedIn;
    }

    public void setPlacedIn(int placedIn) {
        this.placedIn = placedIn;
    }

    @Nullable
    public String getNameForList() {
        if (!Strings.isNullOrEmpty(name)) {
            return name;
        } else {
            return store;
        }
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(name);
    }
}
