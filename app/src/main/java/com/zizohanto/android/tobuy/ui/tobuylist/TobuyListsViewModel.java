package com.zizohanto.android.tobuy.ui.tobuylist;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.zizohanto.android.tobuy.data.model.TobuyList;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListDataSource;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;
import com.zizohanto.android.tobuyList.BR;

import java.util.List;

public class TobuyListsViewModel extends BaseObservable {

    // These observable fields will update Views automatically
    public final ObservableList<TobuyList> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> noTobuyListLabel = new ObservableField<>();

    public final ObservableBoolean tobuyListsAddViewVisible = new ObservableBoolean();

    final ObservableField<String> snackbarText = new ObservableField<>();

    private final TobuyListsRepository mTobuyListsRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private Context mContext; // To avoid leaks, this must be an Application Context.

    private TobuyListsNavigator mNavigator;

    public TobuyListsViewModel(TobuyListsRepository repository, Context context) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mTobuyListsRepository = repository;
    }

    void setNavigator(TobuyListsNavigator navigator) {
        mNavigator = navigator;
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    public void start() {
        loadTobuyLists(false);
    }

    @Bindable
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void loadTobuyLists(boolean forceUpdate) {
        loadTobuyLists(forceUpdate, true);
    }

    public String getSnackbarText() {
        return snackbarText.get();
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    public void addNewTobuyList() {
        if (mNavigator != null) {
            mNavigator.addNewTobuyList();
        }
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TobuyListDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadTobuyLists(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        if (forceUpdate) {

            mTobuyListsRepository.refreshTobuyLists();
        }

        mTobuyListsRepository.getTobuyLists(new TobuyListDataSource.LoadTobuyListsCallback() {
            @Override
            public void onTobuyListsLoaded(List<TobuyList> tobuyLists) {
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(tobuyLists);
                notifyPropertyChanged(BR.empty); // It's a @Bindable so update manually
            }

            @Override
            public void onDataNotAvailable() {
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                mIsDataLoadingError.set(true);
            }
        });
    }
}
