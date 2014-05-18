package de.vanmar.android.yarrn.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;

/**
 * Created by Kolja on 18.05.14.
 */
public class AddFavoriteDialog extends Dialog {

    public interface AddFavoriteDialogListener {
        void onSave(String comment, String tags);
    }

    private AddFavoriteDialogListener listener;
    private final YarrnPrefs_ prefs;

    public AddFavoriteDialog(Context context, AddFavoriteDialogListener listener, YarrnPrefs_ prefs) {
        super(context);
        this.listener = listener;
        this.prefs = prefs;
        setTitle(R.string.add_favorite_title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_favorite_dialog);

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
}
