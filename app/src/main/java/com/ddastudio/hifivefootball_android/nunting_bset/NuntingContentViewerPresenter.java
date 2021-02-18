package com.ddastudio.hifivefootball_android.nunting_bset;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.ddastudio.hifivefootball_android.data.manager.NuntingManager;
import com.ddastudio.hifivefootball_android.nunting_bset.model.PostsListModel;
import com.ddastudio.hifivefootball_android.nunting_bset.model.SiteBoardModel;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;

import io.reactivex.disposables.CompositeDisposable;

public class NuntingContentViewerPresenter implements BaseContract.Presenter {

    SiteBoardModel.Boards board;
    SiteBoardModel.Name site;
    PostsListModel posts;

    String mCurrentUrl;
    NuntingManager dataManager;
    NuntingContentViewerActivity mView;

    @NonNull
    CompositeDisposable mComposite;

    public NuntingContentViewerPresenter() {

    }

    @Override
    public void attachView(BaseContract.View view) {
        this.mView = (NuntingContentViewerActivity)view;
        dataManager = NuntingManager.getInstance();
        this.mComposite = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if ( mComposite != null )
            mComposite.clear();
    }

    public SiteBoardModel.Boards getBoard() {
        return board;
    }

    public void setBoard(SiteBoardModel.Boards board) {
        this.board = board;
    }

    public SiteBoardModel.Name getSite() {
        return site;
    }

    public void setSite(SiteBoardModel.Name site) {
        this.site = site;
    }

    public String getCurrentUrl() {
        return mCurrentUrl;
    }

    public void setCurrentUrl(String CurrentUrl) {
        this.mCurrentUrl = CurrentUrl;
    }

    public PostsListModel getPosts() {
        return posts;
    }

    public void setPosts(PostsListModel posts) {
        this.posts = posts;
    }

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 광고 로드
     * @param adView
     */
//    public void initializeAdView(AdView adView) {
//
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("1e08f2e27301f8ed")
//                .addTestDevice("7726e5821e49d725")
//                .addTestDevice("C688FB9C0634F2E6FF40ADFF71711FFF")
//                .addTestDevice("E4B200C7136AD0708A2BF1FA76A2E94B").addTestDevice("70D53A4C46EFEAAA47D76A37CA7499DA")
//                .build();
//
//        adView.loadAd(adRequest);
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//                //Log.i("html", "onAdClosed ");
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                super.onAdFailedToLoad(errorCode);
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                super.onAdLeftApplication();
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//            }
//        });
//
//    }

    /**
     * 본문 내용을 읽어온다.
     * @param url
     */
//    public void getPostsContents(String url) {
////
////        Flowable<PostsContentsModel> observerble = dataManager.getPostsContents(url);
////
////        mComposite.add(
////                observerble.observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
////                        .subscribe(item -> {
////                                    mView.setHeader(item.getTitle(),
////                                            item.getUser(),
////                                            item.getReg());
////                                    mView.bindData(item.getHtmlText());
////                                },
////                                e -> mView.showErrorMessage(e.getMessage()))
////        );
////    }


    /**
     * 댓글 정보를 읽어온다.
     * @param url
     */
//    public void getContentsReplay(String url) {
//
//        Flowable<List<ContentsReplayModel>> observable= dataManager.getContentsReplay(url);
//
//        mComposite.add(
//                observable.observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
//                        .subscribe(item -> mView.setReplayItems(item),
//                                e -> mView.showErrorMessage(e.getMessage()))
//        );
//    }


    /**
     * 게시글에 유투브가 있는지 확인
     * @param url
     */
    public void checkYouTube(final String url) {

//        Observable.create(new Observable.OnSubscribe<Object>() {
//            @Override
//            public void call(Subscriber<? super Object> subscriber) {
//
//                try {
//                    Document doc = Jsoup.parse(new URL(url).openStream(), "utf-8", url);
//                    String htmlCode = doc.html();
//                    if ( htmlCode.contains("youtube") == true ) {
//                        subscriber.onNext(true);
//                    } else {
//                        subscriber.onNext(false);
//                    }
//                    subscriber.onCompleted();
//                } catch (Exception ex) {
//
//                    Log.i("hong", "ex : " + ex.toString());
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Object>() {
//                    @Override
//                    public void call(Object o) {
//
//                        if ( (boolean)o == true) {
//                            mView.setAdviewVisibility(View.GONE);
//                        } else {
//                            mView.setAdviewVisibility(View.VISIBLE);
//                        }
//                    }
//                });
    }


    public void checkYouTube_Html(final String htmlCode) {

        if ( htmlCode.contains("youtube") == true ) {
            mView.setAdviewVisibility(View.GONE);
        } else {
            mView.setAdviewVisibility(View.VISIBLE);
        }
    }

    public void openSharedIntent(String linkURL) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, linkURL);
        mView.startActivity(Intent.createChooser(shareIntent, "Share link using"));
    }
}
