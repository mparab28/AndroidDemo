package com.mytaxi.android_demo;

import android.Manifest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import com.mytaxi.android_demo.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.acl.Permission;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MyTaxiInstrumentedTest {

    private static final String USER_NAME = "crazydog335";
    private static final String PASSWORD = "venture";
    private static final String SEARCH_STRING = "sa";

    @Rule // create the main activity rule to initialize the application
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule // below rule automatically grants permission on location service for the app; thereby disabling the system popup
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);


    @Test
    public void shouldLoginToAppAndSearchForDriver() throws InterruptedException {
        // find login page fields, validate and perform actions (enter text and click on button)
        onView(withId(R.id.edt_username)).check(matches(isEnabled())).perform(typeText(USER_NAME));
        onView(withId(R.id.edt_password)).check(matches(isEnabled())).perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).check(matches(isClickable())).perform(click());

        TimeUnit.SECONDS.sleep(3); // wait for application to navigate to next page

        onView(withId(R.id.textSearch)).check(matches(isEnabled())).perform(typeText(SEARCH_STRING));
        onView(withId(R.id.searchContainer)).check(matches(withText("Sarah Scott"))).perform(click());

        TimeUnit.SECONDS.sleep(2); // wait to manually validate the successful login
    }

}
