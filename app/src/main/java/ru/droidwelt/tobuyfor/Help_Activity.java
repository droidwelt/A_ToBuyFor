package ru.droidwelt.tobuyfor;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Help_Activity extends Activity {

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        // bar.setSubtitle("Справка");

        TextView tv_help_00 = (TextView) findViewById(R.id.tv_help_00);
        TextView tv_help_01 = (TextView) findViewById(R.id.tv_help_01);
        TextView tv_help_02 = (TextView) findViewById(R.id.tv_help_02);
        TextView tv_help_03 = (TextView) findViewById(R.id.tv_help_03);
        TextView tv_help_04 = (TextView) findViewById(R.id.tv_help_04);
        TextView tv_help_05 = (TextView) findViewById(R.id.tv_help_05);
        TextView tv_help_06 = (TextView) findViewById(R.id.tv_help_06);
        TextView tv_help_07 = (TextView) findViewById(R.id.tv_help_07);
        TextView tv_help_08 = (TextView) findViewById(R.id.tv_help_08);
        TextView tv_help_09 = (TextView) findViewById(R.id.tv_help_09);
        TextView tv_help_10 = (TextView) findViewById(R.id.tv_help_10);
        TextView tv_help_11 = (TextView) findViewById(R.id.tv_help_11);
        TextView tv_help_12 = (TextView) findViewById(R.id.tv_help_12);

        String[] help_list_ans;
        help_list_ans = getResources().getStringArray(R.array.help_list_ans);
        tv_help_00.setText(help_list_ans[0]);
        tv_help_01.setText(help_list_ans[1]);
        tv_help_02.setText(help_list_ans[2]);
        tv_help_03.setText(help_list_ans[3]);
        tv_help_04.setText(help_list_ans[4]);
        tv_help_05.setText(help_list_ans[5]);
        tv_help_06.setText(help_list_ans[6]);
        tv_help_07.setText(help_list_ans[7]);
        tv_help_08.setText(help_list_ans[8]);
        tv_help_09.setText(help_list_ans[9]);
        tv_help_10.setText(help_list_ans[10]);
        tv_help_11.setText(help_list_ans[11]);
        tv_help_12.setText(help_list_ans[12]);


        String[] help_list_qwe;
        help_list_qwe = getResources().getStringArray(R.array.help_list_qwe);
        TextView tv_hh_00 = (TextView) findViewById(R.id.tv_hh_00);
        TextView tv_hh_01 = (TextView) findViewById(R.id.tv_hh_01);
        TextView tv_hh_02 = (TextView) findViewById(R.id.tv_hh_02);
        TextView tv_hh_03 = (TextView) findViewById(R.id.tv_hh_03);
        TextView tv_hh_04 = (TextView) findViewById(R.id.tv_hh_04);
        TextView tv_hh_05 = (TextView) findViewById(R.id.tv_hh_05);
        TextView tv_hh_06 = (TextView) findViewById(R.id.tv_hh_06);
        TextView tv_hh_07 = (TextView) findViewById(R.id.tv_hh_07);
        TextView tv_hh_08 = (TextView) findViewById(R.id.tv_hh_08);
        TextView tv_hh_09 = (TextView) findViewById(R.id.tv_hh_09);
        TextView tv_hh_10 = (TextView) findViewById(R.id.tv_hh_10);
        TextView tv_hh_11 = (TextView) findViewById(R.id.tv_hh_11);
        TextView tv_hh_12 = (TextView) findViewById(R.id.tv_hh_12);

        tv_hh_00.setText(help_list_qwe[0]);
        tv_hh_01.setText(help_list_qwe[1]);
        tv_hh_02.setText(help_list_qwe[2]);
        tv_hh_03.setText(help_list_qwe[3]);
        tv_hh_04.setText(help_list_qwe[4]);
        tv_hh_05.setText(help_list_qwe[5]);
        tv_hh_06.setText(help_list_qwe[6]);
        tv_hh_07.setText(help_list_qwe[7]);
        tv_hh_08.setText(help_list_qwe[8]);
        tv_hh_09.setText(help_list_qwe[9]);
        tv_hh_10.setText(help_list_qwe[10]);
        tv_hh_11.setText(help_list_qwe[11]);
        tv_hh_12.setText(help_list_qwe[12]);


        Bundle extras = getIntent().getExtras(); // получение дополнений;
        if (extras != null) {
            int help_ID = extras.getInt("HELP_ID");
            if (help_ID == 0) tv_help_00.requestFocus();
            if (help_ID == 1) tv_help_01.requestFocus();
            if (help_ID == 2) tv_help_02.requestFocus();
            if (help_ID == 3) tv_help_03.requestFocus();
            if (help_ID == 4) tv_help_04.requestFocus();
            if (help_ID == 5) tv_help_05.requestFocus();
            if (help_ID == 6) tv_help_06.requestFocus();
            if (help_ID == 7) tv_help_07.requestFocus();
            if (help_ID == 8) tv_help_08.requestFocus();
            if (help_ID == 9) tv_help_09.requestFocus();
            if (help_ID == 10) tv_help_10.requestFocus();
            if (help_ID == 11) tv_help_11.requestFocus();
            if (help_ID == 12) tv_help_12.requestFocus();
        }


    }

}
