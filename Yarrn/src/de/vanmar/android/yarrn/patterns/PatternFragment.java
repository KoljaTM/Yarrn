package de.vanmar.android.yarrn.patterns;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.androidquery.util.AQUtility;
import com.google.gson.JsonObject;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Collection;
import java.util.List;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.YarrnSpiceService;
import de.vanmar.android.yarrn.components.AddEditFavoriteDialog;
import de.vanmar.android.yarrn.components.ImageDialog;
import de.vanmar.android.yarrn.projects.PhotoAdapter;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;
import de.vanmar.android.yarrn.ravelry.RavelryResultListener;
import de.vanmar.android.yarrn.ravelry.dts.BookmarkShort;
import de.vanmar.android.yarrn.ravelry.dts.Needle;
import de.vanmar.android.yarrn.ravelry.dts.Pattern;
import de.vanmar.android.yarrn.ravelry.dts.PatternResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.AddFavoriteRequest;
import de.vanmar.android.yarrn.requests.GetPatternRequest;

@EFragment(R.layout.fragment_pattern_detail)
@OptionsMenu(R.menu.pattern_fragment_menu)
public class PatternFragment extends SherlockFragment {

    public static final String ARG_PATTERN_ID = "patternId";

    protected SpiceManager spiceManager;
    private ImageDialog dialog;

    public interface PatternFragmentListener extends IRavelryActivity {
    }

    @ViewById(R.id.gallery)
    HorizontalListView gallery;

    @ViewById(R.id.name)
    TextView name;

    @ViewById(R.id.author)
    TextView author;

    @ViewById(R.id.gauge_description)
    TextView gauge_description;

    @ViewById(R.id.yarn_weight_description)
    TextView yarn_weight_description;

    @ViewById(R.id.yardage_description)
    TextView yardage_description;

    @ViewById(R.id.needles)
    TextView needles;

    @ViewById(R.id.notes)
    WebView notes;

    @FragmentArg(ARG_PATTERN_ID)
    int patternId;

    private PatternFragmentListener listener;

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
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
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
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @UiThread
    protected void clearPattern() {
        View view = getView();
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    protected void displayPattern(final PatternResult patternResult) {
        Pattern pattern = patternResult.pattern;
        getActivity().setTitle(pattern.name);
        name.setText(pattern.name);
        author.setText(pattern.patternAuthor == null ? null : pattern.patternAuthor.name);
        gauge_description.setText(getString(R.string.gauge_title) + pattern.gauge_description);
        yarn_weight_description.setText(getString(R.string.yarn_title) + pattern.yarn_weight_description);
        yardage_description.setText(getString(R.string.yardage_title) + pattern.yardage_description);
        needles.setText(getString(R.string.needles_title) + getNeedlesDescription(pattern.pattern_needle_sizes));
        notes.loadDataWithBaseURL("", pattern.notes_html, "text/html", "UTF-8", "");
        hideIfEmpty(gauge_description, pattern.gauge_description);
        hideIfEmpty(yarn_weight_description, pattern.yarn_weight_description);
        hideIfEmpty(yardage_description, pattern.yardage_description);
        hideIfEmpty(needles, pattern.pattern_needle_sizes);
        adapter.setItems(pattern.photos);
        getView().setVisibility(View.VISIBLE);
    }

    private void hideIfEmpty(View view, String value) {
        view.setVisibility(value != null && !"".equals(value) ? View.VISIBLE : View.GONE);
    }

    private void hideIfEmpty(View view, Collection values) {
        view.setVisibility(values != null && !values.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String getNeedlesDescription(List<Needle> needles) {
        StringBuilder needleString = new StringBuilder();
        if (needles != null)
            for (Needle needle : needles) {
                needleString.append('\n').append(needle.name);
            }
        return needleString.toString();
    }

    @OptionsItem(R.id.menu_refresh)
    public void menuRefresh() {
        onPatternSelected(patternId);
    }

    @OptionsItem(R.id.menu_add_as_favorite)
    public void menuAddAsFavorite() {
        new AddEditFavoriteDialog(getActivity(), new AddEditFavoriteDialog.AddEditFavoriteDialogListener() {
            @Override
            public void onSave(String comment, String tags) {
                JsonObject updateData = new JsonObject();
                updateData.addProperty("type", "pattern");
                updateData.addProperty("favorited_id", patternId);
                updateData.addProperty("comment", comment);
                updateData.addProperty("tag_names", tags);
                spiceManager.execute(new AddFavoriteRequest(prefs, getActivity().getApplication(), updateData), new RequestListener<BookmarkShort>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        AQUtility.report(spiceException);
                    }

                    @Override
                    public void onRequestSuccess(BookmarkShort bookmarkShort) {
                        Toast.makeText(getActivity(), R.string.add_favorite_success, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, prefs).show();

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