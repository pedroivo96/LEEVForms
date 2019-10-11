package com.ufpi.leevforms.View;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ufpi.leevforms.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ImageView appIcon;
    private final int TIME_OUT = 2000;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appIcon = findViewById(R.id.appIcon);
        mAuth = FirebaseAuth.getInstance();

        Glide
                .with(getContext())
                .load(getResources().getDrawable(R.drawable.leev_icon))
                .into(appIcon);

        //Verifica se há um usuário logado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            //Há um usuário logado
            Log.i("TAG", currentUser.getEmail());

            Intent intent = new Intent(getContext(), UserActivity.class);
            startActivity(intent);
            finish();
        }
        else{

            //Não há um usuário logado
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent start = new Intent(getContext(), LoginActivity.class);
                    startActivity(start);
                    finish();
                }
            }, TIME_OUT);
            finish();
        }
    }

    private Context getContext(){
        return this;
    }
}
