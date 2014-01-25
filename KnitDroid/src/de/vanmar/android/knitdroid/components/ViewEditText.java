package de.vanmar.android.knitdroid.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import de.vanmar.android.knitdroid.R;

/**
 * Created by Kolja on 23.01.14.
 */
@EViewGroup(R.layout.view_edit_text)
public class ViewEditText extends RelativeLayout {

    private final String titleText;
    private final String bodyText;
    private final int inputType;
    private final int ems;
    private final int lines;

    @ViewById(R.id.title)
    TextView title;

    @ViewById(R.id.editText)
    EditText editText;

    @ViewById(R.id.viewText)
    TextView viewText;

    @ViewById(R.id.toggleButton)
    ImageButton toggleButton;

    private boolean isEditMode = false;
    private OnSaveListener onSaveListener;

    public interface OnSaveListener {
        void onSave(ViewEditText view, Editable text);
    }

    public ViewEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ViewEditText_);
        try {
            titleText = a.getString(R.styleable.ViewEditText__titleText);
            bodyText = a.getString(R.styleable.ViewEditText__bodyText);
            this.inputType = a.getInt(R.styleable.ViewEditText__android_inputType, 0);
            this.ems = a.getInt(R.styleable.ViewEditText__android_ems, 10);
            this.lines = a.getInt(R.styleable.ViewEditText__android_lines, 1);
        } finally {
            a.recycle();
        }

    }

    @AfterViews
    public void afterViews() {
        applyEditMode();
        toggleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    doSave();
                }
                isEditMode = !isEditMode;
                applyEditMode();
            }
        });
        applyStyling();
    }

    public void setOnSaveListener(OnSaveListener listener) {
        this.onSaveListener = listener;
    }

    private void doSave() {
        viewText.setText(editText.getText());
        if (onSaveListener != null) {
            onSaveListener.onSave(this, editText.getText());
        }
    }

    private void applyStyling() {
        setBodyText(bodyText);
        setTitleText(titleText);
        editText.setInputType(inputType);
        viewText.setInputType(inputType);
        editText.setEms(ems);
        viewText.setEms(ems);
        editText.setLines(lines);
        viewText.setLines(lines);
    }

    private void applyEditMode() {
        editText.setVisibility(isEditMode ? VISIBLE : GONE);
        viewText.setVisibility(!isEditMode ? VISIBLE : GONE);
        toggleButton.setBackgroundResource(isEditMode ? R.drawable.save : R.drawable.edit);
    }

    public void setTitleText(String titleText) {
        title.setText(titleText);
    }

    public void setBodyText(String bodyText) {
        editText.setText(bodyText);
        viewText.setText(bodyText);
    }
}
