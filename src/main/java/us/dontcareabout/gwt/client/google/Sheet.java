package us.dontcareabout.gwt.client.google;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

public class Sheet<T extends SheetEntry> {
	//因為 JavaScriptObject subclass 不能有 field
	//又希望 entry 可以只 build 一次
	//所以另外包了一個標準 JavaScriptObject 的 instance 作 delegate
	private SheetJS<T> gsjs;
	private ArrayList<T> entry;

	public Sheet(String json) {
		gsjs = JsonUtils.safeEval(json);
	}

	public Date getUpdated() {
		return gsjs.getUpdated();
	}

	public ArrayList<T> getEntry() {
		if (entry == null) {
			entry = new ArrayList<>();
			JsArray<T> entryJS = gsjs.getEntry();
			for (int i = 0; i < entryJS.length(); i++) {
				T t = entryJS.get(i);
				t.setIndex(i);
				entry.add(t);
			}
		}

		return entry;
	}

	static final class SheetJS<E extends SheetEntry> extends JavaScriptObject {
		protected SheetJS() {}

		Date getUpdated() {
			return DateTimeFormat.getFormat(PredefinedFormat.ISO_8601).parse(getUpdatedString());
		}

		private native String getUpdatedString() /*-{
			return this.feed.updated.$t;
		}-*/;

		native JsArray<E> getEntry() /*-{
			return this.feed.entry;
		}-*/;
	}
}