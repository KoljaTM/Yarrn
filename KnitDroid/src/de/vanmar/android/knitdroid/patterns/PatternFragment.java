package de.vanmar.android.knitdroid.patterns;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.components.ImageDialog;
import de.vanmar.android.knitdroid.projects.PhotoAdapter;
import de.vanmar.android.knitdroid.ravelry.AbstractRavelryGetRequest;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.RavelryResultListener;
import de.vanmar.android.knitdroid.ravelry.dts.Pattern;
import de.vanmar.android.knitdroid.ravelry.dts.PatternResult;

@EFragment(R.layout.fragment_pattern_detail)
@OptionsMenu(R.menu.fragment_menu)
public class PatternFragment extends SherlockFragment {

    public static final String ARG_PATTERN_ID = "patternId";

    protected SpiceManager spiceManager;

    public interface PatternFragmentListener extends IRavelryActivity {
    }

    @ViewById(R.id.gallery)
    HorizontalListView gallery;

    @ViewById(R.id.name)
    TextView name;

    @ViewById(R.id.author)
    TextView author;

    @ViewById(R.id.notes)
    WebView notes;

    @ViewById(R.id.gauge_description)
    TextView gauge_description;

    @ViewById(R.id.yarn_weight_description)
    TextView yarn_weight_description;

    @ViewById(R.id.yardage_description)
    TextView yardage_description;

    @FragmentArg(ARG_PATTERN_ID)
    int patternId;

    private PatternFragmentListener listener;

    private PhotoAdapter adapter;

    @Pref
    KnitdroidPrefs_ prefs;

    @AfterViews
    public void afterViews() {
        if (spiceManager == null) {
            spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
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
        if (activity instanceof PatternFragmentListener) {
            listener = (PatternFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement PatternFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void onPatternSelected(final int patternId) {
        clearPattern();
        if (patternId != 0) {
            GetPatternRequest request = new GetPatternRequest(this.getActivity().getApplication(), prefs, patternId);
            spiceManager.execute(request, request.getCacheKey(), AbstractRavelryGetRequest.CACHE_DURATION, new PatternListener(listener));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this.getActivity());

        onPatternSelected(patternId);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @UiThread
    protected void clearPattern() {
        getView().setVisibility(View.GONE);
    }

    @UiThread
    protected void displayPattern(final PatternResult patternResult) {
        Pattern pattern = patternResult.pattern;
        getActivity().setTitle(pattern.name);
        name.setText(pattern.name);
        author.setText(pattern.patternAuthor == null ? null : pattern.patternAuthor.name);
        gauge_description.setText(getString(R.string.gauge_title) + pattern.gauge_description);
        yarn_weight_description.setText(getString(R.string.yarn_title) + pattern.yarn_weight_description);
        yardage_description.setText(getString(R.string.yardage_title) + pattern.yardage_description);
        notes.loadDataWithBaseURL("", pattern.notes_html, "text/html", "UTF-8", "");

        adapter.clear();
        adapter.addAll(pattern.photos);
        getView().setVisibility(View.VISIBLE);
    }

    @OptionsItem(R.id.menu_refresh)
    public void menuRefresh() {
        onPatternSelected(patternId);
    }

    class PatternListener extends RavelryResultListener<PatternResult> {

        protected PatternListener(IRavelryActivity activity) {
            super(activity);
        }

        @Override
        public void onRequestSuccess(PatternResult result) {
            displayPattern(result);
        }
    }
}