package com.zizohanto.android.tobuy.ui.addedittobuylist;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.data.model.TobuyList;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListDataSource;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;
import com.zizohanto.android.tobuyList.R;

import java.util.Date;
import java.util.List;

public class AddEditTobuyListViewModel extends BaseObservable implements TobuyListDataSource.GetTobuyListCallback {

    public final ObservableField<String> name = new ObservableField<>();

    public final ObservableField<String> store = new ObservableField<>();

    public final ObservableField<Double> budget = new ObservableField<>();

    public final ObservableField<Double> balance = new ObservableField<>();

    public final ObservableField<Double> totalCost = new ObservableField<>();

    public final ObservableField<List<Item>> items = new ObservableField<>();

    public final ObservableField<Date> dueDate = new ObservableField<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> snackbarText = new ObservableField<>();

    private final TobuyListsRepository mTobuyListsRepository;

    private final Context mContext;  // To avoid leaks, this must be an Application Context.

    private String mTobuyListId;

    private boolean mIsNewTobuyList;

    private boolean mIsDataLoaded = false;

    private AddEditTobuyListNavigator mAddEditTobuyListNavigator;

    AddEditTobuyListViewModel(Context context, TobuyListsRepository tobuyListsRepository) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mTobuyListsRepository = tobuyListsRepository;
    }

    void onActivityCreated(AddEditTobuyListNavigator navigator) {
        mAddEditTobuyListNavigator = navigator;
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mAddEditTobuyListNavigator = null;
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
        items.set(tobuyList.getItems());
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
        boolean isNewTobuyList = isNewTobuyList();
        if (isNewTobuyList()) {
            createTobuyList(name.get(), store.get(), budget.get(), balance.get(),
                    totalCost.get(), items.get(), dueDate.get());
        } else {
            updateTobuyList(name.get(), store.get(), budget.get(), balance.get(),
                    totalCost.get(), items.get(), dueDate.get());
        }
    }

    public void addNewItem() {
        if (mTobuyListId == null) {
            snackbarText.set(mContext.getString(R.string.tobuy_list_not_saved_message));
            return;
        }
        navigateOnToAddNewItem();
    }

    @Nullable
    public String getSnackbarText() {
        return snackbarText.get();
    }

    private boolean isNewTobuyList() {
        return mIsNewTobuyList;
    }

    @Bindable
    public boolean isEmpty() {

        List<Item> items1 = items.get();
        if (items1 == null) {
            return true;
        }

        return items1.isEmpty();
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
        if (mAddEditTobuyListNavigator != null) {
            mAddEditTobuyListNavigator.onTobuyListSaved();
        }
    }

    private void navigateOnToAddNewItem() {
        if (mAddEditTobuyListNavigator != null) {
            mAddEditTobuyListNavigator.onAddNewTobuyItem(mTobuyListId);
        }
    }
}
