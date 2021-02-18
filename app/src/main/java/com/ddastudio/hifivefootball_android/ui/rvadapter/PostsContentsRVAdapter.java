package com.ddastudio.hifivefootball_android.ui.rvadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.UserModel;
import com.ddastudio.hifivefootball_android.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by hongmac on 2017. 9. 15..
 */

public class PostsContentsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<UserModel> mItems;
    LayoutInflater mInflater;

    private static OnItemClickListener listener; // Define listener member variable

    public interface OnItemClickListener {
        // Define the listener interface
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PostsContentsRVAdapter(Context context) {

        this.mItems = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        RecyclerView.ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        v = inflater.inflate(R.layout.row_user_item, parent, false);
        holder = new UserViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((UserViewHolder)holder).bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(List<UserModel> users) {
        mItems = users;
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.avatar) ImageView ivAvatar;
        @BindView(R.id.user_name) TextView tvUserName;
        Context mContext;

        public UserViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();

            tvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(mContext, "Position : " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
                }
            });

            //
            // Setup the click listener
            //
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Triggers click upwards to the adapter on click
//                    if (listener != null)
//                        listener.onItemClick(v, getLayoutPosition());
//                }
//            });
        }

        public void bind(UserModel item) {
            tvUserName.setText(item.getUsername());

            // *** 사용자 프로필 ***
//            Glide.with(mContext)
//                    .load(item.getAvatarUrl())
//                    .asBitmap()
//                    .centerCrop()
//                    .transform(new CropCircleTransformation(mContext))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(CommonUtils.getDrawable(mContext, R.drawable.ic_face))
//                    .into(ivAvatar);
        }
    }
}
