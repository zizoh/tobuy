package com.zizohanto.android.tobuy.ui.tobuylistdetail;

import android.content.Context;

import androidx.annotation.Nullable;

import com.zizohanto.android.tobuy.TobuyListViewModel;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;


/**
 * Listens to user actions from the list item in ({@link com.zizohanto.android.tobuy.ui.tobuylist.TobuyListFragment}) and redirects them to the
 * Fragment's actions listener.
 */
public class TobuyListDetailViewModel extends TobuyListViewModel {

    @Nullable
    private TobuyListDetailNavigator mTobuyListDetailNavigator;

    public TobuyListDetailViewModel(Context context, TobuyListsRepository tobuyListsRepository) {
        super(context, tobuyListsRepository);
    }

    public void setNavigator(TobuyListDetailNavigator tobuyListDetailNavigator) {
        mTobuyListDetailNavigator = tobuyListDetailNavigator;
    }

    public void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mTobuyListDetailNavigator = null;
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
