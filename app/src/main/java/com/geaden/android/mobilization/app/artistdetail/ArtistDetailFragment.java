package com.geaden.android.mobilization.app.artistdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.geaden.android.mobilization.app.ArtistsApplication;
import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.data.ArtistsRepository;
import com.geaden.android.mobilization.app.util.Constants;
import com.google.common.base.Joiner;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Main UI for the artist's detail screen.
 *
 * @author Gennady Denisov
 */
public class ArtistDetailFragment extends Fragment implements ArtistDetailContract.View {
    public static final String ARTIST_ID = "artist_id";

    @Inject
    ArtistsRepository mArtistsRepository;

    private ArtistDetailContract.UserActionsListener mActionsListener;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.artist_details_name)
    TextView mArtistName;

    @Bind(R.id.artist_details_description)
    TextView mArtistDescription;

    @Bind(R.id.cover_image)
    ImageView mCoverImage;

    @Bind(R.id.artist_details_genres)
    TextView mArtistGenres;

    @Bind(R.id.artist_details_albums_tracks)
    TextView mArtistAlbumsTrack;

    @Bind(R.id.artist_link_fab)
    FloatingActionButton mArtistLinkFab;

    @Bind(R.id.artist_detail_genres_icon)
    ImageView mArtistGenresIcon;

    @Bind(R.id.artist_details_albums_tracks_icon)
    ImageView mArtistAlbumsTracksIcon;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Bind(R.id.appbar)
    AppBarLayout mAppBar;

    // Name of the artist.
    private String mName;

    public ArtistDetailFragment() {

    }

    public static ArtistDetailFragment newInstance(long artistId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARTIST_ID, artistId);
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assign singleton instances to repository field annotated with Inject.
        ((ArtistsApplication) getActivity().getApplication()).getRepositoryComponent().inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActionsListener = new ArtistDetailPresenter(mArtistsRepository, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, root);

        // Initialize toolbar
        getActivityCast().setSupportActionBar(mToolbar);
        getActivityCast().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivityCast().getSupportActionBar().setTitle(null);

        // Add scroll listener to show toolbar title when collapsed.
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShown = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbar.setTitle(mName);
                    mToolbar.setBackground(null);
                    hideName();
                    isShown = true;
                } else if (isShown) {
                    mCollapsingToolbar.setTitle(null);
                    mToolbar.setBackground(ContextCompat.getDrawable(getActivity(),
                            R.drawable.overlay_bg_top_down));
                    showName(mName);
                    isShown = false;
                }
            }
        });
        return root;
    }

    private ArtistDetailActivity getActivityCast() {
        return (ArtistDetailActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        long artistId = getArguments().getLong(ARTIST_ID);
        mActionsListener.openArtist(artistId);
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            showName(getString(R.string.loading));
        }
    }

    @Override
    public void showName(String name) {
        mName = name;
        mArtistName.setBackground(ContextCompat.getDrawable(getActivity(),
                R.drawable.overlay_bg_bottom_up));
        mArtistName.setText(name);
    }

    @Override
    public void showDescription(String description) {
        // Capitalize description.
        String capitalizedDescription = Character.toUpperCase(description.charAt(0)) +
                description.substring(1, description.length());
        mArtistDescription.setText(capitalizedDescription);
    }

    @Override
    public void showCover(String coverLink) {
        Glide.with(this)
                .load(coverLink)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mCoverImage);
    }

    @Override
    public void showCover(Integer resourceId) {
        Glide.with(this)
                .load(resourceId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(new GlideDrawableImageViewTarget(mCoverImage) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                    }
                });
    }

    @Override
    public void showMissingArtist() {
        hideName();
        hideGenres();
        hideAlbumsAndTracks();
        hideOpenArtistLinkFab();
        showCover(R.drawable.nav_header_background);
        showDescription(getString(R.string.missing_artist));
    }

    @Override
    public void showGenres(String[] genres) {
        setVisible(mArtistGenresIcon);
        setVisible(mArtistGenres);
        mArtistGenres.setText(Joiner.on(Constants.GENRES_SEPARATOR).skipNulls()
                .join(genres));
    }

    @Override
    public void showAlbumsAndTracks(int albums, int tracks) {
        setVisible(mArtistAlbumsTracksIcon);
        setVisible(mArtistAlbumsTrack);
        mArtistAlbumsTrack.setText(getString(R.string.artist_details_albums_tracks, albums, tracks));
    }

    /**
     * Helper method to set visibility to #VISIBLE of a view.
     *
     * @param view the view to set visibility to.
     */
    private static void setVisible(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Helper method to set visibility to #GONE of a view.
     *
     * @param view the view to set visibility to.
     */
    private static void setGone(View view) {
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void showOpenArtistLinkFab(@Nullable final String artistLink) {
        setVisible(mArtistLinkFab);
        mArtistLinkFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.openArtistLink(getActivity(), artistLink);
            }
        });
    }

    @Override
    public void hideOpenArtistLinkFab() {
        setGone(mArtistLinkFab);
    }

    @Override
    public void hideName() {
        mArtistName.setText("");
        mArtistName.setBackground(null);
    }

    @Override
    public void hideGenres() {
        setGone(mArtistGenresIcon);
        setGone(mArtistGenres);
    }

    @Override
    public void hideAlbumsAndTracks() {
        setGone(mArtistAlbumsTracksIcon);
        setGone(mArtistAlbumsTrack);
    }
}
