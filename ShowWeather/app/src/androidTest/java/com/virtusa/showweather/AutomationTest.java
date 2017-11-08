package com.virtusa.showweather;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by Arun Balaji Sampath on 11/8/2017.
 */


@RunWith(AndroidJUnit4.class)
@LargeTest
public class AutomationTest {
    private String mUserDataZip;
    private String mResultActualLocation;

    @Rule
    public ActivityTestRule<ShowWeatherActivity> mActivityRule = new ActivityTestRule<ShowWeatherActivity>(ShowWeatherActivity.class);

    @Before
    public void initValidString() {
        // Specify a valid string.
        mUserDataZip = "18052";
        mResultActualLocation = "Whitehall";
    }

    @Test
    public void checkZipCode() {
        // Type text and then press the button.
        Espresso.onView(ViewMatchers.withId(R.id.editTextZipCode)).perform(ViewActions.typeText(mUserDataZip),ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.buttonShowCurrentWeather)).perform(ViewActions.click());

   // Check that the text was changed.
        Espresso.onView(ViewMatchers.withId(R.id.textViewWeatherLocalName)).check(ViewAssertions.matches(ViewMatchers.withText(mResultActualLocation)));
    }


}
