package ru.droidwelt.tobuyfor;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

class Edit_Detail_DBCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	Edit_Detail_DBCursorAdapter(Context context, Cursor c) {
		super(context, c, 0); // FLAG_AUTO_REQUERY.
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {

		TextView tv0 = (TextView) v.findViewById(R.id.edit_item_detail_name);
		int indexname = Edit_Activity.getDetailAdapter().getCursor().getColumnIndex("name");
		String s = Edit_Activity.getDetailAdapter().getCursor().getString(indexname);
		tv0.setText(s);

		int indexDone = Edit_Activity.getDetailAdapter().getCursor().getColumnIndex("done");
		s = WMA.strnormalize(Edit_Activity.getDetailAdapter().getCursor().getString(indexDone));
		if (s.equals("OK")) {
			tv0.setBackgroundColor(context.getResources().getColor(R.color.c_bgr_done));
		} else {
			int indexcolor = Edit_Activity.getDetailAdapter().getCursor().getColumnIndex("color");
			int clr = Edit_Activity.getDetailAdapter().getCursor().getInt(indexcolor);
			tv0.setBackgroundColor(WMA.getIndexedColor(clr));
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.edit_item_detail, parent, false);
	}

}
