package fr.siegel.datlist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import fr.siegel.datlist.Utils.KeyboardUtils;
import fr.siegel.datlist.Utils.SharedPreference;
import fr.siegel.datlist.Utils.TextUtils;
import fr.siegel.datlist.constants.HTTPCodes;
import fr.siegel.datlist.constants.Intents;
import fr.siegel.datlist.model.User;
import fr.siegel.datlist.services.EndpointFactory;
import fr.siegel.datlist.services.UserEndpoint;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends AppCompatActivity {

    public static final int LOGIN_OK = 0;

    @Bind(R.id.username_edit_text)
    EditText usernameEditText;
    @Bind(R.id.password_edit_text)
    EditText passwordEditText;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.show_password_checkbox)
    CheckBox showPasswordCheckBox;
    @Bind(R.id.login_button)
    Button loginButton;
    @Bind(R.id.password_error_text_view)
    TextView passwordErrorTextView;
    @Bind(R.id.username_error_text_view)
    TextView usernameErrorTextView;
    @Bind(R.id.loading_layout)
    LinearLayout loadingLinearLayout;
    @Bind(R.id.login_layout)
    LinearLayout loginLinearLayout;

    @OnClick(R.id.login_button)
    public void submit() {
        if (!checkForLoginErrors()) {

            KeyboardUtils.hideKeyboard(this);
            showLoadingScreen();

            if (createAccount) {
                Call<User> createAccountCall = userEndpoint.createUser(new User(usernameEditText.getText().toString(), passwordEditText.getText().toString()));
                createAccountCall.enqueue(createAccountCallback);
            } else {
                Call<User> loginCall = userEndpoint.loginUser(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                loginCall.enqueue(loginCallback);
            }
        }
    }

    @OnCheckedChanged(R.id.show_password_checkbox)
    public void onChecked(boolean checked) {
        if (!checked) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
    }


    private Callback<User> loginCallback = new Callback<User>() {
        @Override
        public void onResponse(Response<User> response, Retrofit retrofit) {
            if (response.body() != null) {
                User user = response.body();
            } else if (response.code() == HTTPCodes.NOT_FOUND) {
                showLoginScreen();
                passwordErrorTextView.setVisibility(View.VISIBLE);
                passwordErrorTextView.setText(R.string.login_incorrect_password);
            }
        }

        @Override
        public void onFailure(Throwable t) {
            showLoginScreen();
        }
    };

    private UserEndpoint userEndpoint;
    private boolean createAccount;

    private Callback<User> createAccountCallback = new Callback<User>() {
        @Override
        public void onResponse(Response<User> response, Retrofit retrofit) {
            if (response.body() != null) {
                User user = response.body();
                loginUser(user);
            } else if (response.code() == HTTPCodes.CONFLICT) {
                showLoginScreen();
                showAlertDialog(HTTPCodes.CONFLICT);
            }
        }

        @Override
        public void onFailure(Throwable t) {
            showLoginScreen();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(Intents.CREATE_ACCOUNT)) {
            createAccount = extras.getBoolean(Intents.CREATE_ACCOUNT, false);
        }

        setTitle((createAccount ? getString(R.string.activity_title_create_account) : getString(R.string.activity_title_sign_in)));
        loginButton.setText(createAccount ? getString(R.string.button_create_account) : getString(R.string.button_sign_in));

        userEndpoint = new EndpointFactory().getUserEndpoint();
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

    public void showLoadingScreen() {
        if (loginLinearLayout != null || loadingLinearLayout != null) {
            loadingLinearLayout.setVisibility(View.VISIBLE);
            loginLinearLayout.setVisibility(View.GONE);
        }
    }

    public void showLoginScreen() {
        if (loginLinearLayout != null || loadingLinearLayout != null) {
            loadingLinearLayout.setVisibility(View.GONE);
            loginLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void loginUser(User user) {
        //Application.getApplication().setUser(user);
        SharedPreference.setUserId(getBaseContext(), user.getUsername());
        setResult(0);
        finish();
    }

    public boolean checkForLoginErrors() {

        boolean errors = false;

        if (usernameEditText == null || passwordEditText == null || usernameErrorTextView == null || passwordErrorTextView == null) {
            finish();
            return true;
        }

        if (TextUtils.notEmpty(usernameEditText.getText().toString())) {
            usernameErrorTextView.setVisibility(View.VISIBLE);
            errors = true;
        }

        if (TextUtils.notEmpty(passwordEditText.getText().toString())) {
            passwordErrorTextView.setVisibility(View.VISIBLE);
            passwordErrorTextView.setText(getString(R.string.login_password_error));
            errors = true;
        }

        return errors;
    }

    public void showAlertDialog(int errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        switch (errorMessage) {
            case HTTPCodes.CONFLICT:
                builder.setTitle(R.string.login_dialog_username_conflict_title);
                builder.setMessage(R.string.login_dialog_username_conflict_message);
                break;
        }
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
