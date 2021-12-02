package site.yogaji.contactapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import site.yogaji.contactapp.R;

public class SettingFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

    }

}
