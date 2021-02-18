package com.ddastudio.hifivefootball_android.settings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        /*if (App.getInstance().isAuthorized()) {
            findPreference("auto_login").setEnabled(true);
            findPreference("push_message").setEnabled(true);
        } else {
            findPreference("auto_login").setEnabled(false);
            findPreference("push_message").setEnabled(false);
        }*/

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /*───-----------------------------------────────────────────────────────*/

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

    /*───-----------------------------------────────────────────────────────*/

//    @Override
//    public boolean onPreferenceChange(Preference preference, Object o) {
//        Log.i("hong", "onPreferenceChange");
//        return false;
//    }
//
//    @Override
//    public boolean onPreferenceClick(Preference preference) {
//        Log.i("hong", "onPreferenceClick");
//        return false;
//    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_settings, container, false);
//    }

}
