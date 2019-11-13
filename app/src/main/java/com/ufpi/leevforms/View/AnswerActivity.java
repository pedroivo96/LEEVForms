package com.ufpi.leevforms.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevforms.Adapter.QuestionAnswerAdapter1;
import com.ufpi.leevforms.Model.Answer;
import com.ufpi.leevforms.Model.QuestionAnswer;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.DateTimeUtils;
import com.ufpi.leevforms.Utils.NavigationDrawerUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class AnswerActivity extends AppCompatActivity {

    private ArrayList<QuestionAnswer> questionAnswers;

    private TextView tAnswerDescription;
    private TextView tAnswerCreationDate;
    private ListView lQuestionAnswers;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private SharedPreferences prefs = null;

    private String answerId;
    private String formId;

    private Answer answer;

    private HashMap<String, String> questionsDescriptions;

    private DatabaseReference mDatabaseForms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        tAnswerDescription = findViewById(R.id.tAnswerDescription);
        tAnswerCreationDate = findViewById(R.id.tAnswerCreationDate);
        lQuestionAnswers = findViewById(R.id.lQuestionAnswers);

        questionAnswers = new ArrayList<>();
        questionsDescriptions = new HashMap<>();

        answerId = getIntent().getStringExtra("answerId");
        formId = getIntent().getStringExtra("formId");

        prefs = getSharedPreferences(ConstantUtils.APPLICATION_ID, MODE_PRIVATE);

        configureNavigationDrawer();

        mDatabaseForms = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.FORMS_BRANCH);

        mDatabaseForms
                .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                .child(formId)
                .child(ConstantUtils.ANSWERS_BRANCH)
                .orderByKey()
                .equalTo(answerId)
                .addListenerForSingleValueEvent(getAnswerInformations());

    }

    private ValueEventListener getAnswerInformations() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){

                        String answerDescription = d.child(ConstantUtils.ANSWERS_FIELD_DESCRIPTION).getValue(String.class);

                        if(answerDescription.isEmpty() || answerDescription.equals(null)){
                            tAnswerDescription.setText("Descrição não informada");
                        }
                        else{
                            tAnswerDescription.setText(answerDescription);
                        }

                        tAnswerCreationDate.setText(DateTimeUtils.getDateTimeFromTimeStamp(d.child(ConstantUtils.ANSWERS_FIELD_CREATIONDATE).getValue(Long.class), DateTimeUtils.DATE_FORMAT_4));

                    }

                    mDatabaseForms
                            .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                            .child(formId)
                            .child(ConstantUtils.QUESTIONS_BRANCH)
                            .addListenerForSingleValueEvent(getQuestionsDescriptions());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getQuestionsDescriptions() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){

                        questionsDescriptions.put(d.getKey(), d.child(ConstantUtils.QUESTIONS_FIELD_DESCRIPTION).getValue(String.class));

                    }

                    //Obter as respostas associadas a cada uma das questões
                    mDatabaseForms
                            .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                            .child(formId)
                            .child(ConstantUtils.ANSWERS_BRANCH)
                            .child(answerId)
                            .child(ConstantUtils.ANSWERS_FIELD_QUESTIONANSWERS)
                            .addListenerForSingleValueEvent(getQuestionAnswers());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getQuestionAnswers() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){

                        QuestionAnswer questionAnswer = new QuestionAnswer();
                        questionAnswer.setIdQuestion(d.getKey());
                        questionAnswer.setDescription((ArrayList<String>) d.child(ConstantUtils.ANSWERS_FIELD_DESCRIPTION).getValue());
                        questionAnswer.setQuestionDescription(questionsDescriptions.get(d.getKey()));

                        questionAnswers.add(questionAnswer);
                    }

                    QuestionAnswerAdapter1 questionAnswerAdapter1 = new QuestionAnswerAdapter1(questionAnswers, getContext());
                    lQuestionAnswers.setAdapter(questionAnswerAdapter1);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
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
}
