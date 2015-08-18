package de.vanmar.android.yarrn.queues;

import android.app.Activity;
import android.widget.ListView;

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
import de.vanmar.android.yarrn.ravelry.dts.QueuedProjectSmall;
import de.vanmar.android.yarrn.ravelry.dts.QueuesResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.ListQueuesRequest;

@EFragment(R.layout.fragment_queues)
@OptionsMenu(R.menu.fragment_menu)
public class QueuesFragment extends PagingListFragment<QueuesResult, QueuedProjectSmall> {

    public interface QueuesFragmentListener extends IRavelryActivity {
        /**
         * Project with projectId was selected, 0 if no project selected
         */
        void onQueuedProjectSelected(int projectId, String username);
    }

    @ViewById(R.id.queuelist)
    ListView queuelist;

    @Pref
    YarrnPrefs_ prefs;

    private QueuesAdapter adapter;

    private QueuesFragmentListener listener;

    @AfterViews
    @Override
    public void afterViews() {
        super.afterViews();
        adapter = new QueuesAdapter(getActivity()) {

            @Override
            protected void onProjectClicked(final QueuedProjectSmall project) {
                listener.onQueuedProjectSelected(project.id, prefs.username().get());
            }

        };
        queuelist.setAdapter(adapter);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof QueuesFragmentListener) {
            listener = (QueuesFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement QueuesFragmentListener");
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

    protected void displayResult(final QueuesResult result) {
        super.displayResult(result);
        if (getActivity() != null) {
            getActivity().setTitle(R.string.my_queues_title);
        }
    }

    @Override
    protected ListView getListView() {
        return queuelist;
    }

    @Override
    protected YarrnAdapter<QueuedProjectSmall> getAdapter() {
        return adapter;
    }

    @Override
    protected AbstractRavelryGetRequest<QueuesResult> getRequest(int page) {
        return new ListQueuesRequest(this.getActivity().getApplication(), prefs, page, PAGE_SIZE);
    }

    @Override
    protected IRavelryActivity getRavelryActivity() {
        return listener;
    }
}