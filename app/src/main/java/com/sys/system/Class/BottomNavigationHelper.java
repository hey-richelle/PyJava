package com.sys.system.Class;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sys.system.Activity.profile;
import com.sys.system.Activity.student_page;
import com.sys.system.R;
import com.sys.system.Activity.easy2;

public class BottomNavigationHelper implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Context context;

    public BottomNavigationHelper(Context context) {
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                // Handle Home item click
                Toast.makeText(context, "Home", Toast.LENGTH_LONG).show();
                Intent homeIntent = new Intent(context, student_page.class);
                context.startActivity(homeIntent);
                return true;

            case R.id.quiz:
                // Handle Quiz item click
                Toast.makeText(context, "Quiz", Toast.LENGTH_LONG).show();
                Intent quizIntent = new Intent(context, easy2.class);
                context.startActivity(quizIntent);
                return true;

            case R.id.picture:
                // Handle Profile item click
                Toast.makeText(context, "Profile", Toast.LENGTH_LONG).show();
                Intent profileIntent = new Intent(context, profile.class);
                context.startActivity(profileIntent);
                return true;
        }
        return false;
    }
}
