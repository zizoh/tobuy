package com.zizohanto.android.tobuy.data.item.source.local;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.zizohanto.android.tobuy.data.item.ItemDataSource;
import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ItemLocalDataSource implements ItemDataSource {

    private static volatile ItemLocalDataSource INSTANCE;

    private ItemDao mItemDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private ItemLocalDataSource(@NonNull AppExecutors appExecutors,
                                @NonNull ItemDao itemDao) {
        mAppExecutors = appExecutors;
        mItemDao = itemDao;
    }

    public static ItemLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                  @NonNull ItemDao itemDao) {
        if (INSTANCE == null) {
            synchronized (ItemLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ItemLocalDataSource(appExecutors, itemDao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Note: {@link LoadItemsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getItems(final String tobuyListId, @NonNull final LoadItemsCallback callback) {

        LiveData<List<Item>> items = mItemDao.getItemsOfList(tobuyListId);
        items.observeForever(new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                if (items.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable();
                } else {
                    callback.onItemsLoaded(items);
                }
            }
        });
    }

    /**
     * Note: {@link GetItemCallback#onDataNotAvailable()} is fired if the {@link Item} isn't
     * found.
     */
    @Override
    public void getItem(@NonNull final String tobuyId, @NonNull final GetItemCallback callback) {

        LiveData<Item> item = mItemDao.getItemWithId(tobuyId);
        item.observeForever(new Observer<Item>() {
            @Override
            public void onChanged(Item item) {
                if (item != null) {
                    callback.onItemLoaded(item);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        });
    }

    @Override
    public void saveItem(@NonNull final Item item) {
        checkNotNull(item);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
               mItemDao.insertItem(item);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);

    }

    @Override
    public void refreshItems() {

    }

    @Override
    public void deleteAllItems() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mItemDao.deleteItems();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteItem(final String itemId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mItemDao.deleteItemWithId(itemId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }
}
