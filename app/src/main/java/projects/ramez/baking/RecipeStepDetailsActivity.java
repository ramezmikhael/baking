package projects.ramez.baking;

import android.content.res.Configuration;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import projects.ramez.baking.models.Recipe;
import projects.ramez.baking.models.Step;

public class RecipeStepDetailsActivity extends AppCompatActivity
        implements RecipeStepDetailsFragment.OnRecipeStepDetailInteractionListener {

    public static final String ARG_RECIPE = "recipe";
    public static final String ARG_STEP_ID = "step_id";

    private boolean mIsTablet;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        int stepId;

        if (bundle == null) {
            return;
        }

        mRecipe = bundle.getParcelable(ARG_RECIPE);
        stepId = bundle.getInt(ARG_STEP_ID);

         /*
         * If Tablet or Phone in portrait mode, set Activity's title
         * else hide the actionBar altogether.
         */
        mIsTablet = getResources().getBoolean(R.bool.isTablet);
        if(mIsTablet == false
                && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getSupportActionBar().setTitle(mRecipe.getName());
        }

        setContentView(R.layout.activity_recipe_step_details);

        RecipeStepDetailsFragment recipeDetailsFragment = RecipeStepDetailsFragment.newInstance(
                mRecipe.getSteps()[stepId], mRecipe.getSteps().length - 1);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.recipe_steps_details, recipeDetailsFragment)
                .commit();

    }

    @Override
    public void onChangeToStep(int pos) {
        RecipeStepDetailsFragment fragment = RecipeStepDetailsFragment.newInstance(mRecipe.getSteps()[pos],
                mRecipe.getSteps().length - 1);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.recipe_steps_details, fragment);
        transaction.commit();
    }
}
