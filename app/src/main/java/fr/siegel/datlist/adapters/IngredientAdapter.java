package fr.siegel.datlist.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import fr.siegel.datlist.R;
import fr.siegel.datlist.backend.datListApi.model.Ingredient;
import fr.siegel.datlist.defs.RecyclerViewType;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private Context mContext;
    private List<Ingredient> mRecipeIngredients;
    private List<Ingredient> mUserIngredients;
    public ArrayList<View> mLayouts;

    public IngredientAdapter(List<Ingredient> ingredientList) {
        ingredientList.add(new Ingredient());
        this.mUserIngredients = ingredientList;
        mLayouts = new ArrayList<>();
    }

    public IngredientAdapter(Context context, List<Ingredient> recipeIngredients, List<Ingredient> userIngredients) {
        this.mContext = context;
        this.mRecipeIngredients = recipeIngredients;
        userIngredients.add(new Ingredient());
        this.mUserIngredients = userIngredients;
    }

    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType == RecyclerViewType.REGULAR_VIEW ? R.layout.item_ingredient : R.layout.item_list_add_button, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        mLayouts.add(position, viewHolder.mLayout);

        if (viewHolder.getItemViewType() == RecyclerViewType.REGULAR_VIEW) {
            viewHolder.mTextView.setText(mUserIngredients.get(position).getName());
        }

        if (mRecipeIngredients != null) {

            viewHolder.mLayout.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            viewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.white));

            for (int i = 0; i < mUserIngredients.size(); i++) {
                if (mUserIngredients.get(i).getName().equals(mRecipeIngredients.get(position).getName())) {
                    viewHolder.mLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.dark_blue));
                }
            }
            viewHolder.mTextView.setText(mRecipeIngredients.get(position).getName());
        }
        viewHolder.mTextView.setText(mUserIngredients.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mUserIngredients.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == mUserIngredients.size() - 1 ? RecyclerViewType.ADD_VIEW : RecyclerViewType.REGULAR_VIEW;
    }

    public View getTextViewAt(int position){
        View autoCompleteTextView = mLayouts.get(position);
        return autoCompleteTextView;
    }

    public int addIngredient(Ingredient ingredient, int position) {
        this.mUserIngredients.add(position, ingredient);
        this.notifyItemInserted(position);
        return position;
    }

    public void removeIngredient(int position) {
        this.mUserIngredients.remove(position);
        this.notifyItemRemoved(position);
    }

    public Ingredient getIngredient(int position) {
        return mUserIngredients.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public AutoCompleteTextView mTextView;
        public RelativeLayout mLayout;

        public ViewHolder(View view) {
            super(view);

            mLayout = (RelativeLayout) view.findViewById(R.id.linear_layout);
            mTextView = (AutoCompleteTextView) view.findViewById(R.id.item_ingredient_name_auto_complete_text_view);

        }
    }
}