package de.vanmar.android.knitdroid.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.androidquery.AQuery;

import de.vanmar.android.knitdroid.R;

/**
 * Created by Kolja on 22.01.14.
 */
public class ImageDialog extends Dialog {
    private String imageUrl;

    public ImageDialog(Context context, String imageUrl) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.imageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_dialog);
        //.progress(R.id.progressbar)
        new AQuery(findViewById(R.id.image_dialog)).id(R.id.imageView).progress(R.id.progressbar).image(imageUrl);
    }

}
