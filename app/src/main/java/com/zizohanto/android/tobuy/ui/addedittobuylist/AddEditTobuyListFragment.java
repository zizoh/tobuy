package com.zizohanto.android.tobuy.ui.addedittobuylist;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.zizohanto.android.tobuy.Injection;
import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;
import com.zizohanto.android.tobuy.ui.tobuylistdetail.ItemNavigator;
import com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailViewModel;
import com.zizohanto.android.tobuy.util.SnackbarUtils;
import com.zizohanto.android.tobuyList.R;
import com.zizohanto.android.tobuyList.databinding.AddTobuyListFragBinding;
import com.zizohanto.android.tobuyList.databinding.ItemTobuyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddEditTobuyListFragment extends Fragment {

    public static final String ARGUMENT_EDIT_TOBUYLIST_ID = "EDIT_TOBUYLIST_ID";

    private AddEditTobuyListViewModel mViewModel;

    private AddTobuyListFragBinding mViewDataBinding;

    private Observable.OnPropertyChangedCallback mSnackbarCallback;
    private AddEditTobuyListFragment.ItemsAdapter mItemAdapter;

    public static AddEditTobuyListFragment newInstance() {
        return new AddEditTobuyListFragment();
    }

    public AddEditTobuyListFragment() {
        // Required empty public constructor
    }

    public void setViewModel(@NonNull AddEditTobuyListViewModel viewModel) {
        mViewModel = checkNotNull(viewModel);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

        setupSnackbar();

        setupActionBar();

        setupListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.add_tobuy_list_frag, container, false);
        if (mViewDataBinding == null) {
            mViewDataBinding = AddTobuyListFragBinding.bind(root);
        }

        mViewDataBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);
        setRetainInstance(false);

        return mViewDataBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            mViewModel.start(getArguments().getString(ARGUMENT_EDIT_TOBUYLIST_ID));
        } else {
            mViewModel.start(null);
        }
    }

    @Override
    public void onDestroy() {
        if (mSnackbarCallback != null) {
            mViewModel.snackbarText.removeOnPropertyChangedCallback(mSnackbarCallback);
        }

        super.onDestroy();
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setupFab() {
        Button button = Objects.requireNonNull(getActivity()).findViewById(R.id.fab_add_tobuyitem);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.addNewItem();
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (getArguments().get(ARGUMENT_EDIT_TOBUYLIST_ID) != null) {
            actionBar.setTitle(R.string.edit_tobuylist);
        } else {
            actionBar.setTitle(R.string.add_tobuylist);
        }
    }

    private void setupListAdapter() {
        ListView listView = mViewDataBinding.itemList;

        mItemAdapter = new AddEditTobuyListFragment.ItemsAdapter(
                new ArrayList<Item>(0),
                (AddEditTobuyListActivity) getActivity(),
                Injection.provideTobuyListsRepository(getContext().getApplicationContext()),
                mViewModel);
        listView.setAdapter(mItemAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                mViewModel.saveTobuyList();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_tobuy_list_frag_menu, menu);
    }

    public static class ItemsAdapter extends BaseAdapter {

        private final AddEditTobuyListViewModel mAddEditTobuyListViewModel;
        @Nullable
        private ItemNavigator mItemNavigator;
        private List<Item> mItems;

        private TobuyListsRepository mTobuyListsRepository;

        public ItemsAdapter(List<Item> items, AddEditTobuyListActivity itemNavigator,
                            TobuyListsRepository tobuyListsRepository,
                            AddEditTobuyListViewModel tobuyListViewModel) {
            mItemNavigator = itemNavigator;
            mTobuyListsRepository = tobuyListsRepository;
            mAddEditTobuyListViewModel = tobuyListViewModel;
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
                            mAddEditTobuyListViewModel.snackbarText.set(viewmodel.getSnackbarText());
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
