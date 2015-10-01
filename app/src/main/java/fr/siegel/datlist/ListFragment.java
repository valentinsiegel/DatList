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
import android.widget.AutoCompleteTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.siegel.datlist.adapters.IngredientToBuyAdapter;
import fr.siegel.datlist.adapters.RecyclerItemClickListener;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.IngredientToBuy;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class ListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private AutoCompleteTextView mEditText;
    private List<IngredientToBuy> mIngredientToBuyList;
    private DatListApi datListApi;
    private User mCurrentUser;
    private IngredientToBuyAdapter mRecyclerViewAdapter;
    public OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_get_items:
                    break;
                case R.id.button_add_items:
                    addIngredientToBuy();
                    mEditText.setText("");
                    break;
            }
        }
    };
    RecyclerItemClickListener onRecipeClickListeners = new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            if (mIngredientToBuyList != null) {

                buyIngredient(mIngredientToBuyList.get(position));
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    });

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        datListApi = EndpointAsyncTask.getApi();

        mCurrentUser = Application.getApplication().getUser();

        retrieveIngredientToBuy();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mRecyclerViewAdapter = new IngredientToBuyAdapter(mIngredientToBuyList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mEditText = (AutoCompleteTextView) view.findViewById(R.id.edit_text);

        (view.findViewById(R.id.button_add_items)).setOnClickListener(onClickListener);

        mRecyclerView.addOnItemTouchListener(onRecipeClickListeners);

        return view;
    }

    public void retrieveIngredientToBuy() {
        new AsyncTask<Void, Void, Boolean>() {

            List<IngredientToBuy> ingredientToBuyList;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    ingredientToBuyList = datListApi.listIngredientToBuy(mCurrentUser.getUsername()).execute().getItems();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    mIngredientToBuyList = ingredientToBuyList;
                    if (mIngredientToBuyList == null)
                        mIngredientToBuyList = new ArrayList<>();

                    mRecyclerViewAdapter = new IngredientToBuyAdapter(mIngredientToBuyList);
                    mRecyclerView.setAdapter(mRecyclerViewAdapter);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }

                super.onPostExecute(success);
            }

        }.execute();
    }

    public void addIngredientToBuy() {
        new AsyncTask<Void, Void, Boolean>() {

            IngredientToBuy ingredientToBuy;

            @Override
            protected void onPreExecute() {

                ingredientToBuy = new IngredientToBuy().setName(mEditText.getText().toString());
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    datListApi.addIngredientToBuy(mCurrentUser.getUsername(), ingredientToBuy).execute();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {

                    mRecyclerViewAdapter.addIngredient(ingredientToBuy);
                }

                super.onPostExecute(success);
            }

        }.execute();
    }

    public void buyIngredient(final IngredientToBuy ingredientToBuy) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    datListApi.buyIngredient(mCurrentUser.getUsername(), ingredientToBuy).execute();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    retrieveIngredientToBuy();
                }

                super.onPostExecute(success);
            }

        }.execute();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
