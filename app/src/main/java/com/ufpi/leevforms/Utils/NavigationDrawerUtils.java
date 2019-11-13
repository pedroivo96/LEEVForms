package com.ufpi.leevforms.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.ufpi.leevforms.Adapter.MyStudentsAdapter;
import com.ufpi.leevforms.R;
import com.ufpi.leevforms.View.FormRegisterActivity;
import com.ufpi.leevforms.View.LoginActivity;
import com.ufpi.leevforms.View.MyFormsActivity;
import com.ufpi.leevforms.View.MyStudentsActivity;
import com.ufpi.leevforms.View.UserActivity;

public class NavigationDrawerUtils {

    public static NavigationView.OnNavigationItemSelectedListener getNavigationDrawerItemSelectedListener(final Context context, final int userType, final DrawerLayout drawerLayout){
        return new NavigationView.OnNavigationItemSelectedListener() {

            public void finishCurrentActivity(){
                if(! (context instanceof UserActivity))
                    ((Activity) context).finish();
            }

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(userType == ConstantUtils.USER_TYPE_TEACHER){

                    //É um professor
                    switch (item.getItemId()) {

                        case R.id.nav_my_profile: {
                            if(! (context instanceof UserActivity)) {
                                Intent intent = new Intent(context, UserActivity.class);
                                context.startActivity(intent);
                                finishCurrentActivity();
                            }
                            break;
                        }

                        case R.id.nav_form_register:{
                            Intent intent = new Intent(context, FormRegisterActivity.class);
                            context.startActivity(intent);
                            break;
                        }

                        case R.id.nav_my_forms:{
                            Intent intent = new Intent(context, MyFormsActivity.class);
                            context.startActivity(intent);
                            break;
                        }

                        case R.id.nav_my_students:{
                            Intent intent = new Intent(context, MyStudentsActivity.class);
                            context.startActivity(intent);
                            break;
                        }

                        case R.id.nav_logout:{
                            FirebaseAuth.getInstance().signOut();
                            Intent intent2 = new Intent(context, LoginActivity.class);
                            context.startActivity(intent2);
                            finishCurrentActivity();

                            break;
                        }

                        default: {
                            //Toast.makeText(this, "Menu Default", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
                else{

                    //É um aluno
                    switch (item.getItemId()) {

                        case R.id.nav_my_profile: {
                            if(! (context instanceof UserActivity)) {
                                Intent intent = new Intent(context, UserActivity.class);
                                context.startActivity(intent);
                                finishCurrentActivity();
                            }
                            break;
                        }

                        case R.id.nav_form_register:{
                            Intent intent = new Intent(context, FormRegisterActivity.class);
                            context.startActivity(intent);
                            break;
                        }

                        case R.id.nav_my_forms:{
                            Intent intent = new Intent(context, MyFormsActivity.class);
                            context.startActivity(intent);
                            break;
                        }

                        case R.id.nav_logout:{
                            FirebaseAuth.getInstance().signOut();
                            Intent intent2 = new Intent(context, LoginActivity.class);
                            context.startActivity(intent2);
                            finishCurrentActivity();

                            break;
                        }

                        default: {
                            //Toast.makeText(this, "Menu Default", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        };
    }

}


