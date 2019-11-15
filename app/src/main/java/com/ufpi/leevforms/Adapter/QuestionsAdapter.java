package com.ufpi.leevforms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ufpi.leevforms.Model.Question;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class QuestionsAdapter extends BaseAdapter {

    private ArrayList<Question> questions;
    private Context context;

    public QuestionsAdapter(ArrayList<Question> questions, Context context){
        this.questions = questions;
        this.context = context;

        Collections.sort(this.questions, new Comparator<Question>() {
            @Override
            public int compare(Question q1, Question q2) {
                if (q1.getOrder() > q2.getOrder())
                {
                    return 1;
                }
                else if (q1.getOrder() < q2.getOrder())
                {
                    return -1;
                }
                return 0;
            }
        });
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int position) {
        return questions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.question_without_remove_item_listview, parent, false);

        Question question = questions.get(position);

        TextView tQuestionDescription = view1.findViewById(R.id.tQuestionDescription);
        TextView tQuestionType = view1.findViewById(R.id.tQuestionType);
        TextView tQuestionsAnswersOptions = view1.findViewById(R.id.tQuestionsAnswersOptions);
        LinearLayout linearLayoutQuestionOptions = view1.findViewById(R.id.linearLayoutQuestionOptions);

        tQuestionDescription.setText(question.getDescription());

        if(question.getType() == ConstantUtils.QUESTION_TYPE_SUBJETIVE) tQuestionType.setText("(Subjetiva)");
        if(question.getType() == ConstantUtils.QUESTION_TYPE_OBJETIVE_SINGLE_ANSWER) {
            tQuestionType.setText("(Objetiva com resposta única)");
            //tQuestionsAnswersOptions.setText(question.getOptions().toString());

            for(String questionOption : question.getOptions()){
                View questionAnswerView = inflater.inflate(R.layout.textview_question_option, linearLayoutQuestionOptions, false);
                TextView tQuestionOption = questionAnswerView.findViewById(R.id.tQuestionOption);

                tQuestionOption.setText(questionOption);

                linearLayoutQuestionOptions.addView(questionAnswerView);
            }
        }
        if(question.getType() == ConstantUtils.QUESTION_TYPE_OBJETIVE_MULTIPLE_ANSWER) {
            tQuestionType.setText("(Objetiva com múltiplas respostas)");
            //tQuestionsAnswersOptions.setText(question.getOptions().toString());

            for(String questionOption : question.getOptions()){
                View questionAnswerView = inflater.inflate(R.layout.textview_question_option, linearLayoutQuestionOptions, false);
                TextView tQuestionOption = questionAnswerView.findViewById(R.id.tQuestionOption);

                tQuestionOption.setText(questionOption);

                linearLayoutQuestionOptions.addView(questionAnswerView);
            }
        }

        return view1;
    }
}
