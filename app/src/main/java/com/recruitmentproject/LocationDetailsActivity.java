package com.recruitmentproject;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class LocationDetailsActivity extends AppCompatActivity implements Callback {

    private ProgressBar progressBar;
    private ImageView imageViewAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.location_details));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        progressBar = (ProgressBar) findViewById(R.id.picassaProgressBar);

        DatabaseHelper locationsDb = new DatabaseHelper(this);
        Cursor res = locationsDb.getLocationData(getIntent().getExtras().getInt("id"));

        if(res.getCount() != 0) {
            while (res.moveToNext()) {

                imageViewAvatar = (ImageView) findViewById(R.id.imageViewDetailsAvatar);
                TextView textViewName = (TextView) findViewById(R.id.textViewDetailsName);
                TextView textViewLongitude = (TextView) findViewById(R.id.textViewDetailsLongituude);
                TextView textViewLatitude = (TextView) findViewById(R.id.textViewDetailsLatitude);

                loadImage(res.getString(2));
                textViewName.setText(res.getString(1));
                textViewLongitude.setText(res.getString(4));
                textViewLatitude.setText(res.getString(3));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private synchronized void loadImage(String uri)
    {
        Picasso.with(this).load(uri)
                .error(R.drawable.ic_crop_original_black_48dp)
                .resize(600, 600)
                .into(imageViewAvatar, this);
    }

    @Override
    public void onSuccess()
    {
        progressBar.setVisibility(View.GONE);
        imageViewAvatar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError()
    {
        progressBar.setVisibility(View.GONE);
        imageViewAvatar.setVisibility(View.VISIBLE);
    }
}
