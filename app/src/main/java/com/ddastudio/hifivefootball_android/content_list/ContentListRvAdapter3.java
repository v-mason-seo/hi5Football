package com.ddastudio.hifivefootball_android.content_list;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.content_list.widget.BadgedImageView;
import com.ddastudio.hifivefootball_android.data.model.StreamViewerModel;
import com.ddastudio.hifivefootball_android.glide.GlideApp;
import com.ddastudio.hifivefootball_android.utils.FileUtil;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.helper.ToroPlayerHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;


public class ContentListRvAdapter3 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static int PHOTO = 0;
    public final static int VIDEO = 1;

    Context mContext;
    List<ContentHeaderModel> mItems;

    public ContentListRvAdapter3(Context context) {
        this.mContext = context;
        this.mItems = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {

        ContentHeaderModel item = mItems.get(position);

        if ( item != null
                && item.getImgs() != null
                && item.getImgs().size() > 0
                && item.getImgs().get(0).getStreamType() == StreamViewerModel.MP4) {
            return VIDEO;
        } else {
            return PHOTO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case VIDEO:
                return new VideoContentViewHolder(inflater.inflate(R.layout.row_content_video_card_item, parent, false));
            case PHOTO:
                return new ContentViewHolder(inflater.inflate(R.layout.row_content_card_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ContentHeaderModel item = (ContentHeaderModel)mItems.get(position);


        switch (holder.getItemViewType()) {
            case VIDEO:
                VideoContentViewHolder videoHolder = (VideoContentViewHolder)holder;
                videoHolder.bindData(item);

                if ( item != null && item.getUser() != null && !TextUtils.isEmpty(item.getUser().getAvatarUrl())) {
                    GlideApp.with(mContext)
                            .load(item.getUser().getAvatarUrl())
                            .transform(new CircleCrop())
                            .centerCrop()
                            .placeholder(R.drawable.ic_person_grey_vector)
                            .into(videoHolder.ivAvatar);
                }

                if ( item != null && item.getImgs() != null && item.getImgs().size() > 0) {
                    videoHolder.ivImage.setVisibility(View.VISIBLE);
                    GlideApp.with(mContext)
                            .load(item.getImgs().get(0).getStreamUrl())
                            .centerCrop()
                            .placeholder(R.drawable.ic_person_grey_vector)
                            .into(videoHolder.ivImage);
                } else {
                    videoHolder.ivImage.setVisibility(View.GONE);
                }
                break;

            case PHOTO:
                ContentViewHolder contentHolder = (ContentViewHolder)holder;
                contentHolder.bindData(item);

                if ( item != null && item.getUser() != null && !TextUtils.isEmpty(item.getUser().getAvatarUrl())) {
                    GlideApp.with(mContext)
                            .load(item.getUser().getAvatarUrl())
                            .transform(new CircleCrop())
                            .centerCrop()
                            .placeholder(R.drawable.ic_person_grey_vector)
                            .into(contentHolder.ivAvatar);
                }

                if ( item != null && item.getImgs() != null && item.getImgs().size() > 0) {
                    contentHolder.ivImage.setVisibility(View.VISIBLE);
                    GlideApp.with(mContext)
                            .load(item.getImgs().get(0).getStreamUrl())
                            .centerCrop()
                            .placeholder(R.drawable.ic_person_grey_vector)
                            .into(contentHolder.ivImage);
                } else {
                    contentHolder.ivImage.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void onNewData(List<ContentHeaderModel> items) {
        final ContentListDiffUtil diffCallback = new ContentListDiffUtil(mItems, items);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.mItems.clear();
        this.mItems.addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addItem(ContentHeaderModel item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void addItems(List<ContentHeaderModel> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public ContentHeaderModel getItem(int position) {
        return mItems.get(position);
    }


    public void removeItem(int position) {

        if ( mItems != null || position < 0
                || position >= mItems.size()) return;

        mItems.remove(position);
        this.notifyItemRemoved(position);
    }

    /*-----------------------------------------------------------------------*/


    // Define listener member variable
    private OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
        void onAvatarClick(View itemView, int position);
        void onCommentClick(View itemView, int position);
        void onScrapClick(View itemView, int position);
        void onHifiveClick(View itemView, int position);
        void onMoreClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    /*-----------------------------------------------------------------------*/



    public class ContentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_avatar) ImageView ivAvatar;
        @BindView(R.id.tv_nickname) TextView tvNickname;
        @BindView(R.id.tv_updated) TextView tvUpdated;
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_preview) TextView tvPreview;
        @BindView(R.id.iv_image) BadgedImageView ivImage;
        @BindView(R.id.iv_comment) ImageView ivComment;
        @BindView(R.id.tv_comment_count) TextView tvCommentCount;
        @BindView(R.id.iv_hifive) ImageView ivHifive;
        @BindView(R.id.tv_hifive_count) TextView tvHifiveCount;
        @BindView(R.id.iv_scraped) ImageView ivScrap;
        @BindView(R.id.iv_more_item) ImageView ivMore;

        View.OnClickListener clickListener  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        switch (v.getId()) {
                            case R.id.iv_avatar:
                                listener.onAvatarClick(v, position);
                                break;
                            case R.id.iv_comment:
                                listener.onCommentClick(v, position);
                                break;
                            case R.id.iv_scraped:
                                listener.onScrapClick(v, position);
                                break;
                            case R.id.iv_hifive:
                                listener.onHifiveClick(v, position);
                                break;
                            case R.id.iv_more_item:
                                listener.onMoreClick(v, position);
                                break;
                            default:
                                listener.onItemClick(v, position);
                                break;
                        }
                    }
                }
            }
        };

        public ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(clickListener);
            ivAvatar.setOnClickListener(clickListener);
            ivHifive.setOnClickListener(clickListener);
            ivComment.setOnClickListener(clickListener);
            ivScrap.setOnClickListener(clickListener);
            ivMore.setOnClickListener(clickListener);


        }

        public void bindData(ContentHeaderModel item) {

            if ( item == null )
                return;

            tvTitle.setText(item.getTitle());
            tvNickname.setText(item.getNickNameAndUserName());
            tvUpdated.setText(item.getUpdatedString());
            tvCommentCount.setText(item.getCommentString());
            tvHifiveCount.setText(item.getLikerString());

            if ( TextUtils.isEmpty(item.getPreview())) {
                tvPreview.setVisibility(View.GONE);
            } else {
                tvPreview.setVisibility(View.VISIBLE);
                tvPreview.setText(item.getPreview());
            }

            if ( item.getImgs() != null && item.getImgs().size() > 0) {

                String extension = FileUtil.getExtensionFromUrl(item.getImgs().get(0).getStreamUrl());

                // MP4, JPG 이미지 타입 뱃지 달기
                if ( !TextUtils.isEmpty(extension)) {
                    ivImage.setBadge(extension.toUpperCase());
                }
            }
        }
    }


    /*-----------------------------------------------------------------------*/



    public class VideoContentViewHolder extends ContentViewHolder implements ToroPlayer {

        @BindView(R.id.player) PlayerView playerView;

        Uri mUri;
        ToroPlayerHelper helper;

        public VideoContentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(ContentHeaderModel item) {
            super.bindData(item);

            if ( item == null )
                return;

            this.mUri = Uri.parse(item.getImgs().get(0).getStreamUrl());
        }

        /*-------------------------------------------------------------------*/

        private ToroPlayer.EventListener toroPlayerEventListener = new EventListener() {
            @Override
            public void onBuffering() {
                // do something
            }

            @Override
            public void onPlaying() {
                // do something
                ivImage.setVisibility(View.GONE);
            }

            @Override
            public void onPaused() {
                // do something
            }

            @Override
            public void onCompleted() {
                // do something
            }
        };

        /*-------------------------------------------------------------------*/

        @NonNull
        @Override
        public View getPlayerView() {
            return playerView;
        }

        @NonNull
        @Override
        public PlaybackInfo getCurrentPlaybackInfo() {
            return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
        }

        @Override
        public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
            if (helper == null) {
                helper = new ExoPlayerViewHelper(this, mUri);
                helper.addPlayerEventListener(toroPlayerEventListener);
            }
            helper.initialize(container, playbackInfo);
        }

        @Override
        public void play() {
            if (helper != null) {
                helper.play();
                ivImage.setVisibility(View.GONE);
            }
        }

        @Override
        public void pause() {
            if (helper != null) helper.pause();
        }

        @Override
        public boolean isPlaying() {
            return helper != null && helper.isPlaying();
        }

        @Override
        public void release() {
            if (helper != null) {
                helper.removePlayerEventListener(toroPlayerEventListener);
                helper.release();
                helper = null;
            }
        }

        @Override
        public boolean wantsToPlay() {
            return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
        }

        @Override
        public int getPlayerOrder() {
            return getAdapterPosition();
        }
    }

}
