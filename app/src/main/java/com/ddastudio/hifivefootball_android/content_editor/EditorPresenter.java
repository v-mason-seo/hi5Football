package com.ddastudio.hifivefootball_android.content_editor;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.ddastudio.hifivefootball_android.App;
import com.ddastudio.hifivefootball_android.R;
import com.ddastudio.hifivefootball_android.common.Constants;
import com.ddastudio.hifivefootball_android.common.PostBodyType;
import com.ddastudio.hifivefootball_android.data.event.EditorEvent;
import com.ddastudio.hifivefootball_android.data.manager.ContentManager;
import com.ddastudio.hifivefootball_android.data.model.OpenGraphModel;
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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.reactivestreams.Publisher;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import id.zelory.compressor.Compressor;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hongmac on 2017. 9. 8..
 */

public class EditorPresenter implements BaseContract.Presenter {

    public static final int REQUEST_CODE_CHOOSE = 23;

    EditorActivity mView;
    Compressor imageCompressor;
    ContentManager mContentsManager;
    TransferUtility transferUtility;
    // 링크 프리뷰
    TextCrawler textCrawler;

    int mSelectedBoardId;
    private List<TransferObserver> observers;
    final CompositeDisposable composite = new CompositeDisposable();

    public EditorPresenter(int boardId) {
        this.mSelectedBoardId = boardId;
    }

    @Override
    public void attachView(BaseContract.View view) {
        mView = (EditorActivity)view;
        mContentsManager = ContentManager.getInstance();
        transferUtility = AWSUtil.getTransferUtility(mView.getApplicationContext());
        imageCompressor = new Compressor(mView).setQuality(80)
                .setCompressFormat(Bitmap.CompressFormat.JPEG);
        textCrawler = new TextCrawler();
    }

    @Override
    public void detachView() {
        if ( textCrawler != null) {
            textCrawler.cancel();
        }

        if ( composite != null ) {
            composite.clear();
        }
    }

    public int getSelectedBoardId() {
        return mSelectedBoardId;
    }

    public void setSelectedBoardId(int selectedBoardId) {
        this.mSelectedBoardId = selectedBoardId;
    }

    /*public void onPause() {

        List<TransferObserver> list = transferUtility.getTransfersWithType(TransferType.UPLOAD);

        Log.i("hong", "transfer list size : " + list.size());
        for(int i=0; i < list.size(); i++) {
            Log.i("hong", String.format("[%d] state : %s", i, list.get(i).getState().name()) );
            list.get(i).cleanTransferListener();
        }
    }*/

    /*public void onStart() {

        // Use TransferUtility to get all upload transfers.
        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);
        TransferListener listener = new UploadListener();
    }*/

    /*----------------------------------------------------------------------------------------------*/

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
                    App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_insert_link, url, PostBodyType.PLAIN, og));
                } else {
                    App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_insert_link, url, PostBodyType.PLAIN, null));
                }
                mView.hideLoading();
            }
        }, url);

    }

    public void getOpenGraphMetaTag(String url) {

        Flowable<Document> doc = Flowable.defer(new Callable<Publisher<? extends Document>>() {
            @Override
            public Publisher<? extends Document> call() throws Exception {
                Connection con = Jsoup.connect(url);
                Document doc = con.get();

                return Flowable.just(doc);
            }
        });

        doc.subscribeOn(Schedulers.io())
//                .flatMap(document -> {
//
//                    //document.select("meta[name^=desc]").attr("content")
//                    //link : http://m.sports.naver.com/basketball/news/read.nhn?oid=486&aid=0000000717, image : http://dthumb.phinf.naver.net/?src=http://imgnews.naver.net/image/486/2018/03/15/150758223_GettyImages-90533536211.jpg&type=w430&service=sports
//                    Elements ogTags = document.select("meta[property^=og:]");
//                    return Flowable.just(ogTags);
//                })
                .map(document -> {

                    OpenGraphModel og = new OpenGraphModel();
                    String title = document.select("meta[name^=desc]").attr("content");
                    if (TextUtils.isEmpty(title)) {
                        title = document.select("title").first().text();
                    }
                    og.setTitle(title);
                    og.setUrl(url);
                    Elements elements = document.select("meta[property^=og:]");

                    for (int i = 0; i < elements.size(); i++ ) {

                        Element element = elements.get(i);
                        String text = element.attr("property");
                        if ( text.equals("og:url")) {
                            og.setUrl(element.attr("content"));
                        } else if (text.equals("og:image")) {
                            og.setImage(element.attr("content"));
                        } else if ( text.equals("og:description")) {
                            og.setDescription(element.attr("content"));
                        } else if ( text.equals("og:title")) {
                            og.setTitle(element.attr("content"));
                        }
                    }

                    //Timber.i("[getOpenGraphMetaTag] 3. thread name : %s, link : %s, image : %s", Thread.currentThread().getName(), og.getUrl(), og.getImage());

                    return og;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( og -> App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_insert_link, url, PostBodyType.PLAIN, og)),
                        e ->  {
                            mView.showErrorMessage(e.getMessage());
                            mView.hideLoading();
                        },
                        () -> mView.hideLoading());
    }

    /*----------------------------------------------------------------------------------------------*/

    private String getImageFileName(String userName, String extension) {

        String dateString = DateUtils.convertDateToString(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss_SSS");
        return dateString + "_" + userName + extension;
    }

    public void openLocalImagePicker() {

        RxPermissions rxPermissions = new RxPermissions(mView);
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
                                    .forResult(REQUEST_CODE_CHOOSE);
                        } else {
                            Toast.makeText(mView, R.string.permission_request_denied, Toast.LENGTH_LONG).show();
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
                .map(uri -> FileUtil.getFile(mView.getApplicationContext(), uri))
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

        composite.add(disposable);

    }

    private void uploadS3(File file) {

        mView.showLoading();

        String fileName = getImageFileName(App.getAccountManager().getUserName(), FileUtil.getExtension(file.getName()));

        //Log.d("hong", "file size : " + FileUtil.getReadableFileSize(file.length()));
        //Log.d("hong", "file name : " + fileName);

        try {
            TransferObserver observer = transferUtility.upload(Constants.UPLOAD_POST_ORIGINAL_BUCKET_NAME, fileName, file);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    if ( state == TransferState.COMPLETED) {
                        // TODO: 2017. 9. 11. 베이스 url 변수로 저장할 것.
                        String url = "https://s3.ap-northeast-2.amazonaws.com/hifivesoccer/origin/article/" + fileName;
                        App.getInstance().bus().send(new EditorEvent.ActionButtonClickEvent(R.id.action_insert_image, url, PostBodyType.PLAIN));
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
                    mView.hideLoading();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*----------------------------------------------------------------------------------------------*/

}
