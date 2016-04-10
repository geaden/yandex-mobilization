package com.geaden.android.mobilization.app.artistdetail;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.geaden.android.mobilization.app.R;

/**
 * Detail activity to display artist details.
 *
 * @author Gennady Denisov
 */
public class ArtistDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ARTIST_ID = "artist_id";

    /**
     * Helper method to the activity.
     *
     * @param activity  the activity to be launched.
     * @param artistId  requested artist id.
     * @param coverView artist's cover image.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void launch(Activity activity, long artistId, View coverView) {
        Intent intent = getLaunchIntent(activity, artistId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, coverView, coverView.getTransitionName());
            ActivityCompat.startActivity(activity, intent, options.toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    /**
     * Helper method to get launching intent.
     *
     * @param context  the Context to instantiate Intent with.
     * @param artistId request artist id to open activity for.
     * @return intent to be launched.
     */
    public static Intent getLaunchIntent(Context context, long artistId) {
        Intent intent = new Intent(context, ArtistDetailActivity.class);
        intent.putExtra(EXTRA_ARTIST_ID, artistId);
        return intent;
    }

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
