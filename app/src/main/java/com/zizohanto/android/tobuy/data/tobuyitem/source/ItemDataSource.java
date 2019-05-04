package com.zizohanto.android.tobuy.data.tobuyitem.source;

import androidx.annotation.NonNull;

import com.zizohanto.android.tobuy.data.model.Item;

import java.util.List;

public interface ItemDataSource {

    void getItems(String tobuyListId, @NonNull LoadItemsCallback callback);

    void getItem(@NonNull String itemId, @NonNull GetItemCallback callback);

    void saveItem(@NonNull Item item);

    void refreshItems();

    void deleteAllItems();

    void deleteItem(String itemId);

    interface LoadItemsCallback {

        void onItemsLoaded(List<Item> items);

        void onDataNotAvailable();
    }

    interface GetItemCallback {

        void onItemLoaded(Item item);

        void onDataNotAvailable();
    }
}
