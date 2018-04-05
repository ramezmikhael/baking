package projects.ramez.baking;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import projects.ramez.baking.models.Ingredient;
import projects.ramez.baking.models.Recipe;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    public static Recipe latestOpenedRecipe;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_provider);

        if(latestOpenedRecipe != null) {
            // Ingredients
            StringBuilder sIngredients = new StringBuilder();
            for (Ingredient ing : latestOpenedRecipe.getIngredients()) {
                sIngredients.append(ing.getIngredient() + " (" + ing.getQuantity() + " " +
                        ing.getMeasure() + ")");
                sIngredients.append("\n");
            }

            // Construct the RemoteViews object

            views.setTextViewText(R.id.recipe_name, latestOpenedRecipe.getName());
            views.setTextViewText(R.id.recipe_ingredients, sIngredients);

            // Handle click to open the selected recipe in activity
            Intent intent = new Intent(context, RecipeDetailsActivity.class);
            intent.putExtra(RecipeDetailsActivity.EXTRA_RECIPE, latestOpenedRecipe);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            views.setOnClickPendingIntent(R.id.root_layout, pendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateIngredientsWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

