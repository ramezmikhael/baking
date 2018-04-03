package projects.ramez.baking;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import projects.ramez.baking.adapters.RecipeStepsAdapter;
import projects.ramez.baking.models.Ingredient;
import projects.ramez.baking.models.Recipe;


public class RecipeDetailsFragment extends Fragment implements RecipeStepsAdapter.OnItemClickListener {

    @BindView(R.id.ingredients) TextView tvIngredients;
    @BindView(R.id.steps_recyclerview) RecyclerView rvSteps;

    private static final String ARG_RECIPE = "recipe";

    private Recipe mRecipe;

    private OnRecipeDetailInteractionListener mListener;
    private String TAG = "RecipeDetailsFragment";

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailsFragment newInstance(Recipe recipe) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = getArguments().getParcelable(ARG_RECIPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        ButterKnife.bind(this, rootView);

        // Ingredients
        StringBuilder sIngredients = new StringBuilder();
        for (Ingredient ing : mRecipe.getIngredients()) {
            sIngredients.append(ing.getIngredient() + " (" + ing.getQuantity() + " " +
                    ing.getMeasure() + ")");
            sIngredients.append("\n");
        }
        tvIngredients.setText(sIngredients);

        // Steps
        RecipeStepsAdapter recipeStepsAdapter = new RecipeStepsAdapter(getActivity(), mRecipe.getSteps());
        recipeStepsAdapter.setOnStepClickListener(this);
        rvSteps.setAdapter(recipeStepsAdapter);
        rvSteps.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSteps.setNestedScrollingEnabled(false);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeDetailInteractionListener) {
            mListener = (OnRecipeDetailInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeDetailInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRecipeStepClicked(int position) {
        Log.d(TAG, "Clicked step: " + String.valueOf(position));
        if (mListener != null) {
            mListener.onStepClicked(position);
        }
    }

    public interface OnRecipeDetailInteractionListener {
        void onStepClicked(int pos);
    }
}
