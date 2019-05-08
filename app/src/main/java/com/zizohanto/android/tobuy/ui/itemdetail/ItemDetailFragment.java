package com.zizohanto.android.tobuy.ui.itemdetail;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.zizohanto.android.tobuy.util.SnackbarUtils;
import com.zizohanto.android.tobuyList.R;
import com.zizohanto.android.tobuyList.databinding.ItemDetailFragBinding;

import java.util.Objects;

/**
 * Main UI for the tobuyItem detail screen.
 */
public class ItemDetailFragment extends Fragment {

    public static final String ARGUMENT_ITEM_ID = "ITEM_ID";

    public static final int REQUEST_EDIT_ITEM = 1;

    private ItemDetailViewModel mViewModel;
    private Observable.OnPropertyChangedCallback mSnackbarCallback;

    public static ItemDetailFragment newInstance(String itemId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_ITEM_ID, itemId);
        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public void setViewModel(ItemDetailViewModel tobuyItemViewModel) {
        mViewModel = tobuyItemViewModel;
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setupFab() {
        Button button = Objects.requireNonNull(getActivity()).findViewById(R.id.fab_edit_item);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.startEditItem();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.start(getArguments().getString(ARGUMENT_ITEM_ID));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ItemDetailFragBinding viewDataBinding = ItemDetailFragBinding.inflate(inflater, container, false);

        viewDataBinding.setView(this);
        viewDataBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);

        View root = viewDataBinding.getRoot();

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mViewModel.deleteItem();
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.itemdetail_fragment_menu, menu);
    }
}
