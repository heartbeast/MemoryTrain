package com.yao.memorytrain.db;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

// --- Entity Definition ---
@Entity(tableName = "MaxNum")
public class MaxNum {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int level;
    public int times;
    public String date;
}

// --- DAO Definition ---
@Dao
interface MaxNumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MaxNum maxNum);

    @Update
    void update(MaxNum maxNum);

    @Query("SELECT * FROM MaxNum WHERE level = :level LIMIT 1")
    MaxNum getMaxNumByLevel(int level);

    @Query("SELECT * FROM MaxNum")
    List<MaxNum> getAllMaxNums();
}