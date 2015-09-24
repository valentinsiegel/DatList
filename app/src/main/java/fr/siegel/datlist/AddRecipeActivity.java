package fr.siegel.datlist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import fr.siegel.datlist.backend.datListApi.model.Recipe;

public class AddRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            addRecipe();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addRecipe() {
        new AsyncTask<Void, Void, Recipe>() {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
            }

            @Override
            protected Recipe doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Recipe recipe) {
                super.onPostExecute(recipe);
            }
        }.execute();
    }
}
