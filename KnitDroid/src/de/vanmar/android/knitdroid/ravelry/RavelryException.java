package de.vanmar.android.knitdroid.ravelry;

public class RavelryException extends Exception {
	private final int statusCode;

	public RavelryException(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
