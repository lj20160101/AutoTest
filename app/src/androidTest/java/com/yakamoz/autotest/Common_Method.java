package com.yakamoz.autotest;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.IAutomationSupport;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
/**
 * Created by yakamoz on 15-11-14.
 */
public class Common_Method  {

    public static String foldername;
    public static final int KEY_POWER = 26;
    public static final int KEY_BACK = 4;
    public static final int KEY_CENTER = 23;
    public static final int KEY_DEL = 67;
    public static final int KEY_ENTER = 66;
    public static final int KEY_HOME = 3;
    public static final int KEY_MENU = 82;
    public static final int KEY_APP_SWITCH = 187;
    public static final int KEY_SETTING = 176;
    // This key is used to exempt online tracing
    public static final String KEY_TRACE = "trace";
    public static final int KEY_VOLUME_DOWN = 25;
    public static final int KEY_VOLUME_MUTE = 164;
    // define static keyevent
    public static final int KEY_VOLUME_UP = 24;
    public static final int KEYCODE_APP_SWITCH = 83;
    public static UiDevice mDevice;
    // Define status code
    public static final int STATUS_ANR = 10;
    public static final int STATUS_FC = 11;
    public static final int STATUS_SCREENSHOT = 17;
    public static final int STATUS_STEP = 15;
    public static final int STATUS_TITLE = 16;
    public static final int STATUS_TOMBSTONES = 12;
    public static String tag = "autotag";
    public static String MTCalltag = "MTCall";
    public static String EndCalltag = "EndCall";
    public static String KillUItag = "KillUI";
    public ArrayList<String> listApps = new ArrayList<String>();
    public static final String PACKAGE_HOME = "com.android.launcher3";

    // define mDevice params, mDevice number etc.
    public static String sim1Receiver = "13811835847";
    public static String sim2Receiver = "13811574295";
    public static String LetvUser1 = "262269342@qq.com";
    public static String LetvPWD1 = "123456";
    public static String mDeviceNumber = "18612620837";
    public static String mDeviceDialNumber = "10086";
    public static String mDeviceContactName = "123";
    public static String mDeviceContactNumber = "13800138000";
    public static String mDeviceSMSReceiver = "10086";
    public static String mDeviceWifiAP = "Letv";
    public static String WifiPWD = "letv4321";
    public static String WifiAP = "TCAUTO-1";
    public static String mDeviceWifiPWD = "987654321";
    public static String email = "letvMTBF1@163.com";
    public static String emailpwd = "letv123";
    public static int TestCaseLoop = 10;
    // Define sdcard root directory
    public final String ROOT_DIR = "/sdcard";


    protected void setUp() throws RemoteException{
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        mDevice = UiDevice.getInstance(instrumentation);
        registerCommonWatcher();
        listApps = initListApps();
        if (!mDevice.isScreenOn()) {
            mDevice.wakeUp();
            sleepInt(1);
        }
        unLockDevice();
        mDevice.pressHome();
    }


    protected void tearDown() throws RemoteException {
        // log(getName() + " tearDown()----------");
        // System.out.println("*********************************");
        press_back(2);
        press_home(1);
        System.out.println("The mDevice type is: " + android.os.Build.MODEL);

        unregisterCommonWatcher();
        callShell("/system/bin/sh /data/local/tmp/PullLog.sh " + foldername);
        Log.i(tag, "==========================case end time is :"
                + getCurrentTime());
        Log.i(KillUItag, "KillUiautomator");
        if(callShell("getprop ro.product.model").contains("X6")){
            sleepInt(100);
        }
        if(callShell("getprop ro.product.model").contains("Le 1")){
            sleepInt(100);
        }
    }

    public static String callShell(String shellString) {
        try {
            Process process = Runtime.getRuntime().exec(shellString);
            int exitValue = process.waitFor();
            System.out.println("================process: " + shellString
                    + "return!!!================");
            if (0 != exitValue) {
                System.out.println("call shell failed. error code is :"
                        + exitValue);
            }
            InputStream in = process.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int count = -1;
            while ((count = in.read(data, 0, 1024)) != -1)
                outStream.write(data, 0, count);
            data = null;
            System.out
                    .println("================Call shell function return!!!================");
            return new String(outStream.toByteArray(), "UTF-8");
        } catch (Throwable e) {
            System.out.println("call shell failed!!");
            e.printStackTrace();
            return null;
        }

    }

