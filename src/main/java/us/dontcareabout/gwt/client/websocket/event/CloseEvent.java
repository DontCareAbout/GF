package us.dontcareabout.gwt.client.websocket.event;

import com.google.gwt.event.shared.GwtEvent;

public class CloseEvent extends GwtEvent<CloseHandler>{
	public static final GwtEvent.Type<CloseHandler> TYPE = new GwtEvent.Type<CloseHandler>();

	@Override
	public GwtEvent.Type<CloseHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CloseHandler handler) {
		handler.onClose(this);
	}
}
