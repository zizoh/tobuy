package com.zizohanto.android.tobuy.ui.addedittobuylist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zizohanto.android.tobuy.Injection;
import com.zizohanto.android.tobuy.ViewModelHolder;
import com.zizohanto.android.tobuy.ui.addedititem.AddEditItemActivity;
import com.zizohanto.android.tobuy.util.ActivityUtils;
import com.zizohanto.android.tobuyList.R;

public class AddEditTobuyListActivity extends AppCompatActivity implements AddEditTobuyListNavigator {

    public static final int REQUEST_CODE = 1;

    public static final int ADD_EDIT_RESULT_OK = RESULT_FIRST_USER + 1;

    public static final String ADD_EDIT_VIEWMODEL_TAG = "ADD_EDIT_VIEWMODEL_TAG";

    private AddEditTobuyListViewModel mViewModel;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tobuylist_act);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        AddEditTobuyListFragment addEditTaskFragment = findOrCreateViewFragment();

        mViewModel = findOrCreateViewModel();

        // Link View and ViewModel
        addEditTaskFragment.setViewModel(mViewModel);

        mViewModel.onActivityCreated(this);
    }

    @Override
    public void onTobuyListSaved() {
        setResult(ADD_EDIT_RESULT_OK);
        finish();
    }

    @Override
    public void onAddNewTobuyItem(String tobuyListId) {
        Intent intent = AddEditItemActivity.getActivityIntent(this, tobuyListId);
        startActivityForResult(intent, AddEditItemActivity.REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    @NonNull
    private AddEditTobuyListFragment findOrCreateViewFragment() {
        // View Fragment
        AddEditTobuyListFragment addEditTobuyListFragment = (AddEditTobuyListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (addEditTobuyListFragment == null) {
            addEditTobuyListFragment = AddEditTobuyListFragment.newInstance();

            // Send the task ID to the fragment
            Bundle bundle = new Bundle();
            bundle.putString(AddEditTobuyListFragment.ARGUMENT_EDIT_TOBUYLIST_ID,
                    getIntent().getStringExtra(AddEditTobuyListFragment.ARGUMENT_EDIT_TOBUYLIST_ID));
            addEditTobuyListFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTobuyListFragment, R.id.contentFrame);
        }
        return addEditTobuyListFragment;
    }

    private AddEditTobuyListViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
        @SuppressWarnings("unchecked")
        ViewModelHolder<AddEditTobuyListViewModel> retainedViewModel =
                (ViewModelHolder<AddEditTobuyListViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(ADD_EDIT_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
            AddEditTobuyListViewModel viewModel = new AddEditTobuyListViewModel(
                    getApplicationContext(),
                    Injection.provideTobuyListsRepository(getApplicationContext()));

            // and bind it to this Activity's lifecycle using the Fragment Manager.
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    ADD_EDIT_VIEWMODEL_TAG);
            return viewModel;
        }
    }
}
