package com.zizohanto.android.tobuy.ui.addedititem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zizohanto.android.tobuy.Injection;
import com.zizohanto.android.tobuy.ViewModelHolder;
import com.zizohanto.android.tobuy.util.ActivityUtils;
import com.zizohanto.android.tobuyList.R;

import static com.zizohanto.android.tobuy.ui.addedititem.AddEditItemFragment.ARGUMENT_EDIT_ITEM_ID;
import static com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailActivity.EXTRA_TOBUYLIST_ID;

public class AddEditItemActivity extends AppCompatActivity implements AddEditItemNavigator {

    public static final int REQUEST_CODE = 1;

    public static final int ADD_EDIT_RESULT_OK = RESULT_FIRST_USER + 1;

    public static final String ADD_EDIT_ITEM_VIEWMODEL_TAG = "ADD_EDIT_ITEM_VIEWMODEL_TAG";

    private AddEditItemViewModel mViewModel;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_act);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        AddEditItemFragment addEditTaskFragment = findOrCreateViewFragment();

        mViewModel = findOrCreateViewModel();

        // Link View and ViewModel
        addEditTaskFragment.setViewModel(mViewModel);

        mViewModel.onActivityCreated(this);
    }

    @Override
    public void onItemSaved() {
        setResult(ADD_EDIT_RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    @NonNull
    private AddEditItemFragment findOrCreateViewFragment() {
        String itemId = getIntent().getStringExtra(ARGUMENT_EDIT_ITEM_ID);
        String tobuyListId = getIntent().getStringExtra(EXTRA_TOBUYLIST_ID);

        // View Fragment
        AddEditItemFragment addEditItemFragment = (AddEditItemFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (addEditItemFragment == null) {
            addEditItemFragment = AddEditItemFragment.newInstance(itemId, tobuyListId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditItemFragment, R.id.contentFrame);
        }
        return addEditItemFragment;
    }

    private AddEditItemViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
        @SuppressWarnings("unchecked")
        ViewModelHolder<AddEditItemViewModel> retainedViewModel =
                (ViewModelHolder<AddEditItemViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(ADD_EDIT_ITEM_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
            AddEditItemViewModel viewModel = new AddEditItemViewModel(
                    getApplicationContext(),
                    Injection.provideItemsRepository(getApplicationContext()));

            // and bind it to this Activity's lifecycle using the Fragment Manager.
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    ADD_EDIT_ITEM_VIEWMODEL_TAG);
            return viewModel;
        }
    }


}
