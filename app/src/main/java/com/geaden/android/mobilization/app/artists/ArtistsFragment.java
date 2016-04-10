package com.geaden.android.mobilization.app.artists;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.geaden.android.mobilization.app.artistdetail.ArtistDetailActivity;
import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.data.ArtistsRepository;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display artists cards.
 *
 * @author Gennady Denisov
 */
public class ArtistsFragment extends Fragment implements ArtistsContract.View {

    @Inject
    ArtistsRepository mArtistsRepository;

    private ArtistsPresenter mActionsListener;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.artists_list)
    RecyclerView mRecyclerView;

    @Bind(R.id.empty)
    TextView mEmptyView;

    private ArtistsAdapter mArtistsAdapter;


    public ArtistsFragment() {

    }

    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assign singleton instances to repository field annotated with Inject.
        ((ArtistsApplication) getActivity().getApplication()).getRepositoryComponent().inject(this);
        mArtistsAdapter = new ArtistsAdapter(getActivity(), new ArrayList<Artist>(0), mItemListener);
    }

    // Open artist details on click.
    ArtistItemListener mItemListener = new ArtistItemListener() {
        @Override
        public void onArtistClick(Artist clickedArtist, View coverView) {
            mActionsListener.openArtistDetails(clickedArtist, coverView);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mActionsListener = new ArtistsPresenter(mArtistsRepository, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_artists, container, false);
        ButterKnife.bind(this, root);

        mRecyclerView.setAdapter(mArtistsAdapter);

        int numColumns = getContext().getResources().getInteger(R.integer.num_artists_columns);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));

        mArtistsAdapter.setEmptyView(mEmptyView);

        // Pull-to-refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadArtists(true);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadArtists(false);
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        // Prevent unexpected behavior if the view is not in the place.
        if (getView() == null) {
            return;
        }

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void showArtists(List<Artist> artists) {
        mArtistsAdapter.replaceData(artists);
    }

    @Override
    public void showArtistDetailUi(long artistId, View coverView) {
        ArtistDetailActivity.launch(
                getActivity(), artistId, coverView);
    }

    /**
     * Recycler view adapter to display artists.
     */
    public static class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ViewHolder> {

        private Context mContext;
        private List<Artist> mArtists;
        private ArtistItemListener mItemListener;
        private View mEmptyView;

        public ArtistsAdapter(Context context, List<Artist> artists, ArtistItemListener itemListener) {
            setList(artists);
            mContext = context;
            mItemListener = itemListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View artistView = inflater.inflate(R.layout.item_artist, parent, false);

            return new ViewHolder(artistView, mItemListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Artist artist = mArtists.get(position);

            viewHolder.artistName.setText(artist.getName());
            viewHolder.artistGenres.setText(Joiner.on(", ").skipNulls()
                    .join(artist.getGenres()));
            viewHolder.albumsTracks.setText(mContext.getString(R.string.albums_tracks, artist.getAlbums(),
                    artist.getTracks()));
            // This app uses Glide for image loading
            Glide.with(mContext)
                    .load(artist.getCover().getSmall())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new GlideDrawableImageViewTarget(viewHolder.coverSmall) {
                        @Override
                        public void onResourceReady(GlideDrawable resource,
                                                    GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            // TODO: EspressoIdlingResource.decrement(); // App is idle.
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return mArtists.size();
        }

        private void setList(List<Artist> artists) {
            mArtists = checkNotNull(artists);
        }

        public Artist getItem(int position) {
            return mArtists.get(position);
        }

        /**
         * Alternative method to set empty view.
         *
         * @param emptyView view representing empty state.
         */
        public void setEmptyView(View emptyView) {
            mEmptyView = emptyView;
        }

        /**
         * Replaces data and notifies about data set change. Alternative to CursorAdapter#swapData.
         *
         * @param artists list or artists to replace.
         */
        public void replaceData(@NonNull List<Artist> artists) {
            if (artists.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            setList(artists);
            notifyDataSetChanged();
        }

        /**
         * Use of ViewHolder pattern.
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.artist_name)
            TextView artistName;

            @Bind(R.id.artist_genres)
            TextView artistGenres;

            @Bind(R.id.artist_albums_tracks)
            TextView albumsTracks;

            @Bind(R.id.cover_small)
            ImageView coverSmall;

            private ArtistItemListener mItemListener;

            public ViewHolder(View itemView, ArtistItemListener listener) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mItemListener = listener;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Artist artist = getItem(position);
                mItemListener.onArtistClick(artist, coverSmall);
            }
        }
    }

    /**
     * Listener for artist item clicks.
     */
    interface ArtistItemListener {
        void onArtistClick(Artist clickedArtist, View coverView);
    }
}
