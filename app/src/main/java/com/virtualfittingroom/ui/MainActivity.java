package com.virtualfittingroom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.virtualfittingroom.R;
import com.virtualfittingroom.data.PreferenceManager;
import com.virtualfittingroom.data.api.UserDataApi;
import com.virtualfittingroom.data.models.UserModel;
import com.virtualfittingroom.databinding.ActivityMainBinding;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    private AppBarConfiguration mAppBarConfiguration;
    private TextView tvUsername, tvUserEmail;
    private CircleImageView civUserAvatar;
    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // >>>>> UI SETUP
        // BINDING MAIN
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        // DRAWERLAYOUT SETUP
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_katalog_pria, R.id.nav_katalog_wanita, R.id.nav_app_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController myNavController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, myNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, myNavController);


        tvUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvName);
        tvUserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvEmail);
        civUserAvatar = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.civProfile);

        // setup logout btn
        navigationView.getMenu().findItem(R.id.nav_app_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                drawer.close();
                actionLogout();
                return false;
            }
        });

//        >>>> FLOATING ACTIONBAR
        binding.appBarMain.fab.setVisibility(View.GONE);

        // <<<<< UI SETUP

        // >>>>> DATA INIT
        mainActivityViewModel = new MainActivityViewModel();
//        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        // <<<<< DATA INIT
        mainActivityViewModel.getPreferenceData().observe(this, preferenceData1 -> {
            if(preferenceData1 != null){
                tvUsername.setText(preferenceData1.getUser().getName());
                tvUserEmail.setText(preferenceData1.getUser().getEmail());
                Picasso.get()
                        .load(preferenceData1.getUser().getAvatar())
                        .placeholder(R.drawable.placehold_avatar)
                        .into(civUserAvatar);
            }
        });
        updateUserData();
    }


    private void actionLogout(){
        // clear local preference
        if((new PreferenceManager(getApplicationContext())).clearLocal()){
            Toast.makeText(this, R.string.toast_logout_success, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
            finish();
        }
    }

    public FloatingActionButton getFloatingActionButton(){
        return this.binding.appBarMain.fab;
    }

    public void updateUserData(){
        PreferenceManager.PreferenceData preferenceData
                = (new PreferenceManager(getApplicationContext())).getLocal();

        UserDataApi userDataApi = new UserDataApi(getString(R.string.url_base_api), preferenceData.getAuthToken());
        userDataApi.getUserData(new UserDataApi.GetUserDataCallback() {
            @Override
            public void onSuccess(UserModel user) {
                preferenceData.setUser(user);
                mainActivityViewModel.getPreferenceData().setValue(preferenceData);
            }

            @Override
            public void onUnauthenticated() {
                actionLogout();
            }

            @Override
            public void onError(String message) {
                actionLogout();
            }

            @Override
            public void onFailure(Throwable t) {
                actionLogout();
            }
        });
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public class MainActivityViewModel extends ViewModel{
        private MutableLiveData<PreferenceManager.PreferenceData> preferenceData;

        public MainActivityViewModel(){
            preferenceData = new MutableLiveData<>();
        }

        public MutableLiveData<PreferenceManager.PreferenceData> getPreferenceData(){
            return preferenceData;
        }
        
    }
}