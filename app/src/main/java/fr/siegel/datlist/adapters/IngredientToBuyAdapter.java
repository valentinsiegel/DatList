package fr.siegel.datlist.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.siegel.datlist.R;
import fr.siegel.datlist.backend.datListApi.model.IngredientToBuy;

public class IngredientToBuyAdapter extends RecyclerView.Adapter<IngredientToBuyAdapter.ViewHolder> {

    private List<IngredientToBuy> ingredientToBuyList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public IngredientToBuyAdapter(List<IngredientToBuy> ingredientToBuyList) {
        this.ingredientToBuyList = ingredientToBuyList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public IngredientToBuyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
        holder.mTextView.setText(ingredientToBuyList.get(position).getName());
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (ingredientToBuyList != null)
            return ingredientToBuyList.size();
        return 0;
    }

    public void addIngredient(IngredientToBuy ingredientToBuy) {
        this.ingredientToBuyList.add(0, ingredientToBuy);
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


