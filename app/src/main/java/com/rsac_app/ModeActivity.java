package com.rsac_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ModeActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
    }



    /*
        To run when user presses as User Button___ Jumps to user activity
    */
    public void setUser(View V)
    {
        Intent intnt = new Intent(ModeActivity.this, UserActivity.class);
        startActivity(intnt);
        finish();
        Log.d("hey","User_Mode") ;
    }

    /*
      To run when user presses as Admin Button___ Jumps to Admin activity
    */
    public void setAdmin(View V)
    {
        Intent intnt = new Intent(ModeActivity.this, AdminActivity.class);
        startActivity(intnt);
        finish();
        Log.d("hey","User_Mode") ;
    }

}
