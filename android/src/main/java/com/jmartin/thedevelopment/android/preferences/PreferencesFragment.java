package com.jmartin.thedevelopment.android.preferences;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.jmartin.thedevelopment.android.R;
import com.jmartin.thedevelopment.android.model.Constants;

/**
 * Created by jeff on 2014-03-25.
 */
public class PreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference ratePreference = findPreference(getString(R.string.preferences_rate_key));

        if (ratePreference != null) {
            ratePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Constants.TD_GOOGLE_PLAY_URL));
                    startActivity(intent);
                    return false;
                }
            });
        }

        Preference gotoTDPreference = findPreference(getString(R.string.preferences_goto_td_key));

        if (gotoTDPreference != null) {
            gotoTDPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Constants.TD_URL));
                    startActivity(intent);
                    return false;
                }
            });
        }
    }
}
