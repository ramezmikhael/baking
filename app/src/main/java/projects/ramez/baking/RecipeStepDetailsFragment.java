package projects.ramez.baking;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import projects.ramez.baking.models.Recipe;
import projects.ramez.baking.models.Step;


public class RecipeStepDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_STEP = "step";
    private static final String ARG_MAX_STEP_ID = "max_step_id";

    private boolean mIsTablet;
    private int mOrientation;
    private int mMaxStepId;
    private Step mStep;
    private SimpleExoPlayer mPlayer;

    OnRecipeStepDetailInteractionListener mListener;

    @Nullable @BindView(R.id.step_description) TextView tvStepDescription;
    @BindView(R.id.videoPlayer) PlayerView playerView;
    @Nullable @BindView(R.id.next_fab) FloatingActionButton fabNext;
    @Nullable @BindView(R.id.pre_fab) FloatingActionButton fabPrevious;
    @Nullable @BindView(R.id.no_video) TextView tvNoVideo;

    public RecipeStepDetailsFragment() {
        // Required empty public constructor
    }

    public static RecipeStepDetailsFragment newInstance(Step step, int maxStepId) {
        RecipeStepDetailsFragment fragment = new RecipeStepDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP, step);
        args.putInt(ARG_MAX_STEP_ID, maxStepId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStep = getArguments().getParcelable(ARG_STEP);
            mMaxStepId = getArguments().getInt(ARG_MAX_STEP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_step_details, container, false);
        ButterKnife.bind(this, view);

        mIsTablet = getResources().getBoolean(R.bool.isTablet);
        mOrientation = getActivity().getResources().getConfiguration().orientation;

        // Phone-Land: video
        // Tablet: video + description
        // Phone-Portrait: video + description + buttons
        if(mOrientation == Configuration.ORIENTATION_PORTRAIT || mIsTablet) {
            tvStepDescription.setText(mStep.getDescription());
        }
        if(!mIsTablet && mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            fabNext.setOnClickListener(this);
            fabPrevious.setOnClickListener(this);
            showHideNextPreButtons();
        }

        // Hide NO VIDEO message if there is available video to play
        if(!mStep.getVideoURL().isEmpty()) {
            tvNoVideo.setVisibility(View.GONE);
            handleVideoPlayer();
        }

        return view;
    }

    private void showHideNextPreButtons() {
        if(mStep.getId() <= 0 ){
            fabPrevious.setVisibility(View.INVISIBLE);
            fabNext.setVisibility(View.VISIBLE);
        } else if(mStep.getId() >= mMaxStepId) {
            fabPrevious.setVisibility(View.VISIBLE);
            fabNext.setVisibility(View.INVISIBLE);
        } else {
            fabPrevious.setVisibility(View.VISIBLE);
            fabNext.setVisibility(View.VISIBLE);
        }
    }

    private void handleVideoPlayer() {
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();

        mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), getString(R.string.app_name)), null);

        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mStep.getVideoURL()));

        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);

        playerView.setPlayer(mPlayer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if(mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeStepDetailInteractionListener) {
            mListener = (OnRecipeStepDetailInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeStepDetailInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        releasePlayer();

        if(view.getId() == R.id.next_fab) {
            mListener.onChangeToStep(mStep.getId() + 1);
        } else if(view.getId() == R.id.pre_fab) {
            mListener.onChangeToStep(mStep.getId() - 1);
        }

        showHideNextPreButtons();
    }

    public interface OnRecipeStepDetailInteractionListener {
        void onChangeToStep(int pos);
    }
}
