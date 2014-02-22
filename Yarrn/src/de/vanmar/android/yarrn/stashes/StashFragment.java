package de.vanmar.android.yarrn.stashes;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.octo.android.robospice.SpiceManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.YarrnSpiceService;
import de.vanmar.android.yarrn.components.ImageDialog;
import de.vanmar.android.yarrn.projects.PhotoAdapter;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;
import de.vanmar.android.yarrn.ravelry.RavelryResultListener;
import de.vanmar.android.yarrn.ravelry.dts.Stash;
import de.vanmar.android.yarrn.ravelry.dts.StashResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.GetStashRequest;

@EFragment(R.layout.fragment_stash_detail)
@OptionsMenu(R.menu.fragment_menu)
public class StashFragment extends SherlockFragment {

    public static final String ARG_STASH_ID = "stashId";
    public static final String ARG_USERNAME = "username";

    protected SpiceManager spiceManager;

    public interface StashFragmentListener extends IRavelryActivity {
    }

    @ViewById(R.id.gallery)
    HorizontalListView gallery;

    @ViewById(R.id.name)
    TextView name;

    @ViewById(R.id.location)
    TextView location;

    @ViewById(R.id.notes)
    WebView notes;

    @FragmentArg(ARG_STASH_ID)
    int stashId;
    @FragmentArg(ARG_USERNAME)
    String username;

    private StashFragmentListener listener;

    private PhotoAdapter adapter;

    @Pref
    YarrnPrefs_ prefs;

    @AfterViews
    public void afterViews() {
        if (spiceManager == null) {
            spiceManager = new SpiceManager(YarrnSpiceService.class);
        }

        adapter = new PhotoAdapter(getActivity());
        gallery.setAdapter(adapter);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new ImageDialog(getActivity(), adapter.getItem(position).mediumUrl).show();
            }
        });
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof StashFragmentListener) {
            listener = (StashFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement StashFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void onStashSelected(final int stashId) {
        clearStash();
        if (stashId != 0) {
            GetStashRequest request = new GetStashRequest(this.getActivity().getApplication(), prefs, stashId, username);
            spiceManager.execute(request, request.getCacheKey(), AbstractRavelryGetRequest.CACHE_DURATION, new StashListener(listener));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this.getActivity());

        onStashSelected(stashId);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @UiThread
    protected void clearStash() {
        getView().setVisibility(View.GONE);
    }

    @UiThread
    protected void displayStash(final StashResult stashResult) {
        Stash stash = stashResult.stash;
        getActivity().setTitle(stash.name);
        name.setText(stash.name);
        location.setText(stash.location);
        notes.loadDataWithBaseURL("", stash.notes_html, "text/html", "UTF-8", "");

        adapter.clear();
        adapter.addAll(stash.photos);
        getView().setVisibility(View.VISIBLE);
    }

    /*
    private void hideIfEmpty(View view, String value) {
        view.setVisibility(value != null && !"".equals(value) ? View.VISIBLE : View.GONE);
    }

    private void hideIfEmpty(View view, Collection values) {
        view.setVisibility(values != null && !values.isEmpty() ? View.VISIBLE : View.GONE);
    }*/

    @OptionsItem(R.id.menu_refresh)
    public void menuRefresh() {
        onStashSelected(stashId);
    }

    class StashListener extends RavelryResultListener<StashResult> {

        protected StashListener(IRavelryActivity activity) {
            super(activity);
        }

        @Override
        public void onRequestSuccess(StashResult result) {
            displayStash(result);
        }
    }
}