package us.dontcareabout.gwt.client.websocket;

public enum ReadyState {
	CONNECTING,
	OPEN,
	CLOSING,
	CLOSED,
	;

	public static ReadyState valueOf(int readyState) {
		switch(readyState) {
		case 0: return CONNECTING;
		case 1: return OPEN;
		case 2: return CLOSING;
		case 3: return CLOSED;
		default: return null;
		}
	}
}
