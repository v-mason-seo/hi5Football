package com.ddastudio.hifivefootball_android.data.model.community;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;

import static j2html.TagCreator.p;

/**
 * Created by hongmac on 2018. 3. 16..
 */

public class EditorVideoModel extends MultipleItem {

    //int itemType;
    String url;
    String html;

    public EditorVideoModel(String url) {
        itemType = MultipleItem.EDITOR_VIDEO;
        this.url = url;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public String getUrl() {
        return url;
    }

    public String getHtml(Context context, boolean isAutoPlay) {

        StringBuilder sb = new StringBuilder();

        if ( !TextUtils.isEmpty(url) ) {

            sb.append(context.getResources().getString(R.string.video_html, isAutoPlay ? "muted" : "", url));
            sb.append(p().render());
        }

        return sb.toString();
    }
}
