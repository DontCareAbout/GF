package us.dontcareabout.gwt.client.data;

import com.google.gwt.core.client.GWT;

@FunctionalInterface
public interface Callback<T> {
	void onSuccess(T result);

	default void onError(Throwable exception) {
		GWT.log("Callback Error", exception);
	}
}