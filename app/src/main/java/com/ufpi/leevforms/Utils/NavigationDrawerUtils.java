package com.ufpi.leevforms.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

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

                        default: {
                            //Toast.makeText(this, "Menu Default", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
                else{

                    //É um aluno
                    switch (item.getItemId()) {


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


