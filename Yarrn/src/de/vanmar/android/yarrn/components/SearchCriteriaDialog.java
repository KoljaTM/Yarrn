package de.vanmar.android.yarrn.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;

/**
 * Created by Kolja on 22.01.14.
 */
public class SearchCriteriaDialog extends Dialog implements CompoundButton.OnCheckedChangeListener {

    private SearchCriteria searchCriteria = null;
    private YarrnPrefs_ prefs;
    private RadioButton searchByUser;
    private RadioButton searchBySelf;
    private RadioButton searchByFriends;
    private RadioButton searchByAnyone;
    private SearchCriteria.SearchContext searchContext;

    public SearchCriteriaDialog(Context context, SearchCriteria.SearchContext searchContext, YarrnPrefs_ prefs) {
        super(context);
        this.searchContext = searchContext;
        this.prefs = prefs;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_criteria_dialog);

        searchByUser = (RadioButton) findViewById(R.id.search_by_user);
        searchBySelf = (RadioButton) findViewById(R.id.search_by_self);
        searchByFriends = (RadioButton) findViewById(R.id.search_by_friends);
        searchByAnyone = (RadioButton) findViewById(R.id.search_by_anyone);

        searchByUser.setOnCheckedChangeListener(this);
        searchBySelf.setOnCheckedChangeListener(this);
        searchByFriends.setOnCheckedChangeListener(this);
        searchByAnyone.setOnCheckedChangeListener(this);

        final EditText searchBy = (EditText) findViewById(R.id.search_by);
        searchBy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchByUser.setChecked(true);
            }
        });

        findViewById(R.id.add_search_by).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchByUser.isChecked()) {
                    if (searchBy.getText().length() > 0) {
                        // by=<user>
                        String user = searchBy.getText().toString();
                        SearchCriteriaDialog.this.searchCriteria = SearchCriteria.byUser(user, searchContext, getContext().getString(R.string.search_by_user_title) + " " + user);
                        dismiss();
                    }
                } else if (searchByFriends.isChecked()) {
                    // friends=yes
                    SearchCriteriaDialog.this.searchCriteria = new SearchCriteria("friends", "yes", getContext().getString(R.string.search_by_friends_title));
                    dismiss();
                } else if (searchBySelf.isChecked()) {
                    // by:<self>
                    SearchCriteriaDialog.this.searchCriteria = SearchCriteria.byUser(prefs.username().get(), searchContext, getContext().getString(R.string.search_by_self_title));
                    dismiss();
                } else {
                    // <nothing>
                    SearchCriteriaDialog.this.searchCriteria = null;
                    dismiss();
                }
            }
        });
    }

    public SearchCriteria getSearchCriteria() {
        return this.searchCriteria;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView != searchByUser) {
                searchByUser.setChecked(false);
            }
            if (buttonView != searchBySelf) {
                searchBySelf.setChecked(false);
            }
            if (buttonView != searchByFriends) {
                searchByFriends.setChecked(false);
            }
            if (buttonView != searchByAnyone) {
                searchByAnyone.setChecked(false);
            }
        }
    }
}
