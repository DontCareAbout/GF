package us.dontcareabout.gwt.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window.Location;
import com.sencha.gxt.core.client.GXT;

import us.dontcareabout.gwt.client.iCanUse.Feature;

/**
 * GF 版的 {@link EntryPoint}，提供下列功能：
 * <ul>
 * 	<li>偵測語系，自動跳轉至該語系的 GWT 格式 URL</li>
 * 	<li>
 * 		定義 version。
 * 		在 browser 的 JS console 輸入 <code>version</code> 或 <code>fullVersion</code>，
 * 		可得知程式定義的版本資訊
 * 	</li>
 * 	<li>偵測 browser 是否支援指定 {@link Feature}</li>
 * </ul>
 *
 * 原本寫在 {@link #onModuleLoad()} 的程式，請轉移到 {@link #start()}。
 * <p>
 * 要使用「偵測 browser 是否支援指定 {@link Feature}」功能，
 * 請在 constructor 中呼叫 {@link #needFeature(Feature...)}，
 * 則在 {@link #onModuleLoad()} 時會檢測是否全部 {@link Feature} 是否都支援。
 * 如果沒有全部都支援會呼叫 {@link #featureFail()}，
 * 可透過 {@link #getNotSupport()} 取得不支援的 {@link Feature} 清單；
 * 成功才會呼叫 {@link #start()}。
 */
public abstract class GFEP implements EntryPoint {
	private final String defaultLocale;
	private HashSet<Feature> needSupport = new HashSet<>();
	private ArrayList<Feature> notSupport = new ArrayList<>();

	//GWT 要求 EntryPoint 一定要有 default constructor，所以只能用這種方式注入
	public GFEP() {
		this.defaultLocale = defaultLocale();
		setVersion(version());
		setFullVersion(fullVersion());
	}

	/** 定義版本資訊 */
	protected abstract String version();

	/**
	 * 定義預設的語系。
	 * 如果瀏覽器偵測的語系與預設語系相同，則不會重新指定 locale 參數（也就不會跳轉頁面）。
	 * <p>
	 * <b>注意</b>：language code 與 territory code 之間是「_」而非「-」。
	 */
	protected abstract String defaultLocale();

	/** 等同於 {@link EntryPoint#onModuleLoad()} */
	protected abstract void start();

	@Override
	public final void onModuleLoad() {
		//如果有跳轉頁面，就不做 start()，以減少畫面閃爍機率
		if (redirectByLocale()) { return; }
		if (!checkFeature()) {
			featureFail();
			return;
		}

		start();
	}

	protected void needFeature(Feature... features) {
		for (Feature f : features) {
			needSupport.add(f);
		}
	}

	/**
	 * 預設的失敗處理程序
	 */
	protected void featureFail() {
		for (Feature f : notSupport) {
			Console.log(f.name());
		}
	}

	protected List<Feature> getNotSupport() {
		return notSupport;
	}

	/**
	 * @return 是否有要求跳轉頁面
	 */
	private boolean redirectByLocale() {
		//query string 已經有指定就好啦
		if (Location.getParameter("locale") != null) { return false; }

		String browser = language();

		if (browser == null) {	//browser 不支援就會得到 null
			browser = defaultLocale;
		} else {
			//預防萬一，轉成 GWT 要求的格式
			browser = browser.replace("-", "_");
		}

		if (browser.equals(defaultLocale)) { return false; }

		UrlBuilder builder = Location.createUrlBuilder().setParameter("locale", browser);
		Location.assign(builder.buildString());
		return true;
	}

	private boolean checkFeature() {
		for (Feature f : needSupport) {
			if (!f.support) { notSupport.add(f); }
		}

		return notSupport.isEmpty();
	}

	private String fullVersion() {
		return "GWT : " + GWT.getVersion() + "\n"
			+ "GXT : " + GXT.getVersion().getRelease() + "\n"
			+ "GF  : " + GF.getVersion() + "\n"
			+ "app : " + version();
	}

	private static native String language() /*-{
		return navigator.languages ?
			navigator.languages[0] :
			(navigator.language || navigator.userLanguage);
	}-*/;

	private static native void setFullVersion(String version) /*-{
		$wnd.fullVersion = version;
	}-*/;

	private static native void setVersion(String version) /*-{
		$wnd.version = version;
	}-*/;
}
