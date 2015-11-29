package fr.siegel.datlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.siegel.datlist.Utils.Constants;
import fr.siegel.datlist.constants.Intents;

public class SetupSyncActivity extends AppCompatActivity {

    @OnClick({R.id.login_button, R.id.create_account_button})
    public void goNext(View view){
        Intent intent = new Intent(SetupSyncActivity.this, LoginActivity.class);
        switch (view.getId()){
            case R.id.create_account_button:
                intent.putExtra(Intents.CREATE_ACCOUNT, true);
                break;
            case R.id.login_button:
                intent.putExtra(Intents.CREATE_ACCOUNT, false);
                break;
        }
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_sync);

        ButterKnife.bind(this);

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
