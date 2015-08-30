package fr.siegel.datlist;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import fr.siegel.datlist.backend.ingredientEndpoint.model.Ingredient;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EndpointAsyncTask endpointAsyncTask;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvents();
        endpointAsyncTask = new EndpointAsyncTask();

    }

    public void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editText = (EditText) findViewById(R.id.edit_text);
    }
    public void initEvents(){
        (findViewById(R.id.button_get_items)).setOnClickListener(onClickListener);
        (findViewById(R.id.button_add_items)).setOnClickListener(onClickListener);
    }

    public OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_get_items:

                    endpointAsyncTask.listIngredients(recyclerView, getBaseContext());
                    break;
                case R.id.button_add_items:
                    endpointAsyncTask.insertIngredient(new Ingredient().setName(String.valueOf(editText.getText())));
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
