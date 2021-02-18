package com.ddastudio.hifivefootball_android.match_arena;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.ViewType;
import com.ddastudio.hifivefootball_android.data.model.arena_chat.ArenaImageModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.ui.widget.BadgedImageView;

import java.util.List;

/**
 * Created by hongmac on 2018. 3. 25..
 */

public class ArenaImageRvAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private final @ColorInt int initialGifBadgeColor;
    public boolean hasFadedIn = false;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ArenaImageRvAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(ViewType.ARENA_IMAGE, R.layout.row_arena_image_item);

        initialGifBadgeColor = 0x40ffffff;

    }

    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        super.onViewRecycled(holder);

        if ( holder.getItemViewType() == ViewType.ARENA_IMAGE) {
            BadgedImageView iv2 = (BadgedImageView)holder.getView(R.id.iv_preview2);
            iv2.setBadgeColor(initialGifBadgeColor);
            iv2.setForeground(ContextCompat.getDrawable(mContext, R.drawable.mid_grey_ripple));
            iv2.setDrawBadge(false);
        }

    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder = super.onCreateViewHolder(parent, viewType);

        if ( viewType == ViewType.ARENA_IMAGE ) {
            final BadgedImageView iv2 = (BadgedImageView)holder.getView(R.id.iv_preview2);

            // play animated GIFs whilst touched
            iv2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    // check if it's an event we care about, else bail fast
                    final int action = event.getAction();
                    if (!(action == MotionEvent.ACTION_DOWN
                            || action == MotionEvent.ACTION_UP
                            || action == MotionEvent.ACTION_CANCEL)) return false;

                    // get the image and check if it's an animated GIF
                    final Drawable drawable = iv2.getDrawable();
                    if (drawable == null) return false;

                    GifDrawable gif = null;
                    if (drawable instanceof GifDrawable) {
                        gif = (GifDrawable) drawable;
                    } else if (drawable instanceof TransitionDrawable) {
                        // we fade in images on load which uses a TransitionDrawable; check its layers
                        TransitionDrawable fadingIn = (TransitionDrawable) drawable;
                        for (int i = 0; i < fadingIn.getNumberOfLayers(); i++) {
                            if (fadingIn.getDrawable(i) instanceof GifDrawable) {
                                gif = (GifDrawable) fadingIn.getDrawable(i);
                                break;
                            }
                        }
                    }

                    if (gif == null) return false;
                    // GIF found, start/stop it on press/lift
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            gif.start();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            gif.stop();
                            break;
                    }

                    return false;
                }
            });
        }

        return holder;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        helper.addOnClickListener(R.id.iv_preview2);

        ArenaImageModel aim = (ArenaImageModel)item;
        BadgedImageView iv2 = (BadgedImageView)helper.getView(R.id.iv_preview2);

        if ( aim.getUrl().contains(".gif")) {
            GlideApp.with(mContext)
                    .load(aim.getUrl())
                    .into(iv2);

//            iv2.setDrawBadge(true);
//            Glide.with(mContext)
//                .load(aim.getUrl())
//                .listener(new RequestListener<String, GlideDrawable>() {
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource,
//                                                   String model,
//                                                   Target<GlideDrawable> target,
//                                                   boolean isFromMemoryCache,
//                                                   boolean isFirstResource) {
//                        if (!hasFadedIn) {
//                            iv2.setHasTransientState(true);
//                            final ObservableColorMatrix cm = new ObservableColorMatrix();
//                            final ObjectAnimator saturation = ObjectAnimator.ofFloat(
//                                    cm, ObservableColorMatrix.SATURATION, 0f, 1f);
//                            saturation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                @Override
//                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                    // just animating the color matrix does not invalidate the
//                                    // drawable so need this update listener.  Also have to create a
//                                    // new CMCF as the matrix is immutable :(
//                                    iv2.setColorFilter(new ColorMatrixColorFilter(cm));
//                                }
//                            });
//                            saturation.setDuration(2000L);
//                            saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(mContext));
//                            saturation.addListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    iv2.clearColorFilter();
//                                    iv2.setHasTransientState(false);
//                                }
//                            });
//                            saturation.start();
//                            hasFadedIn = true;
//                        }
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable>
//                            target, boolean isFirstResource) {
//                        return false;
//                    }
//                })
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .fitCenter()
//                .into(new DribbbleTarget(iv2, false))
//                //.into(iv2)
//            ;
        } else {
//            Glide.with(mContext)
//                    .load(aim.getUrl())
//                    //.asBitmap()
//                    //.centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    //.placeholder(R.drawable.ic_face)
//                    .into((ImageView)helper.getView(R.id.iv_preview));

            GlideApp.with(mContext)
                    .load(aim.getUrl())
                    //.asBitmap()
                    //.centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.placeholder(R.drawable.ic_face)
                    .into(iv2);
        }

    }
}
