package com.theappschef.bloodpressuremonitor;

import android.text.format.DateFormat;

import java.text.DecimalFormat;

public class Constant {

    public static String FIREBASE_NOTIFICATION_MESSAGE_TOPIC="Userssss";
    public static String FIREBASE_NODE_USER="expenses";
    public static String FIREBASE_NODE_EXPENSE="expenses";
    public static String TYPE_EXPENSE="Expense";
    public static String TYPE_INCOME="Income";
    public static String TYPE_="Type";
    public static String TYPE_ACCOUNT="Account";
    public static String DAY_FORMAT="dd";
    public static String MONTH_FORMAT="MMM";
    public static String YEAR_FORMAT="yyyy";
    public static String DATE_FORMAT=DAY_FORMAT+" "+MONTH_FORMAT+" "+YEAR_FORMAT;
    public static String MONTH_YEAR_FORMAT=MONTH_FORMAT+" "+YEAR_FORMAT;
    public String CURRENT_MONTH=String.valueOf(DateFormat.format(MONTH_FORMAT, new java.util.Date()));
    public String CURRENT_YEAR=String.valueOf(DateFormat.format(YEAR_FORMAT, new java.util.Date()));
    public String CURRENT_DAY=String.valueOf(DateFormat.format(DAY_FORMAT, new java.util.Date()));

    public static final int CONST_IMAGE_FILE_SIZE = 10; //KB
    public static final String CONST_IMAGE_STORE_CONTAINER = "PROOFS";
    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

    public static int REMINDER_HOUR = 21;
    public static int REMINDER_MIN = 0;

    public static String[] item_half_year = {"Jan-June", "July-December"};
    public static String[] item_quarters = {"Jan-March", "April-June", "July-September", "October-December"};
    public static String CURRENT_PAGE = "carrentpage";
    public static String CURRENT_TAB = "currenttab";
    public static String TAB_REPORT = "tab_report";

    public static int ACount = 0;
    public static int BCount = 0;
    public static int CCount = 0;
    public static int Click = 0;
    public static String CurrentPage = null;
    public static String CurrentTab = null;
    public static String TAB_ABOUT = "tab_about";
    public static String TAB_ABOUT_CK = "tab_about_ck";
    public static String TAB_ADHYAY = "tab_adhyay";
    public static String TAB_CHANAKYAQUOTE = "tab_chanakya_quote";
    public static String TAB_FAVORITE = "tab_favorite";
    public static final String TAB_HOME = "tab_home";

    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_SERVER_KEY = "key=AIzaSyBTDpVyvct9tlm-MomsXQbDPNmTbmVhHx4";
}
