package ru.droidwelt.tobuyfor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class News_Activity extends Activity {

	private boolean cb_state = false;

	private class MySimpleAdapter extends SimpleAdapter {

		MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
						int[] to) {
			super(context, data, resource, from, to);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_activity);		
		setTitle(getString(R.string.s_news));

		String[] newslist_qwe = getResources().getStringArray(R.array.news_list_qwe);
		String[] newslist_ans = getResources().getStringArray(R.array.news_list_ans);

		Button btn = (Button) findViewById(R.id.news_install);
		btn.setOnClickListener(onDlgBtnClick);

		CheckBox cb = (CheckBox) findViewById(R.id.news_checkBox);
		cb.setOnClickListener(onDlgBtnClick);

		ArrayList<Map<String, Object>> data = new ArrayList<>(newslist_qwe.length);
		Map<String, Object> m;

		for (int i = 0; i < newslist_qwe.length; i++) {
			m = new HashMap<>();
			m.put("QWE", newslist_qwe[i].trim());
			m.put("ANS", newslist_ans[i].trim());
			data.add(m);
		}

		String[] from = { "QWE", "ANS" };
		int[] to = { R.id.news_item1, R.id.news_item2 };

		MySimpleAdapter sAdapter = new MySimpleAdapter(this, data, R.layout.news_item, from, to);

		ListView lvMain = (ListView) findViewById(R.id.listView1);
		lvMain.setAdapter(sAdapter);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////
	android.view.View.OnClickListener onDlgBtnClick = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {

			int id = v.getId();
			switch (id) {

			case R.id.news_checkBox:
				cb_state = !cb_state;
				break;
				
			case R.id.news_install:
				String  packageName = "ru.droidwelt.tobuyforpro";
				 Uri uri = Uri.parse("market://details?id=" + packageName);
			        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			        try {			           
			            startActivity(intent);			          
			        } catch (ActivityNotFoundException anfe) {
			          Log.w("XXX", "Google Play is not installed; cannot install " + packageName);
			        }
				break;

			default:
				break;
			}
		}
	};
	
	

	public void writeCurrentRunningBuild() {
		if (cb_state) {
			PackageInfo pinfo;
			String sCurrentRunningBuild;
			try {
				pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
				sCurrentRunningBuild = Integer.toString(pinfo.versionCode);
				
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
				Editor editor = sp.edit();
				editor.putString("LastRunningBuild", sCurrentRunningBuild);
				editor.apply();
			} catch (NameNotFoundException ignored) {
			}
		}
	}

	// защита от закрытия по Back------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			writeCurrentRunningBuild();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// подключение меню ----------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.news_menu, menu);
		return true;
	}

	// меню -----------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			writeCurrentRunningBuild();
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
