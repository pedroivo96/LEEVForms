package com.ufpi.leevforms.View;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.Utils.ConstantUtils;
import com.ufpi.leevforms.Utils.MethodUtils;

public class ForgetPasswordActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutEmail;
    private EditText eEmail;

    private Button bSendRecuperationEmail;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        eEmail = findViewById(R.id.eEmail);

        bSendRecuperationEmail = findViewById(R.id.bSendRecuperationEmail);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("pt-br");

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.USERS_BRANCH);

        bSendRecuperationEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearErrorEmail();

                if(eEmail.getText().toString().isEmpty()){
                    checkEmptyFields();
                }
                else{
                    if(!MethodUtils.isEmailValid(eEmail.getText().toString())){
                        enableAndShowErrorEmail("Informe um e-mail válido");
                    }
                    else{
                        mDatabase
                                .orderByChild(ConstantUtils.USER_FIELD_EMAIL)
                                .equalTo(eEmail.getText().toString())
                                .addListenerForSingleValueEvent(verifyEmail());
                    }
                }
            }
        });
    }

    private ValueEventListener verifyEmail() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mAuth.sendPasswordResetEmail(eEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "E-mail enviado.");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("TAG", e.toString());

                                    Toast.makeText(getContext(), "Não foi possível enviar o e-mail. Verifique sua conexão e se seus dados estão corretos.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    Toast.makeText(getContext(), "O e-mail informado não corresponde a nenhum registro", Toast.LENGTH_SHORT).show();
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

    private void checkEmptyFields(){
        if(eEmail.getText().toString().isEmpty()){
            enableAndShowErrorEmail("O campo de e-mail não pode estar vazio");
        }
    }

    private void enableAndShowErrorEmail(String errorMessage){
        textInputLayoutEmail.setErrorEnabled(true);
        textInputLayoutEmail.setError(errorMessage);
    }

    private void clearErrorEmail(){
        textInputLayoutEmail.setErrorEnabled(false);
        textInputLayoutEmail.setError(null);
    }
}