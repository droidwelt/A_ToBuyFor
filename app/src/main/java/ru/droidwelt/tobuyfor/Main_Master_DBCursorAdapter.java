package ru.droidwelt.tobuyfor;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class Main_Master_DBCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    Main_Master_DBCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags); // FLAG_AUTO_REQUERY.
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {

        TextView tv0 = (TextView) v.findViewById(R.id.main_item_master_name);
        int indexname = Main_Activity.getMasterAdapter().getCursor().getColumnIndex("name");
        String s = WMA.strnormalize(Main_Activity.getMasterAdapter().getCursor().getString(indexname));
        tv0.setText(s);

        int indexdone = Main_Activity.getMasterAdapter().getCursor().getColumnIndex("done");
        String sdone = WMA.strnormalize(Main_Activity.getMasterAdapter().getCursor().getString(indexdone));
        ImageView iv_done = (ImageView) v.findViewById(R.id.main_item_master_done);
        if (sdone.equals(""))
            iv_done.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star));
        else
            iv_done.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_done));

        int indexcolor = Main_Activity.getMasterAdapter().getCursor().getColumnIndex("color");
        int clr = Main_Activity.getMasterAdapter().getCursor().getInt(indexcolor);
        v.setBackgroundColor(WMA.getIndexedColor(clr));

        int id = Main_Activity.getMasterAdapter().getCursor().getInt(0);
        Cursor detCursor = WMA.getDatabase().rawQuery("SELECT name FROM tbl WHERE _id_par=" + id + " ORDER BY name", null);
        int detindex = detCursor.getColumnIndex("name");
        String sdet = "";
        if (detCursor.moveToFirst()) {
            sdet = sdet + WMA.strnormalize(detCursor.getString(detindex)) + " ";
            while (detCursor.moveToNext()) {
                sdet = sdet + WMA.strnormalize(detCursor.getString(detindex)) + " ";
            }
        }
        detCursor.close();
        TextView tv1 = (TextView) v.findViewById(R.id.main_item_master_subname);
        tv1.setText(sdet);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.main_item_master, parent, false);
    }

}
