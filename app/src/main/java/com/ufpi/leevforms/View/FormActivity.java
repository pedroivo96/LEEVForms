package com.ufpi.leevforms.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;

public class FormActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences prefs = null;
    private DatabaseReference mDatabaseForms;
    private String idForm;
    private String studentId;

    private TextView tName;
    private TextView tDescription;
    private TextView tCreationDate;
    private ListView lQuestions;

    private Form form;

    private ArrayList<Question> questions;

    private QuestionsAdapter questionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        tName = findViewById(R.id.tName);
        tDescription = findViewById(R.id.tDescription);
        tCreationDate = findViewById(R.id.tCreationDate);
        lQuestions = findViewById(R.id.lQuestions);

        lQuestions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                createAndShowFormsOptionsMenu(questions.get(position).getId(), position);

                return true;
            }
        });

        idForm = getIntent().getStringExtra("formId");
        studentId = getIntent().getStringExtra("studentId");
        Log.i("TAG", "Depois de selecionar o Form :"+idForm);

        mDatabaseForms = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.FORMS_BRANCH);

        prefs = getSharedPreferences(ConstantUtils.APPLICATION_ID, MODE_PRIVATE);

        configureNavigationDrawer();

        if(studentId == null){
            mDatabaseForms
                    .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                    .orderByKey()
                    .equalTo(idForm)
                    .addListenerForSingleValueEvent(getFormInformations());
        }
        else{
            mDatabaseForms
                    .child(studentId)
                    .orderByKey()
                    .equalTo(idForm)
                    .addListenerForSingleValueEvent(getFormInformations());
        }
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

                    for(DataSnapshot d : dataSnapshot.getChildren()){

                        Question question = new Question();
                        question.setId(d.getKey());
                        question.setDescription((String) d.child(ConstantUtils.QUESTIONS_FIELD_DESCRIPTION).getValue());
                        question.setType(d.child(ConstantUtils.QUESTIONS_FIELD_TYPE).getValue(Integer.class));
                        question.setOptions((ArrayList<String>) d.child(ConstantUtils.QUESTIONS_FIELD_ANSWEROPTIONS).getValue());
                        question.setOrder(d.child(ConstantUtils.QUESTIONS_FIELD_ORDER).getValue(Integer.class));

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
            Intent intent = new Intent(getContext(), FormStatisticsActivity.class);
            intent.putExtra(ConstantUtils.FORMS_FIELD_ID, idForm);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAndShowFormsOptionsMenu(final String idQuestion, final int position){

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.dialog_list_textview, R.id.textView1);
        arrayAdapter.add("Remover");

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(null);
        builderSingle.setTitle("Menu");

        builderSingle.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String strName = arrayAdapter.getItem(which);

                switch (which){
                    case 0:
                        //Remover
                        createAndShowRemoveFormDialog(idQuestion, position);
                        break;
                }
            }
        });
        builderSingle.show();
    }

    private void createAndShowRemoveFormDialog(final String idQuestion, final int position){

        AlertDialog alerta;

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        //define o titulo
        builder.setTitle("Remoção");
        //define a mensagem
        builder.setMessage("Você realmente deseja remover esse questão ?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(getContext(), "positivo=" + arg1, Toast.LENGTH_SHORT).show();

                mDatabaseForms
                        .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                        .child(idForm)
                        .child(ConstantUtils.QUESTIONS_BRANCH)
                        .child(idQuestion)
                        .setValue(null);

                questions.remove(position);
                questionsAdapter.notifyDataSetChanged();
                //myStudents.remove(position);

            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(getContext(), "negativo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }

}
