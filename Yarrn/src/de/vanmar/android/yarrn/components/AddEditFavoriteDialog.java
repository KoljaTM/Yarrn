package de.vanmar.android.yarrn.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.BookmarkShort;

/**
 * Created by Kolja on 18.05.14.
 */
public class AddEditFavoriteDialog extends Dialog {

    private BookmarkShort favorite;

    public interface AddEditFavoriteDialogListener {
        void onSave(String comment, String tags);
    }

    private AddEditFavoriteDialogListener listener;
    private final YarrnPrefs_ prefs;

    public AddEditFavoriteDialog(Context context, AddEditFavoriteDialogListener listener, YarrnPrefs_ prefs) {
        super(context);
        this.listener = listener;
        this.prefs = prefs;
        setTitle(R.string.add_favorite_title);
    }

    public AddEditFavoriteDialog(Context context, AddEditFavoriteDialogListener listener, YarrnPrefs_ prefs, BookmarkShort favorite) {
        this(context, listener, prefs);
        this.favorite = favorite;
        setTitle(R.string.edit_favorite_title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_edit_favorite_dialog);
        initFields();

        ((Button) findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ((Button) findViewById(R.id.button_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = ((EditText) findViewById(R.id.add_favorite_comment)).getText().toString();
                String tags = ((EditText) findViewById(R.id.add_favorite_tags)).getText().toString();
                listener.onSave(comment, tags);
                dismiss();
            }
        });
    }

    private void initFields() {
        if (favorite != null) {
            ((EditText) findViewById(R.id.add_favorite_comment)).setText(favorite.comment);
            ((EditText) findViewById(R.id.add_favorite_tags)).setText(StringUtils.join(favorite.tags, ' '));
        }
    }
}
