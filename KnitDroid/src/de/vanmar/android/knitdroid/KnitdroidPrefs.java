package de.vanmar.android.knitdroid;

import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(Scope.APPLICATION_DEFAULT)
public interface KnitdroidPrefs {

    String accessSecret();

    String accessToken();

    String requestToken();

    String username();

}
