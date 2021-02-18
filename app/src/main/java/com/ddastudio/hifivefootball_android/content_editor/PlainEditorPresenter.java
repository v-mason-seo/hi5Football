package com.ddastudio.hifivefootball_android.content_editor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.auth.LoginActivity;
import com.ddastudio.hifivefootball_android.common.Constants;
import com.ddastudio.hifivefootball_android.common.PostBodyType;
import com.ddastudio.hifivefootball_android.common.PostCellType;
import com.ddastudio.hifivefootball_android.common.PostEditorType;
import com.ddastudio.hifivefootball_android.content_list.model.ContentHeaderModel;
import com.ddastudio.hifivefootball_android.data.event.EditorEvent;
import com.ddastudio.hifivefootball_android.data.manager.ContentManager;
import com.ddastudio.hifivefootball_android.data.manager.IssueManager;
import com.ddastudio.hifivefootball_android.data.model.DBResultResponse;
import com.ddastudio.hifivefootball_android.data.model.OpenGraphModel;
import com.ddastudio.hifivefootball_android.data.model.football.MatchModel;
import com.ddastudio.hifivefootball_android.data.model.football.PlayerModel;
import com.ddastudio.hifivefootball_android.data.model.footballdata.TeamModel;
import com.ddastudio.hifivefootball_android.glide.Glide4Engine;
import com.ddastudio.hifivefootball_android.ui.base.BaseContract;
import com.ddastudio.hifivefootball_android.utils.AWSUtil;
import com.ddastudio.hifivefootball_android.utils.DateUtils;
import com.ddastudio.hifivefootball_android.utils.FileUtil;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

/**
 * Created by hongmac on 2017. 9. 26..
 */

public class PlainEditorPresenter implements BaseContract.Presenter {

    public static final int REQUEST_CODE_CHOOSE = 23;

    Context mContext;
    PlainEditorFragment mView;
    ContentManager mContentManager;
    IssueManager mIssueManager;
    @NonNull
    CompositeDisposable mComposite;

    MatchModel match;
    PlayerModel player;
    TeamModel team;
    PostEditorType postEditorType;
    ContentHeaderModel contentData;

    // 이미지 압축
    Compressor imageCompressor;
    // Amasonz S3
    TransferUtility transferUtility;
    // 링크 프리뷰
    TextCrawler textCrawler;

    public PlainEditorPresenter(PostEditorType mode) {

        this.postEditorType = mode;
    }

    @Override
    public void attachView(BaseContract.View view) {
        mView = (PlainEditorFragment)view;
        mContext = ((PlainEditorFragment) view).getContext();
        mComposite = new CompositeDisposable();
        mContentManager = ContentManager.getInstance();
        mIssueManager = IssueManager.getInstance();

        transferUtility = AWSUtil.getTransferUtility(mView.getContext());
        imageCompressor = new Compressor(mView.getContext()).setQuality(80)
                .setCompressFormat(Bitmap.CompressFormat.JPEG);
        textCrawler = new TextCrawler();
    }

    @Override
    public void detachView() {
        if ( textCrawler != null) {
            textCrawler.cancel();
        }

        if ( mComposite != null )
            mComposite.clear();
    }

    public ContentHeaderModel getContentData() {
        return contentData;
    }

    public void setContentData(ContentHeaderModel contentData) {
        this.contentData = contentData;
    }

    public int getContentId() {
        if ( contentData != null ) {
            return contentData.getContentId();
        }

        return -1;
    }

    public MatchModel getMatch() {
        return match;
    }

    public void setMatch(MatchModel match) {
        this.match = match;
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public TeamModel getTeam() {
        return team;
    }

    public void setTeam(TeamModel team) {
        this.team = team;
    }

    /*---------------------------------------------------------------------------------------------*/

    public void openLoginActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //mView.getActivity().startActivityForResult(intent, MainActivity.REQUEST_OPEN_USER_ACTIVITY);
        mView.startActivity(intent);
    }

    /*---------------------------------------------------------------------------------------------*/

    public PostEditorType getPostEditorType() {
        return postEditorType;
    }

