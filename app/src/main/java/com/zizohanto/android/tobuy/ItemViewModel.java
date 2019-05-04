package com.zizohanto.android.tobuy;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.data.tobuyitem.source.ItemDataSource;
import com.zizohanto.android.tobuy.data.tobuyitem.source.ItemsRepository;
import com.zizohanto.android.tobuyList.R;

import java.util.List;

public class ItemViewModel extends BaseObservable implements ItemDataSource.GetItemCallback {

    public final ObservableField<String> snackbarText = new ObservableField<>();
    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> store = new ObservableField<>();
    public final ObservableField<Double> budget = new ObservableField<>();
    public final ObservableField<Double> balance = new ObservableField<>();
    public final ObservableField<Integer> placedIn = new ObservableField<>();
    public final ObservableField<Double> totalCost = new ObservableField<>();
    public final ObservableField<List<Item>> tobuyItems = new ObservableField<>();
    private final ObservableField<Item> mItemObservable = new ObservableField<>();
    private final ItemsRepository mItemsRepository;

    private final Context mContext;

    private boolean mIsDataLoading;

    public ItemViewModel(Context context, ItemsRepository itemsRepository) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mItemsRepository = itemsRepository;

        // Exposed observables depend on the mTaskObservable observable:
        mItemObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                Item item = mItemObservable.get();
                if (item != null) {
                    name.set(item.getName());
                    store.set(item.getStore());
                    budget.set(item.getPrice());
                    placedIn.set(item.getPlacedIn());
                } else {
                    name.set(mContext.getString(R.string.no_data));
                }
            }
        });
    }

    public void start(String itemId) {
        if (itemId != null) {
            mIsDataLoading = true;
            mItemsRepository.getItem(itemId, this);
        }
    }

    public void setItem(Item item) {
        mItemObservable.set(item);
    }


    @Bindable
    public boolean isDataAvailable() {
        return mItemObservable.get() != null;
    }

    @Bindable
    public boolean isDataLoading() {
        return mIsDataLoading;
    }

    // This could be an observable, but we save a call to Item.getNameForList() if not needed.
    @Bindable
    public String getNameForList() {
        if (mItemObservable.get() == null) {
            return "No data";
        }
        return mItemObservable.get().getNameForList();
    }

    @Override
    public void onItemLoaded(Item item) {
        mItemObservable.set(item);
        mIsDataLoading = false;
        notifyChange();
    }

    @Override
    public void onDataNotAvailable() {
        mItemObservable.set(null);
        mIsDataLoading = false;
    }

    public void deleteItem() {
        if (mItemObservable.get() != null) {
            mItemsRepository.deleteItem(mItemObservable.get().getId());
        }
    }

    public void onRefresh() {
        if (mItemObservable.get() != null) {
            start(mItemObservable.get().getId());
        }
    }

    public String getSnackbarText() {
        return snackbarText.get();
    }

    protected String getItemId() {
        return mItemObservable.get().getId();
    }
}
