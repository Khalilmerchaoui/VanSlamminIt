package app.vanslamminit;

/**
 * Created by user on 13/09/2016.
 */
import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<String> {

    List<String> title = new ArrayList<String>();
    Context c;
    LayoutInflater inflater;

    TextView titleTv;
    Button edit;
    public ListAdapter(Context context, List<String> title) {
        super(context, R.layout.custom_list, title);
        this.c = context;
        this.title = title;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_list, null);
        }
        titleTv = (TextView) convertView.findViewById(R.id.text);
        edit = (Button) convertView.findViewById(R.id.edit);
        edit.setFocusable(false);
        titleTv.setText(title.get(position));
        return convertView;
    }


}