    public static String getCurrentTime() {

        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int day = ca.get(Calendar.DATE);
        int minute = ca.get(Calendar.MINUTE);
        int hour = ca.get(Calendar.HOUR);
        int second = ca.get(Calendar.SECOND);
        String currentTime = (String.valueOf(year) + "-"
                + String.valueOf(month + 1) + "-" + String.valueOf(day) + "-"
                + String.valueOf(hour) + "-" + String.valueOf(minute) + "-" + String
                .valueOf(second));
        return currentTime;

    }

    public void addStep(String comment) {
        send_status(STATUS_STEP, "caseStep", comment);
    }

    // create case folder to save logs and screenshot
    public void createDir(String dir) {
        File folder = new File(dir);
        if (folder.exists()) {
            System.out.println("The folder " + dir + " has existed!");
            System.out.println("current_case_root_folder=" + foldername);
            // createLogFile(dir);
            return;
        }
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        // create folder
        if (folder.mkdirs()) {
            System.out.println("Create folder " + dir + " success!");
            System.out.println("current_case_root_folder=" + foldername);
            // createLogFile(dir);
        } else {
            System.out.println("Create folder " + dir + " fail!");
        }
    }

    public boolean gotoHomeScreen() throws Exception,
            RemoteException {
        if (!mDevice.isScreenOn()) {
            System.out.println("检测到屏幕处于关闭状态");
            mDevice.wakeUp();
            sleepInt(2);
            verify("屏幕没有被点亮", mDevice.isScreenOn());
        }
        mDevice.pressHome();
        return false;
    }

    public void openAPM() throws Exception {
        launchApp(AppName.SETTING);
        sleepInt(2);
        UiObject APM1= new UiObject(new UiSelector().className("android.widget.LinearLayout").index(1))
                .getChild(new UiSelector().className("android.widget.RelativeLayout").index(1))
                .getChild(new UiSelector().resourceId("android:id/title").index(0));
        UiObject APM2 = new UiObject(new UiSelector().className("android.widget.LinearLayout").index(2))
                .getChild(new UiSelector().className("android.widget.RelativeLayout").index(1))
                .getChild(new UiSelector().resourceId("android:id/title").index(0));
        if(APM1.getText().equals("飞行模式")){
            UiObject switchWidget1= new UiObject(new UiSelector().className("android.widget.LinearLayout").index(1))
                    .getChild(new UiSelector().resourceId("android:id/widget_frame").index(2))
                    .getChild(new UiSelector().resourceId("com.android.settings:id/switchWidget"));
            if (switchWidget1.isChecked()) {
                return;
            } else {
                switchWidget1.click();
                sleepInt(5);
                verify("Can't open Airplan mode.", switchWidget1.isChecked());
            }

        }else if(APM2.getText().equals("飞行模式")){
            UiObject switchWidget2= new UiObject(new UiSelector().className("android.widget.LinearLayout").index(2))
                    .getChild(new UiSelector().resourceId("android:id/widget_frame").index(2))
                    .getChild(new UiSelector().resourceId("com.android.settings:id/switchWidget"));
            if (switchWidget2.isChecked()) {
                return;
            } else {
                switchWidget2.click();
                sleepInt(5);
                verify("Can't open Airplan mode.", switchWidget2.isChecked());
            }
        }
    }

    public void closeAPM() throws UiObjectNotFoundException {
        launchApp(AppName.SETTING);
        sleepInt(2);
        UiObject APM1= new UiObject(new UiSelector().className("android.widget.LinearLayout").index(1))
                .getChild(new UiSelector().className("android.widget.RelativeLayout").index(1))
                .getChild(new UiSelector().resourceId("android:id/title").index(0));
        UiObject APM2 = new UiObject(new UiSelector().className("android.widget.LinearLayout").index(2))
                .getChild(new UiSelector().className("android.widget.RelativeLayout").index(1))
                .getChild(new UiSelector().resourceId("android:id/title").index(0));
        if(APM1.getText().equals("飞行模式")){
            UiObject switchWidget1= new UiObject(new UiSelector().className("android.widget.LinearLayout").index(1))
                    .getChild(new UiSelector().resourceId("android:id/widget_frame").index(2))
                    .getChild(new UiSelector().resourceId("com.android.settings:id/switchWidget"));
            if (!switchWidget1.isChecked()) {
                return;
            } else {
                switchWidget1.click();
                sleepInt(5);
                verify("Can't close Airplan mode.", !switchWidget1.isChecked());
            }

        }else if(APM2.getText().equals("飞行模式")){
            UiObject switchWidget2= new UiObject(new UiSelector().className("android.widget.LinearLayout").index(2))
                    .getChild(new UiSelector().resourceId("android:id/widget_frame").index(2))
                    .getChild(new UiSelector().resourceId("com.android.settings:id/switchWidget"));
            if (!switchWidget2.isChecked()) {
                return;
            } else {
                switchWidget2.click();
                sleepInt(5);
                verify("Can't close Airplan mode.", !switchWidget2.isChecked());
            }
        }
    }

