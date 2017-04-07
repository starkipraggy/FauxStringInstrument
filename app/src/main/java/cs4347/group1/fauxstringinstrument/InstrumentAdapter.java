package cs4347.group1.fauxstringinstrument;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Wilson Choo on 4/8/2017.
 */

public class InstrumentAdapter extends ArrayAdapter<Field> {
    public InstrumentAdapter(Context context, Field[] fields) {
        super(context, android.R.layout.simple_spinner_item, fields);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Field currField = getItem(position);

        TextView tv = new TextView(getContext());
        tv.setText(currField.getName());

        return tv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Field currField = getItem(position);

        TextView tv = new TextView(getContext());
        tv.setText(currField.getName());

        return tv;
    }

}
