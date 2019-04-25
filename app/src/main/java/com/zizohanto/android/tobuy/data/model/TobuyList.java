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
    private String mTobuyListId;

    @Nullable
    private String mName;

    @Nullable
    private String mStore;
    private Double mBudget;
    private Double mBalance;
    private Double mTotalCost;
    public List<TobuyItem> mTobuyItems;
    private Date mDueDate;

    @Ignore
    public TobuyList(String name, String store, Double budget, Double balance, Double totalCost,
                     List<TobuyItem> tobuyItems, Date dueDate) {
        this(UUID.randomUUID().toString(), name, store, budget, balance, totalCost, tobuyItems, dueDate);
    }

    public TobuyList(String tobuyListId, String name, String store, Double budget, Double balance,
                     Double totalCost, List<TobuyItem> tobuyItems, Date dueDate) {
        mTobuyListId = tobuyListId;
        mName = name;
        mStore = store;
        mBudget = budget;
        mBalance = balance;
        mTotalCost = totalCost;
        mTobuyItems = tobuyItems;
        mDueDate = dueDate;
    }

    public String getTobuyListId() {
        return mTobuyListId;
    }

    public void setTobuyListId(String tobuyListId) {
        mTobuyListId = tobuyListId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getStore() {
        return mStore;
    }

    public void setStore(String store) {
        mStore = store;
    }

    public Double getBudget() {
        return mBudget;
    }

    public void setBudget(Double budget) {
        mBudget = budget;
    }

    public Double getBalance() {
        return mBalance;
    }

    public void setBalance(Double balance) {
        mBalance = balance;
    }

    public Double getTotalCost() {
        return mTotalCost;
    }

    public void setTotalCost(Double totalCost) {
        mTotalCost = totalCost;
    }

    public List<TobuyItem> getToBuyItems() {
        return mTobuyItems;
    }

    public void setToBuyItems(List<TobuyItem> tobuyItems) {
        this.mTobuyItems = tobuyItems;
    }

    public Date getDueDate() {
        return mDueDate;
    }

    public void setDueDate(Date dueDate) {
        mDueDate = dueDate;
    }

    @Nullable
    public String getNameForList() {
        if (!Strings.isNullOrEmpty(mName)) {
            return mName;
        } else {
            return mStore;
        }
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mName) /*&& mTobuyItems == null*/ /*&& mTobuyItems.size() == 0*/;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TobuyList tobuyList = (TobuyList) o;
        return Objects.equal(mTobuyListId, tobuyList.mTobuyListId) &&
                Objects.equal(mName, tobuyList.mName) &&
                Objects.equal(mStore, tobuyList.mStore) &&
                Objects.equal(mBudget, tobuyList.mBudget) &&
                Objects.equal(mBalance, tobuyList.mBalance) &&
                Objects.equal(mTotalCost, tobuyList.mTotalCost) &&
                Objects.equal(mTobuyItems, tobuyList.mTobuyItems) &&
                Objects.equal(mDueDate, tobuyList.mDueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mTobuyListId, mName, mStore, mBudget, mBalance, mTotalCost,
                mTobuyItems, mDueDate);
    }

    @Override
    public String toString() {
        return "TobuyList with name " + mName;
    }
}
