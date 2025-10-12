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
@Entity(tableName = "best")
public class Best {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String gametype;
    public int level;
    public int best;
    public String info;
    public String date;
}

// --- DAO Definition ---
@Dao
interface BestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Best best);

    @Update
    void update(Best best);

    @Query("SELECT * FROM best WHERE gametype = :gameType AND level = :level LIMIT 1")
    Best getBest(String gameType, int level);

    @Query("SELECT * FROM best ORDER BY date DESC")
    List<Best> getAllBests();

    @Query("DELETE FROM best WHERE id = :id")
    void deleteById(int id);
}