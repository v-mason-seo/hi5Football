package com.ddastudio.hifivefootball_android.content_editor.model;

import android.text.TextUtils;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;

import j2html.attributes.Attr;

import static j2html.TagCreator.br;
import static j2html.TagCreator.div;
import static j2html.TagCreator.figcaption;
import static j2html.TagCreator.img;
import static j2html.TagCreator.p;
import static j2html.TagCreator.u;

/**
 * Created by hongmac on 2017. 9. 26..
 */

public class EditorImageModel extends MultipleItem {

    int type;
    String url;
    String caption;
    boolean isSelected = false;

    public EditorImageModel() {
        super(MultipleItem.EDITOR_IMAGE);
        isSelected = false;
    }

    public EditorImageModel(String url) {
        super(MultipleItem.EDITOR_IMAGE);
        this.isSelected = false;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getCaption() {
        return caption;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getHtml() {

        StringBuilder sb = new StringBuilder();

        if ( !TextUtils.isEmpty(url) ) {

            sb.append(div(Attr.shortFormFromAttrsString(".img")).with(img().withSrc(url)).render());
            if ( !TextUtils.isEmpty(caption) ) {
                figcaption().withSrc(caption);
            }
            sb.append(p().render());
        }

        return sb.toString();
    }
}
