package com.ufpi.leevforms.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevforms.Adapter.QuestionAnswerAdapter;
import com.ufpi.leevforms.Adapter.QuestionsAdapter;
import com.ufpi.leevforms.Model.Form;
import com.ufpi.leevforms.Model.Question;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.DateTimeUtils;
import com.ufpi.leevforms.Utils.NavigationDrawerUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class AnswerRegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences prefs = null;
    private DatabaseReference mDatabaseForms;
    private String id;

    private ListView lQuestions;

    private Form form;

    private ArrayList<Question> questions;

    private TextInputLayout textInputLayoutDescription;
    private EditText eDescription;

    private CoordinatorLayout myCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_register);

        lQuestions = findViewById(R.id.lQuestions);
        textInputLayoutDescription = findViewById(R.id.textInputLayoutDescription);
        eDescription = findViewById(R.id.eDescription);
        myCoordinatorLayout = findViewById(R.id.myCoordinatorLayout);

        lQuestions.setItemsCanFocus(true);

        id = getIntent().getStringExtra(ConstantUtils.FORMS_FIELD_ID);
        Log.i("TAG", "Depois de mudar de intent :"+id);

        mDatabaseForms = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.FORMS_BRANCH);

        prefs = getSharedPreferences(ConstantUtils.APPLICATION_ID, MODE_PRIVATE);

        configureNavigationDrawer();

        mDatabaseForms
                .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                .orderByKey()
                .equalTo(id)
                .addListenerForSingleValueEvent(getFormInformations());
    }

    private ValueEventListener getFormInformations() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){

                        form = new Form();

                        form.setId(d.getKey());
                        form.setName((String) d.child(ConstantUtils.FORMS_FIELD_NAME).getValue());
                        form.setDescription((String) d.child(ConstantUtils.FORMS_FIELD_DESCRIPTION).getValue());
                        form.setCreationDate((Long) d.child(ConstantUtils.FORMS_FIELD_CREATIONDATE).getValue());

                        mDatabaseForms
                                .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                                .child(form.getId())
                                .child(ConstantUtils.QUESTIONS_BRANCH)
                                .addListenerForSingleValueEvent(getFormQuestions());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getFormQuestions() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    questions = new ArrayList<>();
                    QuestionAnswerAdapter questionsAdapter;

                    for(DataSnapshot d : dataSnapshot.getChildren()){

                        Question question = new Question();
                        question.setId(d.getKey());
                        question.setDescription((String) d.child(ConstantUtils.QUESTIONS_FIELD_DESCRIPTION).getValue());
                        question.setType(d.child(ConstantUtils.QUESTIONS_FIELD_TYPE).getValue(Integer.class));
                        question.setOptions((ArrayList<String>) d.child(ConstantUtils.QUESTIONS_FIELD_ANSWEROPTIONS).getValue());

                        questions.add(question);
                    }

                    questionsAdapter = new QuestionAnswerAdapter(questions, getContext());
                    lQuestions.setAdapter(questionsAdapter);
                    questionsAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_answer_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.finalizeAnswerRegister) {

            for(Question question : questions){

                for(String answer : question.getAnswers()){
                    Log.i("TAG", "Resposta da questão "+question.getId()+": "+answer);
                }
            }
            registerFormAnswer();
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerFormAnswer(){

        DatabaseReference elementReference = mDatabaseForms
                .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                .child(form.getId())
                .child(ConstantUtils.ANSWERS_BRANCH)
                .push();

        elementReference.child(ConstantUtils.ANSWERS_FIELD_CREATIONDATE).setValue(Calendar.getInstance().getTime().getTime());
        elementReference.child(ConstantUtils.ANSWERS_FIELD_DESCRIPTION).setValue(eDescription.getText().toString());
        elementReference.child(ConstantUtils.ANSWERS_FIELD_VISIBLE).setValue(true);

        for(Question question : questions){
            elementReference
                    .child(ConstantUtils.ANSWERS_FIELD_QUESTIONANSWERS)
                    .child(question.getId())
                    .child(ConstantUtils.ANSWERS_FIELD_DESCRIPTION)
                    .setValue(question.getAnswers());
        }

        simplySnackbar("Formulário cadastrado com sucesso");

        ((Activity) getContext()).finish();

    }

    public void simplySnackbar(String message){

        Snackbar snackbar = Snackbar.make(myCoordinatorLayout,message, Snackbar.LENGTH_LONG);
        snackbar.show();

    }
}
