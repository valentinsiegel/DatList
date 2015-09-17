package fr.siegel.datlist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import fr.siegel.datlist.Utils.SharedPreference;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class MainActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    private static final int LOGIN_OK = 0;
    private DrawerLayout mDrawerLayout;
    private Application mApplication;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = new Application();
        mCurrentUser = mApplication.getUser();

        if (mCurrentUser == null) {
            long userId = SharedPreference.getUserId(this);
            if (userId == 0) {
                Intent intent = new Intent(this, SetupSyncActivity.class);
                startActivityForResult(intent, LOGIN_OK);
            } else {
                retrieveProfile(userId);
            }

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open_drawer, R.string.drawer_close_drawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        ((NavigationView) findViewById(R.id.navigation_view)).setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.drawer_layout_list:
                        return true;
                    case R.id.drawer_layout_recipes:
                        return true;
                    case R.id.drawer_layout_items:
                        Fragment fragment = new ListFragment();
                        Bundle args = new Bundle();
                        fragment.setArguments(args);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_content, fragment)
                                .commit();
                        return true;
                    case R.id.drawer_layout_settings:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);

                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private void retrieveProfile(final long userId) {


        new AsyncTask<Void, Void, User>() {
            DatListApi datListApi = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                datListApi = EndpointAsyncTask.getApi();
            }

            @Override
            protected User doInBackground(Void... params) {
                try {
                    return datListApi.retrieveUserById(userId).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                mCurrentUser = user;
                ((TextView) findViewById(R.id.drawer_layout_username)).setText(mCurrentUser.getUsername());
                ((TextView) findViewById(R.id.drawer_layout_email)).setText(mCurrentUser.getUsername());
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_OK && resultCode == 0) {
            mCurrentUser = mApplication.getUser();
            ((TextView) findViewById(R.id.drawer_layout_username)).setText(mCurrentUser.getUsername());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
