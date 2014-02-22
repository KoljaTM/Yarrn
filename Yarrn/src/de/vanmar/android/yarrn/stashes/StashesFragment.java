package de.vanmar.android.yarrn.stashes;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.octo.android.robospice.SpiceManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.YarrnSpiceService;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;
import de.vanmar.android.yarrn.ravelry.RavelryResultListener;
import de.vanmar.android.yarrn.ravelry.dts.StashShort;
import de.vanmar.android.yarrn.ravelry.dts.StashesResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.ListStashesRequest;

@EFragment(R.layout.fragment_stashes)
@OptionsMenu(R.menu.stashes_menu)
public class StashesFragment extends SherlockFragment {

    protected SpiceManager spiceManager;

    public interface StashesFragmentListener extends IRavelryActivity {
        /**
         * Stash with stashId was selected, 0 if no stash selected
         */
        void onStashSelected(int stashId, String username);
    }

    @ViewById(R.id.stashlist)
    ListView stashlist;

    @ViewById(R.id.sort)
    Spinner sort;

    @ViewById(R.id.sort_reverse)
    CheckBox sortReverse;

    @Pref
    YarrnPrefs_ prefs;

    private StashesAdapter adapter;

    private StashesFragmentListener listener;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    public void afterViews() {
        if (spiceManager == null) {
            spiceManager = new SpiceManager(YarrnSpiceService.class);
        }
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

    private void applySort() {
        prefs.stashSort().put(sort.getSelectedItemPosition());
        prefs.stashSortReverse().put(sortReverse.isChecked());
        loadStashes();
    }

    @UiThread
    protected void displayStashes(final StashesResult result) {
        adapter.clear();
        adapter.addAll(result.stashes);
        getActivity().setTitle(R.string.my_stashes_title);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof StashesFragmentListener) {
            listener = (StashesFragmentListener) activity;
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

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this.getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStashes();
    }

    void loadStashes() {
        ListStashesRequest request = new ListStashesRequest(this.getActivity().getApplication(), prefs);
        spiceManager.execute(request, request.getCacheKey(), AbstractRavelryGetRequest.CACHE_DURATION, new RavelryResultListener<StashesResult>(StashesFragment.this.listener) {
            @Override
            public void onRequestSuccess(StashesResult stashesResult) {
                displayStashes(stashesResult);
            }
        });
    }

    @Override
    public void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @OptionsItem(R.id.menu_refresh)
    public void menuRefresh() {
        loadStashes();
    }
}