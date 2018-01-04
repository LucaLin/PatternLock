package com.example.r30_a.testlayout;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class checkUnit {

    @Rule
    public ActivityTestRule<bookActivity> mActivityTestRule = new ActivityTestRule<>(bookActivity.class);

    @Test
    public void checkUnit() {
        ViewInteraction appCompatButton = onView(allOf(withId(R.id.btnAlert), withText("機器人"),
                                          childAtPosition(childAtPosition(
                                          withClassName(is("android.widget.LinearLayout")),
                                         3),
                                         1),
                                          isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText = onView(allOf(withId(R.id.editText),
                                            childAtPosition(
                                            allOf(withId(R.id.testOTP),
                                            childAtPosition(withClassName(is("android.widget.FrameLayout")),
                                            0)),
                                            1),
                                            isDisplayed()));
        appCompatEditText.perform(replaceText("130137"), closeSoftKeyboard());

        ViewInteraction appCompatImageButton = onView(allOf(withId(R.id.btnOK),
                                                childAtPosition(
                                                childAtPosition(
                                                withId(R.id.testOTP),
                                                3),
                                                1),
                                                isDisplayed()));
        appCompatImageButton.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
