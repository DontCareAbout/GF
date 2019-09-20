package us.dontcareabout.gwt.client.iCanUse;

public enum Feature {
	Canvas(com.google.gwt.canvas.client.Canvas.isSupported()),
	Chrome_Only(isChrome()),

	File_API(hasFileApi()),
	Form_Data(hasFormData()),

	Storage(com.google.gwt.storage.client.Storage.isSupported()),

	Video(com.google.gwt.media.client.Video.isSupported()),

	WebSocket(us.dontcareabout.gwt.client.websocket.WebSocket.isSupported()),

	XHR_Upload_Progress(hasXhrUploadProgress()),
	;

	public final boolean support;

	Feature(boolean support) {
		this.support = support;
	}

	//reference: http://stackoverflow.com/questions/4565112/javascript-how-to-find-out-if-the-user-browser-is-chrome
	private static native boolean isChrome() /*-{
		var isChromium = window.chrome,
			winNav = window.navigator,
			vendorName = winNav.vendor,
			isOpera = winNav.userAgent.indexOf("OPR") > -1,
			isIEedge = winNav.userAgent.indexOf("Edge") > -1,
			isIOSChrome = winNav.userAgent.match("CriOS");

		if(isIOSChrome){
			return true;
		} else if(isChromium !== null && isChromium !== undefined && vendorName === "Google Inc." && isOpera == false && isIEedge == false) {
			return true;
		} else {
			return false;
		}
	}-*/;

	private static native boolean hasFileApi() /*-{
		var input = document.createElement("INPUT");
		input.type = "file";
		return "files" in input;
	}-*/;

	private static native boolean hasFormData() /*-{
		return !! window.FormData;
	}-*/;

	private static native boolean hasXhrUploadProgress() /*-{
		var xhr = new XMLHttpRequest();
		return !! (xhr && ("upload" in xhr) && ("onprogress" in xhr.upload));
	}-*/;
}
