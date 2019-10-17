package com.ufpi.leevforms.View;

import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevforms.Adapter.QuestionsAdapter;
import com.ufpi.leevforms.Model.Form;
import com.ufpi.leevforms.Model.Question;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.DateTimeUtils;
import com.ufpi.leevforms.Utils.NavigationDrawerUtils;

import java.util.ArrayList;

public class FormActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences prefs = null;
    private DatabaseReference mDatabaseForms;
    private String idForm;

    private TextView tName;
    private TextView tDescription;
    private TextView tCreationDate;
    private ListView lQuestions;

    private Form form;

    private ArrayList<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        tName = findViewById(R.id.tName);
        tDescription = findViewById(R.id.tDescription);
        tCreationDate = findViewById(R.id.tCreationDate);
        lQuestions = findViewById(R.id.lQuestions);

        idForm = getIntent().getStringExtra(ConstantUtils.FORMS_FIELD_ID);
        Log.i("TAG", "Depois de selecionar o Form :"+idForm);

        mDatabaseForms = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.FORMS_BRANCH);

        prefs = getSharedPreferences(ConstantUtils.APPLICATION_ID, MODE_PRIVATE);

        configureNavigationDrawer();

        mDatabaseForms
                .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                .orderByKey()
                .equalTo(idForm)
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

                        tName.setText(form.getName());
                        tDescription.setText(form.getDescription());
                        tCreationDate.setText(DateTimeUtils.getDateTimeFromTimeStamp(form.getCreationDate(), DateTimeUtils.DATE_FORMAT_4));

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
                    QuestionsAdapter questionsAdapter;

                    for(DataSnapshot d : dataSnapshot.getChildren()){

                        Question question = new Question();
                        question.setId(d.getKey());
                        question.setDescription((String) d.child(ConstantUtils.QUESTIONS_FIELD_DESCRIPTION).getValue());
                        question.setType(d.child(ConstantUtils.QUESTIONS_FIELD_TYPE).getValue(Integer.class));
                        question.setOptions((ArrayList<String>) d.child(ConstantUtils.QUESTIONS_FIELD_ANSWEROPTIONS).getValue());

                        questions.add(question);
                    }

                    questionsAdapter = new QuestionsAdapter(questions, getContext());
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
        getMenuInflater().inflate(R.menu.menu_toolbar_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.newAnswer) {

            Intent intent = new Intent(getContext(), AnswerRegisterActivity.class);
            intent.putExtra(ConstantUtils.FORMS_FIELD_ID, idForm);
            startActivity(intent);
        }
        if(id == R.id.formAnswers){

            Intent intent = new Intent(getContext(), FormListAnswersActivity.class);
            intent.putExtra(ConstantUtils.FORMS_FIELD_ID, idForm);
            startActivity(intent);
        }
        if(id == R.id.formStatistics){

        }

        return super.onOptionsItemSelected(item);
    }

}
