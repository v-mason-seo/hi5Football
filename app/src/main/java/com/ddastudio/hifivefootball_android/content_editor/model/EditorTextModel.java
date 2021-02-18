package com.ddastudio.hifivefootball_android.content_editor.model;

import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;

import com.ddastudio.hifivefootball_android.data.model.MultipleItem;

import j2html.attributes.Attr;

import static j2html.TagCreator.body;
import static j2html.TagCreator.br;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.p;

/**
 * Created by hongmac on 2017. 9. 26..
 */

public class EditorTextModel extends MultipleItem {

    String mainText;

    public EditorTextModel() {
        super(MultipleItem.EDITOR_TEXT);
    }

    public EditorTextModel(String text) {
        super(MultipleItem.EDITOR_TEXT);
        this.mainText = text;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getHtml() {

        StringBuilder sb = new StringBuilder();

        if (!TextUtils.isEmpty(mainText)) {


            sb.append(div(Attr.shortFormFromAttrsString(".text")).withText(mainText).render().replace("\n", "<br>"));
            sb.append(p().render());
        }

        return sb.toString();
    }

    @Override
    public int getItemType() {
        return super.getItemType();
    }
}
