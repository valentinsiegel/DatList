package fr.siegel.datlist.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.siegel.datlist.R;
import fr.siegel.datlist.backend.datListApi.model.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {


    private Context mContext;
    private List<Recipe> mRecipeList;

    public RecipeAdapter() {

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecipeAdapter(List<Recipe> recipeList) {
        this.mRecipeList = recipeList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.recipeName.setText(mRecipeList.get(position).getName());
        holder.recipeDesciption.setText(mRecipeList.get(position).getDescription());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mRecipeList != null)
            return mRecipeList.size();
        else
            return 0;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView recipeName;
        public TextView recipeDesciption;

        public ViewHolder(View view) {
            super(view);
            recipeName = (TextView) view.findViewById(R.id.recipe_name);
            recipeDesciption = (TextView) view.findViewById(R.id.recipe_description);
        }
    }
}