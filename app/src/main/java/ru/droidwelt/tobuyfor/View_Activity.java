package ru.droidwelt.tobuyfor;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class View_Activity extends Activity {

    private static View_Activity instance;
    private static Context context;

    static final int EDIT_RESULTCODE = 201;
    private static long MASTER_ID = 0; //
    private String _mastername;

    private static ListView detailListView;
    private static detail_DBCursorAdapter detailAdapter;
    private static Cursor detailCursor;
    private boolean detailModified = false;

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    private class detail_DBCursorAdapter extends SimpleCursorAdapter {

        private Context context;

        @SuppressWarnings("deprecation")
        detail_DBCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            this.context = context;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int pos, View inView, ViewGroup parent) {
            View v = super.getView(pos, inView, parent);

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.view_activity, null);
            }

            TextView tv0 = (TextView) v.findViewById(R.id.view_item_detail_name);
            int indexName = View_Activity.getDetailAdapter().getCursor().getColumnIndex("name");
            String s = View_Activity.getDetailAdapter().getCursor().getString(indexName);
            tv0.setText(s);

            int indexDone = View_Activity.getDetailAdapter().getCursor().getColumnIndex("done");
            s = WMA.strnormalize(View_Activity.getDetailAdapter().getCursor().getString(indexDone));
            if (s.equals("OK")) {
                tv0.setBackgroundColor(getResources().getColor(R.color.c_bgr_done));
            } else {
                int indexcolor = View_Activity.getDetailAdapter().getCursor().getColumnIndex("color");
                int clr = View_Activity.getDetailAdapter().getCursor().getInt(indexcolor);
                tv0.setBackgroundColor(WMA.getIndexedColor(clr));
            }
            return (v);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras(); // получение дополнений;
        MASTER_ID = extras.getLong("MASTER_ID");

        Cursor masterCursor = WMA.getDatabase().rawQuery("SELECT _id, name FROM tbl WHERE _id=" + MASTER_ID, null);
        masterCursor.moveToFirst();
        int masternameIndex = masterCursor.getColumnIndex("name");
        _mastername = WMA.strnormalize(masterCursor.getString(masternameIndex));
        masterCursor.close();
        if (bar != null)
            bar.setTitle(_mastername);

        String[] from = new String[]{"name"};
        int[] to = new int[]{R.id.view_item_detail_name};

        setDetailAdapter(new detail_DBCursorAdapter(this, R.layout.view_item_detail, null, from, to));
        detailListView = (ListView) findViewById(R.id.view_detail_list);
        detailListView.setOnItemClickListener(detailListener);
        detailListView.setOnItemLongClickListener(detailLoggListener);
        detailListView.setAdapter(getDetailAdapter());
        detailCursor = WMA.getDatabase().rawQuery("SELECT _id, name, done,color  FROM tbl WHERE _id_par=" + MASTER_ID + " ORDER BY done,color,name", null);
        getDetailAdapter().swapCursor(detailCursor);

    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // слушатель событий в ListView, отметка выполнения
    OnItemClickListener detailListener = new OnItemClickListener() {

        @SuppressWarnings("deprecation")
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            int indexDone = detailCursor.getColumnIndex("done");
            String sdone = WMA.strnormalize(detailCursor.getString(indexDone));
            if (sdone.equals(""))
                sdone = "OK";
            else
                sdone = "";
            ContentValues editDetail = new ContentValues();
            editDetail.put("done", sdone);
            WMA.getDatabase().update("tbl", editDetail, "_id=" + id, null);
            detailCursor.requery();
            detailModified = true;
        }
    };

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // длинный клик, удаление записи
    OnItemLongClickListener detailLoggListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            deleteDetailRecord(id);
            return false;
        }
    };

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case EDIT_RESULTCODE:
                    long current_ID = returnedIntent.getLongExtra("MASTER_ID", 0);
                    if (current_ID >= 0) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                    break;
            }
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // подключение меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_menu, menu);
        return true;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.view_menuItem_edit:
                Intent viewContact = new Intent(View_Activity.this, Edit_Activity.class);
                viewContact.putExtra("MASTER_ID", MASTER_ID);
                startActivityForResult(viewContact, EDIT_RESULTCODE);
                return true;

            case R.id.view_menuItem_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("plain/text");
                i.putExtra(Intent.EXTRA_TEXT, prepareTextToSend(MASTER_ID));
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.s_event_report_subject) + " " + _mastername);
                i = Intent.createChooser(i, getString(R.string.s_send_report));
                startActivity(i);
                return true;

            case android.R.id.home:
                if (detailModified) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                }
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String prepareTextToSend(long id) {
        String res = _mastername + ": ";
        Cursor detCursor = WMA.getDatabase().rawQuery("SELECT name, done FROM tbl WHERE _id_par=" + id + " ORDER BY done, name", null);
        int name_index = detCursor.getColumnIndex("name");
        int done_index = detCursor.getColumnIndex("done");
        String sdone;
        if (detCursor.moveToFirst()) {
            res = res + WMA.strnormalize(detCursor.getString(name_index)).trim();
            sdone = WMA.strnormalize(detCursor.getString(done_index)).trim();
            if (!sdone.equals(""))
                res = res + " - " + sdone;
            res = res + ";";
            while (detCursor.moveToNext()) {
                res = res + WMA.strnormalize(detCursor.getString(name_index)).trim();
                sdone = WMA.strnormalize(detCursor.getString(done_index)).trim();
                if (!sdone.equals(""))
                    res = res + " - " + sdone;
                res = res + ";";
            }
        }
        detCursor.close();

        res = res + getString(R.string.s_prepare_to_share);
        return res;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////
        public static detail_DBCursorAdapter getDetailAdapter() {
        return detailAdapter;
    }

    public static void setDetailAdapter(detail_DBCursorAdapter detailAdapter) {
        View_Activity.detailAdapter = detailAdapter;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    // удаление записи
    private void deleteDetailRecord(final long id_Delete) {
        final Timer timer = new Timer();
        AlertDialog.Builder builder = new AlertDialog.Builder(View_Activity.this);
        builder.setTitle(R.string.s_delete_detail);

        builder.setNegativeButton(R.string.s_no, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timer.purge();
                timer.cancel();
            }
        });

        builder.setPositiveButton(R.string.s_yes, new DialogInterface.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(DialogInterface dialog, int button) {
                timer.purge();
                timer.cancel();
                WMA.getDatabase().delete("tbl", "_id=" + id_Delete, null);
                detailCursor.requery();
                detailModified = true;
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

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Обработка нажатия, возврат true, если обработка выполнена
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:
                if (detailModified) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                }
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
