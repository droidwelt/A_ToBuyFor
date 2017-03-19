package ru.droidwelt.tobuyfor;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class About_Activity extends Activity {

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		setTitle(getString(R.string.s_about));
		TextView versionTextView = (TextView) findViewById(R.id.about_Version);
		TextView dbsizeTextView = (TextView) findViewById(R.id.about_dbsize);
		
		PackageInfo pinfo;
		try {
			pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
			versionTextView.setText("Version "+pinfo.versionName+" build "+pinfo.versionCode);
		} catch (NameNotFoundException e) {	
			versionTextView.setText("");
			e.printStackTrace();
		}       		
				
		DB_OpenHelper dbh = new DB_OpenHelper (About_Activity.this,null);
		dbsizeTextView.setText(getString(R.string.s_about_records)+" "+Main_Activity.getMasterAdapter().getCount()+
				";  "+dbh.getDatabaseSize()+" kб");
		
		
	}
}
