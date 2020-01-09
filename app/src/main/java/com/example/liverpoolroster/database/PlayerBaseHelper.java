package com.example.liverpoolroster.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.liverpoolroster.Player;
import com.example.liverpoolroster.database.PlayerDbSchema.PlayerTable;

public class PlayerBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "lfcplayers.db";

    public PlayerBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PlayerTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                PlayerTable.Cols.UUID + ", " +
                PlayerTable.Cols.NAME + ", " +
                PlayerTable.Cols.NUMBER + ", " +
                PlayerTable.Cols.POSITION + ", " +
                PlayerTable.Cols.NATIONALITY + ", " +
                PlayerTable.Cols.BIRTHDATE + ", " +
                PlayerTable.Cols.PROFILEURL +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
