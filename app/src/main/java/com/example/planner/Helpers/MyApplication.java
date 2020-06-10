package com.example.planner.Helpers;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.planner.Realm.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    public SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().name("appdb.realm").build();
        Realm.setDefaultConfiguration(config);

        prefs = getSharedPreferences("Pref", MODE_PRIVATE);


        checkFirstRun();


    }


    public void checkFirstRun() {
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    User user = realm.createObject(User.class);


                }
            });
            prefs.edit().putBoolean("isFirstRun", false).apply();

            prefs.edit().putInt("sound", 1).apply();
            prefs.edit().putInt("vibrate", 1).apply();

        }
    }

}