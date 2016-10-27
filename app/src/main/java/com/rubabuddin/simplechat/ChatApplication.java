package com.rubabuddin.simplechat;

import android.app.Application;
import android.content.res.Configuration;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

/**
 * Created by rubab.uddin on 10/26/2016.
 */

public class ChatApplication extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models here
        ParseObject.registerSubclass(Message.class);
        // Existing initialization happens after all classes are registered

        // Required initialization logic here!
        Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("93bd0b59a90d46b1999e484431b83f41")
        .server("https://simplechatclient.herokuapp.com/parse/")
        .addNetworkInterceptor(new ParseLogInterceptor()).build());

    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
