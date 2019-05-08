package com.zizohanto.android.tobuy.ui.tobuylistdetail;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zizohanto.android.tobuy.Injection;
import com.zizohanto.android.tobuy.ViewModelHolder;
import com.zizohanto.android.tobuy.ui.addedititem.AddEditItemActivity;
import com.zizohanto.android.tobuy.ui.addedittobuylist.AddEditTobuyListActivity;
import com.zizohanto.android.tobuy.ui.addedittobuylist.AddEditTobuyListFragment;
import com.zizohanto.android.tobuy.ui.itemdetail.ItemDetailActivity;
import com.zizohanto.android.tobuy.ui.tobuylist.TobuyListActivity;
import com.zizohanto.android.tobuy.util.ActivityUtils;
import com.zizohanto.android.tobuyList.R;

import static com.zizohanto.android.tobuy.ui.addedittobuylist.AddEditTobuyListActivity.ADD_EDIT_RESULT_OK;
import static com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailFragment.REQUEST_EDIT_TOBUYLIST;

public class TobuyListDetailActivity extends AppCompatActivity implements TobuyListDetailNavigator, ItemNavigator {

    public static final String EXTRA_TOBUYLIST_ID = "TOBUYLIST_ID";

    public static final String EXTRA_FROM_WIDGET = "FROM_WIDGET";

    public static final String TOBUYLISTDETAIL_VIEWMODEL_TAG = "TOBUYLISTDETAIL_VIEWMODEL_TAG";

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final int EDIT_RESULT_OK = RESULT_FIRST_USER + 3;

    private TobuyListDetailViewModel mTobuyListViewModel;
    private String mTobuyListId;
    private boolean mFromWidget;

    public static Intent getIntentForActivity(Context context, String tobuyListId, boolean fromWidget) {
        Intent intent = new Intent(context, TobuyListDetailActivity.class);
        intent.putExtra(EXTRA_TOBUYLIST_ID, tobuyListId);
        intent.putExtra(EXTRA_FROM_WIDGET, fromWidget);
        return intent;
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
                        .findFragmentByTag(TOBUYLISTDETAIL_VIEWMODEL_TAG);

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
                    TOBUYLISTDETAIL_VIEWMODEL_TAG);
            return viewModel;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tobuylistdetail_act);

        setupToolbar();

        Intent intent = getIntent();
        mTobuyListId = intent.getStringExtra(EXTRA_TOBUYLIST_ID);
        mFromWidget = intent.getBooleanExtra(EXTRA_FROM_WIDGET, false);

        TobuyListDetailFragment tobuyListDetailFragment = findOrCreateViewFragment();

        mTobuyListViewModel = findOrCreateViewModel();
        mTobuyListViewModel.setNavigator(this);
        mTobuyListViewModel.setItemNavigator(this);

        // Link View and ViewModel
        tobuyListDetailFragment.setViewModel(mTobuyListViewModel);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    @NonNull
    private TobuyListDetailFragment findOrCreateViewFragment() {
        // Get the requested tobuyList id
        String tobuyListId = getIntent().getStringExtra(EXTRA_TOBUYLIST_ID);

        TobuyListDetailFragment tobuyListDetailFragment = (TobuyListDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame_tobuy_list_detail_frag);

        if (tobuyListDetailFragment == null) {
            tobuyListDetailFragment = TobuyListDetailFragment.newInstance(tobuyListId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    tobuyListDetailFragment, R.id.content_frame_tobuy_list_detail_frag);
        }
        return tobuyListDetailFragment;
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
        if (mFromWidget) {
            Intent intent = new Intent(this, TobuyListActivity.class);
            startActivity(intent);
            return true;
        }
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
        Intent intent = new Intent(this, AddEditTobuyListActivity.class);
        intent.putExtra(AddEditTobuyListFragment.ARGUMENT_EDIT_TOBUYLIST_ID, mTobuyListId);
        startActivityForResult(intent, REQUEST_EDIT_TOBUYLIST);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void openItemDetails(String itemId) {
        Intent intent = ItemDetailActivity.getIntent(this, itemId, mTobuyListId);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivityForResult(intent, AddEditItemActivity.REQUEST_CODE, options.toBundle());
    }
}
