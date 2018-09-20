package com.mytaxi.android_demo.utils;

import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;

/**
 *
 * Simple wrapper over CountingIdlingResource to let espresso automatically synchronize between navigation
 * This would avoid to use Thread.sleep as synchronization points in test code
 *
 */
public class MyTaxiIdlingResource {

    private static final String RESOURCE_NAME = "GLOBAL_LOADER";
    private static CountingIdlingResource countingIdlingResource = new CountingIdlingResource(RESOURCE_NAME);


    public static void increment() {
        countingIdlingResource.increment();
    }

    public static void decrement() {
        countingIdlingResource.decrement();
    }

    public static boolean isIdleNow() {
        return countingIdlingResource.isIdleNow();
    }

    public static IdlingResource getIdlingResource() {
        return countingIdlingResource;
    }
}
