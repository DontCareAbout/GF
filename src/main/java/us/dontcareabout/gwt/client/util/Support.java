package us.dontcareabout.gwt.client.util;

/**
 * 取得 browser 是否支援某功能的 util method 集散地。
 */
public class Support {
	public static native boolean hasFileApi() /*-{
		var input = document.createElement("INPUT");
		input.type = "file";
		return "files" in input;
	}-*/;

	public static native boolean hasXhrUploadProgress() /*-{
		var xhr = new XMLHttpRequest();
		return !! (xhr && ("upload" in xhr) && ("onprogress" in xhr.upload));
	}-*/;

	public static native boolean hasFormData() /*-{
		return !! window.FormData;
	}-*/;
}
