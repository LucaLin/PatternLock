package com.dbs.omni.tw.controller.bill;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;


public class PDFReaderActivity extends ActivityBase implements OnPageChangeListener, OnLoadCompleteListener {
    private static final String TAG = "PDFReaderActivity";
    public static final String EXTRA_BILL_TITLE = "extra_bill_title";
    public static final String EXTRA_URL = "extra_url";


    private PDFView pdfView;
    private String url = null;
    private String title;

    private boolean isOneInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreader);
        setCenterTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(getIntent() != null && getIntent().hasExtra(EXTRA_BILL_TITLE)) {
            title = getIntent().getStringExtra(EXTRA_BILL_TITLE);
            setCenterTitle(title);
        }

        if(getIntent() != null){
            url = getIntent().getStringExtra(EXTRA_URL);
        }

        ImageButton buttonShare = (ImageButton) findViewById(R.id.button_share);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMsg(TAG, title, title, url);
            }
        });

        isOneInput = true;
        readPDF(url);
//        pdfView.fromFile(new File(url))
//                .onError(onErrorListener)
//                .onPageChange(this)
////                .swipeVertical(true)
////                .showMinimap(false)
//                .enableAnnotationRendering(true)
//                .onLoad(this).load();
////
//        PDDocument document = null;
//        try {
//            document = PDDocument.load(new File(url));
//            if(document.isEncrypted()) {
//                pdfView = (PDFView) findViewById(R.id.pdfview);
//                pdfView.fromFile(new File(url)).password("D122145566")
//                        .onPageChange(this)
////                .swipeVertical(true)
////                .showMinimap(false)
//                        .enableAnnotationRendering(true)
//                        .onLoad(this).load();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    private void readPDF(String filePath) {
        readPDF(filePath, null);
    }

    private void readPDF(String filePath, String password) {
        pdfView = (PDFView) findViewById(R.id.pdfview);
        pdfView.fromFile(new File(filePath))
                .onError(onErrorListener)
                .onPageChange(this)
                .password(password)
                .enableAnnotationRendering(true)
                .onLoad(this).load();
    }

    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
            Log.d("PDF Reader", t.getMessage());

            if (t.getMessage().contains("Password required or incorrect password") && isOneInput) {
                isOneInput = false;
                showPasswordAlert(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText editText = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_password);
                        readPDF(url, editText.getText().toString());

                    }
                }, false);

            } else {
                finish();
            }
        }
    };

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    private void showPasswordAlert (int btnResId, DialogInterface.OnClickListener btnClickListener, boolean cancelable){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this).setCancelable(cancelable);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pdf_password_edit, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(btnResId, btnClickListener);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File f = new File(filePath);
            if (f != null && f.exists() && f.isFile()) {
                String type;
                String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
//                if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("file/*");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
                intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
                intent.putExtra(Intent.EXTRA_TEXT, msgText);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, activityTitle));
            } else {
            }
        } else {
        }


    }

}
