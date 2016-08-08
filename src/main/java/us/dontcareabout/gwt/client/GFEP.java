package us.dontcareabout.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window.Location;

/**
 * GF 版的 {@link EntryPoint}，提供下列功能：
 * <ul>
 * 	<li>偵測語系，自動跳轉至該語系的 GWT 格式 URL</li>
 * 	<li>
 * 		定義 version。
 * 		在 browser 的 JS console 輸入 <code>version</code>，
 * 		可得知程式定義的版本資訊
 * 	</li>
 * </ul>
 *
 * 原本寫在 {@link #onModuleLoad()} 的程式，請轉移到 {@link #start()}。
 */
public abstract class GFEP implements EntryPoint {
	private final String defaultLocale;

	//GWT 要求 EntryPoint 一定要有 default constructor，所以只能用這種方式注入
	public GFEP() {
		this.defaultLocale = defaultLocale();
		setVersion(version());
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

		start();
	}

	/**
	 * @return 是否有要求跳轉頁面
	 */
	private boolean redirectByLocale() {
		//query string 已經有指定就好啦
		if (Location.getParameter("locale") != null) { return false; }

		//預防萬一，轉成 GWT 要求的格式
		String browser = language().replace("-", "_");

		if (browser.equals(defaultLocale)) { return false; }

		UrlBuilder builder = Location.createUrlBuilder().setParameter("locale", browser);
		Location.assign(builder.buildString());
		return true;
	}

	private static native String language() /*-{
		return navigator.languages ?
			navigator.languages[0] :
			(navigator.language || navigator.userLanguage);
	}-*/;

	private static native void setVersion(String version) /*-{
		$wnd.version = version;
	}-*/;
}
