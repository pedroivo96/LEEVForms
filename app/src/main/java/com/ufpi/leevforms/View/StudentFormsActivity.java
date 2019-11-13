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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevforms.Adapter.FormsAdapter;
import com.ufpi.leevforms.Model.Form;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.NavigationDrawerUtils;

import java.util.ArrayList;

public class StudentFormsActivity extends AppCompatActivity {

    private String studentId;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FormsAdapter formsAdapter;

    private SharedPreferences prefs = null;

    private LinearLayout linearLayoutStudentForms;
    private LinearLayout linearLayoutNoStudentForms;

    private ListView lStudentForms;
    private ArrayList<Form> forms;

    private DatabaseReference mDatabaseForms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_forms);

        prefs = getSharedPreferences(ConstantUtils.APPLICATION_ID, MODE_PRIVATE);

        studentId = getIntent().getStringExtra("studentId");

        linearLayoutStudentForms = findViewById(R.id.linearLayoutStudentForms);
        linearLayoutNoStudentForms = findViewById(R.id.linearLayoutNoStudentForms);
        lStudentForms = findViewById(R.id.lStudentForms);

        lStudentForms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getContext(), FormActivity.class);
                intent.putExtra("formId", forms.get(position).getId());
                intent.putExtra("studentId", studentId);
                startActivity(intent);
            }
        });

        configureNavigationDrawer();

        mDatabaseForms = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.FORMS_BRANCH);

        mDatabaseForms
                .child(studentId)
                .addValueEventListener(getStudentForms());
    }

    private ValueEventListener getStudentForms() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    showStudentFormsLayout();
                    notShowNoStudentFormsLayout();

                    Log.i("TAG", "Número de resultados :"+dataSnapshot.getChildrenCount());

                    forms = new ArrayList<>();

                    for(DataSnapshot d : dataSnapshot.getChildren()){

                        Form form = new Form();

                        if((Boolean) d.child(ConstantUtils.FORMS_FIELD_VISIBLE).getValue()){
                            form.setId(d.getKey());
                            form.setName((String) d.child(ConstantUtils.FORMS_FIELD_NAME).getValue());
                            form.setCreationDate((Long) d.child(ConstantUtils.FORMS_FIELD_CREATIONDATE).getValue());
                            form.setDescription((String) d.child(ConstantUtils.FORMS_FIELD_DESCRIPTION).getValue());
                            form.setVisible((Boolean) d.child(ConstantUtils.FORMS_FIELD_VISIBLE).getValue());
                            forms.add(form);
                        }
                    }

                    formsAdapter = new FormsAdapter(forms, getContext());
                    lStudentForms.setAdapter(formsAdapter);
                }
                else{

                    Log.i("TAG", "Sem resultados");
                    showNoStudentFormsLayout();
                    notShowStudentFormsLayout();
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

    private void showStudentFormsLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 10.0f);
        linearLayoutStudentForms.setLayoutParams(params);
    }

    private void notShowStudentFormsLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.0f);
        linearLayoutStudentForms.setLayoutParams(params);
    }

    private void showNoStudentFormsLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 10.0f);
        linearLayoutNoStudentForms.setLayoutParams(params);
    }

    private void notShowNoStudentFormsLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.0f);
        linearLayoutNoStudentForms.setLayoutParams(params);
    }
}
