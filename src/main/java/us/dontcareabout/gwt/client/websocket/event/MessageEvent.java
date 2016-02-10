package us.dontcareabout.gwt.client.websocket.event;

import com.google.gwt.event.shared.GwtEvent;

public class MessageEvent extends GwtEvent<MessageHandler>{
	public static final GwtEvent.Type<MessageHandler> TYPE = new GwtEvent.Type<MessageHandler>();

	private String message;

	public MessageEvent(String msg){
		this.message = msg;
	}

	@Override
	public GwtEvent.Type<MessageHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MessageHandler handler) {
		handler.onMessage(this);
	}

	public String getMessage() {
		return message;
	}
}