    public boolean launchAppByPackage(String packageName)
            throws UiObjectNotFoundException {
        press_back(3);
        press_home(1);
        if (packageName == "" || packageName == null) {
            System.out.println("the App name can't be null!!!");
            return false;
        } else {
            String tag = callShell("am start -n " + packageName);
        }
        sleepInt(3);
        return false;
    }

    public boolean launchApp(String appName) throws UiObjectNotFoundException {
        return launchApp(appName, true);
    }

    public boolean launchApp(String appName, boolean flag)
            throws UiObjectNotFoundException {
        // verify the app is not null
        if (appName == "" || appName == null) {
            System.out.println("the App name can't be null!!!");
            return false;
        }
        // verify if the app is a valid App
        if (!(listApps.contains(appName))) {
            screenShot();
            if (flag) {
                fail("The app " + appName
                        + " is not a valid App, please check the App name.");
            }
            System.out.println("The app " + appName
                    + "is not a valid App, please check the App name.");
            return false;
        }
        press_back(2);
        press_home(1);
        verify("未能返回桌面", mDevice.getCurrentPackageName().equals(PACKAGE_HOME));
        UiObject systemtool = new UiObject(
                new UiSelector().textContains("系统工具"));
        UiObject Google = new UiObject(
                new UiSelector().textContains("Google"));
//		findAppInDesktop(systemtool);
//		verify("未发现系统工具应用", systemtool.exists());
//		systemtool.clickAndWaitForNewWindow();
//		sleepInt(2);
        UiObject app = new UiObject(new UiSelector().textContains(appName));
//		if (app.exists()) {
//			app.clickAndWaitForNewWindow();
//			sleepInt(3);
//			return true;
//		} else {
//			press_back(1);
//			press_home(1);
//			findAppInDesktop(app);
//		}
//		verify("未能在发现应用", app.exists());
//		app.clickAndWaitForNewWindow();
//		sleepInt(1);
////		return true;
        findAppInDesktop(app);
        if(app.exists()){
            app.clickAndWaitForNewWindow();
            sleepInt(1);
        }else{
            press_back(1);
            press_home(1);
            findAppInDesktop(systemtool);
            if(systemtool.exists()){
                systemtool.clickAndWaitForNewWindow();
                sleepInt(2);
                if (app.exists()) {
                    app.clickAndWaitForNewWindow();
                    sleepInt(3);
                }else{
//						verify("未能在发现应用", app.exists());
                    press_back(2);
                    press_home(1);
                    findAppInDesktop(Google);
                    if(Google.exists()){
                        Google.clickAndWaitForNewWindow();
                        sleepInt(2);
                        if (app.exists()) {
                            app.clickAndWaitForNewWindow();
                            sleepInt(3);
                        }else{
                            verify("未能在发现应用", app.exists());
                        }
                    }else{
                        verify("未能在发现应用", app.exists());
                    }
                }
            }
//			else{
//				verify("未能在发现应用", app.exists());
//			}



        }
        return true;
    }

    public void findAppInDesktop(UiObject obj) throws UiObjectNotFoundException {
        for (int i = 0; i < 4; i++) {
            if (!obj.exists()) {
                mDevice.swipe((int) (mDevice.getDisplayWidth() * 0.8),
                        (int) (mDevice.getDisplayHeight() * 0.2),
                        (int) (mDevice.getDisplayWidth() * 0.01),
                        (int) (mDevice.getDisplayHeight() * 0.2), 50);
                sleepInt(1);
            } else {
                break;
            }

        }
    }

