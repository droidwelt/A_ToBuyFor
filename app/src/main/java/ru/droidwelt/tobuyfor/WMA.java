package ru.droidwelt.tobuyfor;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class WMA extends Application {

	private static Context context;

	public static String DB_NAME = "tobuyfor.db3";
	public static String DB_NAMEMODEL = "tobuyfor_etal.db3";
	public static final int CONT_SCANNER = 0x0000c0de;

	public static String DB_PATH = "";

	private static DB_OpenHelper dbh = null;
	private static SQLiteDatabase database = null;

	private static boolean not_use_dict_internal = false;
	private static String last_scanned_code = "";

	@Override
	public void onCreate() {
		super.onCreate();
		WMA.context = getApplicationContext();

		DB_PATH = String.format("//data//data//%s//databases//", getPackageName()); // 4.2

		dbh = new DB_OpenHelper(getAppContext(), DB_NAME);
		database = dbh.getDatabase();
	}

	public static Context getAppContext() {
		return WMA.context;
	}

	public static void getPreferences(Context c) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
		not_use_dict_internal = sp.getBoolean("not_use_dict_internal", false);
	}



	public static String strnormalize(String s) {
		if (s == null)
			return "";
		else
			return s;
	}

	public static SQLiteDatabase getDatabase() {
		return database;
	}

	public static String getLast_scanned_code() {
		return last_scanned_code;
	}

	public static void setLast_scanned_code(String last_scanned_code) {
		WMA.last_scanned_code = last_scanned_code;
	}

	public static String getDB_PATH() {
		return DB_PATH;
	}

	public static boolean hasConnection(final Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiInfo != null && wifiInfo.isConnected()) {
			return true;
		}
		wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifiInfo != null && wifiInfo.isConnected()) {
			return true;
		}
		wifiInfo = cm.getActiveNetworkInfo();
		return wifiInfo != null && wifiInfo.isConnected();
	}

	public static boolean isNot_use_dict_internal() {
		return not_use_dict_internal;
	}

	public static void setNot_use_dict_internal(boolean not_use_dict_internal) {
		WMA.not_use_dict_internal = not_use_dict_internal;
	}

	public static int getIndexedColor(int index) {
		int clr = getAppContext().getResources().getColor(R.color.c_clr_0);
		switch (index) {
		case 1:
			clr = getAppContext().getResources().getColor(R.color.c_clr_1);
			break;
		case 2:
			clr = getAppContext().getResources().getColor(R.color.c_clr_2);
			break;
		case 3:
			clr = getAppContext().getResources().getColor(R.color.c_clr_3);
			break;
		case 4:
			clr = getAppContext().getResources().getColor(R.color.c_clr_4);
			break;
		case 5:
			clr = getAppContext().getResources().getColor(R.color.c_clr_5);
			break;
		case 6:
			clr = getAppContext().getResources().getColor(R.color.c_clr_6);
			break;
		case 7:
			clr = getAppContext().getResources().getColor(R.color.c_clr_7);
			break;
		}
		return clr;
	}

}
