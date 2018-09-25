package com.mytaxi.android_demo.util;

import android.util.Log;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Field;


/**
 * Custom Matcher Implementation; Code copied from HasPropertyWithValue matcher;
 *
 * Reason to create this custom matcher implementation: Was getting an error
 * 'java.lang.NoClassDefFoundError: Failed resolution of: Ljava/beans/Introspector;'
 *
 * The problem is not technically a hamcrest issue,
 * but an issue with android that it does not implement all of the java.beans classes,
 * such as PropertyUtils which Hamcrest makes use of. Worked around it by creating an android-specific
 * hasProperty matcher that works based on pure reflection instead of using PropertyUtils
 *
 * Please refer to: https://groups.google.com/forum/#!msg/android-test-kit-discuss/5w1bhljm3ag/ht-txuQ3O8YJ
 *
 * @param <T>
 */
public class AndroidHasProperty<T> extends TypeSafeMatcher<T> {
    private String propertyName;
    private Matcher<Object> valueMatcher;

    @SuppressWarnings("unchecked")
    private AndroidHasProperty(String propertyName, Matcher<?> valueMatcher) {
        this.propertyName = propertyName;
        this.valueMatcher = (Matcher<Object>) valueMatcher;
    }

    @Override
    protected boolean matchesSafely(T item) {
        try {
            Field propertyField = item.getClass().getDeclaredField(propertyName);
            propertyField.setAccessible(true);
            Object value = propertyField.get(item);
            return valueMatcher.matches(value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.w("Custom matcher error", "No matching field found with property name: " + propertyName);
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("hasPropertyAndroid(")
                .appendValue(propertyName).appendText(", ")
                .appendDescriptionOf(valueMatcher).appendText(")");
    }

    public static Matcher<Object> hasPropertyAndroid(String propertyName, Matcher<?> valueMatcher) {
        return new AndroidHasProperty<>(propertyName, valueMatcher);
    }
}
