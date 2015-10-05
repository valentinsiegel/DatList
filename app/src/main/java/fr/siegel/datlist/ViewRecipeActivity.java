package fr.siegel.datlist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.siegel.datlist.adapters.IngredientAdapter;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.Ingredient;
import fr.siegel.datlist.backend.datListApi.model.IngredientToBuy;
import fr.siegel.datlist.backend.datListApi.model.IngredientToBuyList;
import fr.siegel.datlist.backend.datListApi.model.Recipe;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class ViewRecipeActivity extends AppCompatActivity {

    private User mCurrentUser;
    private String mRecipeName;
    private Recipe mCurrentRecipe;
    private RecyclerView mIngredientListRecycleView;
    private DatListApi mDatListApi;
    private List<Ingredient> mRecipeIngredientList;
    private List<Ingredient> mUserIngredientList;
    private IngredientAdapter ingredientAdapter;
    private OnClickListener prepareRecipeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            addIngredientToBuyList(mUserIngredientList);
        }
    };

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

                    ingredientAdapter = new IngredientAdapter(ViewRecipeActivity.this, mCurrentRecipe.getIngredientList(), mUserIngredientList);
                    mIngredientListRecycleView.setAdapter(ingredientAdapter);

                    ingredientAdapter.notifyDataSetChanged();
                }
                super.onPostExecute(recipe);
            }
        }.execute();
    }

    public void getIngredientList() {
        new AsyncTask<Void, Void, Boolean>() {

            List<Ingredient> ingredientList;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    ingredientList = mDatListApi.listIngredients(mCurrentUser.getUsername()).execute().getItems();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (success) {
                    mUserIngredientList = (ingredientList == null) ? new ArrayList<Ingredient>() : ingredientList;


                }


                getRecipeDetails();
            }
        }.execute();
    }

    private void initView() {
        ((TextView) findViewById(R.id.recipe_name_text_view)).setText(mCurrentRecipe.getName());
        ((TextView) findViewById(R.id.recipe_description_text_view)).setText(mCurrentRecipe.getDescription());


        findViewById(R.id.prepare_recipe_button).setOnClickListener(prepareRecipeListener);

    }

    private void addIngredientToBuyList(final List<Ingredient> ingredientList) {
        new AsyncTask<Void, Void, Boolean>() {

            List<Ingredient> userList = ingredientList;
            List<Ingredient> recipeList = mCurrentRecipe.getIngredientList();
            IngredientToBuyList notFound = new IngredientToBuyList();


            @Override
            protected void onPreExecute() {
                notFound.setIngredientToBuyList(new ArrayList<IngredientToBuy>());
                for (int i = 0; i < recipeList.size(); i++) {
                    notFound.getIngredientToBuyList().add(i, new IngredientToBuy().setName(recipeList.get(i).getName()));
                }

            }

            @Override
            protected Boolean doInBackground(Void... params) {
                for (int i = 0; i < recipeList.size(); i++) {
                    for (int j = 0; j < userList.size(); j++) {
                        if (recipeList.get(i).getName().equals(userList.get(j).getName())) {
                            notFound.remove(recipeList.get(i));
                        }
                    }
                }
                try {
                    mDatListApi.addIngredientToBuy(mCurrentUser.getUsername(), notFound).execute();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    GoogleJsonResponseException googleJsonResponseException = (GoogleJsonResponseException) e;
                    String errorMessage = googleJsonResponseException.getStatusMessage();
                    return false;

                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success)
                    Toast.makeText(ViewRecipeActivity.this, "Success", Toast.LENGTH_SHORT).show();
                super.onPostExecute(success);
            }
        }.execute();
    }
}
