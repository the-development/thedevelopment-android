package com.jmartin.thedevelopment.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jmartin.thedevelopment.android.model.Constants;
import com.jmartin.thedevelopment.android.model.Interview;
import com.jmartin.thedevelopment.android.model.TDWebView;
import com.jmartin.thedevelopment.android.preferences.PreferencesActivity;
import com.squareup.picasso.Picasso;


/**
 * Created by jeff on 2014-03-25.
 */
public class InterviewActivity extends Activity {

    private Interview interview;

    private RelativeLayout heroLayout;
    private LinearLayout infoContainer;

    private ImageView heroImageView;
    private ImageView dpImageView;
    private TextView nameTextView;
    private TextView positionTextView;
    private TextView dateTextView;
    private TDWebView contentWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        interview = (Interview) getIntent().getSerializableExtra(Constants.INTERVIEW_INTENT_TAG);

        heroLayout = (RelativeLayout) findViewById(R.id.hero_container);
        infoContainer = (LinearLayout) findViewById(R.id.info_container);

        heroLayout.setOnClickListener(new ScreenshotClickListener());

        heroImageView = (ImageView) findViewById(R.id.hero);
        dpImageView = (ImageView) findViewById(R.id.dp);
        nameTextView = (TextView) findViewById(R.id.name);
        positionTextView = (TextView) findViewById(R.id.position);
        dateTextView = (TextView) findViewById(R.id.date);
        contentWebView = (TDWebView) findViewById(R.id.interview_webview);

        contentWebView.setOnScrollChangedCallback(new TDWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int horiz, int vert, int oldHoriz, int oldVert) {
                int newHeight = Math.max(getDP(100), heroLayout.getHeight() - vert);
                RelativeLayout.LayoutParams adjustedLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, newHeight);
                heroLayout.setLayoutParams(adjustedLP);
            }
        });

        processInterview();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_preferences:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void processInterview() {
        Picasso.with(this).load(interview.getDpImage()).fit().centerCrop().into(dpImageView);
        nameTextView.setText(interview.getName());
        dateTextView.setText(interview.getPublishedDate());
        positionTextView.setText(interview.getPosition());

        contentWebView.loadData(interview.getContent(), "text/html", Constants.UTF_CHARSET);

        Picasso.with(this).load(interview.getImage()).into(heroImageView);
    }

    private int getDP(int px) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, dm));
    }

    /* Click Listener for screenshot overlay */
    private class ScreenshotClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (infoContainer.getVisibility() == View.VISIBLE)
                infoContainer.setVisibility(View.INVISIBLE);
            else
                infoContainer.setVisibility(View.VISIBLE);
        }
    }
}
