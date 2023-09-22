package us.dontcareabout.gwt.client.data;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class DataEvent<T, H extends EventHandler> extends GwtEvent<H> {
	public final T data;

	public DataEvent(T data) { this.data = data; }
}
