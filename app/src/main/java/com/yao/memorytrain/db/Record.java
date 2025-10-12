package com.yao.memorytrain.db;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import java.util.List;

// --- Entity Definition ---
@Entity(tableName = "record")
public class Record {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date;
    public int allscore;
    public String game1;
    public int score1;
    public String game2;
    public int score2;
    public String game3;
    public int score3;
    public String game4;
    public int score4;
    public String game5;
    public int score5;
}

// --- DAO Definition ---
@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Record record);

    @Query("SELECT * FROM record ORDER BY date DESC")
    List<Record> getAllRecords();

    @Query("SELECT * FROM record WHERE date = :date")
    List<Record> getRecordsByDate(String date);

    @Query("DELETE FROM record")
    void deleteAll();
}