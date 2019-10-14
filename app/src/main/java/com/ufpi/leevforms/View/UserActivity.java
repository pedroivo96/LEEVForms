package com.ufpi.leevforms.View;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.NavigationDrawerUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class UserActivity extends AppCompatActivity {

    private static final String networkName = "PEDRO";

    //------------------------- NavigationDrawer---------------------------------------------------
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView tUserId;
    private TextView tUserName;
    private TextView tUserEmail;
    private TextView tUserProjects;

    private SharedPreferences prefs = null;

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseFrequencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.USERS_BRANCH);
        mDatabaseFrequencies = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.FREQUENCIES_BRANCH);

        prefs = getSharedPreferences(ConstantUtils.APPLICATION_ID, MODE_PRIVATE);

        tUserId = findViewById(R.id.tUserId);
        tUserName = findViewById(R.id.tUserName);
        tUserEmail = findViewById(R.id.tUserEmail);
        tUserProjects = findViewById(R.id.tUserProjects);

        /* Método responsável por configurar os eventos de edição de informações do usuário ao apertar
         * e segurar cada TextView */
        setUpdateUserInformationEvents();

        configureNavigationDrawer();

        //Busca as informação do usuário logado para exibí-las nessa tela
        mDatabaseUsers
                .orderByChild(ConstantUtils.USER_FIELD_EMAIL)
                .equalTo(prefs.getString(ConstantUtils.USER_FIELD_EMAIL,""))
                .addValueEventListener(getUserInformationListener());

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

    private ValueEventListener getUserInformationListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        tUserId.setText(d.getKey());
                        tUserName.setText((String) d.child(ConstantUtils.USER_FIELD_NAME).getValue());
                        tUserEmail.setText((String) d.child(ConstantUtils.USER_FIELD_EMAIL).getValue());
                        tUserProjects.setText((String) d.child(ConstantUtils.USER_FIELD_PROJECTS).getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void setUpdateUserInformationEvents(){
        tUserName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                createAndShowInputDialog(ConstantUtils.USER_FIELD_NAME);

                return true;
            }
        });

        tUserEmail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                createAndShowInputDialog(ConstantUtils.USER_FIELD_EMAIL);

                return true;
            }
        });

        tUserProjects.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                createAndShowInputDialog(ConstantUtils.USER_FIELD_PROJECTS);

                return true;
            }
        });
    }

    private void createAndShowInputDialog(final String field){

        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = promptsView.findViewById(R.id.eInformation);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                updateField(field, userInput.getText().toString());
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

    private void updateField(String field, String updateInformation){
        HashMap<String, Object> result = new HashMap<>();
        result.put(field, updateInformation);

        mDatabaseUsers
                .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                .updateChildren(result);
    }
}
