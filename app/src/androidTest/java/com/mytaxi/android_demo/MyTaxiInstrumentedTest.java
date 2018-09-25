package com.mytaxi.android_demo;

import android.Manifest;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import com.mytaxi.android_demo.activities.MainActivity;
import com.mytaxi.android_demo.models.Driver;
import com.mytaxi.android_demo.utils.MyTaxiIdlingResource;
import com.mytaxi.android_demo.utils.storage.SharedPrefStorage;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mytaxi.android_demo.util.AndroidHasProperty.hasPropertyAndroid;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class MyTaxiInstrumentedTest {

    private static final String USER_NAME = "crazydog335";
    private static final String PASSWORD = "venture";
    private static final String SEARCH_TEXT = "sa";
    private static final String DRIVER_NAME = "Sarah Scott"; // second driver on the autocomplete suggestion list

    @Rule // create the main activity rule to initialize the application
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Rule // below rule automatically grants permission on location service for the app; thereby disabling the system popup
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before // ensure application always starts in a clean state (from login page)
    public void cleanupStorage() {
        // Done to handle already logged in user issue
        // First approach was handling by try catch
        SharedPrefStorage sharedPrefStorage = new SharedPrefStorage(getInstrumentation().getTargetContext());
        sharedPrefStorage.resetUser();
        mainActivityTestRule.launchActivity(null);
    }

    @Before // register idlingResource; this is to avoid having Thread.sleep as synchronization
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(MyTaxiIdlingResource.getIdlingResource());
    }

    @Test
    public void shouldLoginAndSearchForDriverToCall() {

        // find login page fields, validate and perform actions (enter text and click on button)
        onView(withId(R.id.edt_username)).check(matches(isEnabled())).perform(typeText(USER_NAME));
        onView(withId(R.id.edt_password)).check(matches(isEnabled())).perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).check(matches(isClickable())).perform(click());

        // this is no more required since we are synchronizing using idlingResource
        //TimeUnit.SECONDS.sleep(3); // wait for application to navigate to next page

        // enter text('sa') in search text field and then click on driver searching it through the name ('Sarah Scott')
        onView(withId(R.id.textSearch)).check(matches(isEnabled())).perform(typeText(SEARCH_TEXT), closeSoftKeyboard());
        onDriverSearch(DRIVER_NAME).check(matches(isDisplayed())).perform(click());

        // validate if the correct driver profile is opened and then click on call button on driver profile activity
        onView(withId(R.id.textViewDriverName)).check(matches(isDisplayed())).check(matches(withText(DRIVER_NAME)));
        onView(withId(R.id.fab)).check(matches(isClickable())).perform(click());
    }

    // Returns the driver from DriverAdapter List based on the name that is passed in method parameters
    private static DataInteraction onDriverSearch(final String driverName) {

        // return onData(is(instanceOf(Driver.class))).inRoot(RootMatchers.isPlatformPopup()).atPosition(1);

        // First approach used-Matchers.hasProperty()
        // Gave Exception-- * 'java.lang.NoClassDefFoundError: Failed resolution of: Ljava/beans/Introspector;'
        // mName is the property name under which driver's name is stored in Driver class
        return onData(allOf(is(instanceOf(Driver.class)), hasPropertyAndroid("mName", equalTo(driverName))))
                .inRoot(RootMatchers.isPlatformPopup());
    }

    @After // unregister idlingResource on test completion
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(MyTaxiIdlingResource.getIdlingResource());
    }

}
