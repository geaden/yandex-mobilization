package com.geaden.android.mobilization.app.artistdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
    TextView mArtistTracksAlbums;

    @Bind(R.id.artist_link_fab)
    FloatingActionButton mArtistLinkFab;

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
            mArtistName.setText(getString(R.string.loading));
        }
    }

    @Override
    public void showName(String name) {
        mArtistName.setText(name);
    }

    @Override
    public void showDescription(String description) {
        mArtistDescription.setText(description);
    }

    @Override
    public void showCover(String coverLink) {
        // This app uses Glide for image loading
        Glide.with(this)
                .load(coverLink)
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
        // TODO: Implement this

    }

    @Override
    public void showGenres(String[] genres) {
        mArtistGenres.setText(Joiner.on(Constants.GENRES_SEPARATOR).skipNulls()
                .join(genres));
    }

    @Override
    public void showAlbumsAndTracks(int albums, int tracks) {
        mArtistTracksAlbums.setText(getString(R.string.artist_details_albums_tracks, albums, tracks));
    }

    @Override
    public void showOpenArtistLinkFab(@Nullable final String artistLink) {
        if (null == artistLink) {
            mArtistLinkFab.setVisibility(View.GONE);
        } else {
            mArtistLinkFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionsListener.openArtistLink(getActivity(), artistLink);
                }
            });
        }
    }
}
