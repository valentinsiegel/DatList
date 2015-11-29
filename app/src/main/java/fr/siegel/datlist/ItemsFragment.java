package fr.siegel.datlist;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

import fr.siegel.datlist.Utils.KeyboardUtils;
import fr.siegel.datlist.Utils.Utils;
import fr.siegel.datlist.adapters.IngredientAdapter;
import fr.siegel.datlist.adapters.RecyclerItemClickListener;
import fr.siegel.datlist.backend.datListApi.DatListApi;
import fr.siegel.datlist.backend.datListApi.model.Ingredient;
import fr.siegel.datlist.backend.datListApi.model.User;
import fr.siegel.datlist.defs.RecyclerViewType;
import fr.siegel.datlist.services.EndpointAsyncTask;

public class ItemsFragment extends Fragment {

  private RecyclerView mItemListRecyclerView;
  private User currentUser;
  private List<Ingredient> ingredientList;
  private IngredientAdapter mIngredientAdapter;
  private AutoCompleteTextView mIngredientNameTextView;
  private Button mCurrentAddButton;
  private boolean isButtonShown;
  private int pos;
  private ArrayAdapter<String> dictionaryAdapter;

  public ItemsFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_items, container, false);

    currentUser = Application.getApplication().getUser();

    mItemListRecyclerView = (RecyclerView) view.findViewById(R.id.item_list);

    dictionaryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, currentUser.getDictionary());

    //mEditText = (AutoCompleteTextView) view.findViewById(R.id.ingredient_name_edit_text);
