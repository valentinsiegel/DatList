package fr.siegel.datlist.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.siegel.datlist.R;
import fr.siegel.datlist.backend.datListApi.model.Ingredient;
import fr.siegel.datlist.backend.datListApi.model.Recipe;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {


    private Context context;
    private List<Ingredient> recipeIngredients;
    private Recipe recipe;
    private List<Ingredient> userIngredients;

    // Provide a suitable constructor (depends on the kind of dataset)
    public IngredientAdapter(List<Ingredient> ingredientList) {
        this.recipeIngredients = ingredientList;
    }

    public IngredientAdapter(Context context, List<Ingredient> recipeIngredients, List<Ingredient> userIngredients) {
        this.context = context;
        this.recipeIngredients = recipeIngredients;
        this.recipe = recipe;
        this.userIngredients = userIngredients;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
/*        if(userIngredients != null)
            for (int i = 0; i < getItemCount(); i++){
                for(int j = 0; j < userIngredients.size(); j++){
                    if(!recipeIngredients.get(position).equals(userIngredients.get(j)))
                        holder.mLinearLayout.setBackgroundColor(Color.parseColor("#F44336"));
                }

            }*/
        if (userIngredients != null) {
     /*        for (int i = 0; i < recipeIngredients.size(); i++) {
                Ingredient ingredient = recipeIngredients.get(i);*/
            if (!userIngredients.contains(recipeIngredients.get(position))) {
                holder.mLinearLayout.setBackgroundColor(Color.parseColor("#F44336"));

                /**/
            }
        }

        //}
        ///}
        holder.mTextView.setText(recipeIngredients.get(position).getName());


    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (recipeIngredients != null)
            return recipeIngredients.size();
        return 0;
    }


    public void addIngredient(Ingredient ingredient) {
        this.recipeIngredients.add(0, ingredient);
        this.notifyItemInserted(0);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public LinearLayout mLinearLayout;

        public ViewHolder(View view) {
            super(view);

            mLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);
            mTextView = (TextView) view.findViewById(R.id.ingredient_name);

        }
    }
}