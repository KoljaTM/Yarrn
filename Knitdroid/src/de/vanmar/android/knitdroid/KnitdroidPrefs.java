package de.vanmar.android.knitdroid;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(Scope.APPLICATION_DEFAULT)
public interface KnitdroidPrefs {

	String username();

	String accessToken();

	String accessSecret();

	String requestToken();

}
