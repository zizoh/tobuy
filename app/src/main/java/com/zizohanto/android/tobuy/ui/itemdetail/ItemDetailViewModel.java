package com.zizohanto.android.tobuy.ui.itemdetail;

import android.content.Context;

import androidx.annotation.Nullable;

import com.zizohanto.android.tobuy.ItemViewModel;
import com.zizohanto.android.tobuy.data.item.ItemsRepository;


public class ItemDetailViewModel extends ItemViewModel {

    @Nullable
    private ItemDetailNavigator mItemDetailNavigator; 

    public ItemDetailViewModel(Context context, ItemsRepository tobuyListsRepository) {
        super(context, tobuyListsRepository);
    }

    public void setNavigator(ItemDetailNavigator tobuyListDetailNavigator) {
        mItemDetailNavigator = tobuyListDetailNavigator;
    }

    public void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mItemDetailNavigator = null;
    }

    /**
     * Can be called by the Data Binding Library or the delete menu item.
     */
    public void deleteItem() {
        super.deleteItem();
        if (mItemDetailNavigator != null) {
            mItemDetailNavigator.onItemDeleted();
        }
    }

    public void startEditItem() {
        if (mItemDetailNavigator != null) {
            mItemDetailNavigator.onStartEditItem();
        }
    }
}
