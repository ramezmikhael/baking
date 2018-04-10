package projects.ramez.baking.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import projects.ramez.baking.R;
import projects.ramez.baking.models.Recipe;

/**
 * Created by Ramez on 3/27/2018.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
    private final Context mContext;
    private final List<Recipe> mRecipesList;

    private ItemClickListener mItemClickListener;

    public RecipesAdapter(Context context, List<Recipe> recipesList) {
        mContext = context;
        mRecipesList = recipesList;
    }

    public void setRecipeClickListener(ItemClickListener clicker) {
        mItemClickListener = clicker;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.mRecipeName.setText(mRecipesList.get(position).getName());
        holder.mCount.setText(
                mContext.getResources().getString(R.string.serving) + " " +
                String.valueOf(mRecipesList.get(position).getServings()));

        // If there is an image link load the image,
        // if the link is not a valid image load the default thumb.
        if(!TextUtils.isEmpty(mRecipesList.get(position).getImage())) {
            Picasso.get().load(mRecipesList.get(position).getImage())
                    .error(R.drawable.if_food)
                    .into(holder.mImage);
        }
    }

    @Override
    public int getItemCount() {
        return mRecipesList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mRecipeName;
        final TextView mCount;
        final ImageView mImage;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mRecipeName = itemView.findViewById(R.id.recipe_name);
            mCount = itemView.findViewById(R.id.serving_count);
            mImage = itemView.findViewById(R.id.recipe_thumb);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mItemClickListener.onRecipeClick(view, getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onRecipeClick(View view, int pos);
    }
}
