package com.ddastudio.hifivefootball_android.content_editor;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.ui.base.BaseFragment;
import com.ddastudio.hifivefootball_android.content_editor.utils.HifiveTextEditor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextEditorFragment extends BaseFragment {

    @BindView(R.id.et_body)
    HifiveTextEditor etBody;

    public TextEditorFragment() {
        // Required empty public constructor
    }

    public static TextEditorFragment newInstance() {

        TextEditorFragment fragment = new TextEditorFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text_editor, container, false);
        _unbinder = ButterKnife.bind(this, view);

        return view;
    }

    /*---------------------------------------------------------------------------------------------*/

    @Override
    protected void onEvent(Object event) {
        super.onEvent(event);
    }

    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
    }

    @Override
    public void showMessage(String message) {
        super.showMessage(message);
    }

    @Override
    public void showErrorMessage(String errMessage) {
        super.showErrorMessage(errMessage);
    }


    /*---------------------------------------------------------------------------------------------*/

    @OnClick(R.id.btn_add_html)
    public void onClickAddHtml() {

        final String BOLD = "<b>Bold</b><br><br>";
        final String ITALIT = "<i>Italic</i><br><br>";
        final String UNDERLINE = "<u>Underline</u><br><br>";
        final String STRIKETHROUGH = "<s>Strikethrough</s><br><br>"; // <s> or <strike> or <del>
        final String BULLET = "<ul><li>asdfg</li></ul>";
        final String QUOTE = "<blockquote>Quote</blockquote>";
        final String LINK = "<a href=\"https://github.com/mthli/Knife\">Link</a><br><br>";
        final String ddd = "<div class=img><img src=http://cdn.hifivefootball.com/origin/article/20180330_054212_206_manu.jpg></div><br><br><div class=text>아이돌......</div><br><br>";
        final String eee = "<div class=img><img src=http://cdn.hifivefootball.com/origin/article/20180421_074608_647_jeffchung7.heic></div><br><br><div class=img><img src=http://cdn.hifivefootball.com/origin/article/20180421_074609_044_jeffchung7.heic></div><br><br><div class=text>전 날두의 나라! 포르투갈을 여행중입니다.</div><br><br><div class=text>남미,아프리카를 여행하고 오니 상대적으로 재미가 별로 없네요. 예전 스페인 여행과 브라질,아르헨티나에사 많이 보던 건축양식이라 중복되는 감이 많습니다</div><br><br><div class=text>그래도 몇 안되는 즐거움은 요 고향만두를 사먹는일...</div><br><br><div class=text>베트남 쌀국수 집에서 3.5유로에 팔더군요.\n" +
                "<br>여기 리스본 중국마트에서 고추장부터 미역, 불닭복음면,등 한식재료를 다 파네요</div><br><br><div class=text>내일부터는 만들어 먹는 걸로...</div><br><br><div class=text>하이파이브는 개발이 장기전화 된 듯한데..\n" +
                "<br>화이팅입니당</div><br><br>";
        final String EXAMPLE = BOLD + ITALIT + UNDERLINE + STRIKETHROUGH + BULLET + QUOTE + LINK + eee;

//        SpannableStringBuilder builder = new SpannableStringBuilder();
//        builder.append(Html.fromHtml(EXAMPLE));
//        etBody.setText(builder);
        etBody.fromHtml(EXAMPLE);
    }

    @OnClick(R.id.btn_add_image)
    public void onClickAddImage() {

        // Initialize a new String variable
        String text = "Test text to show ImageSpan in a TextView. This is a search  image/icon and" +
                " this is a play  icon";

        // Initialize a new SpannableStringBuilder instance
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);

        // Initialize a new ImageSpan to display search image/icon
        ImageSpan searchImageSpan = new ImageSpan(getContext(),R.drawable.background_1);

        // Initialize a new ImageSpan to display play image/icon
        ImageSpan playImageSpan = new ImageSpan(getContext(),R.drawable.background_3);

        // Apply the search image to the span
        ssBuilder.setSpan(
                searchImageSpan, // Span to add
                text.indexOf("search") + String.valueOf("search").length(), // Start of the span (inclusive)
                text.indexOf("search") + String.valueOf("search").length()+1, // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        );

        // Display the play image to the span
        ssBuilder.setSpan(
                playImageSpan,
                text.indexOf("play") + String.valueOf("play").length(),
                text.indexOf("play") + String.valueOf("play").length()+1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Display the spannable text to TextView
        etBody.setText(ssBuilder);
    }

    @OnClick(R.id.btn_print_html)
    public void onClickPrintHtml(View view) {
        String html = etBody.toHtml();
        Timber.i("html\n%s", html);
    }



    @OnClick(R.id.btn_bold)
    public void onClickBold(View view) {
        etBody.bold(true);
    }

    @OnClick(R.id.btn_link)
    public void onClickLink(View view) {
        String linkUrl = "http://post.naver.com/viewer/postView.nhn?volumeNo=14996249&memberNo=34218501&mainMenu=CARGAME";
        int start = etBody.getSelectionStart();
        int end = etBody.getSelectionEnd();
        etBody.link(linkUrl, start, end);
    }

    @OnClick(R.id.btn_Quote)
    public void onClickQuote(View view) {
        etBody.quote(true);
    }

    /*---------------------------------------------------------------------------------------------*/



    @OnClick(R.id.btn_add_image2)
    public void onClickAddImage2(View view) {

    }

}
