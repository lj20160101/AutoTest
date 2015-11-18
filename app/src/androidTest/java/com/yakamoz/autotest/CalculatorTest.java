package com.yakamoz.autotest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by yakamoz on 15-11-14.
 */

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class CalculatorTest extends Common_Method {
    private static final int LAUNCH_TIMEOUT = 5000;

    @Override
    @Before
    public void setUp() throws RemoteException{
        super.setUp();
    }


    @Test
    public void CalculatorTest() throws Exception {
        launchApp(AppName.CALCULATOR);
        mDevice.sleep();

        UiObject2 cls = mDevice.wait(Until.findObject(By.res("com.android.calculator2", "clear")), 500);
        cls.click();

        UiObject2 button7 = mDevice.wait(Until.findObject(By.res("com.android.calculator2", "digit7")), 500);
        button7.click();

        UiObject2 buttonX = mDevice.wait(Until.findObject(By.res("com.android.calculator2", "mul")), 500);
        buttonX.click();

        UiObject2 button6 = mDevice.wait(Until.findObject(By.res("com.android.calculator2", "digit6")), 500);
        button6.click();

        UiObject2 buttonEqual = mDevice.wait(Until.findObject(By.res("com.android.calculator2", "equal")), 500);
        buttonEqual.click();
        UiObject2 output = mDevice.wait(Until.findObject(By.res("com.android.calculator2", "formula")), 500);
        assertEquals(output.getText(), "42");

    }

    @After
    public void tearDown() throws RemoteException{
        super.tearDown();
    }


    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}


