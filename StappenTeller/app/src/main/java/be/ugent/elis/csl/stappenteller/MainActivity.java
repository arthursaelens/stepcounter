package be.ugent.elis.csl.stappenteller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

// source: https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer

// NOTE: we use Toolbar and Activity from the support library because
//       ActionBarDrawerToggle only works with the support Toolbar.

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        mDrawerToggle = setupDrawerToggle();

        // tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(mDrawerToggle);

        NavigationView view = findViewById(R.id.nav_view);
        setupDrawerContent(view);

        // select the first fragment in the drawer
        if (savedInstanceState == null) {
            MenuItem item = view.getMenu().getItem(0);
            selectDrawerItem(item);
        }
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // NOTE 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    //         There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger
    // icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onDestroy() {
        // if we're leaving the activity, make sure we kill the service
        if (isFinishing()) {
            Intent intent = new Intent(this, CounterService.class);
            intent.setAction(CounterService.ACTION_STOP);
            startService(intent);
        }
        super.onDestroy();
    }


    // Actions that need to be deferred to ActionBarDrawerToggle

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Navigation drawer

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid Toolbar reference. ActionBarDrawToggle() does not
        //       require it but will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_collector_fragment:
                fragmentClass = CollectorFragment.class;
                break;
            case R.id.nav_counter_fragment:
                fragmentClass = CounterFragment.class;
                break;
            case R.id.nav_new_fragment:
                fragmentClass = NewFragment.class;
            default:
                throw new RuntimeException("Unknown drawer item selected");
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }
}
