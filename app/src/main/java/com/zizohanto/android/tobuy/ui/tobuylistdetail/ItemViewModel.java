package com.zizohanto.android.tobuy.ui.tobuylistdetail;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.zizohanto.android.tobuy.data.item.ItemDataSource;
import com.zizohanto.android.tobuy.data.item.ItemsRepository;
import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuyList.R;

import java.util.List;

public class ItemViewModel extends BaseObservable implements ItemDataSource.GetItemCallback {

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> noItemLabel = new ObservableField<>();

    public final ObservableBoolean itemsAddViewVisible = new ObservableBoolean();

    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> store = new ObservableField<>();
    public final ObservableField<Double> price = new ObservableField<>();
    public final ObservableField<Integer> placedIn = new ObservableField<>();
    private final ObservableField<Item> mItemObservable = new ObservableField<>();


    final ObservableField<String> snackbarText = new ObservableField<>();

    private final ItemsRepository mItemsRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private Context mContext; // To avoid leaks, this must be an Application Context.

    private ItemsNavigator mNavigator;

    public ItemViewModel(ItemsRepository repository, Context context) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mItemsRepository = repository;

        // Exposed observables depend on the mItemObservable observable:
        mItemObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                Item item = mItemObservable.get();
                if (item != null) {
                    name.set(item.getName());
                    store.set(item.getStore());
                    price.set(item.getPrice());
                    placedIn.set(item.getPlacedIn());
                } else {
                    name.set(mContext.getString(R.string.no_data));
                }
            }
        });
    }

    void setNavigator(ItemsNavigator navigator) {
        mNavigator = navigator;
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    public void start(String itemId) {
        loadItems(itemId, false);
    }


    public void loadItems(String itemId, boolean forceUpdate) {
        loadItems(itemId, forceUpdate, true);
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

    @Override
    public void onItemLoaded(Item item) {

    }

    @Override
    public void onDataNotAvailable() {

    }
}
