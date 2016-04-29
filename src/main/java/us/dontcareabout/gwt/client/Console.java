package us.dontcareabout.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

//TODO group()
//TODO profile()
//TODO time()

/**
 * <a href="https://developers.google.com/web/tools/chrome-devtools/debug/console/console-reference">
 * Chrome 版的 Console</a> Java wrapper
 */
public class Console {
	/** 使用 console.log() 印出 {@link String#valueOf(Object)} */
	public static native void log(Object object) /*-{
		console.log(
			@java.lang.String::valueOf(Ljava/lang/Object;)(object)
		);
	}-*/;

	/**
	 * 使用 console.log() 印出支援 format specifiers 的字串
	 * @param format 目前只支援「%s」的 format
	 */
	public static void log(String format, String... params) {
		formatPrint("log", format, transform(params));
	}

	/** 使用 console.error() 印出 {@link String#valueOf(Object)} */
	public static native void error(Object object) /*-{
		console.error(
			@java.lang.String::valueOf(Ljava/lang/Object;)(object)
		);
	}-*/;

	/**
	 * 使用 console.error() 印出支援 format specifiers 的字串
	 * @param format 目前只支援「%s」的 format
	 */
	public static void error(String format, String... params) {
		formatPrint("error", format, transform(params));
	}

	/** 使用 console.info() 印出 {@link String#valueOf(Object)} */
	public static native void info(Object object) /*-{
		console.info(
			@java.lang.String::valueOf(Ljava/lang/Object;)(object)
		);
	}-*/;

	/**
	 * 使用 console.info() 印出支援 format specifiers 的字串
	 * @param format 目前只支援「%s」的 format
	 */
	public static void info(String format, String... params) {
		formatPrint("info", format, transform(params));
	}

	/** 使用 console.warn() 印出 {@link String#valueOf(Object)} */
	public static native void warn(Object object) /*-{
		console.warn(
			@java.lang.String::valueOf(Ljava/lang/Object;)(object)
		);
	}-*/;

	/**
	 * 使用 console.warn() 印出支援 format specifiers 的字串
	 * @param format 目前只支援「%s」的 format
	 */
	public static void warn(String format, String... params) {
		formatPrint("warn", format, transform(params));
	}

	/**	印出 object 的內部結構 */
	//個人認為 console.log() 的效果比 console.dir() 好 ＝＝"
	public static native void inspect(Object object) /*-{
		console.log(object);
	}-*/;

	private static JsArrayString transform(String[] params) {
		JsArrayString paramArray = (JsArrayString) JavaScriptObject.createArray();

		for (String param : params) {
			paramArray.push(param);
		}

		return paramArray;
	}

	//找不到 Java varargs 轉成 JS varargs 的方法，只好用萬惡 eval 大法
	private static native void formatPrint(String type, String format, JsArrayString paramArray) /*-{
		var command = "console." + type + "(format";

		for (var key in paramArray) {
			command += ", \"" + paramArray[key] + "\"";
		}

		eval(command + ");");
	}-*/;
}
