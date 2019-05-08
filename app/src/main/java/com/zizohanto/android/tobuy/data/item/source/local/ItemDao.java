package com.zizohanto.android.tobuy.data.item.source.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zizohanto.android.tobuy.data.model.Item;

import java.util.List;

@Dao
public interface ItemDao {

    /**
     * Select all items from the item table.
     *
     * @return all items.
     */
    @Query("SELECT * FROM item WHERE tobuy_list_id = :tobuyListId")
    LiveData<List<Item>> getItemsOfList(String tobuyListId);

    /**
     * Select a item by id.
     *
     * @param itemId the item id.
     * @return the item with itemId.
     */
    @Query("SELECT * FROM item WHERE id = :itemId")
    LiveData<Item> getItemWithId(String itemId);

    /**
     * Insert a item in the database. If the item already exists, replace it.
     *
     * @param item the item to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(Item item);

    /**
     * Update an item.
     *
     * @param item item to be updated
     * @return the number of items updated. This should always be 1.
     */
    @Update
    int updateTobuy(Item item);

    /**
     * Delete an item by id.
     *
     * @return the number of items deleted. This should always be 1.
     */
    @Query("DELETE FROM item WHERE id = :itemId")
    int deleteItemWithId(String itemId);

    /**
     * Delete all items.
     */
    @Query("DELETE FROM item")
    void deleteItems();

}
