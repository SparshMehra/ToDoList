package com.example.myapplication_2.Util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.myapplication_2.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);
    }

    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;

        try {
            db.beginTransaction();
            String selectQuery = "SELECT * FROM " + TODO_TABLE; // Replace TABLE_NAME with your actual table name
            cur = db.rawQuery(selectQuery, null);

            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        ToDoModel task = new ToDoModel();
                        int idIndex = cur.getColumnIndex(ID);
                        int taskIndex = cur.getColumnIndex(TASK);
                        int statusIndex = cur.getColumnIndex(STATUS);

                        if (idIndex != -1) {
                            task.setId(cur.getInt(idIndex));
                        }
                        if (taskIndex != -1) {
                            task.setTask(cur.getString(taskIndex));
                        }
                        if (statusIndex != -1) {
                            task.setStatus(cur.getInt(statusIndex));
                        }

                        taskList.add(task);
                    } while (cur.moveToNext());
                }
            }

            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            // Handle any exceptions (e.g., log them)
        } finally {
            if (cur != null) {
                cur.close(); // Always close the cursor
            }
            db.endTransaction(); // End the transaction
        }

        return taskList;
    }


    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}
