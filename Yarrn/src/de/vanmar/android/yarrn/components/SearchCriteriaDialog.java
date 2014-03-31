package de.vanmar.android.yarrn.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ViewAnimator;

import java.util.LinkedList;
import java.util.List;

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
    private Spinner searchCriteriaType;
    private ViewAnimator viewAnimator;
    private RadioGroup craft;

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

        searchCriteriaType = (Spinner) findViewById(R.id.search_criteria_type);
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_item, getSearchCriteriaTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchCriteriaType.setAdapter(adapter);

        searchCriteriaType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = searchCriteriaType.getItemAtPosition(position).toString();
                for (int i = 0; i < viewAnimator.getChildCount(); i++) {
                    View v = viewAnimator.getChildAt(i);
                    if (selected.equals(v.getTag())) {
                        viewAnimator.setDisplayedChild(i);
                        return;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        viewAnimator = (ViewAnimator) findViewById(R.id.view_animator);

        searchByUser = (RadioButton) findViewById(R.id.search_by_user);
        searchBySelf = (RadioButton) findViewById(R.id.search_by_self);
        searchByFriends = (RadioButton) findViewById(R.id.search_by_friends);
        searchByAnyone = (RadioButton) findViewById(R.id.search_by_anyone);

        craft = (RadioGroup) findViewById(R.id.craft);

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

        findViewById(R.id.add_search_craft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (craft.getCheckedRadioButtonId()) {
                    case R.id.search_craft_knitting:
                        SearchCriteriaDialog.this.searchCriteria = SearchCriteria.byCraft("knitting", getContext().getString(R.string.search_craft_knitting));
                        dismiss();
                        break;
                    case R.id.search_craft_crochet:
                        SearchCriteriaDialog.this.searchCriteria = SearchCriteria.byCraft("crochet", getContext().getString(R.string.search_craft_crochet));
                        dismiss();
                        break;
                    case R.id.search_craft_weaving:
                        SearchCriteriaDialog.this.searchCriteria = SearchCriteria.byCraft("weaving", getContext().getString(R.string.search_craft_weaving));
                        dismiss();
                        break;
                }
            }
        });
    }

    private List<String> getSearchCriteriaTypes() {
        LinkedList<String> searchCriteriaTypes = new LinkedList<String>();
        searchCriteriaTypes.add(getContext().getText(R.string.search_criteria_name).toString());
        searchCriteriaTypes.add(getContext().getText(R.string.search_criteria_craft).toString());
        return searchCriteriaTypes;
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
