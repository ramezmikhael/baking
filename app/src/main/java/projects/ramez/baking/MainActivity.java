package projects.ramez.baking;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import projects.ramez.baking.adapters.RecipesAdapter;
import projects.ramez.baking.loaders.RecipeListLoader;
import projects.ramez.baking.models.Recipe;
import projects.ramez.baking.utils.JsonUtils;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>,
        RecipesAdapter.ItemClickListener {

    @BindView(R.id.recipes_recyclerview) RecyclerView rvRecipes;

    private String TAG = "MainActivity";
    private static final int RECIPES_LOADER_ID = 1;
    private List<Recipe> mRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        getSupportLoaderManager().restartLoader(RECIPES_LOADER_ID, null, this).forceLoad();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new RecipeListLoader(this, SuperGlobals.RECIPES_URL);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(TAG, "RESULT: " + data);

        mRecipes = JsonUtils.parseRecipesJson(data);
        RecipesAdapter adapter = new RecipesAdapter(this, mRecipes);
        adapter.setRecipeClickListener(this);
        rvRecipes.setAdapter(adapter);
        if(getResources().getBoolean(R.bool.isTablet))
            rvRecipes.setLayoutManager(new GridLayoutManager(this, 3));
        else
            rvRecipes.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onRecipeClick(View view, int pos) {
        Log.d(TAG, "recipe clicked: " + pos);

        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra(RecipeDetailsActivity.EXTRA_RECIPE, mRecipes.get(pos));
        startActivity(intent);
    }
}
