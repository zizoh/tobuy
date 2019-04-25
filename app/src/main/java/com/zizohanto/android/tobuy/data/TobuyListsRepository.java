package com.zizohanto.android.tobuy.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zizohanto.android.tobuy.data.model.TobuyList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class TobuyListsRepository implements TobuyListDataSource {

    private static TobuyListsRepository INSTANCE = null;

    private final TobuyListDataSource mTobuysLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, TobuyList> mCachedTobuys;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private TobuyListsRepository(@NonNull TobuyListDataSource tobuysLocalDataSource) {
        mTobuysLocalDataSource = checkNotNull(tobuysLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tobuysLocalDataSource the backend data source
     * @return the {@link TobuyListsRepository} instance
     */
    public static TobuyListsRepository getInstance(TobuyListDataSource tobuysLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TobuyListsRepository(tobuysLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TobuyListDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tobuys from cache, local data source (SQLite)
     * <p>
     * Note: {@link LoadTobuyListsCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getTobuyLists(@NonNull final LoadTobuyListsCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedTobuys != null && !mCacheIsDirty) {
            callback.onTobuyListsLoaded(new ArrayList<>(mCachedTobuys.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            callback.onDataNotAvailable();

        } else {
            // Query the local storage if available. If not, query the network.
            mTobuysLocalDataSource.getTobuyLists(new LoadTobuyListsCallback() {
                @Override
                public void onTobuyListsLoaded(List<TobuyList> tobuyLists) {
                    refreshCache(tobuyLists);
                    callback.onTobuyListsLoaded(new ArrayList<>(mCachedTobuys.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }
    }

    @Override
    public void getTobuyList(@NonNull final String tobuyId, @NonNull final GetTobuyListCallback callback) {
        checkNotNull(callback);

        TobuyList cachedTobuyList = getTobuyWithId(tobuyId);

        // Respond immediately with cache if available
        if (cachedTobuyList != null) {
            callback.onTobuyListLoaded(cachedTobuyList);
            return;
        }

        mTobuysLocalDataSource.getTobuyList(tobuyId, new GetTobuyListCallback() {
            @Override
            public void onTobuyListLoaded(TobuyList tobuyList) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedTobuys == null) {
                    mCachedTobuys = new LinkedHashMap<>();
                }
                mCachedTobuys.put(tobuyList.getTobuyListId(), tobuyList);
                callback.onTobuyListLoaded(tobuyList);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveTobuyList(@NonNull TobuyList tobuyList) {
        checkNotNull(tobuyList);
        mTobuysLocalDataSource.saveTobuyList(tobuyList);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTobuys == null) {
            mCachedTobuys = new LinkedHashMap<>();
        }
        mCachedTobuys.put(tobuyList.getTobuyListId(), tobuyList);
    }

    @Override
    public void refreshTobuyLists() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTobuyLists() {
        mTobuysLocalDataSource.deleteAllTobuyLists();

        if (mCachedTobuys == null) {
            mCachedTobuys = new LinkedHashMap<>();
        }
        mCachedTobuys.clear();
    }

    @Override
    public void deleteTobuyList(String tobuyListId) {
        mTobuysLocalDataSource.deleteTobuyList(tobuyListId);

        mCachedTobuys.remove(tobuyListId);
    }

    private void refreshCache(List<TobuyList> tobuyLists) {
        if (mCachedTobuys == null) {
            mCachedTobuys = new LinkedHashMap<>();
        }
        mCachedTobuys.clear();
        for (TobuyList tobuyList : tobuyLists) {
            mCachedTobuys.put(tobuyList.getTobuyListId(), tobuyList);
        }
        mCacheIsDirty = false;
    }

    @Nullable
    private TobuyList getTobuyWithId(String id) {
        if (mCachedTobuys == null || mCachedTobuys.isEmpty()) {
            return null;
        } else {
            return mCachedTobuys.get(id);
        }
    }
}
