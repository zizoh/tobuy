package com.zizohanto.android.tobuy.ui.addedititem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zizohanto.android.tobuy.util.SnackbarUtils;
import com.zizohanto.android.tobuyList.R;
import com.zizohanto.android.tobuyList.databinding.AddItemFragBinding;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddEditItemFragment extends Fragment {

    public static final String ARGUMENT_EDIT_ITEM_ID = "EDIT_ITEM_ID";

    public static final String ARGUMENT_TOBUYLIST_ID = "TOBUYLIST_ID";

    private AddEditItemViewModel mViewModel;

    private AddItemFragBinding mViewDataBinding;

    private Observable.OnPropertyChangedCallback mSnackbarCallback;

    public AddEditItemFragment() {
        // Required empty public constructor
    }

    public static AddEditItemFragment newInstance(String itemId, String tobuyListId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_EDIT_ITEM_ID, itemId);
        arguments.putString(ARGUMENT_TOBUYLIST_ID, tobuyListId);
        AddEditItemFragment fragment = new AddEditItemFragment();
        fragment.setArguments(arguments);


        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            String tobuyListId = getArguments().getString(ARGUMENT_TOBUYLIST_ID);
            String itemId = getArguments().getString(ARGUMENT_EDIT_ITEM_ID);
            mViewModel.start(itemId, tobuyListId);
        } /*else {
            tobuyListId = getArguments().getString(ARGUMENT_TOBUYLIST_ID);
            mViewModel.start(null, tobuyListId);
        }*/
    }

    public void setViewModel(@NonNull AddEditItemViewModel viewModel) {
        mViewModel = checkNotNull(viewModel);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

        setupSnackbar();

        setupActionBar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.add_item_frag, container, false);
        if (mViewDataBinding == null) {
            mViewDataBinding = AddItemFragBinding.bind(root);
        }

        mViewDataBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);
        setRetainInstance(false);

        return mViewDataBinding.getRoot();
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
                (FloatingActionButton) getActivity().findViewById(R.id.fab_save_item);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.saveItem();
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (getArguments().get(ARGUMENT_EDIT_ITEM_ID) != null) {
            actionBar.setTitle(R.string.edit_item);
        } else {
            actionBar.setTitle(R.string.add_item);
        }
    }
}
