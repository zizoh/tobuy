package com.zizohanto.android.tobuy.ui.tobuylist;

import android.widget.ListView;

import androidx.databinding.BindingAdapter;

import com.zizohanto.android.tobuy.data.model.TobuyList;

import java.util.List;

/**
 * Contains {@link BindingAdapter}s for the {@link TobuyList} list.
 */
public class TobuyListsBindings {

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:items")
    public static void setItems(ListView listView, List<TobuyList> items) {
        TobuyListFragment.TobuyListsAdapter adapter = (TobuyListFragment.TobuyListsAdapter) listView.getAdapter();
        if (adapter != null)
        {
            adapter.replaceData(items);
        }
    }
}
