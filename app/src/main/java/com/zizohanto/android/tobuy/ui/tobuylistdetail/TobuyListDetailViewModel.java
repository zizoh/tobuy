package com.zizohanto.android.tobuy.ui.tobuylistdetail;

import android.content.Context;

import androidx.annotation.Nullable;

import com.zizohanto.android.tobuy.TobuyListViewModel;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;

import java.lang.ref.WeakReference;


/**
 * Listens to user actions from the list item in ({@link TobuyListDetailFragment}) and redirects them to the
 * Fragment's actions listener.
 */
public class TobuyListDetailViewModel extends TobuyListViewModel {

    @Nullable
    private TobuyListDetailNavigator mTobuyListDetailNavigator;

    String mItemId;
    @Nullable
    private WeakReference<ItemNavigator> mItemNavigator;

    public TobuyListDetailViewModel(Context context, TobuyListsRepository tobuyListsRepository) {
        super(context, tobuyListsRepository);
    }

    public void setNavigator(TobuyListDetailNavigator tobuyListDetailNavigator) {
        mTobuyListDetailNavigator = tobuyListDetailNavigator;
    }

    public void setItemNavigator(ItemNavigator itemNavigator) {
        mItemNavigator = new WeakReference<>(itemNavigator);
    }

    public void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mTobuyListDetailNavigator = null;
        mItemNavigator = null;
    }

    /**
     * Called by the Data Binding library when the row is clicked.
     */
    public void itemClicked() {
        String itemId = getItemId();
        if (itemId == null) {
            // Click happened before item was loaded, no-op.
            return;
        }
        if (mItemNavigator != null && mItemNavigator.get() != null) {
            mItemNavigator.get().openItemDetails(itemId);
        }
    }

    public String getItemId() {
        return mItemId;
    }

    public void setItemId(String itemId) {
        mItemId = itemId;
    }

    /**
     * Can be called by the Data Binding Library or the delete menu item.
     */
    public void deleteTobuyList() {
        super.deleteTobuyList();
        if (mTobuyListDetailNavigator != null) {
            mTobuyListDetailNavigator.onTobuyListDeleted();
        }
    }

    public void startEditTobuyList() {
        if (mTobuyListDetailNavigator != null) {
            mTobuyListDetailNavigator.onStartEditTobuyList();
        }
    }
}
