package com.zizohanto.android.tobuy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.data.model.TobuyList;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListDataSource;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;
import com.zizohanto.android.tobuy.widget.TobuyListWidgetProvider;
import com.zizohanto.android.tobuyList.BR;
import com.zizohanto.android.tobuyList.R;

import java.util.Date;
import java.util.List;

import static com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailFragment.WIDGET_BUDGET;
import static com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailFragment.WIDGET_NAME;
import static com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailFragment.WIDGET_PREF;
import static com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailFragment.WIDGET_PREF_TOBUYLIST_ID;
import static com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailFragment.WIDGET_PRICE;
import static com.zizohanto.android.tobuy.ui.tobuylistdetail.TobuyListDetailFragment.WIDGET_STORE;

public class TobuyListViewModel extends BaseObservable implements TobuyListDataSource.GetTobuyListCallback {

    public final ObservableField<List<Item>> items = new ObservableField<>();

    public final ObservableField<String> snackbarText = new ObservableField<>();

    private final ObservableField<TobuyList> mTobuyListObservable = new ObservableField<>();

    public final ObservableField<String> name = new ObservableField<>();

    public final ObservableField<String> store = new ObservableField<>();

    public final ObservableField<Double> budget = new ObservableField<>();

    public final ObservableField<Double> balance = new ObservableField<>();

    public final ObservableField<Date> dueDate = new ObservableField<>();

    public final ObservableField<Double> totalCost = new ObservableField<>();

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
                    notifyPropertyChanged(BR.empty); // It's a @Bindable so update manually
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

    @Bindable
    public boolean isEmpty() {

        List<Item> items1 = items.get();
        if (items1 == null) {
            return true;
        }

        return items1.isEmpty();
    }

    // This could be an observable, but we save a call to TobuyList.getNameForList() if not needed.
    @Bindable
    public String getNameForList() {
        TobuyList tobuyList = mTobuyListObservable.get();
        if (tobuyList == null) {
            return "No data";
        }
        return tobuyList.getNameForList();
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

    public void showInWidget(Activity activity) {
        if (mTobuyListObservable.get() != null) {
            saveToPreferences(activity, mTobuyListObservable.get());
            TobuyListWidgetProvider.updateWidget(activity);
        }
    }

    private void saveToPreferences(Activity activity, TobuyList tobuyList) {
        SharedPreferences sharedpreferences = activity.getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(WIDGET_PREF_TOBUYLIST_ID, tobuyList.getId());
        editor.putString(WIDGET_NAME, tobuyList.getName());
        editor.putString(WIDGET_PRICE, String.valueOf(tobuyList.getTotalCost()));
        editor.putString(WIDGET_BUDGET, String.valueOf(tobuyList.getBudget()));
        editor.putString(WIDGET_STORE, tobuyList.getStore());
        editor.apply();
    }

    private String getStoreName(String store) {
        if (TextUtils.isEmpty(store)) return "";
        return store;
    }

    public void onRefresh() {
        if (mTobuyListObservable.get() != null) {
            start(mTobuyListObservable.get().getId());
        }
    }

    public String getSnackbarText() {
        return snackbarText.get();
    }

    protected String getItemId() {
        return mTobuyListObservable.get().getId();
    }
}
