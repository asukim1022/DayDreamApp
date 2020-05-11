package com.asukim.daydream;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.replace(android.R.id.content, new SamplePreferenceFragment());
        tx.commit();
    }

    public static class SamplePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
        }
    }
}