    /**
     * 게시글 생성
     * @param boardid
     * @param title
     * @param preview
     * @param content
     * @param tag
     * @param imgs
     * @param allowComment
     */
    public void postContent(String token,
                            int boardid,
                            String title,
                            String preview,
                            String content,
                            Integer arenaid,
                            Integer playerid,
                            Integer teamid,
                            String tag,
                            @Nullable String imgs,
                            PostBodyType bodyType,
                            PostCellType cellType,
                            int allowComment) {


        Flowable<DBResultResponse> observable
                = mContentManager.postContent(token,
                                            boardid,
                                            title,
                                            preview,
                                            content,
                                            arenaid,
                                            playerid,
                                            teamid,
                                            tag,
                                            imgs,
                                            bodyType.value(),
                                            cellType.value(),
                                            allowComment);


        Disposable disposable = observable
                .delay(10, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dbResultModel -> {
                    App.getInstance().bus().send(new EditorEvent.PostContentStatusEvent(EditorEvent.PostContentStatusEvent.STATUS_COMPLETE, boardid));
                    mView.hideLoading();
                },
                e -> {
                    App.getInstance().bus().send(new EditorEvent.PostContentStatusEvent(EditorEvent.PostContentStatusEvent.STATUS_ERROR, boardid));
                    //mView.menuEnable(R.id.action_send_content);
                    mView.showErrorMessage("글쓰기 오류\n-> " + e.getMessage());
                    mView.hideLoading();
                },
                () -> {
                    //mView.menuEnable(R.id.action_send_content);
                    mView.hideLoading();
                });

        mComposite.add(disposable);
    }


    public void updateContent(String token,
                              int contentid,
                              int boardid,
                              String title,
                              String preview,
                              String content,
                              Integer arenaid,
                              Integer playerid,
                              Integer teamid,
                              String tag,
                              @Nullable String imgs,
                              PostBodyType bodyType,
                              PostCellType cellType,
                              int allowComment) {

        Flowable<DBResultResponse> observable
                = mContentManager.updateContent(token,
                                                contentid,
                                                boardid,
                                                title,
                                                preview,
                                                content,
                                                arenaid,
                                                playerid,
                                                teamid,
                                                tag,
                                                imgs,
                                                bodyType.value(),
                                                cellType.value(),
                                                allowComment);

        Disposable disposable
                = observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(dbResultModel -> {
                            if ( dbResultModel.getResult() > 0 ) {
                                App.getInstance().bus().send(new EditorEvent.PostContentStatusEvent(EditorEvent.PostContentStatusEvent.STATUS_COMPLETE, boardid));
                            }
                            mView.hideLoading();
                        },
                        e -> {
                            App.getInstance().bus().send(new EditorEvent.PostContentStatusEvent(EditorEvent.PostContentStatusEvent.STATUS_ERROR, boardid));
                            //mView.menuEnable(R.id.action_send_content);
                            mView.showErrorMessage("글수정 오류\n-> " + e.getMessage());
                            mView.hideLoading();
                        },
                        () -> {
                            //mView.menuEnable(R.id.action_send_content);
                            mView.hideLoading();
                        });

        mComposite.add(disposable);
    }

    // *** Issue ***

    public void postIssue(String token,
                            int boardid,
                            String title,
                            String content,
                            String tag,
                            @Nullable String imgs,
                            int bodyType,
                            int platform) {


        Flowable<DBResultResponse> observable
                = mIssueManager.postIssue(token, boardid, title, content, tag, imgs, bodyType, platform);


        Disposable disposable
                = observable
                .delay(10, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dbResultModel -> {
                    App.getInstance().bus().send(new EditorEvent.PostContentStatusEvent(EditorEvent.PostContentStatusEvent.STATUS_COMPLETE, boardid));
                            mView.hideLoading();
                },
                e -> {
                    App.getInstance().bus().send(new EditorEvent.PostContentStatusEvent(EditorEvent.PostContentStatusEvent.STATUS_ERROR, boardid));
                    //mView.menuDisable(R.id.action_send_content);
                    mView.showErrorMessage("글쓰기 오류\n-> " + e.getMessage());
                    mView.hideLoading();
                },
                () -> {
                    //mView.menuDisable(R.id.action_send_content);
                    mView.hideLoading();
                });

        mComposite.add(disposable);
    }

