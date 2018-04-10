package projects.ramez.baking;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import projects.ramez.baking.models.Ingredient;
import projects.ramez.baking.models.Recipe;
import projects.ramez.baking.models.Step;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by RamezReda on 4/6/2018.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeDetailsActivityTest {

    private final String RECIPE_NAME = "Cake";
    private final String STEP1_NAME = "Step 1";
    private final String STEP2_NAME = "Step 2";
    private final String STEP1_DESCRIPTION = "step 1 description";
    private final String STEP2_DESCRIPTION = "step 2 description";

    @Rule
    public ActivityTestRule<RecipeDetailsActivity> mRule =
            new ActivityTestRule<RecipeDetailsActivity>(RecipeDetailsActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Ingredient[] ingredients = new Ingredient[1];
                    ingredients[0] = new Ingredient();
                    ingredients[0].setQuantity(200);
                    ingredients[0].setMeasure("grams");
                    ingredients[0].setIngredient("Flour");

                    Step[] steps = new Step[2];
                    steps[0] = new Step();
                    steps[0].setId(1);
                    steps[0].setShortDescription(STEP1_NAME);
                    steps[0].setDescription(STEP1_DESCRIPTION);
                    steps[0].setVideoURL("https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4");
                    steps[0].setThumbnailURL("");

                    steps[1] = new Step();
                    steps[1].setId(2);
                    steps[1].setShortDescription(STEP2_NAME);
                    steps[1].setDescription(STEP2_DESCRIPTION);
                    steps[1].setVideoURL("");
                    steps[1].setThumbnailURL("");

                    Recipe recipe = new Recipe(1,
                            RECIPE_NAME,
                            ingredients,
                            steps,
                            8,
                            "");

                    Intent intent = new Intent();
                    intent.putExtra(RecipeDetailsActivity.EXTRA_RECIPE, recipe);
                    return intent;
                }
            };

    @Test
    public void recipeDetailsVideoClick() {
        onView(withId(R.id.steps_recyclerview))
                .perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.step_description)).check(matches(withText(STEP1_DESCRIPTION)));
        onView(withId(R.id.videoPlayer)).check(matches(isDisplayed()));
    }

    @Test
    public void recipeDetailsNoVideoClick() {
        onView(withId(R.id.steps_recyclerview))
                .perform(actionOnItemAtPosition(1, click()));

        onView(withId(R.id.step_description)).check(matches(withText(STEP2_DESCRIPTION)));
        onView(withId(R.id.no_video)).check(matches(isDisplayed()));
    }
}
