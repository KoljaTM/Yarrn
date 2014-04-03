package de.vanmar.android.yarrn.components;

import android.content.Context;
import android.widget.TextView;

import de.vanmar.android.yarrn.R;

/**
 * Created by Kolja on 23.01.14.
 */
public class DeletableTag extends TextView {

    public DeletableTag(Context context, String text) {
        super(context);
        setText(text);
        setBackgroundResource(R.drawable.border_delete);
    }
}
