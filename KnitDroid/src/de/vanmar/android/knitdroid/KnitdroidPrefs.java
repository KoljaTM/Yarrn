package de.vanmar.android.knitdroid;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(Scope.APPLICATION_DEFAULT)
public interface KnitdroidPrefs {

	String accessSecret();

	String accessToken();

	String requestToken();

	String username();

}
