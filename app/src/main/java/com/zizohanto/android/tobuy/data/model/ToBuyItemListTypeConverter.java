package com.zizohanto.android.tobuy.data.model;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ToBuyItemListTypeConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static List<TobuyItem> stringToTobuyItemList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<TobuyItem>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String tobuyListListToString(List<TobuyItem> tobuyListList) {
        return gson.toJson(tobuyListList);
    }
}
