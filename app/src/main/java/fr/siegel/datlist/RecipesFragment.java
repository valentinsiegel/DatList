package fr.siegel.datlist;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

import fr.siegel.datlist.adapters.RecipeAdapter;
import fr.siegel.datlist.adapters.RecyclerItemClickListener;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.Recipe;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class RecipesFragment extends Fragment {

    private static int ADD_RECIPE = 1;

    private OnFragmentInteractionListener mListener;

    private DatListApi mDatListApi = null;
    private User mCurrentUser;
    private List<Recipe> mRecipeList = null;
    private RecyclerView mRecyclerView;
    //ON RECIPE CLICK AND LONG CLICK
    RecyclerItemClickListener onRecipeClickListeners = new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            if (mRecipeList != null) {
                Recipe recipe = mRecipeList.get(position);
                Intent intent = new Intent(getActivity(), ViewRecipeActivity.class);
                intent.putExtra("recipeName", recipe.getName());
                startActivity(intent);
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    });
    private RecipeAdapter mRecipeAdapter;
    private OnClickListener addRecipeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), AddRecipeActivity.class);
            startActivityForResult(intent, ADD_RECIPE);
        }
    };

    public RecipesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        mCurrentUser = Application.getApplication().getUser();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        view.findViewById(R.id.button_add_items).setOnClickListener(addRecipeListener);
        mRecyclerView.addOnItemTouchListener(onRecipeClickListeners);

        refreshRecipeList();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void refreshRecipeList() {
        new AsyncTask<Void, Void, List<Recipe>>() {
            private String username;

            @Override
            protected void onPreExecute() {
                mDatListApi = EndpointAsyncTask.getApi();
                username = mCurrentUser.getUsername();
            }

            @Override
            protected List<Recipe> doInBackground(Void... params) {
                try {
                    return mDatListApi.retrieveRecipeByUser(username).execute().getItems();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Recipe> recipeList) {
                super.onPostExecute(recipeList);
                mRecipeList = recipeList;
                if (mRecipeList != null) {
                    mRecipeAdapter = new RecipeAdapter(recipeList);
                    mRecyclerView.setAdapter(mRecipeAdapter);
                    mRecipeAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int RECIPE_ADDED = 1;
        if (requestCode == ADD_RECIPE && requestCode == RECIPE_ADDED)
            refreshRecipeList();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * LISTENERS
     */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
