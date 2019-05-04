package com.zizohanto.android.tobuy.data.tobuylist;

import androidx.annotation.NonNull;

import com.zizohanto.android.tobuy.data.model.TobuyList;

import java.util.List;

public interface TobuyListDataSource {

    interface LoadTobuyListsCallback {

        void onTobuyListsLoaded(List<TobuyList> tobuyLists);

        void onDataNotAvailable();
    }

    interface GetTobuyListCallback {

        void onTobuyListLoaded(TobuyList tobuyList);

        void onDataNotAvailable();
    }

    void getTobuyLists(@NonNull LoadTobuyListsCallback callback);

    void getTobuyList(@NonNull String tobuyId, @NonNull GetTobuyListCallback callback);

    void saveTobuyList(@NonNull TobuyList tobuyList);

    void refreshTobuyLists();

    void deleteAllTobuyLists();

    void deleteTobuyList(String tobuyListId);
}
