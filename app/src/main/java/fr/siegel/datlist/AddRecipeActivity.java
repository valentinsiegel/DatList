package fr.siegel.datlist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.siegel.datlist.Utils.Utils;
import fr.siegel.datlist.adapters.IngredientAdapter;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.Ingredient;
import fr.siegel.datlist.backend.datListApi.model.Recipe;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class AddRecipeActivity extends AppCompatActivity {

    private TextView mRecipeNameTextView;
    private TextView mRecipeDescriptionTextView;
    private EditText mIngredientNameEditText;
    private List<Ingredient> mIngredientList = null;
    private User mCurrentUser;
    private IngredientAdapter ingredientAdapter;

    private View.OnClickListener addIngredientListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Utils.checkForEmptyString(mIngredientNameEditText.getText().toString())) {
                ingredientAdapter.addIngredient(new Ingredient().setName(mIngredientNameEditText.getText().toString()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = Application.getApplication().getUser();
        mIngredientList = new ArrayList<>();
        initView();
    }

    private void initView() {
        mRecipeNameTextView = (TextView) findViewById(R.id.recipe_name);
        mRecipeDescriptionTextView = (TextView) findViewById(R.id.recipe_description);
        mIngredientNameEditText = (EditText) findViewById(R.id.ingredient_name);

        RecyclerView mIngredientListRecyclerView = (RecyclerView) findViewById(R.id.ingredient_list);
        mIngredientListRecyclerView.setLayoutManager(new LinearLayoutManager(AddRecipeActivity.this));
        ingredientAdapter = new IngredientAdapter(mIngredientList);
        mIngredientListRecyclerView.setAdapter(ingredientAdapter);
        ingredientAdapter.notifyDataSetChanged();

        findViewById(R.id.add_ingredient).setOnClickListener(addIngredientListener);
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
                addRecipe();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addRecipe() {
        new AsyncTask<Void, Void, Boolean>() {

            String recipeName;
            String recipeDescription;
            DatListApi datListApi;

            @Override
            protected void onPreExecute() {
                datListApi = EndpointAsyncTask.getApi();
                recipeName = mRecipeNameTextView.getText().toString();
                recipeDescription = mRecipeDescriptionTextView.getText().toString();
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                if (recipeName != null && recipeDescription != null && mIngredientList != null) {
                    try {
                        datListApi.createRecipe(mCurrentUser.getUsername(), new Recipe().setName(recipeName).setDescription(recipeDescription)).execute();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                } else {
                    showErrors();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (success) {
                    setResult(1);
                    finish();
                } else
                    Toast.makeText(AddRecipeActivity.this, "Recipe not added", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void showErrors() {

    }
}
