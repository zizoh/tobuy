package com.zizohanto.android.tobuy;

import android.content.Context;

import androidx.annotation.NonNull;

import com.zizohanto.android.tobuy.data.TobuyListsRepository;
import com.zizohanto.android.tobuy.data.source.local.AppDatabase;
import com.zizohanto.android.tobuy.data.source.local.TobuyListLocalDataSource;
import com.zizohanto.android.tobuy.util.AppExecutors;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link com.zizohanto.android.tobuy.data.TobuyListDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static TobuyListsRepository provideTobuyListsRepository(@NonNull Context context) {
        checkNotNull(context);
        AppDatabase database = AppDatabase.getInstance(context);
        return TobuyListsRepository.getInstance(
                TobuyListLocalDataSource.getInstance(new AppExecutors(),
                        database.tobuyDao()));
    }
}
