package com.zizohanto.android.tobuy.util;

import com.zizohanto.android.tobuy.data.model.Item;
import com.zizohanto.android.tobuy.data.model.TobuyList;
import com.zizohanto.android.tobuy.data.model.TobuyListWithItems;

import java.util.ArrayList;
import java.util.List;

public class ModelsConverterUtils {
    public static TobuyList convertToTobuyList(TobuyListWithItems tobuyListWithItems) {
        TobuyList tobuyList = tobuyListWithItems.getTobuyList();
        tobuyList.setItems(tobuyListWithItems.getItems());
        return tobuyList;
    }

    public static TobuyListWithItems convertToTobuyListWithItems(TobuyList tobuyList) {
        TobuyListWithItems tobuyListWithItems = new TobuyListWithItems();
        tobuyListWithItems.setTobuyList(tobuyList);
        tobuyListWithItems.setItems(tobuyList.getItems());
        return tobuyListWithItems;
    }

    public static List<TobuyList> convertToListOfTobuyLists(List<TobuyListWithItems> tobuyListWithItems) {
        List<TobuyList> tobuyLists = new ArrayList<>();
        for (int i = 0; i < tobuyListWithItems.size(); i++) {
            List<Item> items = tobuyListWithItems.get(i).getItems();
            if (items != null) {
                TobuyList tobuyList = tobuyListWithItems.get(i).getTobuyList();
                tobuyList.setItems(items);
                tobuyLists.add(tobuyList);
            }
        }
        return tobuyLists;
    }
}
