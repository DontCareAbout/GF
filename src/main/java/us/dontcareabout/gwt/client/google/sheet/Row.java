package us.dontcareabout.gwt.client.google.sheet;

import java.util.Date;

import com.google.common.base.Strings;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * 實做注意事項：
 * <ul>
 * 	<li>（JSNI）必須宣告成 final class、或是每個 method 都宣告成 final</li>
 * 	<li>（JSNI）需要有一個 protected 的 empty constructor</li>
 * 	<li>*Field() 傳入的 column name 是 case sensitive</li>
 * </ul>
 */
public class Row extends JavaScriptObject {
	protected Row() {}

	/**
	 * @return 此 instance 在原始回傳資料（values）的 index + 1 值。
	 * 	若 GET 時沒有指定 cell 區間，這會代表此 instance 在 sheet 中 的 row index。
	 */
	public final Integer getIndex() {
		return intField("GF_INDEX");
	}

	protected final native String stringField(String name) /*-{
		//預防開發人員給錯 name、或是 sheet 上沒有對應 name
		//v3 版對於沒輸入的 cell 就是回傳空字串
		//所以這邊還是延續 v3 的格式回傳空字串
		return this[name] == null ? "" : this[name];
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

	// ==== 只給 Sheet 呼叫 ==== //
	final native void set(String name, String value) /*-{
		this[name] = value;
	}-*/;

	final void setIndex(int i) {
		set("GF_INDEX", "" + i);
	}
}
