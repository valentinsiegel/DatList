package fr.siegel.datlist.services;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import fr.siegel.datlist.backend.ingredientEndpoint.IngredientEndpoint;
import fr.siegel.datlist.backend.ingredientEndpoint.model.Ingredient;

/**
 * Created by Val on 23/08/15.
 */
public class EndpointAsyncTask {

    private static IngredientEndpoint ingredientEndpoint = null;

    public EndpointAsyncTask() {
        if (ingredientEndpoint == null) { // Only do this once
            IngredientEndpoint.Builder builder = new IngredientEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            ingredientEndpoint = builder.build();
        }
    }

    public void listIngredients() {
        new AsyncTask<Void, Void, List<Ingredient>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<Ingredient> doInBackground(Void... params) {
                try {
                    return ingredientEndpoint.listIngredients().execute().getItems();
                } catch (IOException e) {
                    return Collections.EMPTY_LIST;
                }
            }

            @Override
            protected void onPostExecute(List<Ingredient> ingredients) {
                super.onPostExecute(ingredients);
            }
        }.execute();
    }
}
