package com.geaden.android.mobilization.app.artistdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.data.ArtistsRepository;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Main UI for the artist's detail screen.
 *
 * @author Gennady Denisov
 */
public class ArtistDetailFragment extends Fragment implements ArtistDetailContract.View {
    public static final String ARTIST_ID = "artist_id";

    private ArtistsRepository artistsRepository;

    private ArtistDetailContract.UserActionsListener mActionsListener;

    @Bind(R.id.cover_image)
    ImageView mCoverImage;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActionsListener = new ArtistDetailPresenter(artistsRepository, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        long artistId = getArguments().getLong(ARTIST_ID);
        mActionsListener.openArtist(artistId);
    }

    @Override
    public void setProgressIndicator(boolean active) {
        // TODO: logic for progress indicator...

    }

    @Override
    public void showName(String name) {
        // TODO: Implement this

    }

    @Override
    public void showDescription(String description) {
        // TODO: Implement this

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
                        //EspressoIdlingResource.decrement(); // App is idle.
                    }
                });

    }

    @Override
    public void showMissingArtist() {
        // TODO: Implement this

    }

    @Override
    public void showGenres(String[] genres) {
        // TODO: Implement this

    }

    @Override
    public void showTracks(int tracks) {
        // TODO: Implement this

    }

    @Override
    public void showAlbums(int albums) {
        // TODO: Implement this

    }
}
