package de.vanmar.android.yarrn.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;

/**
 * Created by Kolja on 22.01.14.
 */
public class SearchCriteriaDialog extends Dialog {

    private SearchCriteria searchCriteria = null;
    private YarrnPrefs_ prefs;

    public SearchCriteriaDialog(Context context, YarrnPrefs_ prefs) {
        super(context);
        this.prefs = prefs;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_criteria_dialog);

        final EditText searchBy = (EditText) findViewById(R.id.search_by);
        findViewById(R.id.add_search_by).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchBy.getText().length() > 0) {
                    // by=<user>
                    SearchCriteriaDialog.this.searchCriteria = new SearchCriteria("by", searchBy.getText().toString());
                    dismiss();
                }
            }
        });
        findViewById(R.id.add_search_by_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // friends=yes
                SearchCriteriaDialog.this.searchCriteria = new SearchCriteria("friends", "yes");
                dismiss();
            }
        });
        findViewById(R.id.add_search_by_self).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // by:<self>
                SearchCriteriaDialog.this.searchCriteria = new SearchCriteria("by", prefs.username().get());
                dismiss();
            }
        });
        findViewById(R.id.add_search_by_anyone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // by:<self>
                SearchCriteriaDialog.this.searchCriteria = null;
                dismiss();
            }
        });
    }

    public SearchCriteria getSearchCriteria() {
        return this.searchCriteria;
    }
}
