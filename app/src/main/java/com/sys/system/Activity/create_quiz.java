package com.sys.system.Activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sys.system.R;

public class create_quiz extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_logo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; show dialog to confirm going back to menu
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to go back to the menu?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),Admin_page.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //array
    String[] position = {"List of Subject", "Java", "Python"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        getSupportActionBar().setTitle("Admin create Quiz");
        // Get the home button drawable
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
// Set its color to black
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
// Set the modified drawable as the home button icon
        getSupportActionBar().setHomeAsUpIndicator(homeButton);
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        //spinner code by using arrayadapter to set country arrray data
        Spinner spin = findViewById(R.id.spinnerposition); //spinner is spinner id
        spin.setOnItemSelectedListener(this);
        //arrayadapter (set position data to arrayadapter)
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, position);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set array adapter to fill spinner
        spin.setAdapter(aa);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

// Check the third item (index 2)
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                switch (item.getItemId()) {
                    case R.id.home:
                        Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), Admin_page.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0); // Disable animation
                        break;
                    case R.id.quiz:
                        Toast.makeText(getApplicationContext(), "Create Quiz", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), create_quiz.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0); // Disable animation
                        break;
                    case R.id.picture:
                        Toast.makeText(getApplicationContext(), "Delete Quiz", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), deleting_quiz.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0); // Disable animation
                        break;
                    case R.id.progress23:
                        Toast.makeText(getApplicationContext(), "Student progress", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), score_view.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0); // Disable animation
                        break;
                }

                return true;
            }
        });





    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Java"))
        {
            Intent intent = new Intent(getApplicationContext(),Easy_set.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();

        }else if (parent.getItemAtPosition(position).equals("Python")) {
            Intent intent = new Intent(getApplicationContext(), set_normal.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),Admin_page.class);
        startActivity(intent);
        super.onBackPressed();
    }

}
