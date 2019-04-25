package com.zizohanto.android.tobuy.data.source.local;

import androidx.annotation.NonNull;

import com.zizohanto.android.tobuy.data.TobuyListDataSource;
import com.zizohanto.android.tobuy.data.model.TobuyList;
import com.zizohanto.android.tobuy.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TobuyListLocalDataSource implements TobuyListDataSource {

    private static volatile TobuyListLocalDataSource INSTANCE;

    private TobuyDao mTobuyDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private TobuyListLocalDataSource(@NonNull AppExecutors appExecutors,
                                     @NonNull TobuyDao tasksDao) {
        mAppExecutors = appExecutors;
        mTobuyDao = tasksDao;
    }

    public static TobuyListLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                       @NonNull TobuyDao tobuyDao) {
        if (INSTANCE == null) {
            synchronized (TobuyListLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TobuyListLocalDataSource(appExecutors, tobuyDao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Note: {@link LoadTobuyListsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getTobuyLists(@NonNull final LoadTobuyListsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<TobuyList> tobuyLists = mTobuyDao.getTobuyLists();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tobuyLists.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onTobuyListsLoaded(tobuyLists);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);

    }

    /**
     * Note: {@link GetTobuyListCallback#onDataNotAvailable()} is fired if the {@link TobuyList} isn't
     * found.
     */
    @Override
    public void getTobuyList(@NonNull final String tobuyId, @NonNull final GetTobuyListCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final TobuyList tobuyList = mTobuyDao.getTobuyListById(tobuyId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tobuyList != null) {
                            callback.onTobuyListLoaded(tobuyList);
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
    public void saveTobuyList(@NonNull final TobuyList tobuyList) {
        checkNotNull(tobuyList);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mTobuyDao.insertTobuyList(tobuyList);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);

    }

    @Override
    public void refreshTobuyLists() {

    }

    @Override
    public void deleteAllTobuyLists() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTobuyDao.deleteTobuyLists();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteTobuyList(final String tobuyListId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTobuyDao.deleteTobuyListById(tobuyListId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }
}
