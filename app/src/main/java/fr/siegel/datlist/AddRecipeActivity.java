package fr.siegel.datlist;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.siegel.datlist.Utils.Utils;
import fr.siegel.datlist.adapters.IngredientAdapter;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.Ingredient;
import fr.siegel.datlist.backend.datListApi.model.Recipe;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class AddRecipeActivity extends AppCompatActivity {

    public final static String RECIPE_ALREADY_EXIST = "Conflict";
    private List<Ingredient> mIngredientList = null;
    private User mCurrentUser;
    private IngredientAdapter ingredientAdapter;
    private String recipeName;
    private String recipeDescription;


    @Bind(R.id.ingredient_name_text_view) AutoCompleteTextView ingredientNameEditText;
    @Bind(R.id.ingredient_list) RecyclerView ingredientList;
/*    @Bind(R.id.recipe_name) EditText recipeName;
    @Bind(R.id.recipe_description) EditText recipeDescription;*/

    @OnClick(R.id.add_ingredient)
    public void submit(View view){
        if (Utils.checkForEmptyString(ingredientNameEditText.getText().toString())) {
            // ingredientAdapter.addIngredient(new Ingredient().setName(ingredientNameEditText.getText().toString()), );
            ingredientNameEditText.setText("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        ButterKnife.bind(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.activity_title_add_recipe_activity);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mCurrentUser = Application.getApplication().getUser();
        mIngredientList = new ArrayList<>();

        initView();
    }

    private void initView() {
        recipeName = ((EditText) findViewById(R.id.recipe_name)).getText().toString();
        recipeDescription = ((EditText) findViewById(R.id.recipe_description)).getText().toString();

        String[] ingredients = new String[(mCurrentUser.getDictionary() == null) ? 0 : mCurrentUser.getDictionary().size()];
        for (int i = 0; i < mCurrentUser.getDictionary().size(); i++) {
            ingredients[i] = mCurrentUser.getDictionary().get(i);
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(AddRecipeActivity.this, android.R.layout.simple_list_item_1, ingredients);
        ingredientNameEditText.setAdapter(adapter);

        ingredientList.setLayoutManager(new LinearLayoutManager(AddRecipeActivity.this));
        ingredientAdapter = new IngredientAdapter(mIngredientList);
        ingredientList.setAdapter(ingredientAdapter);
        ingredientAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add:
                if (!checkForErrors())
                    addRecipe();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addRecipe() {
        new AsyncTask<Void, Void, Boolean>() {

            DatListApi datListApi = null;
            String errorMessage;

            @Override
            protected void onPreExecute() {
                datListApi = EndpointAsyncTask.getApi();
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    datListApi.createRecipe(mCurrentUser.getUsername(), new Recipe().setName(recipeName).setDescription(recipeDescription).setIngredientList(mIngredientList)).execute();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    GoogleJsonResponseException googleJsonResponseException = (GoogleJsonResponseException) e;
                    errorMessage = googleJsonResponseException.getStatusMessage();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (success) {
                    retrieveUserData(mCurrentUser.getUsername());
                    mCurrentUser = Application.getApplication().getUser();
                    setResult(1);
                    finish();
                } else {
                    showAlertDialog(errorMessage);
                }
            }
        }.execute();
    }

    public void retrieveUserData(final String userId) {

        new AsyncTask<Void, Void, Boolean>() {
            User user;
            DatListApi datListApi;

            @Override
            protected void onPreExecute() {
                datListApi = EndpointAsyncTask.getApi();
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    user = datListApi.retrieveUserById(userId).execute();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return true;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                Application.getApplication().setUser(user);
                mCurrentUser = user;
                super.onPostExecute(aBoolean);
            }
        }.execute();
    }

    public void showAlertDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddRecipeActivity.this);
        switch (errorMessage) {
            case RECIPE_ALREADY_EXIST:
                builder.setTitle(R.string.add_recipe_dialog_username_conflict_title);
                builder.setMessage(R.string.add_recipe_dialog_username_conflict_message);
                break;
        }
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /*
    LISTENERS
     */

    private boolean checkForErrors() {

        boolean errors = false;

        initView();

        if (!Utils.checkForEmptyString(recipeName)) {
            findViewById(R.id.recipe_name_error).setVisibility(View.VISIBLE);
            errors = true;
        } else {
            findViewById(R.id.recipe_name_error).setVisibility(View.GONE);
        }

        if (mIngredientList.size() < 1) {
            findViewById(R.id.recipe_ingredient_error).setVisibility(View.VISIBLE);
            errors = true;
        } else {
            findViewById(R.id.recipe_ingredient_error).setVisibility(View.GONE);
        }

        return errors;
    }
}
