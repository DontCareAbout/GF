package us.dontcareabout.gwt.client.google;

import java.util.Date;

import com.google.common.base.Strings;
import com.google.gwt.core.client.JavaScriptObject;

@Deprecated
public class SheetEntry extends JavaScriptObject {
	protected SheetEntry() {}

	/**
	 * @return 該筆 entry 在 Sheet 的 row index（起始值為 2）
	 */
	public final Integer getIndex() {
		return intField("GF_INDEX");
	}

	protected final native String stringField(String name) /*-{
		//預防開發人員給錯 name、或是 sheet 上沒有對應 name
		//由於 Google Sheet 對於沒輸入的 cell 就是回傳空字串
		//所以這邊也比照辦理回傳空字串
		return this["gsx$" + name] == null ? "" : this["gsx$" + name].$t;
	}-*/;

	protected final Integer intField(String name) {
		String value = stringField(name);
		return Strings.isNullOrEmpty(value) ? 0 : Integer.valueOf(value);
	}

	protected final Long longField(String name) {
		String value = stringField(name);
		return Strings.isNullOrEmpty(value) ? 0 : Long.valueOf(value);
	}

	protected final Double doubleField(String name) {
		String value = stringField(name);
		return Strings.isNullOrEmpty(value) ? 0 : Double.valueOf(value);
	}

	protected final Boolean booleanField(String name) {
		String value = stringField(name);
		return Strings.isNullOrEmpty(value) ? false : Boolean.valueOf(value);
	}

	@SuppressWarnings("deprecation")
	protected final Date dateField(String name) {
		String value = stringField(name);
		return Strings.isNullOrEmpty(value) ? null : new Date(value);
	}

	//只給 Sheet 呼叫
	final native void setIndex(int i) /*-{
		this["gsx$GF_INDEX"] = [];
		this["gsx$GF_INDEX"].$t = i;
	}-*/;
}
