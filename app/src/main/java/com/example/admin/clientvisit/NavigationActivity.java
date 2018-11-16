package com.example.admin.clientvisit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.admin.clientvisit.util.AppCompatActivityWithPermission;

public class NavigationActivity extends AppCompatActivityWithPermission
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    String TAG = "TAG";
    Fragment fragment;
    DrawerLayout drawer;
    int navigation_selected_id;
    public Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
         toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
         drawer =  findViewById(R.id.drawer_layout);
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        if(savedInstanceState==null) {      // config change
            fragment = new ClientListFragment();
            showClientListFragment(fragment);   // first time fragment
        }

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(fragment!=null) {
                    if (navigation_selected_id == R.id.nav_client_list) {

                        // clearing all backStack
//                        while (getSupportFragmentManager().getBackStackEntryCount() > 0){
//                            getSupportFragmentManager().popBackStackImmediate();
//                        }

                        FragmentManager manager = getSupportFragmentManager();
                        if (manager.getBackStackEntryCount() > 0) {
                            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
                            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }

                        showClientListFragment(fragment);
                    }

                }
                navigation_selected_id=0;
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    toggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);// show back button
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {

                    toggle.setDrawerIndicatorEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.syncState();
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });

    }

// Don't want menu on navi activity

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//
//        return true;
//    }
//

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//        return true;
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        fragment=null;

        // Handle navigation view item clicks here.
         navigation_selected_id = item.getItemId();
        Log.d("kunal", "onNavigationItemSelected: "+navigation_selected_id);

        if (navigation_selected_id == R.id.nav_client_list) {

            fragment=new ClientListFragment();

        }
//         else if (id == R.id.nav_gallery) {
//
//        }



        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






    private void showClientListFragment(Fragment fragment) {
        try {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment, "clientListFragment");
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.d(TAG, "showClientListFragment: exp " + e.getMessage());
        }
    }



}
