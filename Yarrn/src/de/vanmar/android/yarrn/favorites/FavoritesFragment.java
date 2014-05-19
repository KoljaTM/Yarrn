package de.vanmar.android.yarrn.favorites;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.util.AQUtility;
import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnAdapter;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.components.AddEditFavoriteDialog;
import de.vanmar.android.yarrn.components.PagingListFragment;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;
import de.vanmar.android.yarrn.ravelry.dts.BookmarkShort;
import de.vanmar.android.yarrn.ravelry.dts.FavoritesResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.DeleteFavoriteRequest;
import de.vanmar.android.yarrn.requests.EditFavoriteRequest;
import de.vanmar.android.yarrn.requests.ListFavoritesRequest;

import static de.vanmar.android.yarrn.requests.ListFavoritesRequest.SearchOption;

@EFragment(R.layout.fragment_favorites)
@OptionsMenu(R.menu.favorites_menu)
public class FavoritesFragment extends PagingListFragment<FavoritesResult, BookmarkShort> {

    public static final int MENU_EDIT = 1;
    public static final int MENU_DELETE = 2;
    @SystemService
    InputMethodManager inputMethodManager;
    @ViewById(R.id.favoritelist)
    ListView favoritelist;
    @ViewById(R.id.query)
    EditText query;
    @ViewById(R.id.search_options)
    Spinner searchOptions;
    @Pref
    YarrnPrefs_ prefs;

    public interface FavoritesFragmentListener extends IRavelryActivity {
        /**
         * Project with projectId was selected, 0 if no project selected
         */
        void onProjectSelected(int projectId, String username);

        /**
         * Pattern with patternId was selected, 0 if no pattern selected
         */
        void onPatternSelected(int patternId);
    }

    private FavoritesAdapter adapter;
    private FavoritesFragmentListener listener;
    private String searchQuery = "";
    private SearchOption searchOption;

    @AfterViews
    @Override
    public void afterViews() {
        super.afterViews();
        adapter = new FavoritesAdapter(getActivity()) {

            @Override
            protected void onProjectClicked(final int id, final String username) {
                listener.onProjectSelected(id, username);
            }

            @Override
            protected void onPatternClicked(int patternId) {
                listener.onPatternSelected(patternId);
            }

        };
        favoritelist.setAdapter(adapter);
        registerForContextMenu(favoritelist);

        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    setSearchQuery(query.getText().toString());
                    inputMethodManager.hideSoftInputFromWindow(query.getWindowToken(), 0);
                    query.clearFocus();
                }
                return true;
            }
        });
        searchOptions.setOnItemSelectedListener(null);
        searchOptions.setSelection(prefs.favoriteSearchOption().get());
        searchOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSearchOption(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        if (view.getId() == R.id.favoritelist) {
            menu.add(Menu.NONE, MENU_EDIT, MENU_EDIT, R.string.edit_favorite);
            menu.add(Menu.NONE, MENU_DELETE, MENU_DELETE, R.string.delete_favorite);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final BookmarkShort favorite = adapter.getItem(info.position);
        if (item.getItemId() == MENU_EDIT) {
            new AddEditFavoriteDialog(getActivity(), new AddEditFavoriteDialog.AddEditFavoriteDialogListener() {
                @Override
                public void onSave(String comment, String tags) {
                    JsonObject updateData = new JsonObject();
                    updateData.addProperty("comment", comment);
                    updateData.addProperty("tag_list", tags);
                    favorite.comment = comment;
                    favorite.tags = Arrays.asList(StringUtils.split(tags, ' '));

                    spiceManager.execute(new EditFavoriteRequest(prefs, getActivity().getApplication(), favorite.id, updateData), new RequestListener<BookmarkShort>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            AQUtility.report(spiceException);
                        }

                        @Override
                        public void onRequestSuccess(BookmarkShort bookmarkShort) {
                            Toast.makeText(getActivity(), R.string.edit_favorite_success, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }, prefs, favorite).show();
        } else if (item.getItemId() == MENU_DELETE) {
            String favoriteId = favorite.id;
            spiceManager.execute(new DeleteFavoriteRequest(prefs, getActivity().getApplication(), favoriteId), new RequestListener<BookmarkShort>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    AQUtility.report(spiceException);
                }

                @Override
                public void onRequestSuccess(BookmarkShort bookmarkShort) {
                    Toast.makeText(getActivity(), R.string.favorite_deleted, Toast.LENGTH_LONG).show();
                    adapter.remove(favorite);
                }
            });
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void setSearchQuery(String newQuery) {
        if (!searchQuery.equals(newQuery)) {
            searchQuery = newQuery;
            loadData(1);
        }
    }

    private void setSearchOption(int position) {
        SearchOption newOption = SearchOption.values()[position];
        if (searchOption != newOption) {
            searchOption = newOption;
            prefs.favoriteSearchOption().put(position);
            loadData(1);
        }
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FavoritesFragmentListener) {
            listener = (FavoritesFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement FavoritesFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @OptionsItem(R.id.menu_refresh)
    public void menuRefresh() {
        super.menuRefresh();
    }

    @Click(R.id.delete)
    public void onDeleteClicked() {
        query.setText(null);
    }

    protected void displayResult(final FavoritesResult result) {
        super.displayResult(result);
        getActivity().setTitle(R.string.my_favorites_title);
    }

    @Override
    protected ListView getListView() {
        return favoritelist;
    }

    @Override
    protected YarrnAdapter<BookmarkShort> getAdapter() {
        return adapter;
    }

    @Override
    protected AbstractRavelryGetRequest<FavoritesResult> getRequest(int page) {
        return new ListFavoritesRequest(this.getActivity().getApplication(), prefs, page, PAGE_SIZE, searchQuery, searchOption);
    }

    @Override
    protected IRavelryActivity getRavelryActivity() {
        return listener;
    }
}