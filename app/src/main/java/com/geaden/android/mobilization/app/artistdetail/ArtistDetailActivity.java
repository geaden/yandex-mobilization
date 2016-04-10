package com.geaden.android.mobilization.app.artistdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.geaden.android.mobilization.app.R;

/**
 * Detail activity to display artist details.
 *
 * @author Gennady Denisov
 */
public class ArtistDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ARTIST_ID = "artist_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get the requested artist id
        Long artistId = getIntent().getLongExtra(EXTRA_ARTIST_ID, -1L);

        initFragment(ArtistDetailFragment.newInstance(artistId));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Initializes detail fragment by adding it to layout.
     *
     * @param detailFragment the fragment to be added.
     */
    private void initFragment(ArtistDetailFragment detailFragment) {
        // Add the NotesDetailFragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, detailFragment);
        transaction.commit();
    }
}
