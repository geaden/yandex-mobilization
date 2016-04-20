package com.geaden.android.mobilization.app.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.geaden.android.mobilization.app.BuildConfig;
import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.util.Constants;

/**
 * Constructs about fragment.
 *
 * @author Gennady Denisov
 */
public class AboutFragment extends PreferenceFragment {

    private Preference mVersionPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.about);
        mVersionPreference = findPreference(getString(R.string.pref_key_version));
        findPreference(getString(R.string.pref_key_mobilization))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(Constants.YANDEX_MOBILIZATION_LINK));
                        startActivity(intent);
                        return false;
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mVersionPreference.setSummary(BuildConfig.VERSION_NAME);
    }
}
