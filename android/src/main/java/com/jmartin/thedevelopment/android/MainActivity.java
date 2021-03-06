package com.jmartin.thedevelopment.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jmartin.thedevelopment.android.adapter.InterviewListAdapter;
import com.jmartin.thedevelopment.android.model.Constants;
import com.jmartin.thedevelopment.android.model.Interview;
import com.jmartin.thedevelopment.android.preferences.PreferencesActivity;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {

    private ArrayList<Interview> interviewArrayList;
    private InterviewListAdapter interviewListAdapter;
    private ListView interviewsListView;

    private MenuItem refreshMenuItem;

    private RelativeLayout heroOverlay;

    private ImageView featuredImageView;
    private TextView featuredName;
    private TextView featuredPosition;
    private TextView featuredDate;

    private Interview featuredInterview;

    private String rssXml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkNetworkAvailability();

        interviewArrayList = new ArrayList<Interview>();

        // Views
        interviewListAdapter = new InterviewListAdapter(this, interviewArrayList);

        heroOverlay = (RelativeLayout) findViewById(R.id.hero_overlay);
        heroOverlay.setOnClickListener(new HeroClickListener());

        featuredImageView = (ImageView) findViewById(R.id.featured_image);
        featuredName = (TextView) findViewById(R.id.featured_name);
        featuredPosition = (TextView) findViewById(R.id.featured_position);
        featuredDate = (TextView) findViewById(R.id.featured_date);

        interviewsListView = (ListView) findViewById(R.id.interviews_listview);
        interviewsListView.setAdapter(interviewListAdapter);
        interviewsListView.setOnItemClickListener(new InterviewListViewOnClickListener());

        loadInterviews(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.SP_FEATURED_INTERVIEW_KEY, new Gson().toJson(featuredInterview));
        outState.putString(Constants.SP_INTERVIEWS_KEY, new Gson().toJson(interviewArrayList));
    }

    @Override
    protected void onPause() {
        super.onPause();
        cacheInterviews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetworkAvailability();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        refreshMenuItem = menu.findItem(R.id.action_refresh);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshInterviews();
                return true;
            case R.id.action_preferences:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshInterviews() {
        if (isNetworkAvailable()) {
            new FetchRssAsyncTask().execute();
        } else {
            Toast.makeText(this, getString(R.string.offline_warning), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkNetworkAvailability() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, getString(R.string.offline_warning), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadInterviews(Bundle savedInstanceState) {
        String interviewsJson;
        String featuredInterviewJson;
        Gson gson = new Gson();

        if (savedInstanceState == null) {
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            interviewsJson = prefs.getString(Constants.SP_INTERVIEWS_KEY, "");
            featuredInterviewJson = prefs.getString(Constants.SP_FEATURED_INTERVIEW_KEY, "");
        } else {
            interviewsJson = savedInstanceState.getString(Constants.SP_INTERVIEWS_KEY, "");
            featuredInterviewJson = savedInstanceState.getString(Constants.SP_FEATURED_INTERVIEW_KEY, "");
        }

        featuredInterview = gson.fromJson(featuredInterviewJson, Interview.class);
        ArrayList<Interview> gsonArrayList = (ArrayList<Interview>) gson.fromJson(interviewsJson, new TypeToken<ArrayList<Interview>>() {}.getType());

        // Only automatically fetch new articles onCreate if the cache is empty.
        if (gsonArrayList == null || gsonArrayList.size() == 0) {
            if (isNetworkAvailable()) {
                new FetchRssAsyncTask().execute();
            }
        } else {
            interviewArrayList.clear();
            interviewArrayList.addAll(gsonArrayList);
            interviewListAdapter.notifyDataSetChanged();
            processFeaturedInterview();
        }
    }

    private void cacheInterviews() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(Constants.SP_FEATURED_INTERVIEW_KEY, new Gson().toJson(featuredInterview));
        editor.putString(Constants.SP_INTERVIEWS_KEY, new Gson().toJson(interviewArrayList));
        editor.commit();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    private void processFeaturedInterview() {
        if (featuredInterview.getName() != null) {
            featuredName.setText(featuredInterview.getName());
            featuredDate.setText(featuredInterview.getPublishedDate());
            featuredPosition.setText(featuredInterview.getPosition());

            Picasso.with(this).load(featuredInterview.getImage()).into(featuredImageView);
        }
    }

    /* Listeners */
    private class InterviewListViewOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            showInterview(interviewListAdapter.getItem(i));
        }
    }

    private class HeroClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (featuredInterview != null)
                showInterview(featuredInterview);
        }
    }

    private void showInterview(Interview interview) {
        // Mark interview as read
        interview.setRead(true);
        interviewListAdapter.notifyDataSetChanged();

        Intent interviewIntent = new Intent(MainActivity.this, InterviewActivity.class);
        interviewIntent.putExtra(Constants.INTERVIEW_INTENT_TAG, interview);
        startActivity(interviewIntent);
    }

    private class FetchRssAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (refreshMenuItem != null) {
                // Animate refresh icon
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView refreshImageView = (ImageView) inflater.inflate(R.layout.refresh_anim_view, null, false);

                Animation refreshAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.refresh_animation);
                refreshAnim.setRepeatCount(Animation.INFINITE);
                refreshImageView.startAnimation(refreshAnim);

                refreshMenuItem.setActionView(refreshImageView);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            InputStream is;
            String result = "";

            try {
                HttpURLConnection connection = client.open(new URL(Constants.RSS_URL));

                is = connection.getInputStream();
                result = readStream(is);

                if (is != null) is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            rssXml = result;
            new ParseRssAsyncTask().execute();
            super.onPostExecute(result);
        }

        private String readStream(InputStream is) throws IOException {
            InputStreamReader isReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isReader);
            String result = reader.readLine();

            while (reader.ready()) {
                result += reader.readLine();
            }

            return result;
        }
    }

    private class ParseRssAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (interviewArrayList != null)
                interviewArrayList.clear();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                xmlPullParserFactory.setNamespaceAware(true);
                XmlPullParser parser = xmlPullParserFactory.newPullParser();

                parser.setInput(new StringReader(rssXml));
                int eventType = parser.getEventType();
                boolean startParse = false;

                String name = "";
                String position = "";
                String date = "";
                String url = "";
                String image = "";
                String dpImage = "";
                String content = "";

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equals(Constants.RSS_TAG_ITEM)) {
                            startParse = true;
                        }

                        if (!startParse) {
                            eventType = parser.next();
                            continue;
                        }

                        if (parser.getName().equals(Constants.RSS_TAG_TITLE)) {
                            name = parser.nextText();
                        } else if (parser.getName().equals(Constants.RSS_TAG_LINK)) {
                            url = parser.nextText();
                        } else if (parser.getName().equals(Constants.RSS_TAG_MEDIA)) {
                            String imgTemp = parser.getAttributeValue(null, Constants.RSS_TAG_MEDIA_ATTR_URL);

                            if (imgTemp.contains(Constants.MEDIACONTENT_SCREENSHOTS_IDENTIFIER)) {
                                image = imgTemp;
                            } else {
                                dpImage = imgTemp;
                            }
                        } else if (parser.getName().equals(Constants.RSS_TAG_PUBDATE)) {
                            SimpleDateFormat rssFormat = new SimpleDateFormat(Constants.RSS_DATE_FORMAT);
                            Date dateObj = rssFormat.parse(parser.nextText());

                            SimpleDateFormat format = new SimpleDateFormat(Constants.INTERVIEW_DATE_FORMAT);
                            date = format.format(dateObj);
                        } else if (parser.getName().equals(Constants.RSS_TAG_CATEGORY)) {
                            position = parser.nextText();
                        } else if (parser.getName().equals(Constants.RSS_TAG_DESC)) {
                            content = Constants.HTML_PREFIX + parser.nextText() + Constants.HTML_SUFFIX;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (parser.getName().equals(Constants.RSS_TAG_ITEM)) {
                            Interview interview = new Interview(name, date, position, url, image, dpImage, content);
                            interviewArrayList.add(interview);
                        }
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("Done parsing RSS", "ArrayList size: " + interviewArrayList.size());

            if (interviewArrayList.size() > 0) {
                featuredInterview = interviewArrayList.get(0);
                interviewArrayList.remove(0);

                interviewListAdapter.notifyDataSetChanged();
                processFeaturedInterview();

                // Stop refresh animation
                if (refreshMenuItem != null && refreshMenuItem.getActionView() != null) {
                    refreshMenuItem.getActionView().clearAnimation();
                    refreshMenuItem.setActionView(null);
                }

                // Scroll listview to top
                interviewsListView.smoothScrollToPosition(0);

                // Toast!
                Toast.makeText(MainActivity.this, getString(R.string.refresh_done), Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(aVoid);
        }
    }
}
