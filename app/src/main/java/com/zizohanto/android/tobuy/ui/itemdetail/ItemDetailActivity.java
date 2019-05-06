package com.zizohanto.android.tobuy.ui.itemdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zizohanto.android.tobuy.Injection;
import com.zizohanto.android.tobuy.ViewModelHolder;
import com.zizohanto.android.tobuy.ui.addedititem.AddEditItemActivity;
import com.zizohanto.android.tobuy.ui.addedititem.AddEditItemFragment;
import com.zizohanto.android.tobuy.util.ActivityUtils;
import com.zizohanto.android.tobuyList.R;

import static com.zizohanto.android.tobuy.ui.addedittobuylist.AddEditTobuyListActivity.ADD_EDIT_RESULT_OK;
import static com.zizohanto.android.tobuy.ui.itemdetail.ItemDetailFragment.REQUEST_EDIT_ITEM;
import static com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailActivity.EXTRA_TOBUYLIST_ID;

public class ItemDetailActivity extends AppCompatActivity implements ItemDetailNavigator {

    public static final String EXTRA_ITEM_ID = "ITEM_ID";

    public static final String ITEMDETAIL_VIEWMODEL_TAG = "DETAIL_VIEWMODEL_TAG";

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final int EDIT_RESULT_OK = RESULT_FIRST_USER + 3;

    private ItemDetailViewModel mItemViewModel;

    public static Intent getIntent(Context context, String itemId, String tobuyListId) {
        Intent intent = new Intent(context, ItemDetailActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        intent.putExtra(EXTRA_TOBUYLIST_ID, tobuyListId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail_act);

        setupToolbar();

        ItemDetailFragment itemDetailFragment = findOrCreateViewFragment();

        mItemViewModel = findOrCreateViewModel();
        mItemViewModel.setNavigator(this);

        // Link View and ViewModel
        itemDetailFragment.setViewModel(mItemViewModel);
    }

    @Override
    protected void onDestroy() {
        mItemViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    @NonNull
    private ItemDetailViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
        @SuppressWarnings("unchecked")
        ViewModelHolder<ItemDetailViewModel> retainedViewModel =
                (ViewModelHolder<ItemDetailViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(ITEMDETAIL_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
            ItemDetailViewModel viewModel = new ItemDetailViewModel(
                    getApplicationContext(),
                    Injection.provideItemsRepository(getApplicationContext()));

            // and bind it to this Activity's lifecycle using the Fragment Manager.
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    ITEMDETAIL_VIEWMODEL_TAG);
            return viewModel;
        }
    }

    @NonNull
    private ItemDetailFragment findOrCreateViewFragment() {
        // Get the requested item id
        String itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);

        ItemDetailFragment itemDetailFragment = (ItemDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame_item_detail_frag);

        if (itemDetailFragment == null) {
            itemDetailFragment = ItemDetailFragment.newInstance(itemId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    itemDetailFragment, R.id.content_frame_item_detail_frag);
        }
        return itemDetailFragment;
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
        if (requestCode == REQUEST_EDIT_ITEM) {
            // If the tobuyItem was edited successfully, go back to the item.
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
    public void onItemDeleted() {
        setResult(DELETE_RESULT_OK);
        // If the item was deleted successfully, go back to the item.
        finish();
    }

    @Override
    public void onStartEditItem() {
        String itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        String tobuyListId = getIntent().getStringExtra(EXTRA_TOBUYLIST_ID);
        Intent intent = new Intent(this, AddEditItemActivity.class);
        intent.putExtra(AddEditItemFragment.ARGUMENT_EDIT_ITEM_ID, itemId);
        intent.putExtra(AddEditItemFragment.ARGUMENT_TOBUYLIST_ID, tobuyListId);
        startActivityForResult(intent, REQUEST_EDIT_ITEM);
    }
}
