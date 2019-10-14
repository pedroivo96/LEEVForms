package com.ufpi.leevforms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ufpi.leevforms.Model.Form;
import com.ufpi.leevforms.Model.Question;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.DateTimeUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FormsAdapter extends BaseAdapter {

    private ArrayList<Form> forms;
    private Context context;

    public FormsAdapter(ArrayList<Form> forms, Context context){
        this.forms = forms;
        this.context = context;
    }

    @Override
    public int getCount() {
        return forms.size();
    }

    @Override
    public Object getItem(int position) {
        return forms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.form_without_remove_item_listview, parent, false);

        Form form = forms.get(position);

        TextView tName = view1.findViewById(R.id.tName);
        TextView tDescription = view1.findViewById(R.id.tDescription);
        TextView tCreationDate = view1.findViewById(R.id.tCreationDate);

        tName.setText(form.getName());
        tDescription.setText(form.getDescription());
        tCreationDate.setText(DateTimeUtils.getDateTimeFromTimeStamp(form.getCreationDate(), DateTimeUtils.DATE_FORMAT_4));

        return view1;
    }
}
