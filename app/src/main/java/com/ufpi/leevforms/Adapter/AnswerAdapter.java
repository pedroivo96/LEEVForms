package com.ufpi.leevforms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ufpi.leevforms.Model.Answer;
import com.ufpi.leevforms.Model.Form;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.DateTimeUtils;

import java.util.ArrayList;

public class AnswerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Answer> answers;

    public AnswerAdapter(ArrayList<Answer> answers, Context context){
        this.answers = answers;
        this.context = context;
    }

    @Override
    public int getCount() {
        return answers.size();
    }

    @Override
    public Object getItem(int position) {
        return answers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.answer_without_remove_item_listview, parent, false);

        Answer answer = answers.get(position);

        TextView tAnswerDescription = view1.findViewById(R.id.tAnswerDescription);
        TextView tAnswerCreationDate = view1.findViewById(R.id.tAnswerCreationDate);

        if(answer.getDescription().equals(null) || answer.getDescription().isEmpty()){
            tAnswerDescription.setText("Descrição não informada");
        }
        else{
            tAnswerDescription.setText(answer.getDescription());
        }

        tAnswerCreationDate.setText(DateTimeUtils.getDateTimeFromTimeStamp(answer.getCreationDate(), DateTimeUtils.DATE_FORMAT_4));

        return view1;
    }
}
