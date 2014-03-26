package com.jmartin.thedevelopment.android.preferences;

import android.app.Activity;
import android.os.Bundle;

import android.view.MenuItem;

/**
 * Created by jeff on 2014-03-25.
 */
public class PreferencesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
