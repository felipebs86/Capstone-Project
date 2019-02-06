package com.fbscorp.capstone.teleprompter;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import com.fbscorp.capstone.teleprompter.data.Text;
import com.fbscorp.capstone.teleprompter.data.TextContract;

import java.util.ArrayList;

import timber.log.Timber;

public class WidgetService extends IntentService {
    ArrayList<Text> texts;
    Text mText;

    public static String ACTION_UPDATE_WIDGET ;
    public WidgetService() {
        super("WidgetService");
        Timber.plant(new Timber.DebugTree());
    }

    public static void setActionUpdateWidget(Context context){
        ACTION_UPDATE_WIDGET = context.getString(R.string.action_update_widget);
        Intent intent = new Intent(context,WidgetService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent!=null) {
            String action = intent.getAction();

            if(action.equals(ACTION_UPDATE_WIDGET)) {
                handleActionUpdateWidget();
            }

        } else {
            handleActionUpdateWidget();
        }
    }



    private void handleActionUpdateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TeleWidget.class));
        new fetch().execute();

        if(texts !=null && texts.size() > 0) {
            mText = texts.get(0);

        } else {
            TeleWidget.updateWidget(this, appWidgetManager, appWidgetIds,null);
            return;
        }


        String[] texts = new String[3];
        texts[0] = mText.title;
        texts[1] = mText.desc;
        texts[2] = String.valueOf(mText.id);
        TeleWidget.updateWidget(this, appWidgetManager, appWidgetIds,texts);
    }

    private void extractData(Cursor cursor) {
        texts = new ArrayList<>();
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            do{
                String title = cursor.getString(cursor.getColumnIndex(TextContract.TextEntry.TITLE));
                Long id = cursor.getLong(cursor.getColumnIndex(TextContract.TextEntry._ID));
                String desc = cursor.getString(cursor.getColumnIndex(TextContract.TextEntry.DESCRIPTION));
                texts.add(new Text(title,desc,id));
                Timber.d(title);
            } while(cursor.moveToNext());
        }
    }

    public class fetch extends AsyncTask<Void,Void,Cursor>{

        @Override
        protected Cursor doInBackground(Void... voids) {
            return getContentResolver().query(TextContract.TextEntry.CONTENT_URI,null,null,null,null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            extractData(cursor);
        }
    }

}
