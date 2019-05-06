package com.zizohanto.android.tobuy.data.item.source.local;

import androidx.annotation.NonNull;

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
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Item> items = mItemDao.getItemsOfList(tobuyListId);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (items.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onItemsLoaded(items);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);

    }

    /**
     * Note: {@link GetItemCallback#onDataNotAvailable()} is fired if the {@link Item} isn't
     * found.
     */
    @Override
    public void getItem(@NonNull final String tobuyId, @NonNull final GetItemCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Item item = mItemDao.getItemWithId(tobuyId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (item != null) {
                            callback.onItemLoaded(item);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
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
