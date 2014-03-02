package de.vanmar.android.yarrn.stashes;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnAdapter;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.components.PagingListFragment;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;
import de.vanmar.android.yarrn.ravelry.dts.StashShort;
import de.vanmar.android.yarrn.ravelry.dts.StashesResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.ListStashesRequest;

@EFragment(R.layout.fragment_stashes)
@OptionsMenu(R.menu.stashes_menu)
public class StashesFragment extends PagingListFragment<StashesResult, StashShort> {

    @ViewById(R.id.stashlist)
    ListView stashlist;
    @ViewById(R.id.sort)
    Spinner sort;
    @ViewById(R.id.sort_reverse)
    CheckBox sortReverse;
    @Pref
    YarrnPrefs_ prefs;

    public interface StashesFragmentListener extends IRavelryActivity {
        /**
         * Stash with stashId was selected, 0 if no stash selected
         */
        void onStashSelected(int stashId, String username);
    }

    private StashesFragment.StashesFragmentListener listener;
    private StashesAdapter adapter;

    @AfterViews
    @Override
    public void afterViews() {
        super.afterViews();
        adapter = new StashesAdapter(getActivity()) {

            @Override
            protected void onStashClicked(final StashShort stash) {
                listener.onStashSelected(stash.id, prefs.username().get());
            }

        };
        stashlist.setAdapter(adapter);

        sort.setOnItemSelectedListener(null);
        sort.setSelection(prefs.stashSort().get(), false);
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sortReverse.setOnCheckedChangeListener(null);
        sortReverse.setChecked(prefs.stashSortReverse().get());
        sortReverse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                applySort();
            }
        });
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof StashesFragment.StashesFragmentListener) {
            listener = (StashesFragment.StashesFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement StashesFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void applySort() {
        prefs.stashSort().put(sort.getSelectedItemPosition());
        prefs.stashSortReverse().put(sortReverse.isChecked());
        loadData(1);
    }

    @OptionsItem(R.id.menu_refresh)
    public void menuRefresh() {
        super.menuRefresh();
    }

    protected void displayResult(final StashesResult result) {
        super.displayResult(result);
        getActivity().setTitle(R.string.my_stashes_title);
    }

    @Override
    protected ListView getListView() {
        return stashlist;
    }

    @Override
    protected YarrnAdapter<StashShort> getAdapter() {
        return adapter;
    }

    @Override
    protected AbstractRavelryGetRequest<StashesResult> getRequest(int page) {
        return new ListStashesRequest(this.getActivity().getApplication(), prefs, page, PAGE_SIZE);
    }

    @Override
    protected IRavelryActivity getRavelryActivity() {
        return listener;
    }
}