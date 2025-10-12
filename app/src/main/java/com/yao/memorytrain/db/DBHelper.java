package com.yao.memorytrain.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBHelper {
    private static final String DATABASE_NAME = "game-db";
    private static volatile DBHelper instance;
    private final AppDatabase database;
    // 创建一个固定大小的线程池来在后台执行数据库操作
    private final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    @Database(entities = {Best.class, Record.class, MaxNum.class}, version = 1)
    public abstract static class AppDatabase extends RoomDatabase {
        public abstract BestDao bestDao();
        public abstract RecordDao recordDao();
        public abstract MaxNumDao maxNumDao();
    }

    // 私有构造函数，防止外部直接创建实例
    private DBHelper(Context context) {
        database = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, DATABASE_NAME)
                // 如果需要，可以在这里添加迁移策略
                // .addMigrations(MIGRATION_1_2)
                .build();
    }

    // 获取DBHelper的单例
    public static DBHelper getInstance(final Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }

    // --- Best 表操作 ---

    public void insertBest(final Best best) {
        databaseWriteExecutor.execute(() -> database.bestDao().insert(best));
    }

    public void updateBest(final Best best) {
        databaseWriteExecutor.execute(() -> database.bestDao().update(best));
    }

    // 注意：所有读取操作都应该是同步的，并在后台线程中调用
    // 或者返回 LiveData/Flow，这里为了简单演示，使用同步方法
    // 在实际项目中，推荐使用异步方式（例如，通过回调或LiveData）
    public Best getBest(String gameType, int level) {
        // 这是一个阻塞操作，切勿在主线程调用！
        return database.bestDao().getBest(gameType, level);
    }

    public List<Best> getAllBests() {
        // 这是一个阻塞操作，切勿在主线程调用！
        return database.bestDao().getAllBests();
    }


    // --- Record 表操作 ---

    public void insertRecord(final Record record) {
        databaseWriteExecutor.execute(() -> database.recordDao().insert(record));
    }

    public List<Record> getAllRecords() {
        // 这是一个阻塞操作，切勿在主线程调用！
        return database.recordDao().getAllRecords();
    }

    public List<Record> getRecordsByDate(String date) {
        // 这是一个阻塞操作，切勿在主线程调用！
        return database.recordDao().getRecordsByDate(date);
    }

    public void deleteAllRecords() {
        databaseWriteExecutor.execute(() -> database.recordDao().deleteAll());
    }


    // --- MaxNum 表操作 ---

    public void insertMaxNum(final MaxNum maxNum) {
        databaseWriteExecutor.execute(() -> database.maxNumDao().insert(maxNum));
    }

    public void updateMaxNum(final MaxNum maxNum) {
        databaseWriteExecutor.execute(() -> database.maxNumDao().update(maxNum));
    }

    public MaxNum getMaxNumByLevel(int level) {
        // 这是一个阻塞操作，切勿在主线程调用！
        return database.maxNumDao().getMaxNumByLevel(level);
    }

    public List<MaxNum> getAllMaxNums() {
        // 这是一个阻塞操作，切勿在主线程调用！
        return database.maxNumDao().getAllMaxNums();
    }
}