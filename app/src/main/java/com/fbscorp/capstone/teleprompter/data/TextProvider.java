package com.fbscorp.capstone.teleprompter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TextProvider extends ContentProvider {

    public static final int TEXTS = 100;
    public static final int TEXTS_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TextContract.AUTHORITY, TextContract.PATH_TEXTS, TEXTS);
        uriMatcher.addURI(TextContract.AUTHORITY, TextContract.PATH_TEXTS +"/#", TEXTS_WITH_ID);
        return uriMatcher;
    }

    private DbHelper DbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DbHelper = new DbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = DbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch(match){
            case TEXTS:
                cursor =  db.query(TextContract.TextEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        String type;
        switch (match){
            case TEXTS:
                type = TextContract.TextEntry.CONTENT_DIR_TYPE;
                break;

            case TEXTS_WITH_ID:
                type = TextContract.TextEntry.CONTENT_ITEM_TYPE;
                break;

            default:
                throw new SQLException("Unknown Uri: " + uri);
        }
        return type;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = DbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch ( match ){
            case TEXTS:
                long id = db.insert(TextContract.TextEntry.TABLE_NAME,null,contentValues);
                if( id > 0 ){
                    returnUri = ContentUris.withAppendedId(TextContract.TextEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert rows into "+uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = DbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int textsDeleted;

        switch (match) {
            case TEXTS:
                textsDeleted = db.delete( TextContract.TextEntry.TABLE_NAME, selection, selectionArgs );

                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        TextContract.TextEntry.TABLE_NAME + "'");

                break;

            case TEXTS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                textsDeleted = db.delete(TextContract.TextEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (textsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return textsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,String[] selectionArgs) {
        final SQLiteDatabase db = DbHelper.getWritableDatabase();
        int textsUpdated = 0;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (sUriMatcher.match(uri)) {
            case TEXTS: {
                textsUpdated = db.update(TextContract.TextEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case TEXTS_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                textsUpdated = db.update(TextContract.TextEntry.TABLE_NAME,
                        contentValues,
                        TextContract.TextEntry._ID + " = ?",
                        new String[]{id});
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (textsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return textsUpdated;
    }
}
