package de.vanmar.android.yarrn.ravelry;

public class RavelryException extends Exception {
    private final int statusCode;

    @Override
    public String toString() {
        return "Unexpected Status code: " + statusCode;
    }

    public RavelryException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
