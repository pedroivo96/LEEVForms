package com.ufpi.leevforms.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ufpi.leevforms.Model.Question;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionAnswerAdapter extends BaseAdapter {

    private ArrayList<Question> questions;
    private Context context;

    public QuestionAnswerAdapter(ArrayList<Question> questions, Context context){
        this.questions = questions;
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view1 = null;

        switch (questions.get(position).getType()){
            case ConstantUtils.QUESTION_TYPE_SUBJETIVE:{

                view1 = inflater.inflate(R.layout.question_subjetive_type_item_listview, parent, false);

                TextView questionDescription = view1.findViewById(R.id.questionDescription);
                questionDescription.setText(questions.get(position).getDescription());

                final EditText questionAnswer = view1.findViewById(R.id.questionAnswer);

                questionAnswer.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        questions.get(position).setAnswers(new ArrayList<String>());
                        questions.get(position).getAnswers().add(s.toString());
                        Log.i("TAG", "String digitada :"+s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                break;
            }
            case ConstantUtils.QUESTION_TYPE_OBJETIVE_SINGLE_ANSWER:{

                view1 = inflater.inflate(R.layout.question_objetive_single_item_listview, parent, false);

                TextView questionDescription = view1.findViewById(R.id.questionDescription);
                questionDescription.setText(questions.get(position).getDescription());

                RadioGroup questionRadioGroup = view1.findViewById(R.id.questionRadioGroup);

                for(String answerOption : questions.get(position).getOptions()){
                    final RadioButton radioButton = new RadioButton(context);
                    radioButton.setId(View.generateViewId());
                    radioButton.setText(answerOption);

                    if(Build.VERSION.SDK_INT>=21)
                    {

                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{

                                        new int[]{-android.R.attr.state_enabled}, //disabled
                                        new int[]{android.R.attr.state_enabled} //enabled
                                },
                                new int[] {

                                        context.getResources().getColor(R.color.primaryTextColor) //disabled
                                        , context.getResources().getColor(R.color.secondaryColor) //enabled

                                }
                        );


                        radioButton.setButtonTintList(colorStateList);//set the color tint list
                        radioButton.invalidate(); //could not be necessary
                    }

                    questionRadioGroup.addView(radioButton);

                    radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){

                                questions.get(position).setAnswers(new ArrayList<String>());
                                questions.get(position).getAnswers().add(radioButton.getText().toString());
                            }
                        }
                    });
                }

                break;
            }
            case ConstantUtils.QUESTION_TYPE_OBJETIVE_MULTIPLE_ANSWER:{

                view1 = inflater.inflate(R.layout.question_objetive_multiple_item_listview, parent, false);

                LinearLayout checkBoxLayout = view1.findViewById(R.id.checkBoxLayout);
                TextView questionDescription = view1.findViewById(R.id.questionDescription);
                questionDescription.setText(questions.get(position).getDescription());

                final HashMap<CheckBox, Boolean> hashMap = new HashMap<>();

                for(String answerOption : questions.get(position).getOptions()){

                    final CheckBox checkBox = new CheckBox(context);
                    checkBox.setId(View.generateViewId());
                    checkBox.setText(answerOption);

                    hashMap.put(checkBox, false);

                    if(Build.VERSION.SDK_INT>=21)
                    {

                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{

                                        new int[]{-android.R.attr.state_enabled}, //disabled
                                        new int[]{android.R.attr.state_enabled} //enabled
                                },
                                new int[] {

                                        context.getResources().getColor(R.color.primaryTextColor) //disabled
                                        , context.getResources().getColor(R.color.secondaryColor) //enabled

                                }
                        );


                        checkBox.setButtonTintList(colorStateList);//set the color tint list
                        checkBox.invalidate(); //could not be necessary
                    }

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            hashMap.put(checkBox, isChecked);

                            questions.get(position).setAnswers(new ArrayList<String>());

                            for(CheckBox checkBox1 : hashMap.keySet()){
                                if(hashMap.get(checkBox1)){
                                    questions.get(position).getAnswers().add(checkBox1.getText().toString());
                                }
                            }
                        }
                    });

                    checkBoxLayout.addView(checkBox);
                }

                break;
            }
        }

        return view1;
    }
}
