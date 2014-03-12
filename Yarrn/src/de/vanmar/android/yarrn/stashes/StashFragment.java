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
import de.vanmar.android.yarrn.ravelry.dts.YarnFiber;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.GetStashRequest;

@EFragment(R.layout.fragment_stash_detail)
@OptionsMenu(R.menu.fragment_menu)
public class StashFragment extends SherlockFragment {

    public static final String ARG_STASH_ID = "stashId";
    public static final String ARG_USERNAME = "username";

    protected SpiceManager spiceManager;
    private ImageDialog dialog;

    public interface StashFragmentListener extends IRavelryActivity {
    }

    @ViewById(R.id.gallery)
    HorizontalListView gallery;

    @ViewById(R.id.name)
    TextView name;

    @ViewById(R.id.location)
    TextView location;

    @ViewById(R.id.details)
    TextView details;

    @ViewById(R.id.color_title)
    TextView colorTitle;

    @ViewById(R.id.color)
    TextView color;

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
                dialog = new ImageDialog(getActivity(), adapter.getItem(position).mediumUrl);
                dialog.show();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @UiThread
    protected void clearStash() {
        getView().setVisibility(View.GONE);
    }

    protected void displayStash(final StashResult stashResult) {
        Stash stash = stashResult.stash;
        getActivity().setTitle(stash.name);
        name.setText(stash.name);
        location.setText(stash.location);
        hideIfEmpty(location, stash.location);
        String detailString = getDetailString(stash);
        details.setText(detailString);
        hideIfEmpty(details, detailString);
        color.setText(stash.color);
        hideIfEmpty(color, stash.color);
        hideIfEmpty(colorTitle, stash.color);
        notes.loadDataWithBaseURL("", stash.notes_html, "text/html", "UTF-8", "");
        adapter.setItems(stash.photos);
        getView().setVisibility(View.VISIBLE);
    }

    private String getDetailString(Stash stash) {
        StringBuilder details = new StringBuilder();
        if (stash.yarn != null && stash.yarn.yarnWeight != null) {
            if (stash.yarn.yarnWeight.name != null)
                details.append(stash.yarn.yarnWeight.name);
            if (stash.yarn.yarnWeight.ply != null)
                details.append(' ').append(stash.yarn.yarnWeight.ply).append(getString(R.string.ply));
        }
        if (stash.yarn != null && stash.yarn.yarnFibers != null) {
            for (YarnFiber fiber : stash.yarn.yarnFibers) {
                if (fiber.fiberType != null) {
                    details.append(' ');
                    if (fiber.percentage > 0)
                        details.append(fiber.percentage).append("% ");
                    details.append(fiber.fiberType.name);
                }
            }
        }
        return details.toString();
    }

    private void hideIfEmpty(View view, String value) {
        view.setVisibility(value != null && !"".equals(value) ? View.VISIBLE : View.GONE);
    }

    /*
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