package fr.siegel.datlist;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.IOException;
import java.util.List;

import fr.siegel.datlist.Utils.Utils;
import fr.siegel.datlist.adapters.IngredientAdapter;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.Ingredient;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class ItemsFragment extends Fragment {

    private RecyclerView mItemListRecyclerView;

    private DatListApi mDatListApi;
    private AutoCompleteTextView mEditText;
    private User mCurrentUser;
    private List<Ingredient> ingredientList;
    private OnClickListener addIngredient = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Utils.checkForEmptyString(mEditText.getText().toString()))
                insertItem(new Ingredient().setName(mEditText.getText().toString()));
            mEditText.setText("");
        }
    };

    public ItemsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);

        mDatListApi = EndpointAsyncTask.getApi();
        mCurrentUser = Application.getApplication().getUser();

        mItemListRecyclerView = (RecyclerView) view.findViewById(R.id.item_list);


        mEditText = (AutoCompleteTextView) view.findViewById(R.id.ingredient_name_edit_text);

        String[] ingredients = new String[mCurrentUser.getDictionary().size()];
        for (int i = 0; i < mCurrentUser.getDictionary().size(); i++) {
            ingredients[i] = mCurrentUser.getDictionary().get(i);
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ingredients);
        mEditText.setAdapter(adapter);

        view.findViewById(R.id.button_add_items).setOnClickListener(addIngredient);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mItemListRecyclerView.setLayoutManager(layoutManager);

        refreshIngredientList();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void insertItem(final Ingredient ingredient) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                mDatListApi = EndpointAsyncTask.getApi();
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    mDatListApi.insertIngredient(mCurrentUser.getUsername(), ingredient).execute();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    retrieveUserData(mCurrentUser.getUsername());
                    refreshIngredientList();
                }

                super.onPostExecute(success);

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

    public void refreshIngredientList() {
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
                ItemsFragment.this.ingredientList = ingredientList;
                if (ItemsFragment.this.ingredientList != null) {
                    IngredientAdapter ingredientAdapter = new IngredientAdapter(ItemsFragment.this.ingredientList);
                    mItemListRecyclerView.setAdapter(ingredientAdapter);
                    ingredientAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
