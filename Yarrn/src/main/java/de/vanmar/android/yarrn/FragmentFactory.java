package de.vanmar.android.yarrn;

import org.androidannotations.annotations.EBean;

import de.vanmar.android.yarrn.favorites.FavoritesFragment;
import de.vanmar.android.yarrn.favorites.FavoritesFragment_;
import de.vanmar.android.yarrn.patterns.PatternFragment;
import de.vanmar.android.yarrn.patterns.PatternFragment_;
import de.vanmar.android.yarrn.patterns.PatternSearchFragment;
import de.vanmar.android.yarrn.patterns.PatternSearchFragment_;
import de.vanmar.android.yarrn.projects.ProjectFragment;
import de.vanmar.android.yarrn.projects.ProjectFragment_;
import de.vanmar.android.yarrn.projects.ProjectSearchFragment;
import de.vanmar.android.yarrn.projects.ProjectSearchFragment_;
import de.vanmar.android.yarrn.projects.ProjectsFragment;
import de.vanmar.android.yarrn.projects.ProjectsFragment_;
import de.vanmar.android.yarrn.queues.QueuedProjectFragment;
import de.vanmar.android.yarrn.queues.QueuedProjectFragment_;
import de.vanmar.android.yarrn.queues.QueuesFragment;
import de.vanmar.android.yarrn.queues.QueuesFragment_;
import de.vanmar.android.yarrn.stashes.StashFragment;
import de.vanmar.android.yarrn.stashes.StashFragment_;
import de.vanmar.android.yarrn.stashes.StashSearchFragment;
import de.vanmar.android.yarrn.stashes.StashSearchFragment_;
import de.vanmar.android.yarrn.stashes.StashesFragment;
import de.vanmar.android.yarrn.stashes.StashesFragment_;

/**
 * Created by Kolja on 28.01.14.
 */
@EBean
public class FragmentFactory {

    public ProjectFragment getProjectFragment() {
        return new ProjectFragment_();
    }

    public ProjectsFragment getProjectsFragment() {
        return new ProjectsFragment_();
    }

    public FavoritesFragment getFavoritesFragment() {
        return new FavoritesFragment_();
    }

    public PatternFragment getPatternFragment() {
        return new PatternFragment_();
    }

    public StashesFragment getStashesFragment() {
        return new StashesFragment_();
    }

    public QueuesFragment getQueuesFragment() {
        return new QueuesFragment_();
    }

    public QueuedProjectFragment getQueuedProjectFragment() {
        return new QueuedProjectFragment_();
    }

    public StashSearchFragment getStashSearchFragment() {
        return new StashSearchFragment_();
    }

    public StashFragment getStashFragment() {
        return new StashFragment_();
    }

    public SettingsFragment getSettingsFragment() {
        return new SettingsFragment_();
    }

    public ProjectSearchFragment getProjectSearchFragment() {
        return new ProjectSearchFragment_();
    }

    public PatternSearchFragment getPatternSearchFragment() {
        return new PatternSearchFragment_();
    }
}
