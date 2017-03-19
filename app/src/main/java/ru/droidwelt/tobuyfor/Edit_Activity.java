package ru.droidwelt.tobuyfor;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.Timer;
import java.util.TimerTask;

public class Edit_Activity extends Activity {


    private static long MASTER_ID = 0; //
    private static long detail_id = 0;

    private static Edit_Detail_DBCursorAdapter detailAdapter;
    private static Edit_Choice_DBCursorAdapter choiceAdapter;
    private boolean detailModified = false;
    private EditText et_name;
    private int et_color = 0;


    private Cursor mychoiceCursor(int filter_color) {
        String s_no_use_dict = "";
        if (WMA.isNot_use_dict_internal()) {
            s_no_use_dict = " AND _id_par<999999 ";
        }
        if (filter_color >= 0) {
            s_no_use_dict = s_no_use_dict + " AND color=" + filter_color + " ";
        }
        return WMA.getDatabase().rawQuery(
                "SELECT _id, name, color FROM tbl WHERE _id_par>0 " + s_no_use_dict
                        + " GROUP BY name ORDER BY color,name", null);
    }


    private Cursor mydetailCursor() {
        return WMA.getDatabase().rawQuery(
                "SELECT _id, name, done, color FROM tbl WHERE _id_par=" + MASTER_ID + " ORDER BY done, color, name",
                null);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // создание активности
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailModified = false;
        setContentView(R.layout.edit_activity);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        WMA.getPreferences(this);
        et_color = 0;

        Bundle extras = getIntent().getExtras(); // получение дополнений;
        MASTER_ID = extras.getLong("MASTER_ID");
        Cursor masterCursor = WMA.getDatabase().rawQuery("SELECT _id, name, color FROM tbl WHERE _id=" + MASTER_ID,
                null);
        masterCursor.moveToFirst();
        if (bar != null)
            bar.setTitle(WMA.strnormalize(masterCursor.getString(masterCursor.getColumnIndex("name"))));
        masterCursor.close();

        setDetailAdapter(new Edit_Detail_DBCursorAdapter(this, mydetailCursor()));
        ListView detailListView = (ListView) findViewById(R.id.edit_detail_list);
        detailListView.setOnItemClickListener(detailListener);
        detailListView.setOnItemLongClickListener(detailLoggListener);
        detailListView.setAdapter(getDetailAdapter());
        setDetailCursor(WMA.getDatabase().rawQuery("SELECT _id, name, done, color FROM tbl WHERE _id_par=" + MASTER_ID + " ORDER BY done, color, name", null));
        getDetailAdapter().swapCursor(getDetailCursor());

        setChoiceAdapter(new Edit_Choice_DBCursorAdapter(this, mychoiceCursor(-1)));
        ListView choiceListView = (ListView) findViewById(R.id.edit_choice_list);
        choiceListView.setOnItemClickListener(choiceListener);
        choiceListView.setAdapter(getChoiceAdapter());
        getChoiceAdapter().swapCursor(getChoiceCursor());

        ImageButton ibf_clr = (ImageButton) findViewById(R.id.edit_flt_clr_clear);
        ImageButton ibf_0 = (ImageButton) findViewById(R.id.edit_flt_clr_0);
        ImageButton ibf_1 = (ImageButton) findViewById(R.id.edit_flt_clr_1);
        ImageButton ibf_2 = (ImageButton) findViewById(R.id.edit_flt_clr_2);
        ImageButton ibf_3 = (ImageButton) findViewById(R.id.edit_flt_clr_3);
        ImageButton ibf_4 = (ImageButton) findViewById(R.id.edit_flt_clr_4);
        ImageButton ibf_5 = (ImageButton) findViewById(R.id.edit_flt_clr_5);
        ImageButton ibf_6 = (ImageButton) findViewById(R.id.edit_flt_clr_6);
        ImageButton ibf_7 = (ImageButton) findViewById(R.id.edit_flt_clr_7);
        ibf_clr.setOnClickListener(onBtnClick);
        ibf_0.setOnClickListener(onBtnClick);
        ibf_1.setOnClickListener(onBtnClick);
        ibf_2.setOnClickListener(onBtnClick);
        ibf_3.setOnClickListener(onBtnClick);
        ibf_4.setOnClickListener(onBtnClick);
        ibf_5.setOnClickListener(onBtnClick);
        ibf_6.setOnClickListener(onBtnClick);
        ibf_7.setOnClickListener(onBtnClick);
    }


    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // слушатель событий в ListView
    // показ записи в верхнем окошке
    OnItemClickListener detailListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            /*
             * int indexName = getDetailCursor().getColumnIndex("name"); String
			 * sname =
			 * WMA.strnormalize(getDetailCursor().getString(indexName).toString
			 * ()); et_name.setText(sname); int indexColor =
			 * getDetailCursor().getColumnIndex("color"); int clr =
			 * getDetailCursor().getInt(indexColor);
			 * et_name.setBackgroundColor(WMA.getIndexedColor(clr));
			 * _detailcolor = clr;
			 */
        }
    };

    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // слушатель событий в choice ListView
    // добавление записи
    OnItemClickListener choiceListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            String sql = "INSERT INTO tbl (_id_par, name, color, done) " + " SELECT " + MASTER_ID
                    + ", name, color,'' FROM tbl  WHERE _id=" + id + " and name not in "
                    + "(select name from tbl where _id_par=" + MASTER_ID + ");";
            WMA.getDatabase().beginTransaction();
            WMA.getDatabase().execSQL(sql);
            WMA.getDatabase().setTransactionSuccessful();
            WMA.getDatabase().endTransaction();
            getDetailAdapter().swapCursor(mydetailCursor());
            DB_OpenHelper.generateSearhUp(MASTER_ID);
            detailModified = true;
        }
    };

    // ////////////////////////////////////////////////////////////////////////////////////////
    // На всякий случай
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 11111:
				/* */
                    break;
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // подключение меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        MenuItem mi = menu.findItem(R.id.edit_menuItem_dict);
        if (WMA.isNot_use_dict_internal())
            mi.setIcon(R.drawable.ic_dict);
        else
            mi.setIcon(R.drawable.ic_dict_use);
        return true;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                if (detailModified) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                }
                finish();
                return true;

            case R.id.edit_menuItem_dict:
                boolean use_dict = !WMA.isNot_use_dict_internal();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                Editor editor = sp.edit();
                editor.putBoolean("not_use_dict_internal", use_dict);
                editor.apply();
                WMA.setNot_use_dict_internal(use_dict);
                invalidateOptionsMenu();
                getChoiceAdapter().changeCursor(mychoiceCursor(-1));
                return true;

            case R.id.edit_menuItem_add:
                detail_id = 0;
                changeNameDetailRecord();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // удаление позиции
    private void deleteDetailRecord() {
        final Timer timer = new Timer();
        AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Activity.this);
        builder.setTitle(R.string.s_delete_detail);

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
                WMA.getDatabase().delete("tbl", "_id=" + detail_id, null);
                getDetailAdapter().swapCursor(mydetailCursor());
                DB_OpenHelper.generateSearhUp(MASTER_ID);
                detailModified = true;
            }
        });

        final AlertDialog dlg = builder.create();
        dlg.show();

        timer.schedule(new TimerTask() {
            public void run() {
                dlg.dismiss();
                timer.purge();
                timer.cancel();
            }
        }, 5000);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // Обработка нажатия, возврат true, если обработка выполнена
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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

    // /////////////////////////////////////////////////////////////////////////////////////////

    public static Edit_Detail_DBCursorAdapter getDetailAdapter() {
        return detailAdapter;
    }

    public static void setDetailAdapter(Edit_Detail_DBCursorAdapter detailAdapter) {
        Edit_Activity.detailAdapter = detailAdapter;
    }

    public static Cursor getDetailCursor() {
        return getDetailAdapter().getCursor();
    }

    public static void setDetailCursor(Cursor c) {
        getDetailAdapter().changeCursor(c);
    }

    public static Edit_Choice_DBCursorAdapter getChoiceAdapter() {
        return choiceAdapter;
    }

    public static void setChoiceAdapter(Edit_Choice_DBCursorAdapter choiceAdapter) {
        Edit_Activity.choiceAdapter = choiceAdapter;
    }

    public static Cursor getChoiceCursor() {
        return getChoiceAdapter().getCursor();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // слушатель нажатия на кнопку
    android.view.View.OnClickListener onBtnClick = new android.view.View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {

                case R.id.edit_flt_clr_clear:
                    getChoiceAdapter().changeCursor(mychoiceCursor(-1));
                    break;

                case R.id.edit_flt_clr_0:
                    getChoiceAdapter().changeCursor(mychoiceCursor(0));
                    break;

                case R.id.edit_flt_clr_1:
                    getChoiceAdapter().changeCursor(mychoiceCursor(1));
                    break;

                case R.id.edit_flt_clr_2:
                    getChoiceAdapter().changeCursor(mychoiceCursor(2));
                    break;

                case R.id.edit_flt_clr_3:
                    getChoiceAdapter().changeCursor(mychoiceCursor(3));
                    break;

                case R.id.edit_flt_clr_4:
                    getChoiceAdapter().changeCursor(mychoiceCursor(4));
                    break;

                case R.id.edit_flt_clr_5:
                    getChoiceAdapter().changeCursor(mychoiceCursor(5));
                    break;

                case R.id.edit_flt_clr_6:
                    getChoiceAdapter().changeCursor(mychoiceCursor(6));
                    break;

                case R.id.edit_flt_clr_7:
                    getChoiceAdapter().changeCursor(mychoiceCursor(7));
                    break;

                case R.id.edit_detail_dlg_clr_0:
                    et_color = 0;
                    et_name.setBackgroundColor(WMA.getIndexedColor(et_color));
                    break;

                case R.id.edit_detail_dlg_clr_1:
                    et_color = 1;
                    et_name.setBackgroundColor(WMA.getIndexedColor(et_color));
                    break;

                case R.id.edit_detail_dlg_clr_2:
                    et_color = 2;
                    et_name.setBackgroundColor(WMA.getIndexedColor(et_color));
                    break;

                case R.id.edit_detail_dlg_clr_3:
                    et_color = 3;
                    et_name.setBackgroundColor(WMA.getIndexedColor(et_color));
                    break;

                case R.id.edit_detail_dlg_clr_4:
                    et_color = 4;
                    et_name.setBackgroundColor(WMA.getIndexedColor(et_color));
                    break;

                case R.id.edit_detail_dlg_clr_5:
                    et_color = 5;
                    et_name.setBackgroundColor(WMA.getIndexedColor(et_color));
                    break;

                case R.id.edit_detail_dlg_clr_6:
                    et_color = 6;
                    et_name.setBackgroundColor(WMA.getIndexedColor(et_color));
                    break;

                case R.id.edit_detail_dlg_clr_7:
                    et_color = 7;
                    et_name.setBackgroundColor(WMA.getIndexedColor(et_color));
                    break;

                default:
                    break;
            }
        }
    };

    // /////////////////////////////////////////////////////////////////////////////////////////
    // длинный клик, удаление записи
    OnItemLongClickListener detailLoggListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            detail_id = id;
            changeNameDetailRecord();
            return false;
        }
    };

    // ------------------------------------------------------------------------------------------
    // редактирование записи
    @SuppressLint("InflateParams")
    public void changeNameDetailRecord() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.s_namedetail);
        final View v = this.getLayoutInflater().inflate(R.layout.edit_detail_dlg_name, null);
        builder.setView(v);
        et_name = (EditText) v.findViewById(R.id.edit_detail_dlg_edittext_name);
        if (detail_id > 0) {
            int nameindex = getDetailCursor().getColumnIndex("name");
            int colorindex = getDetailCursor().getColumnIndex("color");
            String sname = WMA.strnormalize(getDetailCursor().getString(nameindex));
            et_name.setText(sname);
            et_color = getDetailCursor().getInt(colorindex);
            et_name.setBackgroundColor(WMA.getIndexedColor(et_color));
        } else {
            et_name.setText("");
            et_color = 0;
            et_name.setBackgroundColor(WMA.getIndexedColor(et_color));
        }

        final ImageButton ib_0 = (ImageButton) v.findViewById(R.id.edit_detail_dlg_clr_0);
        final ImageButton ib_1 = (ImageButton) v.findViewById(R.id.edit_detail_dlg_clr_1);
        final ImageButton ib_2 = (ImageButton) v.findViewById(R.id.edit_detail_dlg_clr_2);
        final ImageButton ib_3 = (ImageButton) v.findViewById(R.id.edit_detail_dlg_clr_3);
        final ImageButton ib_4 = (ImageButton) v.findViewById(R.id.edit_detail_dlg_clr_4);
        final ImageButton ib_5 = (ImageButton) v.findViewById(R.id.edit_detail_dlg_clr_5);
        final ImageButton ib_6 = (ImageButton) v.findViewById(R.id.edit_detail_dlg_clr_6);
        final ImageButton ib_7 = (ImageButton) v.findViewById(R.id.edit_detail_dlg_clr_7);
        ib_0.setOnClickListener(onBtnClick);
        ib_1.setOnClickListener(onBtnClick);
        ib_2.setOnClickListener(onBtnClick);
        ib_3.setOnClickListener(onBtnClick);
        ib_4.setOnClickListener(onBtnClick);
        ib_5.setOnClickListener(onBtnClick);
        ib_6.setOnClickListener(onBtnClick);
        ib_7.setOnClickListener(onBtnClick);

        builder.setNegativeButton(R.string.s_cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        builder.setPositiveButton(R.string.s_save, new OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String sname = WMA.strnormalize(et_name.getText().toString().trim());
                if (!sname.equals("")) {

                    if (detail_id > 0) {
                        ContentValues editDetail = new ContentValues();
                        editDetail.put("name", sname);
                        editDetail.put("color", et_color);
                        WMA.getDatabase().update("tbl", editDetail, "_id=" + detail_id, null);
                    } else {
                        ContentValues insertDetail = new ContentValues();
                        insertDetail.put("name", sname);
                        insertDetail.put("color", et_color);
                        insertDetail.put("_id_par", MASTER_ID);
                        WMA.getDatabase().insert("tbl", null, insertDetail);
                    }
                    getDetailAdapter().swapCursor(mydetailCursor());
                    DB_OpenHelper.generateSearhUp(MASTER_ID);
                    detailModified = true;
                    et_name.setText("");
                }
            }
        });

        builder.setNeutralButton(R.string.s_delete, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                deleteDetailRecord();
            }
        });

        final AlertDialog dlg = builder.create();
        dlg.show();
    }

}
