package com.geaden.android.mobilization.app.artists;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geaden.android.mobilization.app.R;
import com.geaden.android.mobilization.app.data.Artist;
import com.geaden.android.mobilization.app.data.ArtistsRepository;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display artists cards.
 *
 * @author Gennady Denisov
 */
public class ArtistsFragment extends Fragment implements ArtistsContract.View {

    private ArtistsRepository artistsRepository;
    private ArtistsPresenter mActionsListener;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);

        mActionsListener = new ArtistsPresenter(artistsRepository, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_artists, container, false);
        ButterKnife.bind(this, root);
        // TODO: Initialize recycler view.
        // Pull-to-refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadArtists();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadArtists();
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        // Prevent unexpected behavior if the view is not in place.
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

    }

    @Override
    public void showArtistDetailUi(long artistId) {

    }

    /**
     * Recycler view adapter to display artists.
     */
    public static class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ViewHolder> {

        private List<Artist> mArtists;
        private ArtistItemListener mItemListener;

        public ArtistsAdapter(List<Artist> artists, ArtistItemListener itemListener) {
            setList(artists);
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

            viewHolder.title.setText(artist.getTitle());
            viewHolder.description.setText(artist.getDescription());

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
         * Use of ViewHolder pattern.
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.artist_title)
            TextView title;

            @Bind(R.id.artist_description)
            TextView description;

            private ArtistItemListener mItemListener;

            public ViewHolder(View itemView, ArtistItemListener listener) {
                super(itemView);
                mItemListener = listener;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Artist artist = getItem(position);
                mItemListener.onArtistClick(artist);
            }
        }
    }

    /**
     * Listener for artist item click.
     */
    interface ArtistItemListener {
        void onArtistClick(Artist clickedArtist);

    }
}
