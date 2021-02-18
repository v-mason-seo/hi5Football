package com.ddastudio.hifivefootball_android.ui.rvadapter;

import android.view.View;
import android.widget.RatingBar;

/**
 * Created by hongmac on 2018. 2. 19..
 */

public interface RecyclerViewClickListener {

    void onRowClicked(int position);

    void onViewClicked(View v, int position);
    void onViewRatingChanged(RatingBar ratingBar, float rating, boolean fromUser);

}
