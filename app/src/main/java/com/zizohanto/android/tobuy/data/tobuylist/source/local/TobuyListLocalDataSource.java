package com.zizohanto.android.tobuy.data.tobuylist.source.local;

import androidx.annotation.NonNull;

import com.zizohanto.android.tobuy.data.model.TobuyList;
import com.zizohanto.android.tobuy.data.model.TobuyListWithItems;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListDataSource;
import com.zizohanto.android.tobuy.util.AppExecutors;
import com.zizohanto.android.tobuy.util.ModelsConverterUtils;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TobuyListLocalDataSource implements TobuyListDataSource {

    private static volatile TobuyListLocalDataSource INSTANCE;

    private TobuyListDao mTobuyListDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private TobuyListLocalDataSource(@NonNull AppExecutors appExecutors,
                                     @NonNull TobuyListDao tobuyListDao) {
        mAppExecutors = appExecutors;
        mTobuyListDao = tobuyListDao;
    }

    public static TobuyListLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                       @NonNull TobuyListDao tobuyListDao) {
        if (INSTANCE == null) {
            synchronized (TobuyListLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TobuyListLocalDataSource(appExecutors, tobuyListDao);
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

                List<TobuyListWithItems> tobuyListWithItems = mTobuyListDao.getTobuyLists();

                final List<TobuyList> tobuyLists = ModelsConverterUtils.convertToListOfTobuyLists(tobuyListWithItems);

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
                TobuyListWithItems tobuyListWithItems = mTobuyListDao.getTobuyListWithId(tobuyId);

                final TobuyList tobuyList = ModelsConverterUtils.convertToTobuyList(tobuyListWithItems);
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
//                TobuyList tobuyList = ModelsConverterUtils.convertToTobuyList(tobuyList);
                mTobuyListDao.insertTobuyList(tobuyList);
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
                mTobuyListDao.deleteTobuyLists();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteTobuyList(final String tobuyListId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTobuyListDao.deleteTobuyListWithId(tobuyListId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }
}
