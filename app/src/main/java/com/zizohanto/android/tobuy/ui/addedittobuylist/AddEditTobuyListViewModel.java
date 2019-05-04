package com.zizohanto.android.tobuy.ui.addedittobuylist;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.data.model.TobuyList;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListDataSource;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;
import com.zizohanto.android.tobuyList.R;

import java.util.Date;
import java.util.List;

public class AddEditTobuyListViewModel implements TobuyListDataSource.GetTobuyListCallback {

    public final ObservableField<String> name = new ObservableField<>();

    public final ObservableField<String> store = new ObservableField<>();

    public final ObservableField<Double> budget = new ObservableField<>();

    public final ObservableField<Double> balance = new ObservableField<>();

    public final ObservableField<Double> totalCost = new ObservableField<>();

    public final ObservableField<List<Item>> tobuyItems = new ObservableField<>();

    public final ObservableField<Date> dueDate = new ObservableField<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> snackbarText = new ObservableField<>();

    private final TobuyListsRepository mTobuyListsRepository;

    private final Context mContext;  // To avoid leaks, this must be an Application Context.

    private String mTobuyListId;

    private boolean mIsNewTobuyList;

    private boolean mIsDataLoaded = false;

    private AddEditTobuyListNavigator mAddEditTaskNavigator;

    AddEditTobuyListViewModel(Context context, TobuyListsRepository tobuyListsRepository) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mTobuyListsRepository = tobuyListsRepository;
    }

    void onActivityCreated(AddEditTobuyListNavigator navigator) {
        mAddEditTaskNavigator = navigator;
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mAddEditTaskNavigator = null;
    }

    public void start(String tobuyListId) {
        if (dataLoading.get()) {
            // Already loading, ignore.
            return;
        }
        mTobuyListId = tobuyListId;
        if (tobuyListId == null) {
            // No need to populate, it's a new tobuylist
            mIsNewTobuyList = true;
            return;
        }
        if (mIsDataLoaded) {
            // No need to populate, already have data.
            return;
        }
        mIsNewTobuyList = false;
        dataLoading.set(true);
        mTobuyListsRepository.getTobuyList(tobuyListId, this);
    }

    @Override
    public void onTobuyListLoaded(TobuyList tobuyList) {
        name.set(tobuyList.getName());
        store.set(tobuyList.getStore());
        budget.set(tobuyList.getBudget());
        balance.set(tobuyList.getBalance());
        totalCost.set(tobuyList.getTotalCost());
        tobuyItems.set(tobuyList.getItems());
        dueDate.set(tobuyList.getDueDate());

        dataLoading.set(false);
        mIsDataLoaded = true;
    }

    @Override
    public void onDataNotAvailable() {
        dataLoading.set(false);
    }

    // Called when clicking on fab.
    public void saveTobuyList() {
        if (isNewTobuyList()) {
            createTobuyList(name.get(), store.get(), budget.get(), balance.get(),
                    totalCost.get(), tobuyItems.get(), dueDate.get());
        } else {
            updateTobuyList(name.get(), store.get(), budget.get(), balance.get(),
                    totalCost.get(), tobuyItems.get(), dueDate.get());
        }
    }

    @Nullable
    public String getSnackbarText() {
        return snackbarText.get();
    }

    private boolean isNewTobuyList() {
        return mIsNewTobuyList;
    }

    private void createTobuyList(String name, String store, Double budget, Double balance,
                                 Double totalCost, List<Item> items, Date date) {
        TobuyList newTobuyList = new TobuyList(name, store, budget, balance, totalCost, items, date);
        if (newTobuyList.isEmpty()) {
            snackbarText.set(mContext.getString(R.string.empty_tobuylist_message));
        } else {
            mTobuyListsRepository.saveTobuyList(newTobuyList);
            navigateOnTobuyListSaved();
        }
    }

    private void updateTobuyList(String name, String store, Double budget, Double balance,
                                 Double totalCost, List<Item> items, Date date) {
        if (isNewTobuyList()) {
            throw new RuntimeException("updateTobuyList() was called but tobuylist is new.");
        }
        mTobuyListsRepository.saveTobuyList(
                new TobuyList(mTobuyListId, name, store, budget, balance, totalCost, items, date));
        navigateOnTobuyListSaved(); // After an edit, go back to the list.
    }

    private void navigateOnTobuyListSaved() {
        if (mAddEditTaskNavigator != null) {
            mAddEditTaskNavigator.onTobuyListSaved();
        }
    }
}
