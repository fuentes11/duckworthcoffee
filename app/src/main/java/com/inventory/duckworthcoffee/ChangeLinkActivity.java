package com.inventory.duckworthcoffee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChangeLinkActivity extends AppCompatActivity {

    private static final String TAG = "ChangeLinkActivity";
    private EditText editTextLink;
    private Button buttonSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_link);

        editTextLink = findViewById(R.id.editTextLink);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newLink = editTextLink.getText().toString();
                if (!newLink.isEmpty()) {
                    saveLink(newLink);
                    Toast.makeText(ChangeLinkActivity.this, "Link saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangeLinkActivity.this, "Please enter a link", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveLink(String link) {
        SharedPreferences sharedPreferences = getSharedPreferences("DuckworthCoffeePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LINK", link);
        editor.apply();
        Log.d(TAG, "Link saved: " + link);
        // Devolver la nueva URL a MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("NEW_URL", link);
        setResult(RESULT_OK, resultIntent);
    }

}
