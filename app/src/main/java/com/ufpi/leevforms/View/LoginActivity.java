package com.ufpi.leevforms.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.MethodUtils;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private EditText eEmail;
    private EditText ePassword;

    private Button bLogin;
    private Button bForgetPassword;
    private Button bInsertTest;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.USERS_BRANCH);

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        ePassword = findViewById(R.id.ePassword);
        eEmail = findViewById(R.id.eEmail);

        bLogin = findViewById(R.id.bLogin);
        bForgetPassword = findViewById(R.id.bForgetPassword);
        bInsertTest = findViewById(R.id.bInsertTest);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearErrorEmail();
                clearErrorPassword();

                //Verificar se algum dos campos está vazio
                if(eEmail.getText().toString().isEmpty() ||
                        ePassword.getText().toString().isEmpty()){

                    //Toast.makeText(getContext(), "Um dos campos está vazio!", Toast.LENGTH_SHORT).show();
                    checkEmptyFields();
                }
                else{

                    //Verificar se o campo de e-mail possui um endereço de e-mail válido
                    if(!MethodUtils.isEmailValid(eEmail.getText().toString())){
                        //Toast.makeText(getContext(), "Informe um e-mail válido!", Toast.LENGTH_SHORT).show();
                        enableAndShowErrorEmail("Informe um e-mail válido");
                    }
                    else{

                        //Ou seja, Cadastro finalizado
                        mAuth.signInWithEmailAndPassword(eEmail.getText().toString(), ePassword.getText().toString())
                                .addOnCompleteListener(configureCompleteSignInListener())
                                .addOnFailureListener(configureFailureSigninListener());
                    }
                }
            }
        });

        bForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        bInsertTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference elementReference = mDatabase.push();
                String id = elementReference.getKey();

                mDatabase.child(id).child(ConstantUtils.USER_FIELD_NAME).setValue("Pedro Ivo Soares Barbosa");
                mDatabase.child(id).child(ConstantUtils.USER_FIELD_EMAIL).setValue("soaresbarbosapedroivo@gmail.com");
                mDatabase.child(id).child(ConstantUtils.USER_FIELD_PROJECTS).setValue("Automação Laboratorial");
                mDatabase.child(id).child(ConstantUtils.USER_FIELD_USERTYPE).setValue(ConstantUtils.USER_TYPE_TEACHER);
                mDatabase.child(id).child(ConstantUtils.USER_FIELD_VISIBLE).setValue(true);
                mDatabase.child(id).child(ConstantUtils.USER_FIELD_REGISTERFINALIZED).setValue(true);
                mDatabase.child(id).child(ConstantUtils.USER_FIELD_IDADVISOR).setValue("Sem orientador");
            }
        });

    }

    private Context getContext(){
        return this;
    }

    private OnCompleteListener configureCompleteSignInListener(){
        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(getContext(), "Login com sucesso!", Toast.LENGTH_SHORT).show();

                    //Agora, buscamos as informações referentes a este Usuário
                    mDatabase.orderByChild(ConstantUtils.USER_FIELD_EMAIL).equalTo(eEmail.getText().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){
                                for(DataSnapshot d : dataSnapshot.getChildren()){

                                    //Iremos salvar nas preferences apenas email e id
                                    String email = (String) d.child(ConstantUtils.USER_FIELD_EMAIL).getValue();
                                    final String id = d.getKey();
                                    String name = (String) d.child(ConstantUtils.USER_FIELD_NAME).getValue();
                                    int userType = d.child(ConstantUtils.USER_FIELD_USERTYPE).getValue(Integer.class);

                                    prefs = getSharedPreferences("com.ufpi.leevforms", MODE_PRIVATE);
                                    prefs.edit().putString(ConstantUtils.USER_FIELD_EMAIL, email).commit();
                                    prefs.edit().putString(ConstantUtils.USER_FIELD_NAME, name).commit();
                                    prefs.edit().putString(ConstantUtils.USER_FIELD_ID, id).commit();
                                    prefs.edit().putInt(ConstantUtils.USER_FIELD_USERTYPE, userType).commit();

                                    String instanceId;
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.i("TAG", "getInstanceId failed", task.getException());
                                                        return;
                                                    }

                                                    // Get new Instance ID token
                                                    String token = task.getResult().getToken();

                                                    HashMap<String, Object> result = new HashMap<>();
                                                    result.put(ConstantUtils.USER_FIELD_ID_INSTANCE_FORMS, token);

                                                    mDatabase.child(id).updateChildren(result);

                                                    Intent intent = new Intent(getContext(), UserActivity.class);
                                                    startActivity(intent);
                                                }
                                            });

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    // If sign in fails, display a message to the user.
                    Log.i("TAG", "signInWithEmail:failure", task.getException());

                }
            }
        };
    }

    private OnFailureListener configureFailureSigninListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(e instanceof FirebaseNetworkException){
                    Toast.makeText(getContext(), "Verifique sua conexão", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i("TAG", e.getMessage());
                    Log.i("TAG", ( (FirebaseAuthException) e).getErrorCode());

                    String errorCode = ( (FirebaseAuthException) e).getErrorCode();

                    switch (errorCode){
                        case "ERROR_INVALID_EMAIL":
                            Toast.makeText(getContext(), "E-mail inválido. Verifique o formato", Toast.LENGTH_SHORT).show();
                            break;
                        case "ERROR_USER_NOT_FOUND":
                            Toast.makeText(getContext(), "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                            break;
                        case "ERROR_WRONG_PASSWORD":
                            //Toast.makeText(getContext(), "Senha inválida", Toast.LENGTH_SHORT).show();

                            enableAndShowErrorPassword("Senha inválida");
                            break;
                    }
                }
            }
        };
    }

    private void checkEmptyFields(){
        if(eEmail.getText().toString().isEmpty()){
            enableAndShowErrorEmail("O campo de e-mail não pode estar vazio");
        }
        if(ePassword.getText().toString().isEmpty()){
            enableAndShowErrorPassword("O campo de senha não pode estar vazio");
        }
    }

    private void enableAndShowErrorEmail(String errorMessage){
        textInputLayoutEmail.setErrorEnabled(true);
        textInputLayoutEmail.setError(errorMessage);
    }

    private void enableAndShowErrorPassword(String errorMessage){
        textInputLayoutPassword.setErrorEnabled(true);
        textInputLayoutPassword.setError(errorMessage);
    }

    private void clearErrorPassword(){
        textInputLayoutPassword.setErrorEnabled(false);
        textInputLayoutPassword.setError(null);
    }

    private void clearErrorEmail(){
        textInputLayoutEmail.setErrorEnabled(false);
        textInputLayoutEmail.setError(null);
    }

}