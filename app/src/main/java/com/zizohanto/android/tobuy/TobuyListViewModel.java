package com.zizohanto.android.tobuy;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.data.model.TobuyList;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListDataSource;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;
import com.zizohanto.android.tobuyList.R;

import java.util.Date;
import java.util.List;

public class TobuyListViewModel extends BaseObservable implements TobuyListDataSource.GetTobuyListCallback {

    public final ObservableField<String> snackbarText = new ObservableField<>();

    private final ObservableField<TobuyList> mTobuyListObservable = new ObservableField<>();

    public final ObservableField<String> name = new ObservableField<>();

    public final ObservableField<String> store = new ObservableField<>();

    public final ObservableField<Double> budget = new ObservableField<>();

    public final ObservableField<Double> balance = new ObservableField<>();

    public final ObservableField<Date> dueDate = new ObservableField<>();

    public final ObservableField<Double> totalCost = new ObservableField<>();

    public final ObservableField<List<Item>> items = new ObservableField<>();

    private final TobuyListsRepository mTobuyListsRepository;

    private final Context mContext;

    private boolean mIsDataLoading;

    public TobuyListViewModel(Context context, TobuyListsRepository tobuyListsRepository) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mTobuyListsRepository = tobuyListsRepository;

        // Exposed observables depend on the mTobuyListObservable observable:
        mTobuyListObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                TobuyList tobuyList = mTobuyListObservable.get();
                if (tobuyList != null) {
                    name.set(tobuyList.getName());
                    store.set(tobuyList.getStore());
                    budget.set(tobuyList.getBudget());
                    balance.set(tobuyList.getBalance());
                    dueDate.set(tobuyList.getDueDate());
                    totalCost.set(tobuyList.getTotalCost());
                    items.set(tobuyList.getItems());
                } else {
                    name.set(mContext.getString(R.string.no_data));
                }
            }
        });
    }

    public void start(String tobuyListId) {
        if (tobuyListId != null) {
            mIsDataLoading = true;
            mTobuyListsRepository.getTobuyList(tobuyListId, this);
        }
    }

    public void setTobuyList(TobuyList tobuyList) {
        mTobuyListObservable.set(tobuyList);
    }


    @Bindable
    public boolean isDataAvailable() {
        return mTobuyListObservable.get() != null;
    }

    @Bindable
    public boolean isDataLoading() {
        return mIsDataLoading;
    }

    // This could be an observable, but we save a call to TobuyList.getNameForList() if not needed.
    @Bindable
    public String getNameForList() {
        if (mTobuyListObservable.get() == null) {
            return "No data";
        }
        return mTobuyListObservable.get().getNameForList();
    }

    @Override
    public void onTobuyListLoaded(TobuyList tobuyList) {
        mTobuyListObservable.set(tobuyList);
        mIsDataLoading = false;
        notifyChange();
    }

    @Override
    public void onDataNotAvailable() {
        mTobuyListObservable.set(null);
        mIsDataLoading = false;
    }

    public void deleteTobuyList() {
        if (mTobuyListObservable.get() != null) {
            mTobuyListsRepository.deleteTobuyList(mTobuyListObservable.get().getId());
        }
    }

    public void onRefresh() {
        if (mTobuyListObservable.get() != null) {
            start(mTobuyListObservable.get().getId());
        }
    }

    public String getSnackbarText() {
        return snackbarText.get();
    }

    protected String getTobuyListId() {
        return mTobuyListObservable.get().getId();
    }
}
