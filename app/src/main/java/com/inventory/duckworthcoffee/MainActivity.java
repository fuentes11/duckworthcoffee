package com.inventory.duckworthcoffee;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String EXIT_PASSWORD = "1234";
    private static final String ADMIN_PASSWORD = "admin123"; // Cambia esta contraseña según tus necesidades
    private WebView webView;
    private static final String DEFAULT_URL = "https://inventory.duckworthcoffee.com/login";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        // Configurar la pantalla completa
        enterFullScreenMode();

        webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Permitir la navegación a cualquier URL
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

    // Modificar el método para iniciar ChangeLinkActivity
    private void showAdminPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Admin Password");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                if (password.equals(ADMIN_PASSWORD)) {
                    Log.d(TAG, "Correct admin password entered. Redirecting to change link...");
                    Intent intent = new Intent(MainActivity.this, ChangeLinkActivity.class);
                    startActivityForResult(intent, CHANGE_LINK_REQUEST); // Iniciar actividad con identificador de solicitud
                } else {
                    Log.d(TAG, "Incorrect admin password entered.");
                    Toast.makeText(MainActivity.this, "Incorrect admin password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Admin password entry cancelled.");
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Método para manejar el resultado de ChangeLinkActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_LINK_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                // Recuperar la nueva URL del Intent y cargarla en el WebView
                String newUrl = data.getStringExtra("NEW_URL");
                Log.d(TAG, "New URL received from ChangeLinkActivity: " + newUrl);
                if (newUrl != null && !newUrl.isEmpty()) {
                    webView.loadUrl(newUrl);
                }
            }
        }
    }

    private void showExitPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password to Exit");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                if (password.equals(EXIT_PASSWORD)) {
                    Log.d(TAG, "Correct password entered. Exiting...");
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
        // Configurar la pantalla completa
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
        // Deshabilitar el botón de retroceso
    }
}