/*        if(currentUser.getDictionary() != null){
            String[] ingredients = new String[currentUser.getDictionary().size()];
            for (int i = 0; i < currentUser.getDictionary().size(); i++) {
                ingredients[i] = currentUser.getDictionary().get(i);
            }
        }*/


    //mEditText.setAdapter(adapter);


    final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    mItemListRecyclerView.setLayoutManager(layoutManager);

    refreshIngredientList();

    RecyclerItemClickListener onRecipeClickListeners = new RecyclerItemClickListener(getActivity(), mItemListRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
      @Override
      public void onItemClick(View view, final int position) {
        enableEditOnView(view, mIngredientAdapter.getItemViewType(position), position);
      }

      @Override
      public void onItemLongClick(View view, int position) {
        deleteItem(mIngredientAdapter.getIngredient(position), position);
      }
    });

    mItemListRecyclerView.addOnItemTouchListener(onRecipeClickListeners);

    return view;
  }

  public void enableEditOnView(View view, int viewType, int position) {

    if (isButtonShown) {
      ((AutoCompleteTextView) mIngredientNameTextView).setText("");
      mIngredientNameTextView.setVisibility(View.GONE);
      mCurrentAddButton.setVisibility(View.VISIBLE);
      isButtonShown = false;
    }

    if (mIngredientNameTextView != null) {
      mIngredientNameTextView.setOnFocusChangeListener(null);
      mIngredientNameTextView.setOnKeyListener(null);
    }

    mIngredientNameTextView = (AutoCompleteTextView) view.findViewById(R.id.item_ingredient_name_auto_complete_text_view);

    MyOnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener(position, viewType);
    mIngredientNameTextView.setOnFocusChangeListener(myOnFocusChangeListener);
    mIngredientNameTextView.setOnKeyListener(myOnFocusChangeListener);

    if (viewType == RecyclerViewType.ADD_VIEW) {
      mCurrentAddButton = (Button) view.findViewById(R.id.add_ingredient_button);
      mIngredientNameTextView.setVisibility(View.VISIBLE);
      mCurrentAddButton.setVisibility(View.GONE);
      mIngredientNameTextView.requestFocus();
    } else {
      mIngredientNameTextView.requestFocus();
    }
  }

  public class MyOnFocusChangeListener implements OnFocusChangeListener, OnKeyListener {

    private int mPosition;
    private int mViewType;

    public MyOnFocusChangeListener(int position, int viewType) {
      this.mPosition = position;
      this.mViewType = viewType;
    }

    @Override
    public void onFocusChange(View mAutoCompleteTextView, boolean hasFocus) {
      if (!hasFocus) {
        if (mViewType == RecyclerViewType.ADD_VIEW) {
          if (Utils.checkForEmptyString(mIngredientNameTextView.getText().toString()))
            pos = mIngredientAdapter.addIngredient(new Ingredient().setName(mIngredientNameTextView.getText().toString()), mPosition);

          mIngredientNameTextView.setText("");
          mIngredientNameTextView.setVisibility(View.GONE);
          mCurrentAddButton.setVisibility(View.VISIBLE);
        } else {
          if (Utils.checkForEmptyString(mIngredientNameTextView.getText().toString()))
            pos = mIngredientAdapter.addIngredient(new Ingredient().setName(""), mPosition + 1);
        }

      } else {
        if (mViewType == RecyclerViewType.ADD_VIEW) {
          isButtonShown = true;
        }
        KeyboardUtils.getKeyboardOnView(getActivity(), mAutoCompleteTextView);
      }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
      pos = 0;
      if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
              (keyCode == KeyEvent.KEYCODE_ENTER)) {
/*                if (mViewType == RecyclerViewType.ADD_VIEW) {
                    mIngredientNameTextView.clearFocus();

                } else {
                    mIngredientNameTextView.clearFocus();

                }*/
        mIngredientNameTextView.clearFocus();
        if (pos != 0) {
          new Handler(getActivity().getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
              if (mViewType == RecyclerViewType.ADD_VIEW) {
                //View view = mIngredientAdapter.getTextViewAt(pos + 1);
                enableEditOnView(mIngredientAdapter.getTextViewAt(pos + 1), RecyclerViewType.ADD_VIEW, pos + 1);
              } else {
                //mIngredientAdapter.getTextViewAt(pos);
                enableEditOnView(mIngredientAdapter.getTextViewAt(pos), RecyclerViewType.REGULAR_VIEW, pos);
              }
            }

          }, 1);
        }

        MyOnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener(mPosition, mViewType);
        mIngredientNameTextView.setOnFocusChangeListener(myOnFocusChangeListener);
        mIngredientNameTextView.setOnKeyListener(myOnFocusChangeListener);
        return true;
      }
      return false;
    }
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

      DatListApi datListApi;

      @Override
      protected void onPreExecute() {
        datListApi = EndpointAsyncTask.getApi();
        super.onPreExecute();
      }

      @Override
      protected Boolean doInBackground(Void... params) {
        try {
          datListApi.insertIngredient(currentUser.getUsername(), ingredient).execute();
          return true;
        } catch (IOException e) {
          e.printStackTrace();
          return false;
        }
      }

      @Override
      protected void onPostExecute(Boolean success) {
        if (success) {
          retrieveUserData(currentUser.getUsername());
          //mIngredientAdapter.addIngredient(ingredient);

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
        currentUser = user;
        super.onPostExecute(aBoolean);
      }
    }.execute();
  }

  public void deleteItem(final Ingredient ingredient, final int ingredientIndex) {
    new AsyncTask<Void, Void, Boolean>() {

      DatListApi datListApi;

      @Override
      protected void onPreExecute() {

        datListApi = EndpointAsyncTask.getApi();
        super.onPreExecute();
      }

      @Override
      protected Boolean doInBackground(Void... params) {
        try {
          datListApi.deleteIngredient(currentUser.getUsername(), ingredient.getName(  ))
                  .execute();
          return true;
        } catch (IOException e) {
          e.printStackTrace();
          return true;
        }
      }

      @Override
      protected void onPostExecute(Boolean aBoolean) {
        mIngredientAdapter.removeIngredient(ingredientIndex);
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
          return EndpointAsyncTask.getApi().listIngredients(currentUser.getUsername()).execute()
                  .getItems();
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
          mIngredientAdapter = new IngredientAdapter(ItemsFragment.this.ingredientList);
          mItemListRecyclerView.setAdapter(mIngredientAdapter);
          mIngredientAdapter.notifyDataSetChanged();
        }
      }
    }.execute();
  }


  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }

}
