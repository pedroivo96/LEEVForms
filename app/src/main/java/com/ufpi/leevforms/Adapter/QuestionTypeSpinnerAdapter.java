package com.ufpi.leevforms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ufpi.leevforms.R;

import java.util.ArrayList;

public class QuestionTypeSpinnerAdapter extends BaseAdapter {

    private ArrayList<String> options;
    private Context context;

    public QuestionTypeSpinnerAdapter(ArrayList<String> options, Context context){
        this.options = options;
        this.context = context;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int i) {
        return options.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.item_spinner, null);

        TextView tOption = (TextView) view1.findViewById(R.id.tOption);
        tOption.setText(options.get(i));

        return view1;
    }
}
