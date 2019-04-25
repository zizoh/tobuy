package com.zizohanto.android.tobuy.tobuylist;

import android.content.Context;

import androidx.annotation.Nullable;

import com.zizohanto.android.tobuy.TobuyListViewModel;
import com.zizohanto.android.tobuy.data.TobuyListsRepository;

import java.lang.ref.WeakReference;

/**
 * Listens to user actions from the list item in ({@link TobuyListFragment}) and redirects them to the
 * Fragment's actions listener.
 */
public class TobuyListItemViewModel extends TobuyListViewModel {

    // This navigator is s wrapped in a WeakReference to avoid leaks because it has references to an
    // activity. There's no straightforward way to clear it for each item in a list adapter.
    @Nullable
    private WeakReference<TobuyListItemNavigator> mNavigator;

    public TobuyListItemViewModel(Context context, TobuyListsRepository tobuyListsRepository) {
        super(context, tobuyListsRepository);
    }

    public void setNavigator(TobuyListItemNavigator navigator) {
        mNavigator = new WeakReference<>(navigator);
    }

    /**
     * Called by the Data Binding library when the row is clicked.
     */
    public void tobuyListClicked() {
        String tobuyListId = getTobuyListId();
        if (tobuyListId == null) {
            // Click happened before tobuy was loaded, no-op.
            return;
        }
        if (mNavigator != null && mNavigator.get() != null) {
            mNavigator.get().openTobuyListDetails(tobuyListId);
        }
    }
}