    public void getParsingContent(String html) {

        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("div");

        for ( int i = 0 ; i < elements.size(); i++ ) {

            String className = elements.get(i).attr("class");

            switch (className) {
                case "text":
                    String editText;
                    try {
                        editText = doc.select("div").get(i).html().replace("<br>", "");
                    } catch (Exception ex) {
                        editText = "";
                    }
                    mView.addTextContent(editText);
                    break;

                    case "img":
                    String imgSrc;
                    try {
                        imgSrc = elements.get(i).select("img[src]").attr("src");
                    } catch (Exception ex) {
                        imgSrc = "";
                    }
                    mView.addImageContent(imgSrc);
                    break;

//                case "link":
//                    String link;
//                    try {
//                        link = elements.get(i).select("a[href]").attr("href");
//                    } catch (Exception ex) {
//                        link = "";
//                    }
//                    mView.addLinkContent(link);
//                    break;

                case "link_card":
                case "link":
                    String link_card;
                    try {
                        link_card = elements.get(i).select("a[href]").attr("href");
                    } catch (Exception ex) {
                        link_card = "";
                    }
                    String src = elements.get(i).select("img[src]").attr("src");
                    String title = elements.get(i).select("div.container h5").text();
                    String desc = elements.get(i).select("div.container h6").text();
                    //Timber.i("image src : %s\n title : %s\n, desc : %s", a, b, c);
                    mView.addCardLinkContent(link_card, src, title, desc);
                    break;
            }
        }
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
                                    .maxSelectable(8)
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    .thumbnailScale(0.85f)
                                    .imageEngine(new Glide4Engine())
                                    .forResult(REQUEST_CODE_CHOOSE);
                        } else {
                            mView.showMessage(mView.getResources().getString(R.string.permission_request_denied));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {}
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
                .flatMap(Flowable::fromIterable)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(uri -> FileUtil.getFile(mView.getContext(), uri))
                .subscribeOn(Schedulers.io())
                .flatMap(file -> {
                    String extension = FileUtil.getExtension(file.getName());
                    if ( extension.equals(".gif") || extension.equals(".webm")) {
                        return convertToMp4(file).toFlowable(BackpressureStrategy.BUFFER);
                    } else if ( extension.equals(".mp4") ) {
                        return Flowable.just(file);
                    } else {
                        return imageCompressor.compressToFileAsFlowable(file);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::uploadS3,
                        e -> mView.hideLoading(),
                        () -> mView.hideLoading()
                );

        mComposite.add(disposable);
    }


    /**
     * gif, webm 파일을 mp4로 변환한다.
     * @param file
     */
    private Observable<File> convertToMp4(File file) {

        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                // 파일 null 체크
                if ( file == null ) {
                    if ( !emitter.isDisposed()) {
                        emitter.onNext(null);
                        emitter.onComplete();
                    }
                }


                // 선택된 파일의 확장자
                String extension = FileUtil.getExtension(file.getName());

                // 입력 문자열
                String inFile = file.getPath();

                // 출력 문자열 생성, getCacheDir() : 앱내에서 사용할 캐시 디렉토리
                final String outTempFile = mView.getActivity().getCacheDir() + UUID.randomUUID().toString() + ".mp4";

                //----------------------
                // 이미지 변환하기
                //----------------------

                // 커맨드 입력
                String cmd = "";
                if ( extension.equals(".gif")) {
                    cmd = mView.getResources().getString(R.string.ffmpeg_cmd_convert_gif_to_mp4, inFile, outTempFile);
                } else if ( extension.equals(".webm")) {
                    cmd = mView.getResources().getString(R.string.ffmpeg_cmd_convert_webm_to_mp4, inFile, outTempFile);
                } else {
                    if ( !emitter.isDisposed()) {
                        emitter.onNext(null);
                        emitter.onComplete();
                    }
                }

                // 사용가능 여부 체크
                if (FFmpeg.getInstance(mView.getContext()).isSupported()) {
                } else {
                    if ( !emitter.isDisposed()) {
                        emitter.onNext(null);
                        emitter.onComplete();
                    }
                }

                FFmpeg ffmpeg = FFmpeg.getInstance(mView.getContext());
                ffmpeg.execute(cmd.split("@"), new ExecuteBinaryResponseHandler() {

                    @Override
                    public void onStart() {
                        mView.showLoadingInfo("MP4 압축을 시작합니다.");
                    }

                    @Override
                    public void onProgress(String message) {
                        mView.showLoadingInfo(message);
                    }

                    @Override
                    public void onFailure(String message) {
                        mView.showLoadingInfo("MP4 압축실패");
                        mView.hideLoading();
                    }

                    @Override
                    public void onSuccess(String message) {
                    }

                    @Override
                    public void onFinish() {
                        mView.showLoadingInfo("MP4 압축이 완료되었습니다.");

                        // 변환이 완료되면 파일을 삭제한다.
                        final File tempFile = new File(outTempFile);
                        if ( !emitter.isDisposed()) {
                            emitter.onNext(tempFile);
                            emitter.onComplete();
                        }
                    }
                });
            }
        });



//        // 파일 null 체크
//        if ( file == null )
//            return;
//
//        // 선택된 파일의 확장자
//        String extension = FileUtil.getExtension(file.getName());
//
//        // 입력 문자열
//        String inFile = file.getPath();
//
//        // 출력 문자열 생성, getCacheDir() : 앱내에서 사용할 캐시 디렉토리
//        // getActivity().getCacheDir() + UUID.randomUUID().toString();
//        final String outTempFile = mView.getActivity().getCacheDir() + UUID.randomUUID().toString() + ".mp4";
//
//        //----------------------
//        // 이미지 변환하기
//        //----------------------
//
//        // 커맨드 입력
//        String cmd;
//        if ( extension.equals(".gif")) {
//            cmd = mView.getResources().getString(R.string.ffmpeg_cmd_convert_gif_to_mp4, inFile, outTempFile);
//        } else if ( extension.equals(".webm")) {
//            cmd = mView.getResources().getString(R.string.ffmpeg_cmd_convert_webm_to_mp4, inFile, outTempFile);
//        } else {
//            return;
//        }
//
//        // 사용가능 여부 체크
//        if (FFmpeg.getInstance(mView.getContext()).isSupported()) {
//            Log.i("hong", "[FFmpeg] supported");
//        } else {
//            // ffmpeg is not supported
//            Log.i("hong", "[FFmpeg] not supported");
//            return;
//        }
//
//        FFmpeg ffmpeg = FFmpeg.getInstance(mView.getContext());
//        ffmpeg.execute(cmd.split(" "), new ExecuteBinaryResponseHandler() {
//
//            @Override
//            public void onStart() {
//                Log.i("hong", "[onStart]");
//            }
//
//            @Override
//            public void onProgress(String message) {
//                Log.i("hong", "[onProgress] " + message);
//            }
//
//            @Override
//            public void onFailure(String message) {
//                Log.i("hong", "[onFailure] " + message);
//            }
//
//            @Override
//            public void onSuccess(String message) {
//                Log.i("hong", "[onSuccess] " + message);
//            }
//
//            @Override
//            public void onFinish() {
//                Log.i("hong", "[onFinish]");
//
//                // 변환이 완료되면 파일을 삭제한다.
//                final File tempFile = new File(outTempFile);
//                if ( tempFile.exists()) {
//                    tempFile.delete();
//                }
//            }
//        });
    }

    //ffmpeg version n4.0-39-gda39990 Copyright (c) 2000-2018 the FFmpeg developers
    //  built with gcc 4.9.x (GCC) 20150123 (prerelease)
    //  configuration: --target-os=linux --cross-prefix=/root/bravobit/ffmpeg-android/toolchain-android/bin/arm-linux-androideabi- --arch=arm --cpu=cortex-a8 --enable-runtime-cpudetect --sysroot=/root/bravobit/ffmpeg-android/toolchain-android/sysroot --enable-pic --enable-libx264 --enable-ffprobe --enable-libopus --enable-libvorbis --enable-libfdk-aac --enable-libfreetype --enable-libfribidi --enable-libmp3lame --enable-fontconfig --enable-libvpx --enable-libass --enable-yasm --enable-pthreads --disable-debug --enable-version3 --enable-hardcoded-tables --disable-ffplay --disable-linux-perf --disable-doc --disable-shared --enable-static --enable-runtime-cpudetect --enable-nonfree --enable-network --enable-avresample --enable-avformat --enable-avcodec --enable-indev=lavfi --enable-hwaccels --enable-ffmpeg --enable-zlib --enable-gpl --enable-small --enable-nonfree --pkg-config=pkg-config --pkg-config-flags=--static --prefix=/root/bravobit/ffmpeg-android/build/armeabi-v7a --extra-cflags='-I/root/bravobit/ffmpeg-android/toolchain-android/include -U_FORTIFY_SOURCE -D_FORTIFY_SOURCE=2 -fno-strict-overflow -fstack-protector-all' --extra-ldflags='-L/root/bravobit/ffmpeg-android/toolchain-android/lib -Wl,-z,relro -Wl,-z,now -pie' --extra-cxxflags=
    //  libavutil      56. 14.100 / 56. 14.100
    //  libavcodec     58. 18.100 / 58. 18.100
    //  libavformat    58. 12.100 / 58. 12.100
    //  libavdevice    58.  3.100 / 58.  3.100
    //  libavfilter     7. 16.100 /  7. 16.100
    //  libavresample   4.  0.  0 /  4.  0.  0
    //  libswscale      5.  1.100 /  5.  1.100
    //  libswresample   3.  1.100 /  3.  1.100
    //  libpostproc    55.  1.100 / 55.  1.100
    ///storage/emulated/0/Download/1: No such file or directory
    private void uploadS3(File file) {

        mView.showLoading();

        String fileName = getImageFileName(App.getAccountManager().getUserName(), FileUtil.getExtension(file.getName()));

        try {
            TransferObserver observer = transferUtility.upload(Constants.UPLOAD_POST_ORIGINAL_BUCKET_NAME, fileName, file);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    if ( state == TransferState.COMPLETED) {
                        String url = "http://cdn.hifivefootball.com/origin/article/" + fileName;
                        // 선택된 파일의 확장자
                        String extension = FileUtil.getExtension(file.getName());

                        if ( extension.equals(".gif") || extension.equals(".mp4")) {
                            mView.addVideoContent(url);
                        } else {
                            mView.addImageContent(url);
                        }

                        // 임시파일은 삭제
                        if ( file != null && file.exists()) {
                            file.delete();
                        }

                        observer.cleanTransferListener();
                        mView.hideLoading();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    observer.cleanTransferListener();

                    // 임시파일은 삭제
                    if ( file != null && file.exists()) {
                        file.delete();
                    }
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

    /*---------------------------------------------------------------------------------------------*/

    /**
     * 링크 미리보기 정보 가져오기
     * @param url
     */
    public void getLinkPreviewInfo(String url) {

        textCrawler.makePreview(new LinkPreviewCallback() {
            @Override
            public void onPre() {

            }

            @Override
            public void onPos(SourceContent sourceContent, boolean b) {

                if ( sourceContent.isSuccess() ) {
                    OpenGraphModel og = new OpenGraphModel();

                    String title = !TextUtils.isEmpty(sourceContent.getMetaTags().get("title"))
                            ? sourceContent.getMetaTags().get("title")
                            : sourceContent.getTitle();
                    String description = !TextUtils.isEmpty(sourceContent.getMetaTags().get("description"))
                            ? sourceContent.getMetaTags().get("description")
                            : sourceContent.getDescription();
                    String imageUrl = sourceContent.getMetaTags().get("image");
                    String cannonicalUrl = sourceContent.getCannonicalUrl();
                    og.setTitle(title);
                    og.setDescription(description);
                    og.setImage(imageUrl);
                    og.setUrl(sourceContent.getUrl());
                    og.setCannonicalUrl(cannonicalUrl);
                    mView.addLinkContent(url, og);

                    // 링크추가할때 타이틀이 비어있으면 오픈메타 타이틀을 넣어준다.
                    if ( mView.isTitleAvailable()) {
                        mView.setTitle(title);
                    }

                } else {
                    mView.addLinkContent(url, null);
                }
                mView.hideLoading();
            }
        }, url);

    }
}
