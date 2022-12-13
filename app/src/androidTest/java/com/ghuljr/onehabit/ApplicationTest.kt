package com.ghuljr.onehabit

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ghuljr.onehabit.di.DaggerTestComponent
import com.ghuljr.onehabit.di.module.AppModule
import com.ghuljr.onehabit.ui.intro.login.LoginActivity
import com.ghuljr.onehabit_tools_android.di.StorageModule
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class ApplicationTest {

    @get:Rule
    val testRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun before() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as App

        DaggerTestComponent.builder()
            .appModule(AppModule(app))
            .storageModule(StorageModule())
            .build()
            .inject(this)
    }

    @Test
    fun testLoginEmptyFields() {
        onView(withId(R.id.sign_in_button)).perform(click())
        onView(withId(R.id.email_layout)).check(matches(hasErrorTextMatcher()))
        onView(withId(R.id.password_layout)).check(matches(hasErrorTextMatcher()))
    }

    private fun hasErrorTextMatcher(): Matcher<View> = object : TypeSafeMatcher<View>() {

        override fun describeTo(description: Description?) { }

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            val error = item.hint ?: return false
            val hint = error.toString()
            return hint.isNotBlank()
        }
    }
}