package com.ufpi.leevforms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ufpi.leevforms.Model.Question;
import com.ufpi.leevforms.Model.QuestionAnswer;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;

import java.util.ArrayList;

public class QuestionAnswerAdapter1 extends BaseAdapter {

    private ArrayList<QuestionAnswer> questionAnswers;
    private Context context;

    public QuestionAnswerAdapter1(ArrayList<QuestionAnswer> questionAnswers, Context context){
        this.questionAnswers = questionAnswers;
        this.context = context;
    }

    @Override
    public int getCount() {
        return questionAnswers.size();
    }

    @Override
    public Object getItem(int position) {
        return questionAnswers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.question_answer_listview, parent, false);

        QuestionAnswer questionAnswer = questionAnswers.get(position);

        TextView tQuestionDescription = view1.findViewById(R.id.tQuestionDescription);
        TextView tQuestionAnswerDescription = view1.findViewById(R.id.tQuestionAnswerDescription);

        tQuestionDescription.setText(questionAnswer.getQuestionDescription());
        tQuestionAnswerDescription.setText(questionAnswer.getDescription().toString());

        return view1;
    }
}
