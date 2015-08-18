package de.vanmar.android.yarrn.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

import de.vanmar.android.yarrn.R;

/**
 * Created by Kolja on 25.02.14.
 */
public class WebViewDialog extends Dialog {
    private String url;

    public WebViewDialog(Context context, String url) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview_dialog);
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl(url);
    }

}
