package com.example.ticketapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ticketapp.databinding.ActivityMainBinding;
import com.example.ticketapp.utils.LocaleHelper;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getPersistedLanguage(newBase)));
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpToolBar();
        setUpNavigation();
        
        // Xử lý Deep Link từ MoMo
        handleDeepLink(getIntent());
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }
    
    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();
        if (data != null && "ticketapp".equals(data.getScheme()) && "payment".equals(data.getHost())) {
            Log.d(TAG, "Deep link received: " + data.toString());
            
            // Lấy các tham số từ MoMo callback
            String resultCode = data.getQueryParameter("resultCode");
            String orderId = data.getQueryParameter("orderId");
            String message = data.getQueryParameter("message");
            
            Log.d(TAG, "ResultCode: " + resultCode + ", OrderId: " + orderId + ", Message: " + message);
            
            // Lưu vào SharedPreferences để Fragment xử lý
            getSharedPreferences("momo_payment", MODE_PRIVATE)
                    .edit()
                    .putString("resultCode", resultCode)
                    .putString("orderId", orderId)
                    .putString("message", message)
                    .putLong("timestamp", System.currentTimeMillis())
                    .apply();
            
            // Broadcast payment result để PaymentMethodSelectionFragment xử lý
            Intent broadcastIntent = new Intent("com.example.ticketapp.MOMO_PAYMENT_RESULT");
            broadcastIntent.putExtra("resultCode", resultCode);
            broadcastIntent.putExtra("orderId", orderId);
            broadcastIntent.putExtra("message", message);
            sendBroadcast(broadcastIntent);
        }
    }
    private void setUpNavigation() {
        NavHostFragment navHostFragment =binding.fragmentContainerView.getFragment();
        appBarConfiguration =
                new AppBarConfiguration.Builder(
                        R.id.nav_home // id trong nav_graph
                ).build();
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController,appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
    }
    private void setUpToolBar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true); // vì mình có custom TextView
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}