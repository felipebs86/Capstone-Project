package com.fbscorp.capstone.teleprompter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Texts.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "+ TextContract.TextEntry.TABLE_NAME + " (" +
                TextContract.TextEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TextContract.TextEntry.TITLE + " TEXT NOT NULL, " +
                TextContract.TextEntry.DESCRIPTION + " TEXT NOT NULL );";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TextContract.TextEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
