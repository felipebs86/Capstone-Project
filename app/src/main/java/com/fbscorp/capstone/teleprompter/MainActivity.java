package com.fbscorp.capstone.teleprompter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.fbscorp.capstone.teleprompter.adapters.MainAdapter;
import com.fbscorp.capstone.teleprompter.data.Text;
import com.fbscorp.capstone.teleprompter.data.TextContract;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainAdapter.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_CONSTANT = 123;
    private static String TEXTS;
    private ArrayList <Text> texts;

    @BindView(R.id.main_recycler_view)
    RecyclerView rv;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());
        TEXTS = getString(R.string.texts_tag_main_to_detail);
        toolbarLayout.setTitle(getString(R.string.app_name));
        MobileAds.initialize(this, getString(R.string.app_Id));

        getIdlingResource();
        getSupportLoaderManager().initLoader(LOADER_CONSTANT,null,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.add_fab)
    public void addText() {
        Intent intent = new Intent(this,AddActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClickListener(int position) {
        Intent intent = new Intent(this,DetailActivity.class);
        String[] mTexts = new String[3];
        mTexts[0] = texts.get(position).title;
        mTexts[1] = texts.get(position).desc;
        mTexts[2] = String.valueOf(texts.get(position).id);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TeleWidget.class));
        TeleWidget.updateWidget(this, appWidgetManager, appWidgetIds, mTexts);
        intent.putExtra(TEXTS,mTexts);
        startActivity(intent);
    }

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }

        return mIdlingResource;
    }

    private void extractData(Cursor cursor) {
        texts = new ArrayList<>();

        if(cursor.getCount()>0) {
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                return getContentResolver().query(TextContract.TextEntry.CONTENT_URI,
                        null,null,null,null);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader,final Cursor data) {
        Handler handler = new Handler();
        extractData(data);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(true);
                }
            }
        }, 3000);
        final MainAdapter adapter = new MainAdapter(texts,this);
        rv.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.grid_cols),StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
