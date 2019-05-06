package com.zizohanto.android.tobuy;

import android.content.Context;

import androidx.annotation.NonNull;

import com.zizohanto.android.tobuy.data.AppDatabase;
import com.zizohanto.android.tobuy.data.item.ItemsRepository;
import com.zizohanto.android.tobuy.data.item.source.local.ItemLocalDataSource;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListDataSource;
import com.zizohanto.android.tobuy.data.tobuylist.TobuyListsRepository;
import com.zizohanto.android.tobuy.data.tobuylist.source.local.TobuyListLocalDataSource;
import com.zizohanto.android.tobuy.util.AppExecutors;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link TobuyListDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static TobuyListsRepository provideTobuyListsRepository(@NonNull Context context) {
        checkNotNull(context);
        AppDatabase database = AppDatabase.getInstance(context);
        return TobuyListsRepository.getInstance(
                TobuyListLocalDataSource.getInstance(new AppExecutors(),
                        database.tobuyListDao()));
    }

    public static ItemsRepository provideItemsRepository(Context context) {
        checkNotNull(context);
        AppDatabase database = AppDatabase.getInstance(context);
        return ItemsRepository.getInstance(
                ItemLocalDataSource.getInstance(new AppExecutors(),
                        database.itemDao()));
    }
}
