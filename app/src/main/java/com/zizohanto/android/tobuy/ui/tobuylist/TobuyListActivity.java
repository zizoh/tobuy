package com.zizohanto.android.tobuy.ui.tobuylist;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.zizohanto.android.tobuy.Injection;
import com.zizohanto.android.tobuy.ViewModelHolder;
import com.zizohanto.android.tobuy.ui.addedittobuylist.AddEditTobuyListActivity;
import com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailActivity;
import com.zizohanto.android.tobuy.util.ActivityUtils;
import com.zizohanto.android.tobuyList.R;

public class TobuyListActivity extends AppCompatActivity implements TobuyListItemNavigator, TobuyListsNavigator {

    private DrawerLayout mDrawerLayout;

    public static final String TOBUYLISTS_VIEWMODEL_TAG = "TOBUYLISTS_VIEWMODEL_TAG";

    private TobuyListsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tobuylists_act);

        setupToolbar();

        setupNavigationDrawer();

        TobuyListFragment tobuyListsFragment = findOrCreateViewFragment();

        mViewModel = findOrCreateViewModel();
        mViewModel.setNavigator(this);

        // Link View and ViewModel
        tobuyListsFragment.setViewModel(mViewModel);
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    private TobuyListsViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
        @SuppressWarnings("unchecked")
        ViewModelHolder<TobuyListsViewModel> retainedViewModel =
                (ViewModelHolder<TobuyListsViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(TOBUYLISTS_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
            TobuyListsViewModel viewModel = new TobuyListsViewModel(
                    Injection.provideTobuyListsRepository(getApplicationContext()),
                    getApplicationContext());
            // and bind it to this Activity's lifecycle using the Fragment Manager.
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    TOBUYLISTS_VIEWMODEL_TAG);
            return viewModel;
        }
    }

    @NonNull
    private TobuyListFragment findOrCreateViewFragment() {
        TobuyListFragment tobuyListsFragment =
                (TobuyListFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame_tobuy_list_frag);
        if (tobuyListsFragment == null) {
            // Create the fragment
            tobuyListsFragment = TobuyListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tobuyListsFragment, R.id.content_frame_tobuy_list_frag);
        }
        return tobuyListsFragment;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.list_navigation_menu_item:
                                // Do nothing, we're already on that screen
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void openTobuyListDetails(String tobuyListId) {
        Intent intent = TobuyListDetailActivity.getIntentForActivity(this, tobuyListId, false);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivityForResult(intent, AddEditTobuyListActivity.REQUEST_CODE, options.toBundle());
    }

    @Override
    public void addNewTobuyList() {
        Intent intent = new Intent(this, AddEditTobuyListActivity.class);
        startActivityForResult(intent, AddEditTobuyListActivity.REQUEST_CODE);
    }
}
