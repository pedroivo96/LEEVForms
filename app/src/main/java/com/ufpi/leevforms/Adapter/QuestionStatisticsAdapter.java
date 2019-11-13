package com.ufpi.leevforms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ufpi.leevforms.Model.QuestionStatistic;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;

import java.util.ArrayList;

public class QuestionStatisticsAdapter extends BaseAdapter {

    private ArrayList<QuestionStatistic> questionStatistics;
    private Context context;

    public QuestionStatisticsAdapter(ArrayList<QuestionStatistic> questionStatistics, Context context){
        this.questionStatistics = questionStatistics;
        this.context = context;
    }

    @Override
    public int getCount() {
        return questionStatistics.size();
    }

    @Override
    public Object getItem(int position) {
        return questionStatistics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view1 = null;

        QuestionStatistic questionStatistic = questionStatistics.get(position);

        if(questionStatistic.getQuestion().getType() == ConstantUtils.QUESTION_TYPE_SUBJETIVE){
            //Caso a questão seja subjetiva
            view1 = inflater.inflate(R.layout.statistics_subjetive_item_listview, parent, false);

            final LinearLayout layoutSubjetiveQuestions = view1.findViewById(R.id.layoutSubjetiveQuestions);

            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(layoutSubjetiveQuestions.getVisibility() == View.VISIBLE){
                        layoutSubjetiveQuestions.setVisibility(View.GONE);
                    }
                    else{
                        layoutSubjetiveQuestions.setVisibility(View.VISIBLE);
                    }
                }
            });

            TextView tQuestionDescription = view1.findViewById(R.id.tQuestionDescription);
            tQuestionDescription.setText(questionStatistic.getQuestion().getDescription());

            for(int i = 0; i < questionStatistic.getQuestionAnswers().size(); i++){

                View answerLayout= inflater.inflate(R.layout.question_answer_subjetive, (ViewGroup) layoutSubjetiveQuestions, false);

                TextView tQuestionAnswer = answerLayout.findViewById(R.id.tQuestionAnswer);
                tQuestionAnswer.setText(questionStatistic.getQuestionAnswers().get(i));

                layoutSubjetiveQuestions.addView(answerLayout);

                View separator= inflater.inflate(R.layout.horizontal_separator, (ViewGroup) layoutSubjetiveQuestions, false);

                if(i != questionStatistic.getQuestionAnswers().size()-1){

                    layoutSubjetiveQuestions.addView(separator);
                }
            }
        }
        else{
            //Caso a questão seja objetiva
            view1 = inflater.inflate(R.layout.statistics_objetive_item_listview, parent, false);

            LinearLayout layoutObjetiveQuestions = view1.findViewById(R.id.layoutObjetiveQuestions);
            TextView tQuestionDescription = view1.findViewById(R.id.tQuestionDescription);
            tQuestionDescription.setText(questionStatistic.getQuestion().getDescription());

            for(String answerOption : questionStatistic.getQuestion().getOptions()){

                View answerLayout = inflater.inflate(R.layout.question_answer_objetive, (ViewGroup) layoutObjetiveQuestions, false);

                TextView tQuestionAnswerOption = answerLayout.findViewById(R.id.tQuestionAnswerOption);
                TextView tAnswerOptionCount = answerLayout.findViewById(R.id.tAnswerOptionCount);

                tQuestionAnswerOption.setText(answerOption);

                int answerOptionCount = 0;
                for(String answer : questionStatistic.getQuestionAnswers()){
                    if(answerOption.equals(answer)){
                        answerOptionCount++;
                    }
                }

                tAnswerOptionCount.setText(String.valueOf(answerOptionCount));
                layoutObjetiveQuestions.addView(answerLayout);
            }

        }

        return view1;
    }
}
