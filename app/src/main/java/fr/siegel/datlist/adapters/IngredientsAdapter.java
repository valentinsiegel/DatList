package fr.siegel.datlist.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.siegel.datlist.MainActivity;
import fr.siegel.datlist.R;
import fr.siegel.datlist.backend.datListEndpoint.model.Ingredient;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder>  {



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }


        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public IngredientsAdapter(List<Ingredient> ingredientList) {
        this.mIngredientList = ingredientList;
    }

    public static interface IMyViewHolderClicks {
        public void onPotato(View caller);
    }


    // Create new views (invoked by the layout manager)
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder((TextView) view);
        return viewHolder;
    }

    private List<Ingredient> mIngredientList;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mIngredientList.get(position).getName());

    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }
}