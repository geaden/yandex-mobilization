package com.geaden.android.mobilization.app.data;

/**
 * POJO representing Cover model.
 *
 * @author Gennady Denisov
 */
public final class Cover {
    // Link to small cover
    private String mSmall;

    // Ling to large cover
    private String mBig;

    public Cover(String small, String big) {
        this.mSmall = small;
        this.mBig = big;
    }

    public String getSmall() {
        return mSmall;
    }

    public String getBig() {
        return mBig;
    }
}
