package com.zizohanto.android.tobuy.tobuylistdetail;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zizohanto.android.tobuy.Injection;
import com.zizohanto.android.tobuy.ViewModelHolder;
import com.zizohanto.android.tobuy.addedittobuylist.AddEditTobuyListActivity;
import com.zizohanto.android.tobuy.addedittobuylist.AddEditTobuyListFragment;
import com.zizohanto.android.tobuy.util.ActivityUtils;
import com.zizohanto.android.tobuyList.R;

import static com.zizohanto.android.tobuy.addedittobuylist.AddEditTobuyListActivity.ADD_EDIT_RESULT_OK;
import static com.zizohanto.android.tobuy.tobuylistdetail.TobuyListDetailFragment.REQUEST_EDIT_TOBUYLIST;

public class TobuyListDetailActivity extends AppCompatActivity implements TobuyListDetailNavigator {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    public static final String TASKDETAIL_VIEWMODEL_TAG = "TASKDETAIL_VIEWMODEL_TAG";

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final int EDIT_RESULT_OK = RESULT_FIRST_USER + 3;

    private TobuyListDetailViewModel mTobuyListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tobuylistdetail_act);

        setupToolbar();

        TobuyListDetailFragment tobuyListDetailFragment = findOrCreateViewFragment();

        mTobuyListViewModel = findOrCreateViewModel();
        mTobuyListViewModel.setNavigator(this);

        // Link View and ViewModel
        tobuyListDetailFragment.setViewModel(mTobuyListViewModel);
    }

    @Override
    protected void onDestroy() {
        mTobuyListViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    @NonNull
    private TobuyListDetailViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
        @SuppressWarnings("unchecked")
        ViewModelHolder<TobuyListDetailViewModel> retainedViewModel =
                (ViewModelHolder<TobuyListDetailViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(TASKDETAIL_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
            TobuyListDetailViewModel viewModel = new TobuyListDetailViewModel(
                    getApplicationContext(),
                    Injection.provideTobuyListsRepository(getApplicationContext()));

            // and bind it to this Activity's lifecycle using the Fragment Manager.
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    TASKDETAIL_VIEWMODEL_TAG);
            return viewModel;
        }
    }

    @NonNull
    private TobuyListDetailFragment findOrCreateViewFragment() {
        // Get the requested tobuyList id
        String tobuyListId = getIntent().getStringExtra(EXTRA_TASK_ID);

        TobuyListDetailFragment tobuyListDetailFragment = (TobuyListDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (tobuyListDetailFragment == null) {
            tobuyListDetailFragment = TobuyListDetailFragment.newInstance(tobuyListId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    tobuyListDetailFragment, R.id.contentFrame);
        }
        return tobuyListDetailFragment;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_TOBUYLIST) {
            // If the tobuyList was edited successfully, go back to the list.
            if (resultCode == ADD_EDIT_RESULT_OK) {
                // If the result comes from the add/edit screen, it's an edit.
                setResult(EDIT_RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onTobuyListDeleted() {
        setResult(DELETE_RESULT_OK);
        // If the tobuylist was deleted successfully, go back to the list.
        finish();
    }

    @Override
    public void onStartEditTobuyList() {
        String tobuyListId = getIntent().getStringExtra(EXTRA_TASK_ID);
        Intent intent = new Intent(this, AddEditTobuyListActivity.class);
        intent.putExtra(AddEditTobuyListFragment.ARGUMENT_EDIT_TOBUYLIST_ID, tobuyListId);
        startActivityForResult(intent, REQUEST_EDIT_TOBUYLIST);
    }
}
