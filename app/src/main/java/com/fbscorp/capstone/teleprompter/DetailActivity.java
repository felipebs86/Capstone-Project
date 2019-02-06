package com.fbscorp.capstone.teleprompter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fbscorp.capstone.teleprompter.data.TextContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {
    private static String POSY;
    private static String POSX;
    private static String[] text;
    private static int scrollY;
    private static int scrollX;
    private static String TEXTS;

    @BindView(R.id.detail_text_body)
    TextView tv_text;

    @BindView(R.id.detail_scroll)
    ScrollView sv;

    @BindView(R.id.detail_main_layout)
    CoordinatorLayout detailLayout;

    @BindView(R.id.adView)
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent startingIntent = getIntent();

        TEXTS = getString(R.string.texts_tag_main_to_detail);
        POSX = getString(R.string.POSX);
        POSY = getString(R.string.POSY);

        if(startingIntent.hasExtra(TEXTS)){
            text = startingIntent.getStringArrayExtra(TEXTS);
        }

        setTitle(text[0].substring(1));
        tv_text.setText(text[1]);
        tv_text.setContentDescription(text[1]);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sv.scrollTo(scrollX,scrollY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.settings ) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @OnClick(R.id.playButton)
    public void play() {
        Intent intent = new Intent(this,PlayActivity.class);
        intent.putExtra(TEXTS, text[1]);
        startActivity(intent);
    }

    @OnClick(R.id.removeButton)
    public void remove() {

        if(text[0].startsWith("#")) {
            Snackbar snackbar = Snackbar.make(detailLayout,getString(R.string.cannot_remove),Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

        else {
            Uri uri = TextContract.TextEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(text[2]).build();
            getContentResolver().delete(uri,null,null);
            onBackPressed();

            Toast.makeText(this, getString(R.string.texts_removed_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(POSY,sv.getScrollY());
        outState.putInt(POSX,sv.getScrollX());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scrollY = savedInstanceState.getInt(POSY);
        scrollX = savedInstanceState.getInt(POSX);
    }
}