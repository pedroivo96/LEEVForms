package com.ufpi.leevforms.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ufpi.leevforms.Adapter.QuestionTypeSpinnerAdapter;
import com.ufpi.leevforms.Adapter.QuestionsAdapter;
import com.ufpi.leevforms.Model.Question;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.DateTimeUtils;
import com.ufpi.leevforms.Utils.NavigationDrawerUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FormRegisterActivity extends AppCompatActivity {

    //------------------------- NavigationDrawer---------------------------------------------------
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private SharedPreferences prefs = null;
    private DatabaseReference mDatabaseForms;

    private ArrayList<Question> questions;

    private String selectedQuestionType;

    private ListView lQuestions;

    private QuestionsAdapter questionsAdapter;

    private EditText eName;
    private EditText eDescription;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutDescription;

    private CoordinatorLayout myCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_register);

        eName = findViewById(R.id.eName);
        eDescription = findViewById(R.id.eDescription);
        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutDescription = findViewById(R.id.textInputLayoutDescription);
        myCoordinatorLayout = findViewById(R.id.myCoordinatorLayout);

        questions = new ArrayList<>();

        lQuestions = findViewById(R.id.lQuestions);
        questionsAdapter = new QuestionsAdapter(questions, getContext());
        lQuestions.setAdapter(questionsAdapter);

        lQuestions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                createAndShowQuestionsOptionsMenu(questions.get(position).getId(), position);
                return true;
            }
        });

        prefs = getSharedPreferences(ConstantUtils.APPLICATION_ID, MODE_PRIVATE);

        mDatabaseForms = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.FORMS_BRANCH);

        configureNavigationDrawer();
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
        getMenuInflater().inflate(R.menu.menu_toolbar_form_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_question) {
            createAndShowNewQuestionDialog();
        }
        if (id == R.id.form_register) {
            finalizeFormRegister();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createAndShowNewQuestionDialog(){

        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.new_question_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);

        alertDialogBuilder.setView(promptsView);

        final EditText eQuestionDescription = promptsView.findViewById(R.id.eQuestionDescription);
        final Spinner spQuestionType = promptsView.findViewById(R.id.spQuestionType);

        configureQuestionTypeSpinner(spQuestionType);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                Question question = new Question();
                                question.setDescription(eQuestionDescription.getText().toString());
                                question.setType(spQuestionType.getSelectedItemPosition()+1);

                                questions.add(question);

                                questionsAdapter.notifyDataSetChanged();

                                Log.i("TAG", String.valueOf(questions.size()));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void configureQuestionTypeSpinner(Spinner spQuestionType){
        String[] typeArray = getResources().getStringArray(R.array.question_types);

        ArrayList<String> options = new ArrayList<>();
        options.add(typeArray[0]);
        options.add(typeArray[1]);
        options.add(typeArray[2]);

        SpinnerAdapter mCustomAdapter = new QuestionTypeSpinnerAdapter(options, getContext());
        spQuestionType.setAdapter(mCustomAdapter);

        spQuestionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedQuestionType = (String) adapterView.getItemAtPosition(i);
                int selectedId = i+1;

                Log.i("TAG", selectedQuestionType+": "+selectedId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void finalizeFormRegister(){

        clearErrorName();
        clearErrorDescription();

        if(eName.getText().toString().isEmpty() || eDescription.getText().toString().isEmpty()){
            checkEmptyFields();

        }else{

            if(questions.size() == 0){
                Toast.makeText(getContext(), "Não há nenhuma pergunta cadastrada", Toast.LENGTH_SHORT).show();
            }
            else{
                //Pode continuar
                registerForm();
            }
        }
    }

    private void registerForm(){

        //Salvar as informações principais do Formulário
        DatabaseReference elementReference = mDatabaseForms.child(prefs.getString(ConstantUtils.USER_FIELD_ID, "")).push();

        elementReference.child(ConstantUtils.FORMS_FIELD_NAME).setValue(eName.getText().toString());
        elementReference.child(ConstantUtils.FORMS_FIELD_DESCRIPTION).setValue(eDescription.getText().toString());
        elementReference.child(ConstantUtils.FORMS_FIELD_CREATIONDATE).setValue(Calendar.getInstance().getTime().getTime());

        Log.i("TAG", DateTimeUtils.getDateTimeFromTimeStamp(Calendar.getInstance().getTime().getTime(), DateTimeUtils.DATE_FORMAT_8));

        //Salvar as questões
        for(Question question : questions){

            DatabaseReference elementReference1 = elementReference.child(ConstantUtils.QUESTIONS_BRANCH).push();

            elementReference1.child(ConstantUtils.QUESTIONS_FIELD_DESCRIPTION).setValue(question.getDescription());
            elementReference1.child(ConstantUtils.QUESTIONS_FIELD_TYPE).setValue(question.getType());

            if(question.getOptions().size() > 0){
                elementReference1.child(ConstantUtils.QUESTIONS_FIELD_ANSWEROPTIONS).setValue(question.getOptions());
            }
        }

        simplySnackbar("Formulário cadastrado com sucesso");
    }

    private void checkEmptyFields(){
        if(eName.getText().toString().isEmpty()){
            enableAndShowErrorName("O campo está vazio");
        }
        if(eDescription.getText().toString().isEmpty()){
            enableAndShowErrorDescription("O campo está vazio");
        }
    }

    private void enableAndShowErrorName(String errorMessage){
        textInputLayoutName.setErrorEnabled(true);
        textInputLayoutName.setError(errorMessage);
    }

    private void enableAndShowErrorDescription(String errorMessage){
        textInputLayoutDescription.setErrorEnabled(true);
        textInputLayoutDescription.setError(errorMessage);
    }

    private void clearErrorName(){
        textInputLayoutName.setErrorEnabled(false);
        textInputLayoutName.setError(null);
    }

    private void clearErrorDescription(){
        textInputLayoutDescription.setErrorEnabled(false);
        textInputLayoutDescription.setError(null);
    }

    public void simplySnackbar(String message){

        Snackbar snackbar = Snackbar.make(myCoordinatorLayout,message, Snackbar.LENGTH_LONG);
        snackbar.show();

    }

    private void createAndShowQuestionsOptionsMenu(final String idQuestion, final int position){

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.dialog_list_textview, R.id.textView1);
        arrayAdapter.add("Remover");
        arrayAdapter.add("Adicionar opção de resposta");

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
                        questions.remove(position);
                        break;
                    case 1:

                        //Adicionar opção de resposta
                        createAndShowNewAnswerOptionDialog(position);
                        break;
                }
            }
        });
        builderSingle.show();
    }

    private void createAndShowNewAnswerOptionDialog(final int questionPosition){
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.new_answer_option_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);

        alertDialogBuilder.setView(promptsView);

        final EditText eAnswerOption = promptsView.findViewById(R.id.eAnswerOption);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                questions.get(questionPosition).getOptions().add(eAnswerOption.getText().toString());
                                questionsAdapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
