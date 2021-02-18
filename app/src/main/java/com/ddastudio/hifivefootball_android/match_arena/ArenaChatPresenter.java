package com.ddastudio.hifivefootball_android.match_arena;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.common.Constants;
import com.ddastudio.hifivefootball_android.common.PostBoardType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.data.event.ArenaChatEvent;
import com.ddastudio.hifivefootball_android.data.event.EditorEvent;
import com.ddastudio.hifivefootball_android.data.manager.ContentManager;
import com.ddastudio.hifivefootball_android.data.manager.MatchTalkManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.arena_chat.ReactionModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.glide.Glide4Engine;
import com.ddastudio.hifivefootball_android.main.MainActivity;
import com.ddastudio.hifivefootball_android.ui.activity.ImageViewerActivity;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.utils.AWSUtil;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.ddastudio.hifivefootball_android.utils.FileUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hongmac on 2018. 1. 26..
 */

public class ArenaChatPresenter implements BaseContract.Presenter {

    MatchModel matchData;

    ArenaChatFragment mView;
    ContentManager mContentManager;
    MatchTalkManager mMatchTalkManager;
    Compressor imageCompressor;
    TransferUtility transferUtility;

    @NonNull
    CompositeDisposable _composite;

    boolean mChoiceHomeTeam = true;

    @Override
    public void attachView(BaseContract.View view) {

        this.mView = (ArenaChatFragment)view;
        this.mContentManager = ContentManager.getInstance();
        this.mMatchTalkManager = MatchTalkManager.getInstance();
        this._composite = new CompositeDisposable();
        this.imageCompressor = new Compressor(mView.getContext()).setQuality(60)
                .setCompressFormat(Bitmap.CompressFormat.JPEG);
        this.transferUtility = AWSUtil.getTransferUtility(mView.getContext());
    }

    @Override
    public void detachView() {

        if (_composite != null) {
            _composite.clear();
        }
    }

    public MatchModel getMatchData() {
        return matchData;
    }

    public void setMatchData(MatchModel matchData) {
        this.matchData = matchData;
    }

    public int getMatchId() {
        if ( matchData != null ) {
            return matchData.getMatchId();
        }

        return -1;
    }

    /*---------------------------------------------------------------------------------------------*/

    public boolean isChoiceHomeTeam() {
        return mChoiceHomeTeam;
    }

    public void setChoiceHomeTeam(boolean mChoiceHomeTeam) {
        this.mChoiceHomeTeam = mChoiceHomeTeam;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openLoginActivity() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mView.startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
    }

    public void openImageViewerActivity() {

        Intent intent = new Intent(mView.getContext(), ImageViewerActivity.class);
        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    public void createMatchTalk(Integer matchid,
                                String content,
                                Integer cellType,
                                String status) {

        String token;

        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getActivity().getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Flowable<DBResultResponse> observable
                = mMatchTalkManager.onCreateMatchTalk(token, matchid, content, cellType, status);

        Disposable disposable
                = observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(dbResultModel -> {
                            if ( dbResultModel.getResult() > 0 ) {
                                if ( dbResultModel.getData().containsKey("id") ) {
                                    int talkId = ((Double)dbResultModel.getData().get("id")).intValue();
                                    mView.publishReactionMessage(talkId, content);
                                }
                            }
                        },
                        e -> {
                    mView.showErrorMessage(e.getMessage());
                            mView.hideLoading();
                        },
                        () -> {
                            mView.hideLoading();
                        });

        _composite.add(disposable);

    }

    public void postContent(String title,
                            String preview, // empty
                            String content,
                            PostBoardType boardType,
                            int arenaid,
                            String tag,
                            @Nullable String imgs,
                            int bodyType,
                            PostCellType postCellType,
                            int allowComment) {

        String token;


        if (App.getAccountManager().isAuthorized()) {
            token = App.getAccountManager().getHifiveAccessToken();
        } else {
            Snackbar.make(mView.getActivity().getWindow().getDecorView(), mView.getResources().getString(R.string.is_not_authorized_info), Snackbar.LENGTH_LONG)
                    .setAction("이동", v -> openLoginActivity())
                    .show();

            return;
        }

        Flowable<DBResultResponse> observable
                = mContentManager.postContent(token, boardType.value(), title, preview, content, arenaid, null, null, tag, imgs, bodyType, postCellType.value(), allowComment);


        Disposable disposable
                = observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(dbResultModel -> {
                            App.getInstance().bus().send(new EditorEvent.PostContentStatusEvent(EditorEvent.PostContentStatusEvent.STATUS_COMPLETE, boardType.value()));
                        },
                        e -> {
                            App.getInstance().bus().send(new EditorEvent.PostContentStatusEvent(EditorEvent.PostContentStatusEvent.STATUS_ERROR, boardType.value()));
                            mView.hideLoading();
                        },
                        () -> {
                            mView.hideLoading();
                        });

        _composite.add(disposable);
    }


    /*---------------------------------------------------------------------------------------------*/

    public void openLocalImagePicker() {

        RxPermissions rxPermissions = new RxPermissions(mView.getActivity());
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {

                            Matisse.from(mView)
                                    .choose(MimeType.ofAll(), false)
                                    .countable(true)
                                    .maxSelectable(5)
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(new Glide4Engine())
                                    .forResult(ArenaChatFragment.REQUEST_CODE_CHOOSE);
                        } else {
                            mView.showErrorMessage(mView.getString(R.string.permission_request_denied));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    /**
     * 이미지 압축 및 업로드
     * @param obtainResults
     */
    public void imageCompressNUpload(List<Uri> obtainResults) {

        mView.showLoading();

        Disposable disposable
                = Flowable.fromArray(obtainResults)
                .flatMap(uris -> Flowable.fromIterable(uris))
                .subscribeOn(Schedulers.io())
                .map(uri -> FileUtil.getFile(mView.getContext(), uri))
                .flatMap(file -> {
                    if ( FileUtil.getExtension(file.getName()).equals(".gif")) {
                        return Flowable.just(file);
                    } else {
                        return imageCompressor.compressToFileAsFlowable(file);
                    }
                })
                //.delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        file -> uploadS3(file),
                        e -> {  mView.hideLoading();
                            //Log.i("hong", "imageCompressNUpload error : " + e.getMessage());
                        },
                        () -> mView.hideLoading()
                );

        _composite.add(disposable);

    }

    private void uploadS3(File file) {

        mView.showLoading();

        String fileName = getImageFileName(App.getAccountManager().getUserName(), FileUtil.getExtension(file.getName()));

        try {
            TransferObserver observer = transferUtility.upload(Constants.UPLOAD_ARENA_ORIGINAL_BUCKET_NAME, fileName, file);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    if ( state == TransferState.COMPLETED) {
                        // TODO: 2017. 9. 11. 베이스 url 변수로 저장할 것.
                        String url = "https://s3.ap-northeast-2.amazonaws.com/hifivesoccer/origin/arena/" + fileName;
                        //App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_insert_image, url, PostBodyType.PLAIN));
                        observer.cleanTransferListener();

                        ReactionModel stickerReaction = new ReactionModel();
                        //stickerReaction.setSide(isho);
                        stickerReaction.setImage(url);
                        //Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                        //String message = gson.toJson(stickerReaction);
                        //publishMessage(message);
                        App.getInstance().bus().send(new ArenaChatEvent.ArenaSendMessage(stickerReaction));

                        mView.hideLoading();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    observer.cleanTransferListener();
                    mView.hideLoading();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getImageFileName(String userName, String extension) {

        String dateString = DateUtils.convertDateToString(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss_SSS");
        return dateString + "_" + userName + extension;
    }
}
