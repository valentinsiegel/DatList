package fr.siegel.datlist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import fr.siegel.datlist.Utils.SharedPreference;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class LoginActivity extends AppCompatActivity {

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            new AsyncTask<Void, Void, User>() {

                DatListApi datListApi = null;
                String username;
                String password;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    datListApi = EndpointAsyncTask.getApi();
                    username = ((EditText) findViewById(R.id.username_edit_text)).getText().toString();
                    password = ((EditText) findViewById(R.id.password_edit_field)).getText().toString();
                }

                @Override
                protected User doInBackground(Void... params) {
                    try {
                        return datListApi.createUser(new User().setUsername(username).setPassword(password)).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(User user) {
                    super.onPostExecute(user);
                    Application application = new Application();
                    application.setUser(user);
                    SharedPreference.setUserId(getBaseContext(), user.getId());
                    setResult(0);
                    finish();
                    //Toast.makeText(LoginActivity.this, user.getId().toString() + "-" + user.getUsername(), Toast.LENGTH_SHORT).show();
                }
            }.execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((Button) findViewById(R.id.login_button)).setOnClickListener(onClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