    public void SIM1dataUse() throws UiObjectNotFoundException {
        launchApp(AppName.SETTING);
        sleepInt(2);
        UiObject SIMCards = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("双卡管理"));
        verify("SIM卡管理没有找到", SIMCards.exists());
        SIMCards.clickAndWaitForNewWindow();
        sleepInt(2);
        UiObject cellularData = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("移动数据网络"));
        verify("移动数据网络没有找到", cellularData.exists());
        cellularData.clickAndWaitForNewWindow();
        sleepInt(1);
        UiObject choseSim1 = new UiObject(new UiSelector().className(
                "android.widget.LinearLayout").index(0))
                .getChild(new UiSelector().className(
                        "android.widget.RadioButton").resourceId(
                        "com.android.settings:id/default_set"));
        verify(choseSim1.exists());
        choseSim1.click();
        sleepInt(3);
        UiObject CMCC01 = new UiObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("android:id/summary").textContains("01"));
        sleepInt(15);
        for (int i = 1; i <= 20; i++) {
            sleepInt(1);
            if (CMCC01.exists()) {
                break;
            }
        }
        verify("SIM卡01不存在", CMCC01.exists());
        press_back(3);
    }

    public void SIM2dataUse() throws UiObjectNotFoundException {
        launchApp(AppName.SETTING);
        sleepInt(2);
        UiObject SIMCards = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("双卡管理"));
        verify("SIM卡管理没有找到", SIMCards.exists());
        SIMCards.clickAndWaitForNewWindow();
        sleepInt(1);
        UiObject cellularData = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("移动数据网络"));

        verify("移动数据网络没有找到", cellularData.exists());
        cellularData.clickAndWaitForNewWindow();
        sleepInt(3);
        UiObject choseSim2 = new UiObject(new UiSelector().className(
                "android.widget.LinearLayout").index(1)).getChild(
                new UiSelector().className("android.widget.LinearLayout")
                        .index(1)).getChild(
                new UiSelector().className("android.widget.RadioButton")
                        .resourceId("com.android.settings:id/default_set"));
        verify(choseSim2.exists());
        choseSim2.click();
        sleepInt(3);
        UiObject CMCC02 = new UiObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("android:id/summary").textContains("02"));
        sleepInt(15);
        for (int i = 1; i <= 40; i++) {
            sleepInt(1);
            if (CMCC02.exists()) {
                break;
            }
        }
        verify("sim2卡不存在", CMCC02.exists());
        press_back(3);
    }

    public void switch3G() throws UiObjectNotFoundException {
        launchApp(AppName.SETTING);
        sleepInt(2);
        UiObject CellularNetworks = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("移动网络"));
        verify("移动网络不存在", CellularNetworks.exists());
        CellularNetworks.clickAndWaitForNewWindow();
        sleepInt(1);
        UiObject networkMode = new UiObject(new UiSelector().text("网络模式")
                .className("android.widget.TextView"));
        verify("网络模式不存在", networkMode.exists());
        networkMode.clickAndWaitForNewWindow();
        sleepInt(2);
        UiObject network3G = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("3G"));
        verify("3G网络不存在", network3G.exists());
        network3G.clickAndWaitForNewWindow();
        sleepInt(15);
        UiObject makesure3G = new UiObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("android:id/message").text("3G"));
        verify("3G模式选择失败", makesure3G.exists());
        sleepInt(2);
        press_back(3);
    }

    public void switch4G() throws UiObjectNotFoundException {
        launchApp(AppName.SETTING);
        sleepInt(3);
        UiObject CellularNetworks = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("移动网络"));
        verify("移动网络不存在", CellularNetworks.exists());
        CellularNetworks.clickAndWaitForNewWindow();
        sleepInt(2);
        UiObject networkMode = new UiObject(new UiSelector().text("网络模式")
                .className("android.widget.TextView"));

        verify("网络模式不存在", networkMode.exists());
        networkMode.clickAndWaitForNewWindow();
        sleepInt(2);
        UiObject network4G = new UiObject(new UiSelector().className(
                "android.widget.TextView").textContains("4G"));
        verify("选择4G不成功", network4G.exists());
        network4G.clickAndWaitForNewWindow();
        sleepInt(15);
        UiObject makesure4G = new UiObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("android:id/message").textContains("4G"));
        verify(makesure4G.exists());
        sleepInt(2);
        press_back(3);
    }

    public void connectData() throws UiObjectNotFoundException {
        launchApp(AppName.SETTING);
        sleepInt(3);
        UiObject dataUsage = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("移动网络"));
        for (int i = 0; i < 3; i++) {
            if (!dataUsage.exists()) {
                mDevice.swipe((int) (mDevice.getDisplayWidth() * 0.48),
                        (int) (mDevice.getDisplayHeight() * 0.078),
                        (int) (mDevice.getDisplayWidth() * 0.48),
                        (int) (mDevice.getDisplayHeight() * 0.468), 50);

                // mDevice.swipe(700, 200, 700, 1200, 30);
                sleepInt(2);
            } else {
                break;
            }
        }
        verify("Can't find data usage button.", dataUsage.exists());
        dataUsage.click();
        sleepInt(4);
        UiObject switchWidget = new UiObject(new UiSelector().className(
                "android.widget.LinearLayout").index(0)).getChild(
                new UiSelector().className("android.widget.LinearLayout")
                        .index(1)).getChild(
                new UiSelector().className("com.letv.leui.widget.LeSwitch"));
        verify("Can't find switchWidget button.", switchWidget.exists());
        if (switchWidget.isChecked()) {
            return;
        } else {
            switchWidget.click();
            sleepInt(2);
            verify("Can't enabled data.", switchWidget.isChecked());
        }
        sleepInt(1);
        press_back(4);
    }

    public void disconnectData() throws UiObjectNotFoundException {
        launchApp(AppName.SETTING);
        sleepInt(2);
        UiObject dataUsage = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("移动网络"));
        verify("Can't find data usage button.", dataUsage.exists());
        dataUsage.click();
        sleepInt(2);
        sleepInt(2);
        UiObject switchWidget = new UiObject(new UiSelector().className(
                "android.widget.LinearLayout").index(0)).getChild(
                new UiSelector().className("android.widget.LinearLayout")
                        .index(1)).getChild(
                new UiSelector().className("com.letv.leui.widget.LeSwitch"));
        verify("Can't find switchWidget button.", switchWidget.exists());
        if (!switchWidget.isChecked()) {
            return;
        } else {
            switchWidget.click();
            sleepInt(2);
            verify("Can't disabled data.", !switchWidget.isChecked());
        }
        sleepInt(1);
        press_back(4);
    }

    public void press_back(int times) {
        press_keyevent(times, KEY_BACK);
        System.out.println("press back key " + times + " times");
    }

    public void press_center(int times) {
        press_keyevent(times, KEY_CENTER);
        System.out.println("press center key "+times+" times");
    }

    public void press_home(int times) {
        press_keyevent(times, KEY_HOME);
        System.out.println("press home key "+times+" times");
    }


    public void press_keyevent(int times, int keycode) {
        if (times < 1)
            return;
        for (int i = times; i > 0; i--) {
            mDevice.pressKeyCode(keycode);
            sleepInt(1);
        }
    }

    public void press_menu(int times) {
        press_keyevent(times, KEY_APP_SWITCH);
        System.out.println("press menu  key " + times + " times");

    }

    public void swipe_screen(int startX, int startY, int endX, int endY,
                             int swipeSpeed) {
        mDevice.swipe(startX, startY, endX, endY, swipeSpeed);
        sleepInt(1);
    }

    public void screenShot() {
        String filename = System.currentTimeMillis() + ".png";
        String command = "screencap " + ROOT_DIR + File.separator
                + "AutoSmoke_UI30" + File.separator + foldername
                + File.separator + filename;
        send_status(STATUS_SCREENSHOT, "screencap", filename);
        callShell(command);
    }

    // send status to Console
    public void send_status(int code, String key, String value) {
        Bundle b = new Bundle();
        b.putString(key, value);
        getAutomationSupport().sendStatus(code, b);
    }

    private IAutomationSupport getAutomationSupport() {
        return null;
    }

    public void sleepInt(int sc) {
        System.out.println("wait "+sc+" second");
    }

    public void verify(boolean b) {
        if (!b) {
            screenShot();
            fail();
        }
    }

    public void verify(String msg, boolean b) {
        if (msg == "") {
            verify(b);
        } else if (!b) {
            screenShot();
            fail(msg);
        }
    }

    public void unLockDevice() throws RemoteException {
        mDevice.swipe((int) (mDevice.getDisplayWidth() * 0.37),
                (int) (mDevice.getDisplayHeight() * 0.72),
                (int) (mDevice.getDisplayWidth() * 0.37),
                (int) (mDevice.getDisplayHeight() * 0.117), 50);
        mDevice.swipe((int) (mDevice.getDisplayWidth() * 0.37),
                (int) (mDevice.getDisplayHeight() * 0.72),
                (int) (mDevice.getDisplayWidth() * 0.37),
                (int) (mDevice.getDisplayHeight() * 0.117), 50);
        mDevice.swipe((int) (mDevice.getDisplayWidth() * 0.37),
                (int) (mDevice.getDisplayHeight() * 0.72),
                (int) (mDevice.getDisplayWidth() * 0.37),
                (int) (mDevice.getDisplayHeight() * 0.117), 50);

    }

    // check the ANR and force close
    private void registerCommonWatcher() {
        UiWatcher anrWatcher = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject anrWindows = new UiObject(new UiSelector().text("无响应"));
                if (anrWindows.exists()){
                   screenShot();
                    try {

                        UiObject ok2 = new UiObject(new UiSelector().text("确定"));
                        if (ok2.exists()) {
                            ok2.click();
                        }

                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                        Log.e("UIautomator",
                                "OK button in anr window is gone. Is it recoverd?");
                    } finally {
                        send_status(STATUS_ANR, "ANR",
                                "Detected ANR when running case");
                        // callShell("mv /data/anr " + ROOT_DIR + File.separator
                        // + "AutoSmoke_UI30" + File.separator
                        // + foldername + File.separator + "anr");
                        fail("ANR occurred");
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        UiWatcher fcWatcher = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject fcWindows1 = new UiObject(new UiSelector().className(
                        "android.widget.TextView").textContains("has stopped."));

                UiObject fcWindows2 = new UiObject(new UiSelector().className(
                        "android.widget.TextView").textContains("已停止运行"));
                if (fcWindows1.exists() || fcWindows2.exists()) {
                    screenShot();
                    try {
                        UiObject ok1 = new UiObject(new UiSelector().className(
                                "android.widget.Button").text("OK"));
                        UiObject ok2 = new UiObject(new UiSelector().className(
                                "android.widget.Button").text("确定"));
                        if (ok1.exists()) {
                            ok1.click();
                        } else if (ok2.exists()) {
                            ok2.click();
                        }
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                        Log.e("UIautomator",
                                "OK button in FC window is gone. Is it recoverd?");
                    } finally {
                        send_status(STATUS_FC, "FC",
                                "Detected FC when running case");
                        fail("FC occurred, the case is stoped!");
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        UiWatcher termsWatcher = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject termsWindows = new UiObject(new UiSelector()
                        .className("android.widget.TextView").textContains(
                                "申明和条款"));
                if (termsWindows.exists()) {
                    try {
                        new UiObject(new UiSelector().className(
                                "android.widget.Button").text("同意并继续")).click();
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };


        UiWatcher accessLocation = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject acceWindows = new UiObject(new UiSelector()
                        .packageName("android")
                        .resourceId(
                                "android:id/le_bottomsheet_switchbutton_text")
                        .className("android.widget.TextView").index(1));
                if (acceWindows.exists()) {
                    try {
                        new UiObject(new UiSelector().className(
                                "android.widget.Button").text("允许")).click();
                        sleepInt(2);
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        // Directions for use 使用说明
        UiWatcher DirectionsWatcher = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject DirectionsWindows = new UiObject(new UiSelector()
                        .className("android.widget.TextView").textContains(
                                "使用须知"));
                UiObject NoPrompt = new UiObject(new UiSelector().className(
                        "android.widget.CheckBox").textContains("不再提示"));
                if (DirectionsWindows.exists()) {
                    try {
                        if (!NoPrompt.isChecked()) {
                            NoPrompt.click();
                            sleepInt(2);
                        }
                        new UiObject(
                                new UiSelector()
                                        .className("android.widget.Button")
                                        .resourceId(
                                                "cn.wps.moffice_eng:id/dialog_button_positive")
                                        .text("同意")).click();
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        UiWatcher TagLogWatcher = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject TagLogWindows = new UiObject(
                        new UiSelector()
                                .className("android.widget.TextView")
                                .textContains(
                                        "All log tools are stopped! Would you like to start all log tools"));
                if (TagLogWindows.exists()) {
                    try {
                        new UiObject(new UiSelector().className(
                                "android.widget.Button").text("确定")).click();
                        sleepInt(2);
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    } finally {

                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        UiWatcher ChangeDataUseWatcher = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject ChangeDataUseWindows = new UiObject(new UiSelector()
                        .className("android.widget.TextView").textContains(
                                "无线网络不可用，是否尝试连接移动网络"));
                if (ChangeDataUseWindows.exists()) {
                    try {
                        new UiObject(new UiSelector().className(
                                "android.widget.Button").text("确定")).click();
                        sleepInt(2);
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    } finally {

                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        UiWatcher localWatcher = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject localWindows = new UiObject(new UiSelector()
                        .className("android.widget.TextView").textContains(
                                "地理位置授权"));
                if (localWindows.exists()) {
                    try {
                        UiObject neverTip = new UiObject(new UiSelector()
                                .className("com.letv.leui.widget.LeCheckBox")
                                .text("不再提示"));
                        if (!neverTip.isChecked()) {
                            neverTip.click();
                            sleepInt(2);
                            new UiObject(new UiSelector().className(
                                    "android.widget.Button").text("允许"))
                                    .click();
                        } else {
                            new UiObject(new UiSelector().className(
                                    "android.widget.Button").text("允许"))
                                    .click();
                        }
                        sleepInt(8);
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        UiWatcher letvCountWatcher = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject letvCountWindows = new UiObject(
                        new UiSelector().textContains("暂不"));
                if (letvCountWindows.exists()) {
                    try {
                        letvCountWindows.click();
                        sleepInt(2);
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        UiWatcher MapWatcher = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject quitMapWindows = new UiObject(
                        new UiSelector().textContains("确认要退出百度地图"));
                UiObject yes = new UiObject(
                        new UiSelector().textContains("确定"));
                if (quitMapWindows.exists()) {
                    try {
                        yes.click();
                        sleepInt(2);
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        UiWatcher dataSwitchOffWatcher = new UiWatcher() {
            public boolean checkForCondition() {
                UiObject dataSwitchOffWindows= new UiObject(new UiSelector()
                        .className("android.widget.TextView").textContains(
                                "移动网络关闭"));
                if (dataSwitchOffWindows.exists()) {
                    try {
                        new UiObject(new UiSelector().className(
                                "android.widget.Button").text("打开")).click();
                        sleepInt(2);
                    } catch (UiObjectNotFoundException e) {
                        e.printStackTrace();
                    } finally {

                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        getUiDevice().registerWatcher("anrWatcher", anrWatcher);
        getUiDevice().registerWatcher("fcWatcher", fcWatcher);
        getUiDevice().registerWatcher("termsWatcher", termsWatcher);
        getUiDevice().registerWatcher("TagLogWatcher", TagLogWatcher);
        getUiDevice().registerWatcher("dataSwitchOffWatcher", dataSwitchOffWatcher);
        getUiDevice().registerWatcher("accessLocation", accessLocation);
        getUiDevice().registerWatcher("DirectionsWatcher", DirectionsWatcher);
        getUiDevice().registerWatcher("ChangeDataUseWatcher",
                ChangeDataUseWatcher);
        getUiDevice().registerWatcher("localWatcher", localWatcher);
        getUiDevice().registerWatcher("letvCountWatcher", letvCountWatcher);
        getUiDevice().registerWatcher("MapWatcher", MapWatcher);
    }

    // remove anr and force close uiWatchers
    private void unregisterCommonWatcher() {
        getUiDevice().removeWatcher("anrWatcher");
        getUiDevice().removeWatcher("fcWatcher");
        getUiDevice().removeWatcher("termsWatcher");
        getUiDevice().removeWatcher("dataSwitchOffWatcher");
        getUiDevice().removeWatcher("TagLogWatcher");
        getUiDevice().removeWatcher("accessLocation");
        getUiDevice().removeWatcher("DirectionsWatcher");
        getUiDevice().removeWatcher("ChangeDataUseWatcher");
        getUiDevice().removeWatcher("localWatcher");
        getUiDevice().removeWatcher("letvCountWatcher");
        getUiDevice().removeWatcher("MapWatcher");
    }

    private UiDevice getUiDevice() {
        return mDevice;
    }

    public void openWifi() throws UiObjectNotFoundException {

        launchApp(AppName.SETTING);
        sleepInt(4);
        UiObject wlan = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("WLAN"));
        verify("Can't find Wi-Fi button.", wlan.exists());
        wlan.click();
        sleepInt(2);
        UiObject switchWidget = new UiObject(new UiSelector().className(
                "com.letv.leui.widget.LeSwitch").resourceId(
                "com.android.settings:id/switch_widget"));
        if (switchWidget.isChecked()) {
            return;
        } else {
            switchWidget.click();
            sleepInt(10);
            verify("Can't open wifi.", switchWidget.isChecked());
        }
    }

    public void closeWifi() throws UiObjectNotFoundException {
        launchApp(AppName.SETTING);
        sleepInt(4);
        UiObject wlan = new UiObject(new UiSelector().className(
                "android.widget.TextView").text("WLAN"));
        verify("Can't find Wi-fi button.", wlan.exists());
        wlan.click();
        sleepInt(2);
        UiObject switchWidget = new UiObject(new UiSelector().className(
                "com.letv.leui.widget.LeSwitch").resourceId(
                "com.android.settings:id/switch_widget"));
        if (switchWidget.isChecked()) {
            switchWidget.click();
            sleepInt(5);
            verify("Can't close wifi.", !(switchWidget.isChecked()));
        }
    }

    public void connectWifi() throws UiObjectNotFoundException {
        openWifi();
        sleepInt(2);
        UiObject add = new UiObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("android:id/title").text("添加网络"));
        verify("添加网络按钮不存在", add.exists());
        add.click();
        sleepInt(1);
        UiObject APname2 = new UiObject(new UiSelector()
                .className("android.widget.EditText")
                .resourceId("android:id/edit").text("网络名称"));
        verify("AP名称不存在", APname2.exists());
        APname2.click();
        sleepInt(2);
        APname2.setText(WifiAP);
        sleepInt(2);
        UiObject APsecurity = new UiObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("android:id/title").text("安全性"));
        verify("安全性不存在", APsecurity.exists());
        APsecurity.click();
        sleepInt(2);
        UiObject WPA2 = new UiObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("android:id/le_bottomsheet_text")
                .text("WPA/WPA2 PSK"));
        verify("WPA/WPA2 PSK不存在", WPA2.exists());
        WPA2.click();
        sleepInt(2);
        UiObject WPA = new UiObject(new UiSelector()
                .className("android.widget.TextView").text("WPA/WPA2 PSK")
                .index(1));
        verify("WPA/WPA2 PSK选择不成功", WPA.exists());
        sleepInt(2);
        UiObject APpw = new UiObject(new UiSelector()
                .className("android.widget.TextView")
                .resourceId("android:id/title").text("密码"));
        verify("密码不存在", APpw.exists());
        APpw.click();
        sleepInt(2);
        APpw.setText(WifiPWD);
        UiObject OK = new UiObject(new UiSelector()
                .className("android.view.View")
                .index(0)
                .childSelector(
                        new UiSelector().className(
                                "android.widget.LinearLayout").index(2)));
        verify("ok按钮不存在", OK.exists());
        OK.click();
        sleepInt(6);
    }

    public void pressDialPad(String number) throws UiObjectNotFoundException {
        for (int i = 0; i < number.length(); i++) {
            UiObject numBtn = new UiObject(new UiSelector()
                    .className("android.widget.TextView")
                    .resourceId("com.android.dialer:id/dialpad_key_number")
                    .text(String.valueOf(number.charAt(i))));
            verify(numBtn.exists());
            numBtn.click();
            sleepInt(1);
        }
    }

    private ArrayList<String> initListApps() {
        ArrayList<String> list = new ArrayList<String>() {
            {
                add(AppName.PHONE);
                add(AppName.MESSAGE);
                add(AppName.BROWSER);
                add(AppName.CAMERA);
                add(AppName.CLOCK);
                add(AppName.CALENDAR);
                add(AppName.WEATHER);
                add(AppName.MUSIC);
                add(AppName.TVCONTROLLER);
                add(AppName.APPSTORE);
                add(AppName.LETVVIDEO);
                add(AppName.SETTING);
                add(AppName.FILEMANAGER);
                add(AppName.WALLPAPER);
                add(AppName.GALLERY);
                add(AppName.EMAIL);
                add(AppName.CONTACT);
                add(AppName.DOWNLOAD);
                add(AppName.FEEDBACK);
                add(AppName.RECORDER);
                add(AppName.MAP);
                add(AppName.NOTE);
                add(AppName.SYSTEMTOOL);
                add(AppName.WPS);
                add(AppName.LESTORE);
                add(AppName.LEACCOUNT);
                add(AppName.CALCULATOR);
                add(AppName.SIMCARD);
                add(AppName.VIDEOPLAYER);
                add(AppName.CHROME);
                add(AppName.YOUTUBE);
                add(AppName.GMAIL);
                add(AppName.PLAYMUSIC);
                add(AppName.PLAYMOVIE);
                add(AppName.GOOGLEPICTURE);
                add(AppName.GOOGLESETTING);
                add(AppName.GOOGLE);
                add(AppName.GOOGLEDRIVE);
                add(AppName.VOICESEARCH);
                add(AppName.GOOGLESTORE);
            }
        };
        return list;
    }


}

