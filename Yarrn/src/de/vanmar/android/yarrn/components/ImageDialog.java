package de.vanmar.android.yarrn.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

import de.vanmar.android.yarrn.R;

/**
 * Created by Kolja on 22.01.14.
 */
public class ImageDialog extends Dialog {
    private String imageUrl;

    public ImageDialog(Context context, String imageUrl) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.imageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_dialog);
        WebView webView = (WebView) findViewById(R.id.webview);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            webView.getSettings().setDisplayZoomControls(false);
        }

        webView.loadUrl(imageUrl);
        webView.setBackgroundColor(Color.BLACK);
    }

}
