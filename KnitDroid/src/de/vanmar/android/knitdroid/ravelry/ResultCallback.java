package de.vanmar.android.knitdroid.ravelry;

/** Interface for callbacks to executa after service execution */
public interface ResultCallback<T> {

	void onSuccess(T result);

	void onFailure(Exception exception);
}
