package com.zizohanto.android.tobuy.data.item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zizohanto.android.tobuy.data.model.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ItemsRepository implements ItemDataSource {

    private static ItemsRepository INSTANCE = null;

    private final ItemDataSource mTobuysLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Item> mCachedTobuys;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private ItemsRepository(@NonNull ItemDataSource tobuysLocalDataSource) {
        mTobuysLocalDataSource = checkNotNull(tobuysLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tobuysLocalDataSource the backend data source
     * @return the {@link ItemsRepository} instance
     */
    public static ItemsRepository getInstance(ItemDataSource tobuysLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ItemsRepository(tobuysLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(ItemDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tobuys from cache, local data source (SQLite)
     * <p>
     * Note: {@link LoadItemsCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getItems(final String tobuyListId, @NonNull final LoadItemsCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedTobuys != null && !mCacheIsDirty) {
            callback.onItemsLoaded(new ArrayList<>(mCachedTobuys.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            callback.onDataNotAvailable();

        } else {
            // Query the local storage if available. If not, query the network.
            mTobuysLocalDataSource.getItems(tobuyListId, new LoadItemsCallback() {
                @Override
                public void onItemsLoaded(List<Item> items) {
                    refreshCache(items);
                    callback.onItemsLoaded(new ArrayList<>(mCachedTobuys.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }
    }

    @Override
    public void getItem(@NonNull final String itemId, @NonNull final GetItemCallback callback) {
        checkNotNull(callback);

        Item cachedItem = getTobuyWithId(itemId);

        // Respond immediately with cache if available
        if (cachedItem != null) {
            callback.onItemLoaded(cachedItem);
            return;
        }

        mTobuysLocalDataSource.getItem(itemId, new GetItemCallback() {
            @Override
            public void onItemLoaded(Item item) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedTobuys == null) {
                    mCachedTobuys = new LinkedHashMap<>();
                }
                mCachedTobuys.put(item.getId(), item);
                callback.onItemLoaded(item);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveItem(@NonNull Item item) {
        checkNotNull(item);
        mTobuysLocalDataSource.saveItem(item);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTobuys == null) {
            mCachedTobuys = new LinkedHashMap<>();
        }
        mCachedTobuys.put(item.getId(), item);
    }

    @Override
    public void refreshItems() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllItems() {
        mTobuysLocalDataSource.deleteAllItems();

        if (mCachedTobuys == null) {
            mCachedTobuys = new LinkedHashMap<>();
        }
        mCachedTobuys.clear();
    }

    @Override
    public void deleteItem(String itemId) {
        mTobuysLocalDataSource.deleteItem(itemId);

        mCachedTobuys.remove(itemId);
    }

    private void refreshCache(List<Item> items) {
        if (mCachedTobuys == null) {
            mCachedTobuys = new LinkedHashMap<>();
        }
        mCachedTobuys.clear();
        for (Item item : items) {
            mCachedTobuys.put(item.getId(), item);
        }
        mCacheIsDirty = false;
    }

    @Nullable
    private Item getTobuyWithId(String id) {
        if (mCachedTobuys == null || mCachedTobuys.isEmpty()) {
            return null;
        } else {
            return mCachedTobuys.get(id);
        }
    }
}
