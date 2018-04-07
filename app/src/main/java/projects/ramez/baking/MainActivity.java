package projects.ramez.baking;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

    @BindView(R.id.root_layout)
    ConstraintLayout rootLayout;
    @BindView(R.id.loading)
    TextView tvLoading;
    @BindView(R.id.recipes_recyclerview)
    RecyclerView rvRecipes;

    private final String TAG = "MainActivity";
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(MainActivity.this);
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_credit) {
            startActivity(new Intent(this, CreditActivity.class));
        }
        return true;
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

        if(data.isEmpty()) {
            Snackbar.make(rootLayout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadRecipes();
                        }
                    }).show();
            return;
        }

        // Hide the loading TextView
        tvLoading.setVisibility(View.GONE);

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
