package com.zizohanto.android.tobuy.ui.addedititem;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.zizohanto.android.tobuy.data.item.ItemDataSource;
import com.zizohanto.android.tobuy.data.item.ItemsRepository;
import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuyList.R;

public class AddEditItemViewModel implements ItemDataSource.GetItemCallback {

    public final ObservableField<String> name = new ObservableField<>();

    public final ObservableField<String> store = new ObservableField<>();

    public final ObservableField<Double> price = new ObservableField<>();

    public final ObservableField<Integer> placedIn = new ObservableField<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> snackbarText = new ObservableField<>();

    private final ItemsRepository mItemsRepository;

    private final Context mContext;  // To avoid leaks, this must be an Application Context.

    private String mItemId;

    private String mTobuyListId;

    private boolean mIsNewItem;

    private boolean mIsDataLoaded = false;

    private AddEditItemNavigator mAddEditTaskNavigator;

    AddEditItemViewModel(Context context, ItemsRepository tobuyListsRepository) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mItemsRepository = tobuyListsRepository;
        price.set(0.0);
        placedIn.set(0);
    }

    void onActivityCreated(AddEditItemNavigator navigator) {
        mAddEditTaskNavigator = navigator;
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mAddEditTaskNavigator = null;
    }

    public void start(String itemId, String tobuyListId) {
        if (dataLoading.get()) {
            // Already loading, ignore.
            return;
        }
        mItemId = itemId;
        mTobuyListId = tobuyListId;
        if (itemId == null) {
            // No need to populate, it's a new item
            mIsNewItem = true;
            return;
        }
        if (mIsDataLoaded) {
            // No need to populate, already have data.
            return;
        }
        mIsNewItem = false;
        dataLoading.set(true);
        mItemsRepository.getItem(itemId, this);
    }

    @Override
    public void onItemLoaded(Item item) {
        name.set(item.getName());
        store.set(item.getStore());
        price.set(item.getPrice());
        placedIn.set(item.getPlacedIn());

        dataLoading.set(false);
        mIsDataLoaded = true;
    }

    @Override
    public void onDataNotAvailable() {
        dataLoading.set(false);
    }

    // Called when clicking on fab.
    public void saveItem() {
        if (isNewItem()) {
            String store = this.store.get();
            if (TextUtils.isEmpty(store)) store = "NA";
            createItem(name.get(), store, price.get(), placedIn.get());
        } else {
            updateItem(name.get(), store.get(), price.get(), placedIn.get());
        }
    }

    @Nullable
    public String getSnackbarText() {
        return snackbarText.get();
    }

    private boolean isNewItem() {
        return mIsNewItem;
    }

    private void createItem(String name, String store, double price, int placedIn) {
        Item newItem = new Item(mTobuyListId, name, price, store, placedIn);
        if (newItem.isEmpty()) {
            snackbarText.set(mContext.getString(R.string.empty_item_message));
        } else {
            mItemsRepository.saveItem(newItem);
            navigateOnItemSaved();
        }
    }

    private void updateItem(String name, String store, double price, int placedIn) {
        if (isNewItem()) {
            throw new RuntimeException("updateItem() was called but item is new.");
        }
        mItemsRepository.saveItem(
                new Item(mItemId, mTobuyListId, name, price, store, placedIn));
        navigateOnItemSaved(); // After an edit, go back to the list.
    }

    private void navigateOnItemSaved() {
        if (mAddEditTaskNavigator != null) {
            mAddEditTaskNavigator.onItemSaved();
        }
    }
}
