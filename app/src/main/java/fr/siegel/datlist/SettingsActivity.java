package fr.siegel.datlist;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import fr.siegel.datlist.Utils.SharedPreference;

public class SettingsActivity extends AppCompatActivity {

    private Application mAppInstance;
    private OnClickListener onOptionClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sign_out_layout:
                    Builder builder = new Builder(SettingsActivity.this);
                    builder.setTitle(getString(R.string.dialog_title_sign_out));
                    builder.setMessage(getString(R.string.dialog_message_sign_out));
                    builder.setPositiveButton(getString(R.string.dialog_positive_sign_out), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreference.setUserId(SettingsActivity.this, null);
                            mAppInstance.setUser(null);
                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton(getString(android.R.string.cancel), null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.activity_title_settings);
        }


        findViewById(R.id.sign_out_layout).setOnClickListener(onOptionClick);

        mAppInstance = new Application();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
