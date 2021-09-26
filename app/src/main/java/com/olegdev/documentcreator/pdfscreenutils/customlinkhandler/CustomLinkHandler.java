package com.olegdev.documentcreator.pdfscreenutils.customlinkhandler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.link.LinkHandler;
import com.github.barteksc.pdfviewer.model.LinkTapEvent;

public class CustomLinkHandler implements LinkHandler {

    private static final String TAG = CustomLinkHandler.class.getSimpleName();

    private PDFView pdfView;
    Vibrator vibe;

    public CustomLinkHandler(PDFView pdfView) {
        this.pdfView = pdfView;
        vibe = (Vibrator) this.pdfView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void handleLinkEvent(LinkTapEvent event) {
        vibrate();
        String uri = event.getLink().getUri();
        Integer page = event.getLink().getDestPageIdx();
        if (uri != null && !uri.isEmpty()) {
            handleUri(uri);
        } else if (page != null) {
            handlePage(page);
        }
    }

    private void handleUri(String uri) {
        vibrate();
        Uri parsedUri = Uri.parse(uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, parsedUri);
        Context context = pdfView.getContext();
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Log.w(TAG, "No activity found for URI: " + uri);
        }
    }

    private void vibrate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibe.vibrate(40);
        }
    }

    private void handlePage(int page) {
        pdfView.jumpTo(page);
    }
}