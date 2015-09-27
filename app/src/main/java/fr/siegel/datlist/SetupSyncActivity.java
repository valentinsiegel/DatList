package fr.siegel.datlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class SetupSyncActivity extends AppCompatActivity {

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SetupSyncActivity.this, LoginActivity.class);
            switch (v.getId()) {
                case R.id.login_button:
                    intent.putExtra("CreateAccount", false);
                    break;
                case R.id.create_account_button:
                    intent.putExtra("CreateAccount", true);
                    break;
            }
            int REQUEST_EXIT = 0;
            startActivityForResult(intent, REQUEST_EXIT);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_sync);

        findViewById(R.id.login_button).setOnClickListener(onClickListener);
        findViewById(R.id.create_account_button).setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int LOGIN_OK = 0;
        if (resultCode == LOGIN_OK) {
            setResult(0);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setup_sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
