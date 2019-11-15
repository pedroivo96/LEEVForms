package com.ufpi.leevforms.View;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevforms.Adapter.QuestionStatisticsAdapter;
import com.ufpi.leevforms.Model.Question;
import com.ufpi.leevforms.Model.QuestionStatistic;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.NavigationDrawerUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FormStatisticsActivity extends AppCompatActivity {

    private String idForm;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences prefs = null;
    private DatabaseReference mDatabaseForms;

    private ArrayList<Question> questions;
    private HashMap<String, ArrayList<String>> questionAnswers;

    private ArrayList<QuestionStatistic> questionStatistics;
    private ListView lQuestionStatistics;

    private LinearLayout linearLayoutFormStatistics;
    private LinearLayout linearLayoutNoFormStatistics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_statistics);

        idForm = getIntent().getStringExtra(ConstantUtils.FORMS_FIELD_ID);

        linearLayoutFormStatistics = findViewById(R.id.linearLayoutFormStatistics);
        linearLayoutNoFormStatistics = findViewById(R.id.linearLayoutNoFormStatistics);

        mDatabaseForms = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.FORMS_BRANCH);

        prefs = getSharedPreferences(ConstantUtils.APPLICATION_ID, MODE_PRIVATE);

        configureNavigationDrawer();

        questions = new ArrayList<>();
        questionAnswers = new HashMap<>();

        lQuestionStatistics = findViewById(R.id.lQuestionStatistics);

        mDatabaseForms
                .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                .child(idForm)
                .child(ConstantUtils.QUESTIONS_BRANCH)
                .addListenerForSingleValueEvent(getQuestions());
    }

    private ValueEventListener getQuestions() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    Log.i("TAG", String.valueOf(dataSnapshot.getChildrenCount()));

                    for(DataSnapshot d : dataSnapshot.getChildren()){

                        Question question = new Question();
                        question.setId(d.getKey());
                        question.setDescription(d.child(ConstantUtils.QUESTIONS_FIELD_DESCRIPTION).getValue(String.class));
                        question.setType(d.child(ConstantUtils.QUESTIONS_FIELD_TYPE).getValue(Integer.class));
                        question.setOrder(d.child(ConstantUtils.QUESTIONS_FIELD_ORDER).getValue(Integer.class));

                        if(question.getType() != ConstantUtils.QUESTION_TYPE_SUBJETIVE){
                            question.setOptions((ArrayList<String>) d.child(ConstantUtils.QUESTIONS_FIELD_ANSWEROPTIONS).getValue());
                        }

                        questionAnswers.put(question.getId(), new ArrayList<String>());
                        questions.add(question);
                    }

                    Log.i("TAG", String.valueOf(questionAnswers.keySet().size()));

                    mDatabaseForms
                            .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                            .child(idForm)
                            .child(ConstantUtils.ANSWERS_BRANCH)
                            .addListenerForSingleValueEvent(getFormAnswers());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getFormAnswers() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    showMyFormStatisticsLayout();
                    notShowNoFormStatisticsLayout();

                    for(DataSnapshot d : dataSnapshot.getChildren()){

                        Log.i("TAG", d.getKey());

                        if(d.child(ConstantUtils.ANSWERS_FIELD_VISIBLE).getValue(Boolean.class)){

                            for(String questionId : questionAnswers.keySet()){

                                if(d.child(ConstantUtils.ANSWERS_FIELD_QUESTIONANSWERS)
                                        .child(questionId)
                                        .child(ConstantUtils.ANSWERS_FIELD_DESCRIPTION)
                                        .exists()){

                                    ArrayList<String> answers;

                                    answers = (ArrayList<String>) d
                                            .child(ConstantUtils.ANSWERS_FIELD_QUESTIONANSWERS)
                                            .child(questionId)
                                            .child(ConstantUtils.ANSWERS_FIELD_DESCRIPTION)
                                            .getValue();

                                    answers.addAll(questionAnswers.get(questionId));

                                    questionAnswers.put(questionId, answers);

                                }
                            }
                        }
                    }

                    printOnLogQuestionAnswers();
                    configureQuestionStatisticsListView();
                }
                else{
                    showNoFormStatisticsLayout();
                    notShowMyFormStatisticsLayout();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void configureQuestionStatisticsListView() {
        questionStatistics = new ArrayList<>();

        for(String questionId : questionAnswers.keySet()){

            QuestionStatistic questionStatistic = new QuestionStatistic();

            for(Question question : questions){

                if(question.getId().equals(questionId)){
                    questionStatistic.setQuestion(question);
                }
            }

            questionStatistic.setQuestionAnswers(questionAnswers.get(questionId));
            questionStatistics.add(questionStatistic);
        }

        QuestionStatisticsAdapter questionStatisticsAdapter = new QuestionStatisticsAdapter(questionStatistics, getContext());
        lQuestionStatistics.setAdapter(questionStatisticsAdapter);
        questionStatisticsAdapter.notifyDataSetChanged();

    }

    private void printOnLogQuestionAnswers() {

        Log.i("TAG", "Respostas das questões");
        for(String questionId : questionAnswers.keySet()){

            Log.i("TAG", "Respostas da questão "+questionId+" : "+questionAnswers.get(questionId).toString());
        }
    }

    private Context getContext(){
        return this;
    }

    private void configureNavigationDrawer(){
        //----------------------------Configure NavigationDrawer------------------------------------
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_menu_white, getContext().getTheme());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.open_drawer, R.string.close_drawer);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.getMenu().clear();

        View headerView = navigationView.getHeaderView(0);
        TextView nav_header_nome = (TextView) headerView.findViewById(R.id.nav_header_name);
        nav_header_nome.setText(prefs.getString("name", ""));

        TextView nav_header_email = (TextView) headerView.findViewById(R.id.nav_header_email);
        nav_header_email.setText(prefs.getString("email",""));

        if(prefs.getInt(ConstantUtils.USER_FIELD_USERTYPE, -1) == ConstantUtils.USER_TYPE_STUDENT){
            //Usuário é um estudante
            navigationView.inflateMenu(R.menu.menu_student);
        }
        else{
            //Usuário é um professor
            navigationView.inflateMenu(R.menu.menu_teacher);
        }

        //Configura o evento de seleção de algum item do menu do DrawerLayout
        navigationView.setNavigationItemSelectedListener(
                NavigationDrawerUtils.getNavigationDrawerItemSelectedListener(getContext(),
                        prefs.getInt(ConstantUtils.USER_FIELD_USERTYPE,-1), drawerLayout));

    }

    private void showMyFormStatisticsLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 10.0f);
        linearLayoutFormStatistics.setLayoutParams(params);
    }

    private void notShowMyFormStatisticsLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.0f);
        linearLayoutFormStatistics.setLayoutParams(params);
    }

    private void showNoFormStatisticsLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 10.0f);
        linearLayoutNoFormStatistics.setLayoutParams(params);
    }

    private void notShowNoFormStatisticsLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.0f);
        linearLayoutNoFormStatistics.setLayoutParams(params);
    }

}
