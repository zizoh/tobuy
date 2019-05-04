package com.zizohanto.android.tobuy.ui.tobuylistdetail;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.data.tobuyitem.source.ItemDataSource;
import com.zizohanto.android.tobuy.data.tobuyitem.source.ItemsRepository;

import java.util.List;

public class ItemsViewModel extends BaseObservable {

    // These observable fields will update Views automatically
    public final ObservableList<Item> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> noItemLabel = new ObservableField<>();

    public final ObservableBoolean itemsAddViewVisible = new ObservableBoolean();

    final ObservableField<String> snackbarText = new ObservableField<>();

    private final ItemsRepository mItemsRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private Context mContext; // To avoid leaks, this must be an Application Context.

    private ItemsNavigator mNavigator;

    public ItemsViewModel(ItemsRepository repository, Context context) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mItemsRepository = repository;
    }

    void setNavigator(ItemsNavigator navigator) {
        mNavigator = navigator;
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    public void start(String tobuyListId) {
        loadItems(tobuyListId, false);
    }

    @Bindable
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void loadItems(String tobuyListId, boolean forceUpdate) {
        loadItems(tobuyListId, forceUpdate, true);
    }

    public String getSnackbarText() {
        return snackbarText.get();
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    public void addNewItem() {
        if (mNavigator != null) {
            mNavigator.addNewItem();
        }
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link ItemDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadItems(String tobuyListId, boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        if (forceUpdate) {

            mItemsRepository.refreshItems();
        }

        mItemsRepository.getItems(tobuyListId, new ItemDataSource.LoadItemsCallback() {
            @Override
            public void onItemsLoaded(List<Item> items) {
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(items);
                // TODO uncomment line with BR
                // notifyPropertyChanged(BR.empty); // It's a @Bindable so update manually
            }

            @Override
            public void onDataNotAvailable() {
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                mIsDataLoadingError.set(true);
            }
        });
    }
}
