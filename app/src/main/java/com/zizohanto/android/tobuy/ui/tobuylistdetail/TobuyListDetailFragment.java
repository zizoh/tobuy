package com.zizohanto.android.tobuy.ui.tobuylistdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zizohanto.android.tobuy.Injection;
import com.zizohanto.android.tobuy.TobuyListViewModel;
import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;
import com.zizohanto.android.tobuy.util.SnackbarUtils;
import com.zizohanto.android.tobuyList.R;
import com.zizohanto.android.tobuyList.databinding.ItemTobuyBinding;
import com.zizohanto.android.tobuyList.databinding.TobuyListDetailFragBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Main UI for the tobuylist detail screen.
 */
public class TobuyListDetailFragment extends Fragment {

    public static final String ARGUMENT_TOBUYLIST_ID = "TOBUYLIST_ID";
    public static final String WIDGET_PREF = "com.zizohanto.android.tobuy.widget.pref";
    public static final String WIDGET_PREF_TOBUYLIST_ID = "com.zizohanto.android.tobuy.widget.tobuylistid";
    public static final String WIDGET_NAME = "com.zizohanto.android.tobuy.widget.name";
    public static final String WIDGET_PRICE = "com.zizohanto.android.tobuy.widget.price";
    public static final String WIDGET_BUDGET = "com.zizohanto.android.tobuy.widget.budget";
    public static final String WIDGET_STORE = "com.zizohanto.android.tobuy.widget.store";

    public static final int REQUEST_EDIT_TOBUYLIST = 1;

    TobuyListDetailFragBinding mTobuyListDetailFragBinding;

    private TobuyListDetailViewModel mViewModel;
    private Observable.OnPropertyChangedCallback mSnackbarCallback;
    private ItemsAdapter mItemAdapter;

    public static TobuyListDetailFragment newInstance(String tobuylistId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_TOBUYLIST_ID, tobuylistId);
        TobuyListDetailFragment fragment = new TobuyListDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public void setViewModel(TobuyListDetailViewModel tobuylistViewModel) {
        mViewModel = tobuylistViewModel;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

        setupSnackbar();

        setupFab();

        setupListAdapter();

//        setupRefreshLayout();
    }

    private void setupSnackbar() {
        mSnackbarCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                SnackbarUtils.showSnackbar(getView(), mViewModel.getSnackbarText());
            }
        };
        mViewModel.snackbarText.addOnPropertyChangedCallback(mSnackbarCallback);
    }

    private void setupFab() {
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_tobuy_list);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.startEditTobuyList();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.start(getArguments().getString(ARGUMENT_TOBUYLIST_ID));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTobuyListDetailFragBinding = TobuyListDetailFragBinding.inflate(inflater, container, false);
        mTobuyListDetailFragBinding.setView(this);
        mTobuyListDetailFragBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);

        View root = mTobuyListDetailFragBinding.getRoot();

        return root;
    }

    private void setupListAdapter() {
        ListView listView = mTobuyListDetailFragBinding.itemList;

        mItemAdapter = new ItemsAdapter(
                new ArrayList<Item>(0),
                (TobuyListDetailActivity) getActivity(),
                Injection.provideTobuyListsRepository(getContext().getApplicationContext()),
                mViewModel);
        listView.setAdapter(mItemAdapter);
    }

    @Override
    public void onDestroy() {
        mItemAdapter.onDestroy();
        if (mSnackbarCallback != null) {
            mViewModel.snackbarText.removeOnPropertyChangedCallback(mSnackbarCallback);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mViewModel.deleteTobuyList();

            case R.id.menu_show_in_widget:
                mViewModel.showInWidget(getActivity());
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tobuylistdetail_fragment_menu, menu);
    }

    public static class ItemsAdapter extends BaseAdapter {

        private final TobuyListViewModel mTobuyListsViewModel;
        @Nullable
        private ItemNavigator mItemNavigator;
        private List<Item> mItems;

        private TobuyListsRepository mTobuyListsRepository;

        public ItemsAdapter(List<Item> items, TobuyListDetailActivity itemNavigator,
                            TobuyListsRepository tobuyListsRepository,
                            TobuyListViewModel tobuyListViewModel) {
            mItemNavigator = itemNavigator;
            mTobuyListsRepository = tobuyListsRepository;
            mTobuyListsViewModel = tobuyListViewModel;
            setList(items);

        }

        public void onDestroy() {
            mItemNavigator = null;
        }

        public void replaceData(List<Item> items) {
            setList(items);
        }

        @Override
        public int getCount() {
            return mItems != null ? mItems.size() : 0;
        }

        @Override
        public Item getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Item item = getItem(i);
            ItemTobuyBinding binding;
            if (view == null) {
                // Inflate
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

                // Create the binding
                binding = ItemTobuyBinding.inflate(inflater, viewGroup, false);
            } else {
                // Recycling view
                binding = DataBindingUtil.getBinding(view);
            }

            final TobuyListDetailViewModel viewmodel = new TobuyListDetailViewModel(
                    viewGroup.getContext().getApplicationContext(),
                    mTobuyListsRepository
            );

            viewmodel.setItemNavigator(mItemNavigator);

            binding.setViewmodel(viewmodel);
            viewmodel.setItemId(item.getId());
            binding.setItem(item);
            // To save on PropertyChangedCallbacks, wire the item's snackbar text observable to the
            // fragment's.
            viewmodel.snackbarText.addOnPropertyChangedCallback(
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable observable, int i) {
                            mTobuyListsViewModel.snackbarText.set(viewmodel.getSnackbarText());
                        }
                    });

            return binding.getRoot();
        }


        private void setList(List<Item> items) {
            mItems = items;
            notifyDataSetChanged();
        }
    }
}
