package com.zizohanto.android.tobuy.tobuylistdetail;

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
}
