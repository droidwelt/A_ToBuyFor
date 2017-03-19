package ru.droidwelt.tobuyfor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DB_OpenHelper extends SQLiteOpenHelper {

	private static Context context;
	public static SQLiteDatabase database; 

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////

	DB_OpenHelper(Context context, String dbName) {
		super(context, dbName, null, 1);
		DB_OpenHelper.context = context;
		database = openDataBase();
	}

	SQLiteDatabase getDatabase() {
		return database;
	}

	long getDatabaseSize() {
		File file = new File(WMA.getDB_PATH() + WMA.DB_NAME);
		return file.length() / 1024; 
	}

	// Создаст базу, если она не создана
	private void createDataBase() {
		boolean dbExist = checkDataBase();
		if (!dbExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database!");
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Проверка существования базы данных
	private boolean checkDataBase() {
		SQLiteDatabase checkDb = null;
		try {
			checkDb = SQLiteDatabase.openDatabase(WMA.getDB_PATH() + WMA.DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLException ignored) {
		}
		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null;
	}

	private SQLiteDatabase openDataBase() throws SQLException {
		if (database == null) {
			createDataBase();
			database = SQLiteDatabase.openDatabase(WMA.getDB_PATH() + WMA.DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
		}
		return database;
	}



	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Копирование базы из эталона через getAssets
	private void copyDataBase() throws IOException {
		// Log.i("XXX", "Копирование базы из эталона через getAssets " +
		// WMA.getDB_PATH() + WMA.DB_NAME);
		InputStream externalDbStream = context.getAssets().open(WMA.DB_NAMEMODEL);
		String outFileName = WMA.getDB_PATH() + WMA.DB_NAME;
		OutputStream localDbStream = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = externalDbStream.read(buffer)) > 0) {
			localDbStream.write(buffer, 0, bytesRead);
		}
		localDbStream.close();
		externalDbStream.close();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressLint("DefaultLocale")
	static void generateSearhUp(long mASTER_ID) {
		Cursor detCursor = WMA.getDatabase().rawQuery("SELECT name FROM tbl WHERE _id_par=" + mASTER_ID + " ORDER BY name", null);
		int detindex = detCursor.getColumnIndex("name");
		String sdet = "";
		if (detCursor.moveToFirst()) {
			sdet = sdet + WMA.strnormalize(detCursor.getString(detindex)).trim().toUpperCase() + " ";
			while (detCursor.moveToNext()) {
				sdet = sdet + WMA.strnormalize(detCursor.getString(detindex)).trim().toUpperCase() + " ";
			}
		}
		detCursor.close();
		ContentValues editMaster = new ContentValues();
		editMaster.put("searchup", sdet);
		WMA.getDatabase().update("tbl", editMaster, "_id=" + mASTER_ID, null);
	}

}