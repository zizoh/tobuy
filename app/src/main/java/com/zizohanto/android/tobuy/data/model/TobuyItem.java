package com.zizohanto.android.tobuy.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "tobuy_item")
public class TobuyItem {

    @PrimaryKey
    @NonNull
    private String id;

    @NonNull
    private String tobuyId;

    @Nullable
    private String name;

    @Nullable
    private String store;

    private double price;
    private int placedIn;

    @Ignore
    public TobuyItem(String tobuyId, String name, double price, String store, int placedIn) {
        this(UUID.randomUUID().toString(), tobuyId, name, price, store, placedIn);
    }

    public TobuyItem(String id, String tobuyId, String name, double price, String store, int placedIn) {
        this.id = id;
        this.tobuyId = tobuyId;
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

    public String getTobuyId() {
        return tobuyId;
    }

    public void setTobuyId(String tobuyId) {
        this.tobuyId = tobuyId;
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
}
