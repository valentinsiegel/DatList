package fr.siegel.datlist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import fr.siegel.datlist.adapters.IngredientAdapter;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.Ingredient;
import fr.siegel.datlist.backend.datListApi.model.Recipe;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class ViewRecipeActivity extends AppCompatActivity {

    private User mCurrentUser;
    private String mRecipeName;
    private Recipe mCurrentRecipe;
    private RecyclerView mIngredientListRecycleView;
    private DatListApi mDatListApi;
    private List<Ingredient> mIngredientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        Intent intent = getIntent();
        mCurrentUser = Application.getApplication().getUser();
        mRecipeName = intent.getExtras().getString("recipeName");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mRecipeName);
        }

        mDatListApi = EndpointAsyncTask.getApi();

        mIngredientListRecycleView = (RecyclerView) findViewById(R.id.view_recipe_ingredient_list);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mIngredientListRecycleView.setLayoutManager(layoutManager);
        getIngredientList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRecipeDetails() {
        new AsyncTask<Void, Void, Recipe>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Recipe doInBackground(Void... params) {
                try {
                    return mDatListApi.getRecipe(mRecipeName, mCurrentUser.getUsername()).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Recipe recipe) {
                if (recipe != null) {
                    mCurrentRecipe = recipe;
                    initView();
                }
                super.onPostExecute(recipe);
            }
        }.execute();
    }

    public void getIngredientList() {
        new AsyncTask<Void, Void, List<Ingredient>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<Ingredient> doInBackground(Void... params) {
                try {
                    return mDatListApi.listIngredients(mCurrentUser.getUsername()).execute().getItems();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Ingredient> ingredientList) {
                super.onPostExecute(ingredientList);
                mIngredientList = ingredientList;
                if (mIngredientList != null) {
                    mIngredientList = ingredientList;
                }
                getRecipeDetails();
            }
        }.execute();
    }

    private void initView() {
        ((TextView) findViewById(R.id.recipe_name_text_view)).setText(mCurrentRecipe.getName());
        ((TextView) findViewById(R.id.recipe_description_text_view)).setText(mCurrentRecipe.getDescription());

        IngredientAdapter ingredientAdapter = new IngredientAdapter(this, mCurrentRecipe.getIngredientList(), mCurrentRecipe, mIngredientList);
        mIngredientListRecycleView.setAdapter(ingredientAdapter);

        ingredientAdapter.notifyDataSetChanged();

    }
}
