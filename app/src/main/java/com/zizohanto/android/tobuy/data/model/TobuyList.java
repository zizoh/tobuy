package com.zizohanto.android.tobuy.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity (tableName = "tobuylist")
@TypeConverters({DateConverter.class, ToBuyItemListTypeConverter.class})
public class TobuyList {

    @PrimaryKey
    @NonNull
    private String id;

    @Nullable
    private String name;

    @Nullable
    private String store;
    private Double budget;
    private Double balance;
    private Double totalCost;
    private Date dueDate;

    private List<Item> items;

    @Ignore
    public TobuyList(String name, String store, Double budget, Double balance, Double totalCost,
                     List<Item> items, Date dueDate) {
        this(UUID.randomUUID().toString(), name, store, budget, balance, totalCost, items, dueDate);
    }

    public TobuyList(String id, String name, String store, Double budget, Double balance,
                     Double totalCost, List<Item> items, Date dueDate) {
        this.id = id;
        this.name = name;
        this.store = store;
        this.budget = budget;
        this.balance = balance;
        this.totalCost = totalCost;
        this.items = items;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
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
        return Strings.isNullOrEmpty(name) /*&& items == null*/ /*&& items.size() == 0*/;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TobuyList tobuyList = (TobuyList) o;
        return Objects.equal(id, tobuyList.id) &&
                Objects.equal(name, tobuyList.name) &&
                Objects.equal(store, tobuyList.store) &&
                Objects.equal(budget, tobuyList.budget) &&
                Objects.equal(balance, tobuyList.balance) &&
                Objects.equal(totalCost, tobuyList.totalCost) &&
                Objects.equal(items, tobuyList.items) &&
                Objects.equal(dueDate, tobuyList.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, store, budget, balance, totalCost,
                items, dueDate);
    }

    @Override
    public String toString() {
        return "TobuyList with name " + name;
    }
}
