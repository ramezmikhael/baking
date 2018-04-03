package projects.ramez.baking;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import projects.ramez.baking.models.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity
        implements RecipeDetailsFragment.OnRecipeDetailInteractionListener,
        RecipeStepDetailsFragment.OnRecipeStepDetailInteractionListener {

    public static final String EXTRA_RECIPE = "recipe";

    private Recipe mRecipe;
    private boolean mIsTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey(EXTRA_RECIPE)) {

            mRecipe = bundle.getParcelable(EXTRA_RECIPE);
        }

        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        if(savedInstanceState == null) {
            RecipeDetailsFragment recipeDetailsFragment = RecipeDetailsFragment.newInstance(mRecipe);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.recipe_details, recipeDetailsFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStepClicked(int pos) {

        if(!mIsTablet) {
            Intent intent = new Intent(this, RecipeStepDetailsActivity.class);
            intent.putExtra(RecipeStepDetailsActivity.ARG_RECIPE, mRecipe);
            intent.putExtra(RecipeStepDetailsActivity.ARG_STEP_ID, pos);
            startActivity(intent);
        } else {
            RecipeStepDetailsFragment fragment = RecipeStepDetailsFragment.newInstance(mRecipe.getSteps()[pos],
                    mRecipe.getSteps().length - 1);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.recipe_steps_details, fragment);
            transaction.commit();
        }
    }

    @Override
    public void onChangeToStep(int pos) {
        onStepClicked(pos);
    }
}
