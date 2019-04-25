package com.zizohanto.android.tobuy.data.source.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.zizohanto.android.tobuy.data.model.TobuyList;

import java.util.List;

@Dao
public interface TobuyDao {

    /**
     * Select all tobuylists from the tobuylist table.
     *
     * @return all tobuylist.
     */
    @Query("SELECT * FROM TobuyList")
    List<TobuyList> getTobuyLists();

    /**
     * Select a tobuyList by id.
     *
     * @param tobuyListId the tobuyList id.
     * @return the tobuy with tobuyListId.
     */
    @Query("SELECT * FROM TobuyList WHERE mTobuyListId = :tobuyListId")
    TobuyList getTobuyListById(String tobuyListId);

    /**
     * Insert a tobuyList in the database. If the tobuyList already exists, replace it.
     *
     * @param tobuyList the tobuyList to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTobuyList(TobuyList tobuyList);

    /**
     * Update a tobuyList.
     *
     * @param tobuyList tobuyList to be updated
     * @return the number of tobuys updated. This should always be 1.
     */
    @Update
    int updateTobuy(TobuyList tobuyList);

    /**
     * Delete a tobuyList by id.
     *
     * @return the number of tobuyLists deleted. This should always be 1.
     */
    @Query("DELETE FROM TobuyList WHERE mTobuyListId = :tobuyListId")
    int deleteTobuyListById(String tobuyListId);

    /**
     * Delete all tobuyLists.
     */
    @Query("DELETE FROM TobuyList")
    void deleteTobuyLists();

}
