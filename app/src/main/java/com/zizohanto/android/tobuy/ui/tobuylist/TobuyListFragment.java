package com.zizohanto.android.tobuy.ui.tobuylist;

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
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zizohanto.android.tobuy.Injection;
import com.zizohanto.android.tobuy.ScrollChildSwipeRefreshLayout;
import com.zizohanto.android.tobuy.data.model.TobuyList;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;
import com.zizohanto.android.tobuy.util.SnackbarUtils;
import com.zizohanto.android.tobuyList.R;
import com.zizohanto.android.tobuyList.databinding.ItemTobuylistBinding;
import com.zizohanto.android.tobuyList.databinding.TobuylistsFragBinding;

import java.util.ArrayList;
import java.util.List;

public class TobuyListFragment extends Fragment {

    private TobuyListsViewModel mTobuyListsViewModel;

    private TobuylistsFragBinding mTobuylistsFragBinding;

    private TobuyListsAdapter mListAdapter;

    private Observable.OnPropertyChangedCallback mSnackbarCallback;

    public TobuyListFragment() {
        // Requires empty public constructor
    }

    public static TobuyListFragment newInstance() {
        return new TobuyListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTobuyListsViewModel.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTobuylistsFragBinding = TobuylistsFragBinding.inflate(inflater, container, false);

        mTobuylistsFragBinding.setView(this);

        mTobuylistsFragBinding.setViewmodel(mTobuyListsViewModel);

        setHasOptionsMenu(true);

        View root = mTobuylistsFragBinding.getRoot();

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mTobuyListsViewModel.loadTobuyLists(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tobuylists_fragment_menu, menu);
    }

    public void setViewModel(TobuyListsViewModel viewModel) {
        mTobuyListsViewModel = viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupSnackbar();

        setupFab();

        setupListAdapter();

        setupRefreshLayout();
    }

    @Override
    public void onDestroy() {
        mListAdapter.onDestroy();
        if (mSnackbarCallback != null) {
            mTobuyListsViewModel.snackbarText.removeOnPropertyChangedCallback(mSnackbarCallback);
        }
        super.onDestroy();
    }

    private void setupSnackbar() {
        mSnackbarCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                SnackbarUtils.showSnackbar(getView(), mTobuyListsViewModel.getSnackbarText());
            }
        };
        mTobuyListsViewModel.snackbarText.addOnPropertyChangedCallback(mSnackbarCallback);
    }

    private void setupFab() {
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_tobuylist);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTobuyListsViewModel.addNewTobuyList();
            }
        });
    }

    private void setupListAdapter() {
        ListView listView =  mTobuylistsFragBinding.tobuylistList;

        mListAdapter = new TobuyListsAdapter(
                new ArrayList<TobuyList>(0),
                (TobuyListActivity) getActivity(),
                Injection.provideTobuyListsRepository(getContext().getApplicationContext()),
                mTobuyListsViewModel);
        listView.setAdapter(mListAdapter);
    }

    private void setupRefreshLayout() {
        ListView listView =  mTobuylistsFragBinding.tobuylistList;
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = mTobuylistsFragBinding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);
    }

    public static class TobuyListsAdapter extends BaseAdapter {

        @Nullable
        private TobuyListItemNavigator mTobuyListItemNavigator;

        private final TobuyListsViewModel mTobuyListsViewModel;

        private List<TobuyList> mTobuyLists;

        private TobuyListsRepository mTobuyListsRepository;

        public TobuyListsAdapter(List<TobuyList> tobuyLists, TobuyListActivity tobuyListItemNavigator,
                                 TobuyListsRepository tobuyListsRepository,
                                 TobuyListsViewModel tobuyListsViewModel) {
            mTobuyListItemNavigator = tobuyListItemNavigator;
            mTobuyListsRepository = tobuyListsRepository;
            mTobuyListsViewModel = tobuyListsViewModel;
            setList(tobuyLists);

        }

        public void onDestroy() {
            mTobuyListItemNavigator = null;
        }

        public void replaceData(List<TobuyList> tobuyLists) {
            setList(tobuyLists);
        }

        @Override
        public int getCount() {
            return mTobuyLists != null ? mTobuyLists.size() : 0;
        }

        @Override
        public TobuyList getItem(int i) {
            return mTobuyLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TobuyList tobuyList = getItem(i);
            ItemTobuylistBinding binding;
            if (view == null) {
                // Inflate
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

                // Create the binding
                binding = ItemTobuylistBinding.inflate(inflater, viewGroup, false);
            } else {
                // Recycling view
                binding = DataBindingUtil.getBinding(view);
            }

            final TobuyListItemViewModel viewmodel = new TobuyListItemViewModel(
                    viewGroup.getContext().getApplicationContext(),
                    mTobuyListsRepository
            );

            viewmodel.setNavigator(mTobuyListItemNavigator);

            binding.setViewmodel(viewmodel);
            // To save on PropertyChangedCallbacks, wire the item's snackbar text observable to the
            // fragment's.
            viewmodel.snackbarText.addOnPropertyChangedCallback(
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable observable, int i) {
                            mTobuyListsViewModel.snackbarText.set(viewmodel.getSnackbarText());
                        }
                    });
            viewmodel.setTobuyList(tobuyList);

            return binding.getRoot();
        }


        private void setList(List<TobuyList> tobuyLists) {
            mTobuyLists = tobuyLists;
            notifyDataSetChanged();
        }
    }
}
