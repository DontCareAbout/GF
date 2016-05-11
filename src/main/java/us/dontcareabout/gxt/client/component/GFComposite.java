package us.dontcareabout.gxt.client.component;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.Composite;

/**
 * 加強 GXT 的 {@link Composite}，
 * 目前主要功能是處理 {@link HandlerRegistration}。
 * 在 component 的狀態為 visible 時呼叫 {@link #enrollWhenVisible()}，
 * child class 在這個 method 中註記要處理哪些 {@link HandlerRegistration}，
 * 這些 {@link HandlerRegistration} 會在 invisible 時移除。
 */
public abstract class GFComposite extends Composite {
	private GroupingHandlerRegistration hrGroup = new GroupingHandlerRegistration();

	/**
	 * 使用 {@link #enrollHR(HandlerRegistration)} 註記要自動處理的 {@link HandlerRegistration}
	 */
	protected abstract void enrollWhenVisible();

	protected void enrollHR(HandlerRegistration hr) {
		hrGroup.add(hr);
	}

	@Override
	protected void onShow() {
		super.onShow();
		visibleProcedure();
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		visibleProcedure();
	}

	//這是為了解決在不同的 GXT container（還有不是 container 的 TabPanel）上
	//會有不同的 onLoad() / onShow() / onUnload() / onHide() 行為
	private void visibleProcedure() {
		//onLoad() 是一定會觸發，但是 onShow() 卻未必
		//另外 TabPanel / CardLayout 會先觸發 onShow() 才觸發 onLoad()
		//所以用這個檢查排除重複掛 handler 的問題
		if (hrGroup.getRegistrations().size() != 0) { return; }

		//主要是解決 TabPanel 會出現先觸發 onUnload() 然後再觸發 onShow() 的狀況
		//因此搭配 isVisible() 來克服
		//另一方面也能呼應 enrollWhenVisible() 的名稱
		if (!isVisible()) { return; }

		enrollWhenVisible();
	}

	//removeHandler() 就不用管那麼多了... [茶]
	@Override
	protected void onUnload() {
		super.onUnload();
		hrGroup.removeHandler();
	}

	@Override
	protected void onHide() {
		super.onHide();
		hrGroup.removeHandler();
	}
}
