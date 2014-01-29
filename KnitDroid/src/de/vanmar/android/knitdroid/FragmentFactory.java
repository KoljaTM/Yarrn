package de.vanmar.android.knitdroid;

import org.androidannotations.annotations.EBean;

import de.vanmar.android.knitdroid.favorites.FavoritesFragment;
import de.vanmar.android.knitdroid.favorites.FavoritesFragment_;
import de.vanmar.android.knitdroid.patterns.PatternFragment;
import de.vanmar.android.knitdroid.patterns.PatternFragment_;
import de.vanmar.android.knitdroid.projects.ProjectFragment;
import de.vanmar.android.knitdroid.projects.ProjectFragment_;
import de.vanmar.android.knitdroid.projects.ProjectsFragment;
import de.vanmar.android.knitdroid.projects.ProjectsFragment_;

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
}
