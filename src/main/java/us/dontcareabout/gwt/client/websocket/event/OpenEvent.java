package us.dontcareabout.gwt.client.websocket.event;

import com.google.gwt.event.shared.GwtEvent;

public class OpenEvent extends GwtEvent<OpenHandler>{
	public static final GwtEvent.Type<OpenHandler> TYPE = new GwtEvent.Type<OpenHandler>();

	@Override
	public GwtEvent.Type<OpenHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OpenHandler handler) {
		handler.onOpen(this);
	}
}
