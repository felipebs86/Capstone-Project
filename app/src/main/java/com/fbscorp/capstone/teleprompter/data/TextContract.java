package com.fbscorp.capstone.teleprompter.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class TextContract {

    public static final String AUTHORITY = "com.fbscorp.capstone.teleprompter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TEXTS = "texts";

    public static final class TextEntry implements BaseColumns {

        public static final String TABLE_NAME = "texts";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEXTS).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;

        public static final String _ID = "_id";

        public static final String TITLE = "title";

        public static final String DESCRIPTION = "description";
    }
}
