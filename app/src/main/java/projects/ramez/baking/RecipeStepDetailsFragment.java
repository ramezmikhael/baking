package projects.ramez.baking;

import android.app.Dialog;
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
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import projects.ramez.baking.models.Step;


public class RecipeStepDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_STEP = "step";
    private static final String ARG_MAX_STEP_ID = "max_step_id";
    private static final String ARG_VIDEO_POSITION = "video_position";

    private boolean mExoPlayerFullscreen;
    private int mMaxStepId;
    private Step mStep;
    private SimpleExoPlayer mPlayer;
    private Dialog mFullScreenDialog;

    private OnRecipeStepDetailInteractionListener mListener;

    @BindView(R.id.no_video)
    TextView tvNoVideo;
    @Nullable @BindView(R.id.step_description)
    TextView tvStepDescription;
    @BindView(R.id.videoPlayer)
    PlayerView playerView;
    @Nullable @BindView(R.id.next_fab)
    FloatingActionButton fabNext;
    @Nullable @BindView(R.id.pre_fab)
    FloatingActionButton fabPrevious;

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
        setRetainInstance(true);

        if (getArguments() != null) {
            mStep = getArguments().getParcelable(ARG_STEP);
            mMaxStepId = getArguments().getInt(ARG_MAX_STEP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipe_step_details, container, false);
        ButterKnife.bind(this, rootView);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        // Phone-Land: video
        // Tablet: video + description
        // Phone-Portrait: video + description + buttons
        if(tvStepDescription != null) {
            tvStepDescription.setText(mStep.getDescription());
        }
        if(fabNext != null) {
            fabNext.setOnClickListener(this);
            fabPrevious.setOnClickListener(this);
            showHideNextPreButtons();
        }

        if(!isTablet) {
            if(orientation == Configuration.ORIENTATION_LANDSCAPE && !mStep.getVideoURL().isEmpty()) {
                /*
                 * To handle full-screen video layout I used this source with the help of my great classroom mentor :)
                 * https://geoffledak.com/blog/2017/09/11/how-to-add-a-fullscreen-toggle-button-to-exoplayer-in-android/
                 */
                initFullscreenDialog();
                openFullscreenDialog();
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT && !mStep.getVideoURL().isEmpty()) {
                tvNoVideo.setVisibility(View.GONE);
            } else {
                playerView.setVisibility(View.GONE);
            }
        } else {
            if(mStep.getVideoURL().isEmpty())
                playerView.setVisibility(View.GONE);
            else
                tvNoVideo.setVisibility(View.GONE);
        }

        handleVideoPlayer();
        if(savedInstanceState != null && savedInstanceState.containsKey(ARG_VIDEO_POSITION)) {
            mPlayer.seekTo(savedInstanceState.getLong(ARG_VIDEO_POSITION));
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mPlayer != null) {
            outState.putLong(ARG_VIDEO_POSITION, mPlayer.getCurrentPosition());
        }
    }

    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        getActivity().finish();
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

    // onPause and onStop comes from this demo code:
    // https://github.com/google/ExoPlayer/blob/release-v2/demos/main/src/main/java/com/google/android/exoplayer2/demo/PlayerActivity.java#L185
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
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
