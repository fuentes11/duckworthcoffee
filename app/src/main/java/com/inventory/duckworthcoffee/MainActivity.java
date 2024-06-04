package com.inventory.duckworthcoffee;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String ADMIN_PASSWORD = "DrinkCoffee12!@"; // Change this password as per your needs
    private WebView webView;
    private static final String DEFAULT_URL = "https://inventory.duckworthcoffee.com/login";
    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        startLockTask();
        getWindow().getDecorView().setSystemUiVisibility(flags);

        /* Following code allows the app packages to lock task in true kiosk mode */
        setContentView(R.layout.activity_main);
        // get policy manager
        DevicePolicyManager myDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // get this app package name
        ComponentName mDPM = new ComponentName(this, MyAdmin.class);
        //startLockTask();
        if (myDevicePolicyManager.isDeviceOwnerApp(this.getPackageName())) {
            // get this app package name
            String[] packages = {this.getPackageName()};
            // mDPM is the admin package, and allow the specified packages to lock task
            //myDevicePolicyManager.setLockTaskPackages(mDPM, packages);
            startLockTask();
        } else {
            Toast.makeText(getApplicationContext(),"Not owner", Toast.LENGTH_LONG).show();
        }

        setVolumeMax();


        webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Allow navigation to any URL
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        // Load the URL
        String url = getSavedUrl();
        Log.d(TAG, "Loading URL: " + url);
        webView.loadUrl(url);
    }

    private String getSavedUrl() {
        SharedPreferences sharedPreferences = getSharedPreferences("DuckworthCoffeePrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("LINK", DEFAULT_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_link:
                showAdminPasswordDialog();
                return true;
            case R.id.action_exit:
                showExitPasswordDialog();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final int CHANGE_LINK_REQUEST = 1;

    // Modify the method to start ChangeLinkActivity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setVolumeMax(){
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(
                AudioManager.STREAM_SYSTEM,
                am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),
                0);
    }

    // Method to handle the result from ChangeLinkActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_LINK_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                // Retrieve the new URL from the Intent and load it into the WebView
                String newUrl = data.getStringExtra("NEW_URL");
                Log.d(TAG, "New URL received from ChangeLinkActivity: " + newUrl);
                if (newUrl != null && !newUrl.isEmpty()) {
                    webView.loadUrl(newUrl);
                }
            }
        }
    }
    private void showAdminPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Admin Password");

        // Create a RelativeLayout to contain the EditText and the visibility button
        RelativeLayout layout = new RelativeLayout(this);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setId(View.generateViewId());

        // Create the visibility button
        final ImageButton visibilityButton = new ImageButton(this);
        visibilityButton.setImageResource(R.drawable.baseline_remove_red_eye_24);
        visibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On button click, toggle between password mode and normal text mode
                if (input.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    visibilityButton.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    visibilityButton.setImageResource(R.drawable.baseline_remove_red_eye_24);
                }
                // Move the cursor to the end of the text
                input.setSelection(input.length());
            }
        });

        // Set position constraints for the EditText
        RelativeLayout.LayoutParams inputParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        inputParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        layout.addView(input, inputParams);

        // Set position constraints for the visibility button
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        buttonParams.addRule(RelativeLayout.ALIGN_BOTTOM, input.getId());
        layout.addView(visibilityButton, buttonParams);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                if (password.equals(ADMIN_PASSWORD)) {
                    Log.d(TAG, "Correct admin password entered. Redirecting to change link...");
                    Intent intent = new Intent(MainActivity.this, ChangeLinkActivity.class);
                    startActivityForResult(intent, CHANGE_LINK_REQUEST);
                } else {
                    Log.d(TAG, "Incorrect admin password entered.");
                    Toast.makeText(MainActivity.this, "Incorrect admin password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Cancelled.");
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showExitPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password to Exit");

        // Create a RelativeLayout to contain the EditText and the visibility button
        RelativeLayout layout = new RelativeLayout(this);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setId(View.generateViewId());

        // Create the visibility button
        final ImageButton visibilityButton = new ImageButton(this);
        visibilityButton.setImageResource(R.drawable.baseline_remove_red_eye_24);
        visibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On button click, toggle between password mode and normal text mode
                if (input.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    visibilityButton.setImageResource(R.drawable.baseline_visibility_off_24);
                } else {
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    visibilityButton.setImageResource(R.drawable.baseline_remove_red_eye_24);
                }
                // Move the cursor to the end of the text
                input.setSelection(input.length());
            }
        });

        // Set position constraints for the EditText
        RelativeLayout.LayoutParams inputParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        inputParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        layout.addView(input, inputParams);

        // Set position constraints for the visibility button
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        buttonParams.addRule(RelativeLayout.ALIGN_BOTTOM, input.getId());
        layout.addView(visibilityButton, buttonParams);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                if (password.equals(ADMIN_PASSWORD)) {
                    Log.d(TAG, "Correct password entered. Exiting...");
                    stopLockTask();
                    finish();

                } else {
                    Log.d(TAG, "Incorrect password entered.");
                    Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Exit cancelled.");
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void enterFullScreenMode() {
        // Configure full-screen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enterFullScreenMode();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onBackPressed() {
        // Disable back button
        super.onBackPressed();
    }
}
