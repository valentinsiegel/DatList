package fr.siegel.datlist;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.siegel.datlist.Utils.SharedPreference;
import fr.siegel.datlist.Utils.Utils;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class LoginActivity extends AppCompatActivity {

    public final static String USERNAME_ALREADY_EXIST = "Conflict";
    public final static String USER_NOT_FOUND = "Not Found";

    private boolean createAccount;
    private String mUsername;
    private String mPassword;
    private TextView mPasswordErrorTextView;
    private CompoundButton.OnCheckedChangeListener onShowPasswordCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
            if (!isChecked) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordEditText.setSelection(passwordEditText.getText().length());
            } else {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        }
    };
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!checkForErrors())
                syncAccount();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
    }

    public void initView() {

        mUsername = ((EditText) findViewById(R.id.username_edit_text)).getText().toString();
        mPassword = ((EditText) findViewById(R.id.password_edit_text)).getText().toString();
        mPasswordErrorTextView = (TextView) findViewById(R.id.password_error);
        createAccount = getIntent().getBooleanExtra("CreateAccount", false);

        ((CheckBox) findViewById(R.id.login_show_password_checkbox)).setOnCheckedChangeListener(onShowPasswordCheckedListener);

        if (createAccount) {
            ((Button) findViewById(R.id.login_button)).setText(getString(R.string.button_create_account));
            findViewById(R.id.login_button).setOnClickListener(onClickListener);
        } else {
            ((Button) findViewById(R.id.login_button)).setText(getString(R.string.button_sign_in));
            findViewById(R.id.login_button).setOnClickListener(onClickListener);
        }

        setTitle((createAccount ? getString(R.string.activity_title_create_account) : getString(R.string.activity_title_sign_in)));
    }

    public boolean checkForErrors() {

        boolean errors = false;

        initView();

        if (!Utils.checkForEmptyString(mUsername)) {
            findViewById(R.id.username_error_text_view).setVisibility(View.VISIBLE);
            errors = true;
        } else {
            findViewById(R.id.username_error_text_view).setVisibility(View.GONE);
        }
        if (!Utils.checkForEmptyString(mPassword)) {
            mPasswordErrorTextView.setVisibility(View.VISIBLE);
            mPasswordErrorTextView.setText(getString(R.string.login_password_error));
            errors = true;
        } else {
            mPasswordErrorTextView.setVisibility(View.GONE);
        }

        return errors;
    }

    private void syncAccount() {
        new AsyncTask<Void, Void, Boolean>() {

            DatListApi datListApi = null;
            User user;
            String errorMessage;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                datListApi = EndpointAsyncTask.getApi();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                if (createAccount) {
                    List<String> stringList = new ArrayList<>();
                    stringList.add("");
                    try {
                        user = datListApi.createUser(new User().setUsername(mUsername).setPassword(mPassword).setDictionary(stringList)).execute();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        GoogleJsonResponseException googleJsonResponseException = (GoogleJsonResponseException) e;
                        errorMessage = googleJsonResponseException.getStatusMessage();
                        return false;
                    }
                } else {
                    try {
                        user = datListApi.retrieveUserId(mUsername, mPassword).execute();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        GoogleJsonResponseException googleJsonResponseException = (GoogleJsonResponseException) e;
                        errorMessage = googleJsonResponseException.getStatusMessage();
                        return false;
                    }
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);

                if (success) {
                    Application.getApplication().setUser(user);
                    SharedPreference.setUserId(getBaseContext(), user.getUsername());
                    setResult(0);
                    finish();
                } else if (errorMessage.equals(USER_NOT_FOUND)) {
                    mPasswordErrorTextView.setVisibility(View.VISIBLE);
                    mPasswordErrorTextView.setText(R.string.login_incorrect_password);
                } else
                    showAlertDialog(errorMessage);

            }
        }.execute();
    }

    public void showAlertDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        switch (errorMessage) {
            case USERNAME_ALREADY_EXIST:
                builder.setTitle(R.string.login_dialog_username_conflict_title);
                builder.setMessage(R.string.login_dialog_username_conflict_message);
                break;
        }
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
