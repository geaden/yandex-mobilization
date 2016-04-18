package com.geaden.android.mobilization.app.artists;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.geaden.android.mobilization.app.data.LoadingStatus;
import com.geaden.android.mobilization.app.util.Constants;
import com.geaden.android.mobilization.app.util.Utility;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
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
public class ArtistsFragment extends Fragment implements ArtistsContract.View,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = ArtistsFragment.class.getSimpleName();
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
    private SearchView mSearchView;
    private MenuItem mSearchItem;

    // Order values
    private static final int TRACKS = 0;
    private static final int ALBUMS = 1;

    public ArtistsFragment() {
        setHasOptionsMenu(true);
    }

    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_artists, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearchItem = menu.findItem(R.id.menu_artists_search);
        mSearchView = (SearchView) mSearchItem.getActionView();

        mSearchView.setQueryHint(getString(R.string.artists_search_hint));
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (!TextUtils.isEmpty(mSearchView.getQuery())) {
                    mSearchView.setQuery(null, true);
                }
                return true;
            }
        });

        // Set search query text listener...
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                if (!TextUtils.isEmpty(query)) {
                    mActionsListener.loadArtistsByName(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Don't care about this.
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_artists_order:
                mActionsListener.selectOrder();
                return true;
            case R.id.menu_artists_genres:
                mActionsListener.loadGenres();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showSelectGenresDialog(final String[] genres) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Populate already filtered genres.
        String[] filterGenres = Utility.getFilterGenres(getActivity());
        boolean[] checked = null;
        if (filterGenres.length > 0) {
            checked = new boolean[genres.length];
            int i = 0;
            for (String genre : genres) {
                if (Arrays.binarySearch(filterGenres, genre) > -1) {
                    checked[i++] = true;
                } else {
                    checked[i++] = false;
                }
            }
        }
        final List<String> selectedGenres = Lists.newArrayList(filterGenres);
        builder.setTitle(R.string.artists_genres_selection_title)
                .setMultiChoiceItems(genres,
                        checked,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                String genre = genres[which];
                                if (isChecked) {
                                    selectedGenres.add(genre);
                                } else {
                                    selectedGenres.remove(genre);
                                }
                            }
                        }
                ).setPositiveButton(R.string.filter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Filter genres " + selectedGenres);
                        String[] filterGenres = selectedGenres.toArray(new String[selectedGenres.size()]);
                        setFilteredGenres(filterGenres);
                        mActionsListener.loadArtistsByGenres(filterGenres);
                        dialog.dismiss();
                        if (null != getView() && filterGenres.length > 0) {
                            Snackbar.make(getView(), getString(R.string.filtered_by_genres,
                                    Joiner.on(Constants.GENRES_SEPARATOR).join(filterGenres)),
                                    Snackbar.LENGTH_LONG)
                                    .setAction(R.string.reset, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mActionsListener.resetFilter();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
        builder.show();
    }

    @Override
    public void showSelectOrderDialog() {
        // Prepare sort order dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get currently selected sort order
        String currentOrder = Utility.getPreferredOrder(getActivity());
        int checkedItem = TRACKS;
        if (currentOrder.equals(getString(R.string.pref_order_by_albums))) {
            checkedItem = ALBUMS;
        }
        builder.setTitle(R.string.artists_order_selection_title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(R.array.order_by, checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case TRACKS:
                                        Utility.setPreferredOrder(getActivity(),
                                                getString(R.string.pref_order_by_tracks));
                                        break;
                                    case ALBUMS:
                                        Utility.setPreferredOrder(getActivity(),
                                                getString(R.string.pref_order_by_albums));
                                        break;
                                }
                                dialog.dismiss();
                            }
                        });
        AlertDialog dialog = builder.create();
        // Display the dialog
        dialog.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_load_state))) {
            updateEmptyView();
        } else if (key.equals(getString(R.string.pref_key_order_value))) {
            // Reload artists with new order...
            mActionsListener.loadArtists(false);
        }
    }

    @Override
    public void updateEmptyView() {
        @LoadingStatus int loadingStatus = Utility.getLoadingStatus(getActivity());
        switch (loadingStatus) {
            case LoadingStatus.LOADING:
                mEmptyView.setText(R.string.loading);
                break;
            case LoadingStatus.NOT_FOUND:
                mEmptyView.setText(R.string.not_found);
                break;
            case LoadingStatus.NETWORK_ERROR:
                mEmptyView.setText(R.string.network_error);
                break;
            case LoadingStatus.ERROR:
                mEmptyView.setText(R.string.unknown_error);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loading state " + loadingStatus);
        }
    }

    @Override
    public String[] getFilteredGenres() {
        return Utility.getFilterGenres(getActivity());
    }

    private ArtistsActivity getActivityCast() {
        return (ArtistsActivity) getActivity();
    }

    @Override
    public void showFilteredIcon() {
        getActivityCast().getArtistFilter().setVisibility(View.VISIBLE);
        getActivityCast().getArtistFilter().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.resetFilter();
            }
        });
    }

    @Override
    public void setFilteredGenres(String[] genres) {
        Utility.setFilterGenres(getActivity(), genres);
    }

    @Override
    public void hideFilteredIcon() {
        getActivityCast().getArtistFilter().setVisibility(View.GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assign singleton instances to repository field annotated with Inject.
        ((ArtistsApplication) getActivity().getApplication()).getRepositoryComponent().inject(this);
        mArtistsAdapter = new ArtistsAdapter(new ArrayList<Artist>(0), mItemListener);
    }

    // Open artist details on click.
    ArtistItemListener mItemListener = new ArtistItemListener() {
        @Override
        public void onArtistClick(Artist clickedArtist, View coverView) {
            resetSearchView();
            mActionsListener.openArtistDetails(clickedArtist, coverView);
        }
    };

    /**
     * Helper methods that resets state of search view and it's menu item.
     */
    private void resetSearchView() {
        if (null != mSearchView) {
            mSearchView.setQuery(null, true);
        }
        if (null != mSearchItem) {
            MenuItemCompat.collapseActionView(mSearchItem);
        }
    }

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
        mRecyclerView.addItemDecoration(new MarginDecoration(getActivity()));

        mArtistsAdapter.setEmptyView(mEmptyView);
        mArtistsAdapter.setContext(getActivity());

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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
        // Reset adapter's context.
        mArtistsAdapter.setContext(getActivity());
        mActionsListener.loadArtists(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
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

        public ArtistsAdapter(List<Artist> artists, ArtistItemListener itemListener) {
            setList(artists);
            mItemListener = itemListener;
        }

        public void setContext(Context context) {
            mContext = context;
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
     * Artists grid decoration.
     */
    public static class MarginDecoration extends RecyclerView.ItemDecoration {
        private int margin;

        public MarginDecoration(Context context) {
            margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(margin, margin, margin, margin);
        }
    }

    /**
     * Listener for artist item clicks.
     */
    interface ArtistItemListener {
        void onArtistClick(Artist clickedArtist, View coverView);
    }
}
