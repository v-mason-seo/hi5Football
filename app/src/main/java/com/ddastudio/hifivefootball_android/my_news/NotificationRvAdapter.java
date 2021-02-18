package com.ddastudio.hifivefootball_android.my_news;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.community.NotificationModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.my_news.MyNewsFragment;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 10. 18..
 */

public class NotificationRvAdapter extends BaseQuickAdapter<NotificationModel, BaseViewHolder> {

    boolean unreadNews;

    public NotificationRvAdapter(@LayoutRes int layoutResId, @Nullable List<NotificationModel> data) {
        super(layoutResId, data);
    }

    public NotificationRvAdapter(@Nullable List<NotificationModel> data) {
        super(data);
    }

    public NotificationRvAdapter(@LayoutRes int layoutResId, int selectionType) {
        super(layoutResId);
        if ( selectionType == MyNewsFragment.NOTIFICATION_TYPE_UNREAD ) {
            unreadNews = true;
        } else {
            unreadNews = false;
        }
    }

    public boolean isUnreadNews() {
        return unreadNews;
    }

    public void setUnreadNews(boolean unreadNews) {
        this.unreadNews = unreadNews;
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void convert(BaseViewHolder helper, NotificationModel item) {

        helper.addOnClickListener(R.id.notification_container)
                .addOnClickListener(R.id.notification_confirm);

        helper.setImageResource(R.id.notification_type_icon, getNotificationTypeIcon(item.getNotificationTypeId()))
                .setText(R.id.notification_title, item.getTitle())
                .setGone(R.id.notification_confirm, isUnreadNews())
                .setTextColor(R.id.notification_title, item.getConfirm() == 0
                        ? CommonUtils.getColor(mContext, R.color.md_grey_800)
                        : CommonUtils.getColor(mContext, R.color.md_grey_400))
                ;

        // --- 사용자 프로필 ---
        GlideApp.with(mContext)
                .load(item.getAvatarUrl())
                .centerCrop()
                .transform(new CropCircleTransformation())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.ic_face)
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_face))
                .into((ImageView)helper.getView(R.id.notification_sender_avatar));
    }

    private int getNotificationTypeIcon(int notificationType) {

        String notificationTypeName = App.getHifiveSettingsManager().getNotificationName(notificationType);

        switch (notificationTypeName) {
            case "comment":
                //댓글
                return R.drawable.ic_comment;
            case "reply":
                //대댓글
                return R.drawable.ic_reply;
            case "award":
                //어워드
                return R.drawable.ic_football_award_vector;
            case "badge":
                //뱃지
                return R.drawable.ic_circle_grey_vector;
            case "call":
                //뱃지
                return R.drawable.ic_call_vector;
            case "arena":
                //아레나
                return R.drawable.ic_chat2_vector;
            case "match":
                //매치
                return R.drawable.ic_football_field_vector;
            case "team":
                //침
                return R.drawable.ic_edit;
            case "player":
                //플레이어
                return R.drawable.ic_player_t_shirt_vector;
            case "hifive":
                //플레이어
                return R.drawable.ic_clap_color_vector2;
            case "scrap":
                //플레이어
                return R.drawable.ic_bookmark;
            default:
                return R.drawable.ic_notifications;
        }
    }

}
