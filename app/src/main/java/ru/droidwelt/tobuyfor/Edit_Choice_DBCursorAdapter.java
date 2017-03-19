package ru.droidwelt.tobuyfor;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

class Edit_Choice_DBCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	Edit_Choice_DBCursorAdapter(Context context, Cursor c) {
		super(context, c, 0); // FLAG_AUTO_REQUERY.
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {

		TextView tv0 = (TextView) v.findViewById(R.id.edit_item_choice_name);
		int index = Edit_Activity.getChoiceAdapter().getCursor().getColumnIndex("name");
		String s = Edit_Activity.getChoiceAdapter().getCursor().getString(index);
		tv0.setText(s);

		int indexcolor = Edit_Activity.getChoiceAdapter().getCursor().getColumnIndex("color");
		int clr = Edit_Activity.getChoiceAdapter().getCursor().getInt(indexcolor);
		tv0.setBackgroundColor(WMA.getIndexedColor(clr));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.edit_item_choice, parent, false);
	}

}
