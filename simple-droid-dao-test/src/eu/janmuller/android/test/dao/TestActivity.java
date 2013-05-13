package eu.janmuller.android.test.dao;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity {

    public static final String DB_NAME = "SDD.db";
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //SimpleDroidDao.initialize(this, DB_NAME, 1, null);
    }

}
