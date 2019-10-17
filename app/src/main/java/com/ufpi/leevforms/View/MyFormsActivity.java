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
import com.ufpi.leevforms.Adapter.FormsAdapter;
import com.ufpi.leevforms.Model.Form;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.NavigationDrawerUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MyFormsActivity extends AppCompatActivity {

    private ListView lForms;
    private ArrayList<Form> forms;

    private SharedPreferences prefs = null;
    private DatabaseReference mDatabaseForms;

    private FormsAdapter formsAdapter;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_forms);

        lForms = findViewById(R.id.lForms);
        forms = new ArrayList<>();

        prefs = getSharedPreferences(ConstantUtils.APPLICATION_ID, MODE_PRIVATE);

        configureNavigationDrawer();

        lForms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i("TAG", "ID do Form selecionado :"+forms.get(position).getId());

                Intent intent = new Intent(getContext(), FormActivity.class);
                intent.putExtra(ConstantUtils.FORMS_FIELD_ID, forms.get(position).getId());
                startActivity(intent);

            }
        });
        lForms.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                createAndShowFormsOptionsMenu(forms.get(position).getId(), position);

                return true;
            }
        });

        mDatabaseForms = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.FORMS_BRANCH);

        mDatabaseForms
                .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                .addValueEventListener(getUserForms());
    }

    private Context getContext(){
        return this;
    }

    private ValueEventListener getUserForms(){

        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

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
                    lForms.setAdapter(formsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
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

    private void createAndShowFormsOptionsMenu(final String idForm, final int position){

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
                        createAndShowRemoveFormDialog(idForm, position);
                        break;
                }
            }
        });
        builderSingle.show();
    }

    private void createAndShowRemoveFormDialog(final String idForm, final int position){

        AlertDialog alerta;

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //define o titulo
        builder.setTitle("Remoção");
        //define a mensagem
        builder.setMessage("Você realmente deseja remover esse aluno ?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(getContext(), "positivo=" + arg1, Toast.LENGTH_SHORT).show();

                HashMap<String, Object> result = new HashMap<>();
                result.put(ConstantUtils.FORMS_FIELD_VISIBLE, false);

                mDatabaseForms
                        .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                        .child(idForm)
                        .updateChildren(result);
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
