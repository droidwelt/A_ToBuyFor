package ru.droidwelt.tobuyfor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main_Activity extends FragmentActivity implements LoaderCallbacks<Cursor> {


	static boolean isDictionaryLoaded = false;
	static final int VIEW_RESULTCODE = 101;
	static final int EDIT_RESULTCODE = 102;
	private static long MASTER_ID = 0; //

	private static ListView masterListView;
	private static Main_Master_DBCursorAdapter masterAdapter;

	private String[] sort_list_field;
	private String[] sort_list_name;
	private static String sort_field = "nameup";
	private String[] help_list_name;
	private EditText et;
	private int et_color = 0;
	private boolean cb_done = false;

	// ////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new MyCursorLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		masterAdapter = new Main_Master_DBCursorAdapter(this, c, 0);
		setMasterAdapter(masterAdapter);
		masterListView = (ListView) findViewById(R.id.main_master_list);
		masterListView.setOnItemClickListener(masterListener);
		masterListView.setOnItemLongClickListener(masterLoggListener);
		masterListView.setAdapter(getMasterAdapter());
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	private static class MyCursorLoader extends CursorLoader {
		MyCursorLoader(Context context) {
			super(context);
		}

		@Override
		public Cursor loadInBackground() {
			return getFilterdMasterRecord("", sort_field);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// android.widget.CursorAdapter.FLAG_AUTO_REQUERY
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_activity);
		WMA.getPreferences(this);

		if (savedInstanceState == null) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			String sLastRunningBuild = sp.getString("LastRunningBuild", "0");

			PackageInfo pinfo;
			String sCurrentRunningBuild = "";
			try {
				pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
				sCurrentRunningBuild = Integer.toString(pinfo.versionCode);
			} catch (NameNotFoundException ignored) {
			}

			if (!sLastRunningBuild.equals(sCurrentRunningBuild)) {
				// Показ окна изменений
				Intent newsactivity = new Intent(Main_Activity.this, News_Activity.class);
				startActivity(newsactivity);
			}
		}

		getLoaderManager().initLoader(0, savedInstanceState, this);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	@SuppressLint("DefaultLocale")
	public static Cursor getFilterdMasterRecord(String filter, String sort) {
		Log.i("XXX", "getFilterdMasterRecord " + filter + " " + sort);
		String sortfield;
		if ((sort == null) | (sort != null && sort.trim().equals(""))) {
			sortfield = "nameup";
		} else {
			sortfield = sort;
		}
		String sqlitefilter;
		if ((filter == null) | (filter != null && filter.trim().equals(""))) {
			sqlitefilter = "";
		} else {
			sqlitefilter = " AND searchup LIKE '%" + filter.toUpperCase().trim() + "%'";
		}
		String sql = " SELECT _id,name,done,color,searchup,nameup FROM tbl WHERE _id_par=0 " + sqlitefilter
				+ " ORDER BY done," + sortfield;
		return WMA.getDatabase().rawQuery(sql, null);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	protected void resreshMasterRecord(long id_to_find) {
		if (id_to_find >= 0) {
			getMasterAdapter().changeCursor(getFilterdMasterRecord("", sort_field));
			int n = 0;
			while (n <= getMasterAdapter().getCount() - 1) {
				if (getMasterAdapter().getItemId(n) == id_to_find) {
					break;
				}
				n = n + 1;
			}
			masterListView.smoothScrollToPosition(n);
		} else {
			getLoaderManager().restartLoader(0, null, this);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		final SearchView searchView = (SearchView) menu.findItem(R.id.main_menu_search).getActionView();

		help_list_name = getResources().getStringArray(R.array.help_list_qwe);
		sort_list_field = getResources().getStringArray(R.array.sort_list_field);
		sort_list_name = getResources().getStringArray(R.array.sort_list_name);
		if (sort_field.equals(""))
			sort_field = sort_list_field[0];

		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			// при нажатии на поиск на клавиатуре
			public boolean onQueryTextSubmit(String query) {
				if (DB_OpenHelper.database.isOpen()) {
					onQueryTextChange(searchView.getQuery().toString());
				}
				return true;
			}

			@Override
			// при изменении текста запроса
			public boolean onQueryTextChange(String newText) {
				if (DB_OpenHelper.database.isOpen()) {
					getMasterAdapter().changeCursor(getFilterdMasterRecord(newText, sort_field));
				}
				return false;
			}
		});
		return true;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// выбор из меню
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Поиск
		if (item.getItemId() == R.id.main_menu_search) {
			return true;
		}

		// Настройки
		if (item.getItemId() == R.id.main_menu_pref) {
			Intent intent = new Intent(Main_Activity.this, Pref_Activity.class);
			startActivity(intent);
			return true;
		}

		// О программе
		if (item.getItemId() == R.id.main_menu_about) {
			Intent intent = new Intent(this, About_Activity.class);
			startActivity(intent);
			return true;
		}

		// Новости приложения
		if (item.getItemId() == R.id.main_menu_news) {
			Intent intent = new Intent(this, News_Activity.class);
			startActivity(intent);
			return true;
		}

		// Сортировка
		if (item.getItemId() == R.id.main_menu_order) {
			AlertDialog.Builder adb_order = new AlertDialog.Builder(this);
			adb_order.setTitle(R.string.s_order);
			adb_order.setCancelable(true);
			adb_order.setItems(sort_list_name, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					String sf = sort_list_field[item];
					if (!sf.equals(sort_field)) {
						sort_field = sf;
						getLoaderManager().restartLoader(0, null, Main_Activity.this);
						// getMasterAdapter().changeCursor(getFilterdMasterRecord("",
						// sort_field));
					}
				}
			});
			adb_order.create();
			adb_order.show();
			return true;
		}

		// Справка
		if (item.getItemId() == R.id.main_menu_help) {
			AlertDialog.Builder adb_help = new AlertDialog.Builder(this);
			adb_help.setTitle(R.string.s_help);
			adb_help.setCancelable(true);
			adb_help.setItems(help_list_name, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					Intent intent = new Intent(Main_Activity.this, Help_Activity.class);
					intent.putExtra("HELP_ID", item + 1);
					startActivity(intent);
				}
			});
			adb_help.create();
			adb_help.show();
			return true;
		}

		// добавление новой записи
		if (item.getItemId() == R.id.main_menu_add) {
			addNewRecord();
			return true;
		}

		// Сканнер
		if (item.getItemId() == R.id.main_menu_scanner) {
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.addExtra("SCAN_WIDTH", 800);
			integrator.addExtra("SCAN_HEIGHT", 200);
			// integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
			integrator.addExtra("PROMPT_MESSAGE", getString(R.string.s_scan_progress));
			integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// слушатель клика в ListView
	OnItemClickListener masterListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			Intent viewContact = new Intent(Main_Activity.this, View_Activity.class);
			viewContact.putExtra("MASTER_ID", id);
			startActivityForResult(viewContact, VIEW_RESULTCODE);
			MASTER_ID = id;
		}
	};

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// слушатель длинного клика в ListView
	// выбор действия
	OnItemLongClickListener masterLoggListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v, int position, final long id) {
			final String[] actionName = { getString(R.string.s_choice_action_delete),
					getString(R.string.s_choice_action_copy), getString(R.string.s_choice_action_name) };

			AlertDialog.Builder builder = new AlertDialog.Builder(Main_Activity.this);
			builder.setTitle(R.string.s_choice_action);
			builder.setItems(actionName, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						deleteMasterRecord(id);
					}
					if (item == 1) {
						copyMasterRecord(id);
					}
					if (item == 2) {
						changeNameMasterRecord(id);
					}
				}
			});
			final AlertDialog dlg = builder.create();
			dlg.show();
			return true;
		}
	};

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// удаление записи
	private void deleteMasterRecord(final long id_Delete) {
		final Timer timer = new Timer();
		AlertDialog.Builder builder = new AlertDialog.Builder(Main_Activity.this);
		builder.setTitle(R.string.s_delete_master);

		builder.setNegativeButton(R.string.s_no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				timer.purge();
				timer.cancel();
			}
		});

		builder.setPositiveButton(R.string.s_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int button) {
				timer.purge();
				timer.cancel();
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
					@Override
					protected Object doInBackground(Long... params) {
						WMA.getDatabase().delete("tbl", "_id_par=" + params[0], null);
						WMA.getDatabase().delete("tbl", "_id=" + params[0], null);
						return null;
					}

					@Override
					protected void onPostExecute(Object result) {
						getMasterAdapter().changeCursor(getFilterdMasterRecord("", sort_field));
					}
				};

				deleteTask.execute(id_Delete);
			}
		});

		final AlertDialog dlg = builder.create();
		dlg.show();

		timer.schedule(new TimerTask() {
			public void run() {
				timer.purge();
				timer.cancel();
				dlg.dismiss();
			}
		}, 5000);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// возврат из другой активности
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (resultCode == RESULT_OK) {
		switch (requestCode) {

		case VIEW_RESULTCODE: // из экрана View
			// long current_ID = data.getLongExtra("MASTER_ID", 0);
			// resreshMasterRecord(current_ID);
			resreshMasterRecord(MASTER_ID);
			break;

		case EDIT_RESULTCODE: // из экрана View
			// long current_ID = data.getLongExtra("MASTER_ID", 0);
			// resreshMasterRecord(current_ID);
			resreshMasterRecord(MASTER_ID);
			break;

		case WMA.CONT_SCANNER: // из сканера
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
			if (scanResult != null) {
				// Log.i("CONT_SCANNER", scanResult.getContents());
				WMA.setLast_scanned_code(scanResult.getContents());
				findscancodebygoogle();
			}
			break;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// завершение приложения
	@Override
	public void onBackPressed() {
		final Timer timer = new Timer();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.s_app_name);
		builder.setMessage(R.string.s_exit_appl);

		builder.setNegativeButton(R.string.s_no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				timer.purge();
				timer.cancel();
			}
		});

		builder.setPositiveButton(R.string.s_yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				timer.purge();
				timer.cancel();
				Main_Activity.super.onBackPressed();
			}
		});

		final AlertDialog dlg = builder.create();
		dlg.show();
		timer.schedule(new TimerTask() {
			public void run() {
				timer.purge();
				timer.cancel();
				dlg.dismiss();
			}
		}, 5000);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	public static Main_Master_DBCursorAdapter getMasterAdapter() {
		return masterAdapter;
	}

	public static void setMasterAdapter(Main_Master_DBCursorAdapter masterAdapter) {
		Main_Activity.masterAdapter = masterAdapter;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// редактирование записи
	@SuppressLint("InflateParams")
	public void changeNameMasterRecord(final long master_id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.s_namemaster);
		final View v = this.getLayoutInflater().inflate(R.layout.edit_main_dlg_name, null);
		builder.setView(v);
		et = (EditText) v.findViewById(R.id.edit_main_dlg_edittext_name);
		int nameindex = getMasterCursor().getColumnIndex("name");
		int doneindex = getMasterCursor().getColumnIndex("done");
		int colorindex = getMasterCursor().getColumnIndex("color");
		String sname = WMA.strnormalize(getMasterCursor().getString(nameindex));
		et.setText(sname);
		et_color = getMasterCursor().getInt(colorindex);
		et.setBackgroundColor(WMA.getIndexedColor(et_color));

		final CheckBox checkbox_done = (CheckBox) v.findViewById(R.id.edit_main_dlg_checkbox_done);
		cb_done = false;
		String sdone = WMA.strnormalize(getMasterCursor().getString(doneindex));
		if (!sdone.equals(""))
			cb_done = true;
		checkbox_done.setChecked(cb_done);
		checkbox_done.setOnClickListener(onDlgBtnClick);

		final ImageButton ib_0 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_0);
		final ImageButton ib_1 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_1);
		final ImageButton ib_2 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_2);
		final ImageButton ib_3 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_3);
		final ImageButton ib_4 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_4);
		final ImageButton ib_5 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_5);
		final ImageButton ib_6 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_6);
		final ImageButton ib_7 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_7);

		ib_0.setOnClickListener(onDlgBtnClick);
		ib_1.setOnClickListener(onDlgBtnClick);
		ib_2.setOnClickListener(onDlgBtnClick);
		ib_3.setOnClickListener(onDlgBtnClick);
		ib_4.setOnClickListener(onDlgBtnClick);
		ib_5.setOnClickListener(onDlgBtnClick);
		ib_6.setOnClickListener(onDlgBtnClick);
		ib_7.setOnClickListener(onDlgBtnClick);

		builder.setNegativeButton(R.string.s_cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});

		builder.setPositiveButton(R.string.s_save, new OnClickListener() {
			@SuppressLint("DefaultLocale")
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				EditText et = (EditText) v.findViewById(R.id.edit_main_dlg_edittext_name);
				String sname = WMA.strnormalize(et.getText().toString().trim());
				ContentValues editMaster = new ContentValues();
				editMaster.put("name", sname);
				editMaster.put("nameup", sname.toUpperCase());
				editMaster.put("color", et_color);
				if (cb_done) {
					editMaster.put("done", "OK");
				} else {
					editMaster.put("done", "");
				}
				WMA.getDatabase().update("tbl", editMaster, "_id=" + master_id, null);
				getLoaderManager().restartLoader(0, null, Main_Activity.this);
			}
		});

		final AlertDialog dlg = builder.create();
		dlg.show();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// копирование записи
	@SuppressLint("InflateParams")
	public void copyMasterRecord(final long master_id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.s_copylistname);
		final View v = this.getLayoutInflater().inflate(R.layout.edit_main_dlg_name, null);
		et = (EditText) v.findViewById(R.id.edit_main_dlg_edittext_name);
		int nameindex = getMasterCursor().getColumnIndex("name");
		int colorindex = getMasterCursor().getColumnIndex("color");
		int doneindex = getMasterCursor().getColumnIndex("done");
		String sname = WMA.strnormalize(getMasterCursor().getString(nameindex));
		et.setText(sname);
		builder.setView(v);
		et_color = getMasterCursor().getInt(colorindex);
		et.setBackgroundColor(WMA.getIndexedColor(et_color));

		final CheckBox checkbox_done = (CheckBox) v.findViewById(R.id.edit_main_dlg_checkbox_done);
		cb_done = false;
		String sdone = WMA.strnormalize(getMasterCursor().getString(doneindex));
		if (!sdone.equals(""))
			cb_done = true;
		checkbox_done.setChecked(cb_done);
		checkbox_done.setOnClickListener(onDlgBtnClick);

		final ImageButton ib_0 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_0);
		final ImageButton ib_1 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_1);
		final ImageButton ib_2 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_2);
		final ImageButton ib_3 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_3);
		final ImageButton ib_4 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_4);
		final ImageButton ib_5 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_5);
		final ImageButton ib_6 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_6);
		final ImageButton ib_7 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_7);
		ib_0.setOnClickListener(onDlgBtnClick);
		ib_1.setOnClickListener(onDlgBtnClick);
		ib_2.setOnClickListener(onDlgBtnClick);
		ib_3.setOnClickListener(onDlgBtnClick);
		ib_4.setOnClickListener(onDlgBtnClick);
		ib_5.setOnClickListener(onDlgBtnClick);
		ib_6.setOnClickListener(onDlgBtnClick);
		ib_7.setOnClickListener(onDlgBtnClick);

		builder.setNegativeButton(R.string.s_cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});

		builder.setPositiveButton(R.string.s_copy, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				EditText et = (EditText) v.findViewById(R.id.edit_main_dlg_edittext_name);
				String sname = WMA.strnormalize(et.getText().toString().trim());
				copyMasterRecordExec(sname, master_id);
			}
		});

		final AlertDialog dlg = builder.create();
		dlg.show();
	}

	@SuppressLint("DefaultLocale")
	public void copyMasterRecordExec(String name, final long master_id) {
		String _searchup = WMA.strnormalize(getMasterCursor().getString(getMasterCursor().getColumnIndex("searchup")));

		ContentValues newMaster = new ContentValues();
		newMaster.put("name", name);
		newMaster.put("_id_par", 0);
		newMaster.put("nameup", name.toUpperCase());
		newMaster.put("searchup", _searchup);
		newMaster.put("color", et_color);
		if (cb_done) {
			newMaster.put("done", "OK");
		} else {
			newMaster.put("done", "");
		}
		String currentDateTimeString = (String) DateFormat.format("yyyy-MM-dd", new Date());
		newMaster.put("datetimes", currentDateTimeString);
		long new_id = WMA.getDatabase().insert("tbl", null, newMaster);

		if (new_id > 0) {
			String sql = "INSERT INTO tbl (_id_par, name, color, done) SELECT " + new_id + ", name, color,'' "
					+ " FROM tbl WHERE _id_par=" + master_id + ";";
			WMA.getDatabase().beginTransaction();
			WMA.getDatabase().execSQL(sql);
			WMA.getDatabase().setTransactionSuccessful();
			WMA.getDatabase().endTransaction();
		}
		getLoaderManager().restartLoader(0, null, this);

		int n = 0;
		while (n <= getMasterAdapter().getCount() - 1) {
			if (getMasterAdapter().getItemId(n) == new_id) {
				break;
			}
			n = n + 1;
		}
		masterListView.smoothScrollToPosition(n);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// добавление новой записи
	@SuppressLint("InflateParams")
	public void addNewRecord() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.s_newlistname);
		final View v = this.getLayoutInflater().inflate(R.layout.edit_main_dlg_name, null);
		builder.setView(v);
		et = (EditText) v.findViewById(R.id.edit_main_dlg_edittext_name);
		et.setText("");

		final CheckBox checkbox_done = (CheckBox) v.findViewById(R.id.edit_main_dlg_checkbox_done);
		cb_done = false;
		checkbox_done.setChecked(false);
		checkbox_done.setOnClickListener(onDlgBtnClick);

		final ImageButton ib_0 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_0);
		final ImageButton ib_1 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_1);
		final ImageButton ib_2 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_2);
		final ImageButton ib_3 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_3);
		final ImageButton ib_4 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_4);
		final ImageButton ib_5 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_5);
		final ImageButton ib_6 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_6);
		final ImageButton ib_7 = (ImageButton) v.findViewById(R.id.edit_main_dlg_clr_7);
		ib_0.setOnClickListener(onDlgBtnClick);
		ib_1.setOnClickListener(onDlgBtnClick);
		ib_2.setOnClickListener(onDlgBtnClick);
		ib_3.setOnClickListener(onDlgBtnClick);
		ib_4.setOnClickListener(onDlgBtnClick);
		ib_5.setOnClickListener(onDlgBtnClick);
		ib_6.setOnClickListener(onDlgBtnClick);
		ib_7.setOnClickListener(onDlgBtnClick);

		builder.setNegativeButton(R.string.s_cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});

		builder.setPositiveButton(R.string.s_save, new OnClickListener() {
			@SuppressLint("DefaultLocale")
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				EditText et = (EditText) v.findViewById(R.id.edit_main_dlg_edittext_name);
				String sname = WMA.strnormalize(et.getText().toString().trim());
				String currentDateTimeString = (String) DateFormat.format("yyyy-MM-dd", new Date());
				if (sname.equals(""))
					sname = getString(R.string.s_newrecord_name) + " " + currentDateTimeString;
				ContentValues newMaster = new ContentValues();
				newMaster.put("name", sname);
				newMaster.put("_id_par", 0);
				newMaster.put("nameup", sname.toUpperCase());
				newMaster.put("searchup", "");
				if (cb_done)
					newMaster.put("done", "OK");
				else
					newMaster.put("done", "");
				newMaster.put("color", et_color);
				newMaster.put("datetimes", currentDateTimeString);
				long new_id = WMA.getDatabase().insert("tbl", null, newMaster);

				MASTER_ID = new_id;
				Intent intent = new Intent(Main_Activity.this, Edit_Activity.class);
				intent.putExtra("MASTER_ID", new_id);
				startActivityForResult(intent, EDIT_RESULTCODE);
			}
		});

		final AlertDialog dlg = builder.create();
		dlg.show();

	}

	// /////////////////////////////////////////////////////////////////////////////////////////
	// слушатель нажатия на кнопку
	android.view.View.OnClickListener onDlgBtnClick = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {

			int id = v.getId();
			switch (id) {

			case R.id.edit_main_dlg_clr_0:
				et_color = 0;
				break;

			case R.id.edit_main_dlg_clr_1:
				et_color = 1;
				break;

			case R.id.edit_main_dlg_clr_2:
				et_color = 2;
				break;

			case R.id.edit_main_dlg_clr_3:
				et_color = 3;
				break;

			case R.id.edit_main_dlg_clr_4:
				et_color = 4;
				break;

			case R.id.edit_main_dlg_clr_5:
				et_color = 5;
				break;

			case R.id.edit_main_dlg_clr_6:
				et_color = 6;
				break;

			case R.id.edit_main_dlg_clr_7:
				et_color = 7;
				break;

			case R.id.edit_main_dlg_checkbox_done:
				cb_done = !cb_done;
				break;

			default:
				break;
			}
			et.setBackgroundColor(WMA.getIndexedColor(et_color));
		}
	};

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	public static Cursor getMasterCursor() {
		return getMasterAdapter().getCursor();
	}

	public static void setMasterCursor(Cursor c) {
		getMasterAdapter().changeCursor(c);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// поиск кода сканера
	private void findscancodebygoogle() {
		if (WMA.hasConnection(Main_Activity.this)) {
			AlertDialog.Builder adb_order = new AlertDialog.Builder(this);
			adb_order.setTitle(getString(R.string.s_scan_dlg_title) + " " + WMA.getLast_scanned_code());
			adb_order.setMessage(R.string.s_scan_dlg_search_web);
			adb_order.setCancelable(true);
			adb_order.setNegativeButton(R.string.s_no, null);
			adb_order.setPositiveButton(R.string.s_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					Intent searchintent = new Intent(Intent.ACTION_WEB_SEARCH);
					searchintent.putExtra("query", WMA.getLast_scanned_code());
					searchintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(searchintent);
				}
			});

			adb_order.create();
			adb_order.show();
		} else {
			AlertDialog.Builder adb_order = new AlertDialog.Builder(this);
			adb_order.setTitle(getString(R.string.s_scan_dlg_code) + " " + WMA.getLast_scanned_code());
			adb_order.setCancelable(true);
			adb_order.setPositiveButton(R.string.s_scan_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
				}
			});

			adb_order.create();
			adb_order.show();

		}
	}

}