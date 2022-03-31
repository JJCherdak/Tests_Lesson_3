package com.geekbrains.tests.automator

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class BehaviorTest {

    private val uiDevice: UiDevice = UiDevice.getInstance(getInstrumentation())
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val packageName = context.packageName

    @Before
    fun setup() {
        uiDevice.pressHome()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        uiDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), TIMEOUT)
    }

    @Test
    fun test_MainActivityIsStarted() {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        assertNotNull(editText)
    }

    @Test
    fun test_SearchIsPositive() {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        editText.text = "UiAutomator"

        val search = uiDevice.findObject(By.res(packageName, "toSearchButton"))
        search.click()

        val changedText =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )
        Assert.assertEquals(changedText.text.toString(), "Number of results: 668")
    }

    @Test
    fun test_OpenDetailsScreen() {
        val toDetails = uiDevice.findObject(
            By.res(
                packageName,
                "toDetailsActivityButton"
            )
        )
        toDetails.click()
    }

    @Test
    fun test_IncrementButton() {
        val toDetails = uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))
        toDetails.click()
        val changedText = uiDevice.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")),
            TIMEOUT
        )
        val toIncrement = uiDevice.findObject(By.res(packageName, "incrementButton"))
        toIncrement.click()
        assertEquals(changedText.text, "Number of results: 1")
    }

    @Test
    fun test_DecrementButton() {
        val toDetails = uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))
        toDetails.click()
        val changedText = uiDevice.wait(
            Until.findObject(By.res(packageName, "totalCountTextViewDetails")),
            TIMEOUT
        )
        val toIncrement = uiDevice.findObject(By.res(packageName, "decrementButton"))
        toIncrement.click()
        assertEquals(changedText.text, "Number of results: -1")
    }

    @Test
    fun test_ReturnResultTrue(){
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        editText.text = "written"
        val toSearch = uiDevice.findObject(By.res(packageName, "toSearchButton"))
        toSearch.click()



        val changedText =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )
        Assert.assertEquals(changedText.text, "Number of results: 42")
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}