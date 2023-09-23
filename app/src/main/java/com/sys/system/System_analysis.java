package com.sys.system;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class System_analysis extends AppCompatActivity {
    TextView textViewDescription;
    private BarChart barChart;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_firemess, menu);
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

                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                return true;
            case R.id.action_refresh:
                // refresh button clicked; implement refresh logic here
                Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), System_analysis.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_analysis);
        getSupportActionBar().setTitle("System Analysis");
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        textViewDescription = findViewById(R.id.textViewDescription);
        barChart = findViewById(R.id.bar_chart);

        // Fetch data from Firebase and populate the chart
        populateBarChart();

        findViewById(R.id.deleteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFoodItem(view);
            }
        });


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back(view);
            }
        });
    }
    public void back(View view){
        Intent i = new Intent(getApplicationContext(),score_view.class);
        startActivity(i);
        overridePendingTransition(0,0);
        finish();
    }

    public void deleteFoodItem(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(System_analysis.this);
        builder.setMessage("Are you sure you want to reset?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes, proceed with reset
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                for (String path : databasePaths) {
                    databaseReference.child(path).removeValue(); // Clear data in the path
                }
                Toast.makeText(getApplicationContext(),"The graph reset Successfully", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), System_analysis.class);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No, do nothing
            }
        });

        builder.show(); // Show the AlertDialog
    }



    String[] customHexColors = new String[]{"#E8A9A9", "#E8FFCE", "#4D2DB7"};
    String[] sectionNames = {"Java Easy", "Java Normal", "Java Hard"};
    final String[] databasePaths = {"Java_Easy_wrong", "Java_Normal_wrong", "Java_Hard_wrong"};

    private void populateBarChart() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize a map to store counts of wrong answers for each section
        final Map<String, Integer> wrongAnswerCounts = new HashMap<>();

        for (int i = 0; i < databasePaths.length; i++) {
            final String path = databasePaths[i];
            final String sectionName = sectionNames[i];

            databaseReference.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int totalWrongAnswers = 0;

                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot questionSnapshot : userSnapshot.getChildren()) {
                            String selectedOption = questionSnapshot.child("selectedOption").getValue(String.class);
                            String correctOption = questionSnapshot.child("correctOption").getValue(String.class);

                            // Check if the selected option is not equal to the correct option
                            if (!selectedOption.equals(correctOption)) {
                                totalWrongAnswers++;
                            }
                        }
                    }

                    // Update the count for this section
                    wrongAnswerCounts.put(sectionName, totalWrongAnswers);

                    // Check if we have counts for all three sections
                    if (wrongAnswerCounts.size() == 3) {
                        // Convert the map data to entries for the chart
                        List<BarEntry> entries = new ArrayList<>();
                        int index = 0;
                        for (Map.Entry<String, Integer> entry : wrongAnswerCounts.entrySet()) {
                            entries.add(new BarEntry(index++, entry.getValue()));
                        }

                        // Create a data set from the entries and set custom colors
                        BarDataSet dataSet = new BarDataSet(entries, "Wrong Answer Count");

                        // Set the custom colors using hexadecimal color codes
                        dataSet.setColors(Color.parseColor(customHexColors[0]),
                                Color.parseColor(customHexColors[1]),
                                Color.parseColor(customHexColors[2]));

                        // Create a data object from the data set
                        BarData data = new BarData(dataSet);

                        // Customize the X-axis labels (sections)
                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setDrawLabels(false); // This line disables drawing of X-axis labels

                        barChart.setData(data);

                        // Customize the legend
                        Legend legend = barChart.getLegend();
                        LegendEntry[] legendEntries = new LegendEntry[sectionNames.length];

                        for (int i = 0; i < sectionNames.length; i++) {
                            LegendEntry entry = new LegendEntry();
                            entry.formColor = Color.parseColor(customHexColors[i]); // Set the color for the legend entry
                            entry.label = sectionNames[i]; // Set the label for the legend entry
                            legendEntries[i] = entry;
                        }

                        legend.setCustom(legendEntries);
                        legend.setForm(Legend.LegendForm.SQUARE); // Set the form style of the legend

                        // Increase margin and spacing for legend labels
                        legend.setXEntrySpace(100f); // Adjust the horizontal margin as needed
                        legend.setYEntrySpace(90f); // Adjust the vertical margin as needed

                        // Create a description based on the retrieved data from Firebase
                        StringBuilder descriptionText = new StringBuilder("Here is the incorrect answer count for different User's Java Difficulty:\n\n");

                        for (int i = 0; i < sectionNames.length; i++) {
                            String sectionName = sectionNames[i];
                            int incorrectCount = wrongAnswerCounts.get(sectionName);
                            descriptionText.append(sectionName).append(": ").append(incorrectCount).append(" incorrect answers\n");
                        }

                        // Set the description text to the TextView
                        textViewDescription.setText(descriptionText.toString());

                        barChart.invalidate();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), score_view.class);
        startActivity(i);
        overridePendingTransition(0,0);
        finish();
        super.onBackPressed();
    }
}