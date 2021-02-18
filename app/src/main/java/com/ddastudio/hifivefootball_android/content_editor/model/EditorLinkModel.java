package com.ddastudio.hifivefootball_android.content_editor.model;

import android.content.Context;
import android.text.TextUtils;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.data.model.MultipleItem;
import com.ddastudio.hifivefootball_android.data.model.OpenGraphModel;

import j2html.attributes.Attr;

import static j2html.TagCreator.a;
import static j2html.TagCreator.br;
import static j2html.TagCreator.div;
import static j2html.TagCreator.p;


/**
 * Created by hongmac on 2017. 9. 26..
 */

public class EditorLinkModel extends MultipleItem {

    String link;
    OpenGraphModel og;

    public EditorLinkModel() {
        super(MultipleItem.EDITOR_LINK);
    }

    public String getLink() {
        return link;
    }

    public OpenGraphModel getOg() {
        return og;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setOg(OpenGraphModel og) {
        this.og = og;
    }

    public String getHtml(Context context) {

        if ( og != null ) {
            return getHtml2(context);
        }

        StringBuilder sb = new StringBuilder();
        String description;

        if ( og != null && TextUtils.isEmpty(og.getTitle()) == false ) {
            description = og.getTitle();
        } else {
            description = link;
        }

        if ( !TextUtils.isEmpty(link) ) {
            sb.append(div(Attr.shortFormFromAttrsString(".link")).with(a().withHref(link).withText(description)).render());
            sb.append(p().render());
        }

        return sb.toString();
    }

    /**
     * 카드형태 html
     * @param context
     * @return
     */
    public String getHtml2(Context context) {

        String htmlCode;
        StringBuilder sb = new StringBuilder();

        htmlCode = context.getString(R.string.card_html,
                getLink(),
                og.getImage(),
                og.getTitle(),
                og.getCannonicalUrl()
                //og.getDescription(100)
        );

        sb.append(htmlCode);
        sb.append(p().render());

        return sb.toString();
    }
}
