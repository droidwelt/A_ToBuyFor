package ru.droidwelt.tobuyfor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class Start_Activity extends Activity {

	private Timer timer;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);
		ImageView iv_start = (ImageView) findViewById(R.id.imageView_start);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Start_Activity.this);
		String sLastRunningBuild = sp.getString("SmartBuy_LastRunningBuild", "0");

		PackageInfo pinfo;
		String sCurrentRunningBuild = "";
		try {
			pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
			sCurrentRunningBuild = Integer.toString(pinfo.versionCode);
		} catch (NameNotFoundException ignored) {
		}

		if (!sLastRunningBuild.equals(sCurrentRunningBuild)) {

			AlertDialog.Builder builder = new AlertDialog.Builder(Start_Activity.this);
			builder.setTitle(R.string.s_refresh_dictionary);
			builder.setMessage(R.string.s_wait);
			builder.setCancelable(false);
			AlertDialog dlgLoadDict = builder.create();
			dlgLoadDict.show();
			Editor editor = sp.edit();
			editor.putString("SmartBuy_LastRunningBuild", sCurrentRunningBuild);
			editor.apply();

			appendDictionary();
			dlgLoadDict.dismiss();
		}

		timer = new Timer();

		iv_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				timer.purge();
				timer.cancel();
				Intent intent = new Intent(Start_Activity.this, Main_Activity.class);
				startActivity(intent);
				finish();
			}
		});

		timer.schedule(new TimerTask() {
			public void run() {
				timer.purge();
				timer.cancel();
				Intent intent = new Intent(Start_Activity.this, Main_Activity.class);
				startActivity(intent);
				finish();
			}
		}, 3000);

	}
	
	@Override
	public void onBackPressed() {
		timer.purge();
		timer.cancel();
		finish();
	}


	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Наполнение таблицы данными из dict.xml
	public void appendDictionary() {
		String[] dict = getResources().getStringArray(R.array.dict);
		for (String aDict : dict) {

			String aVal[] = aDict.split(";");
			for (String anAVal : aVal) {
				String s = "'" + anAVal.trim() + "'";
				String sql = "insert into tbl  (_id_par, name, color)  select 1000000 as _id_par," + s
						+ " as name, 0 as color " + " from tbl where  (select count(*) from tbl where name=" + s
						+ " and _id_par=1000000) =0 limit 1";
				// Log.i("COPY", sql);
				WMA.getDatabase().beginTransaction();
				WMA.getDatabase().execSQL(sql);
				WMA.getDatabase().setTransactionSuccessful();
				WMA.getDatabase().endTransaction();
			}
		}
	}

}
