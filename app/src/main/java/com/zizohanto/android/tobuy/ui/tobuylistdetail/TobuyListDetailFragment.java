package com.zizohanto.android.tobuy.ui.tobuylistdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zizohanto.android.tobuy.util.SnackbarUtils;
import com.zizohanto.android.tobuyList.R;
import com.zizohanto.android.tobuyList.databinding.TobuyListDetailFragBinding;

/**
 * Main UI for the tobuylist detail screen.
 */
public class TobuyListDetailFragment extends Fragment {

    public static final String ARGUMENT_TOBUYLIST_ID = "TOBUYLIST_ID";

    public static final int REQUEST_EDIT_TOBUYLIST = 1;

    private TobuyListDetailViewModel mViewModel;
    private Observable.OnPropertyChangedCallback mSnackbarCallback;

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

        View view = inflater.inflate(R.layout.tobuy_list_detail_frag, container, false);

        TobuyListDetailFragBinding viewDataBinding = TobuyListDetailFragBinding.bind(view);
        viewDataBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mViewModel.deleteTobuyList();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tobuylistdetail_fragment_menu, menu);
    }

//    public static class TobuyListsAdapter extends BaseAdapter {
//
//        @Nullable
//        private TobuyListItemNavigator mTobuyListItemNavigator;
//
//        private final TobuyListsViewModel mTobuyListsViewModel;
//
//        private List<TobuyList> mTobuyLists;
//
//        private TobuyListsRepository mTobuyListsRepository;
//
//        public TobuyListsAdapter(List<TobuyList> tobuyLists, TobuyListActivity tobuyListItemNavigator,
//                                 TobuyListsRepository tobuyListsRepository,
//                                 TobuyListsViewModel tobuyListsViewModel) {
//            mTobuyListItemNavigator = tobuyListItemNavigator;
//            mTobuyListsRepository = tobuyListsRepository;
//            mTobuyListsViewModel = tobuyListsViewModel;
//            setList(tobuyLists);
//
//        }
//
//        public void onDestroy() {
//            mTobuyListItemNavigator = null;
//        }
//
//        public void replaceData(List<TobuyList> tobuyLists) {
//            setList(tobuyLists);
//        }
//
//        @Override
//        public int getCount() {
//            return mTobuyLists != null ? mTobuyLists.size() : 0;
//        }
//
//        @Override
//        public TobuyList getItem(int i) {
//            return mTobuyLists.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            TobuyList tobuyList = getItem(i);
//            ItemTobuylistBinding binding;
//            if (view == null) {
//                // Inflate
//                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//
//                // Create the binding
//                binding = ItemTobuylistBinding.inflate(inflater, viewGroup, false);
//            } else {
//                // Recycling view
//                binding = DataBindingUtil.getBinding(view);
//            }
//
//            final TobuyListItemViewModel viewmodel = new TobuyListItemViewModel(
//                    viewGroup.getContext().getApplicationContext(),
//                    mTobuyListsRepository
//            );
//
//            viewmodel.setNavigator(mTobuyListItemNavigator);
//
//            binding.setViewmodel(viewmodel);
//            // To save on PropertyChangedCallbacks, wire the item's snackbar text observable to the
//            // fragment's.
//            viewmodel.snackbarText.addOnPropertyChangedCallback(
//                    new Observable.OnPropertyChangedCallback() {
//                        @Override
//                        public void onPropertyChanged(Observable observable, int i) {
//                            mTobuyListsViewModel.snackbarText.set(viewmodel.getSnackbarText());
//                        }
//                    });
//            viewmodel.setTobuyList(tobuyList);
//
//            return binding.getRoot();
//        }
//
//
//        private void setList(List<TobuyList> tobuyLists) {
//            mTobuyLists = tobuyLists;
//            notifyDataSetChanged();
//        }
//    }
}
