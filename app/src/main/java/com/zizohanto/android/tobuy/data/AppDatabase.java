package com.zizohanto.android.tobuy.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.zizohanto.android.tobuy.data.item.source.local.ItemDao;
import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.data.model.TobuyList;
import com.zizohanto.android.tobuy.data.tobuylist.source.local.TobuyListDao;

@Database(entities = {TobuyList.class, Item.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "tobuyapp";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract TobuyListDao tobuyListDao();
    public abstract ItemDao itemDao();

}
