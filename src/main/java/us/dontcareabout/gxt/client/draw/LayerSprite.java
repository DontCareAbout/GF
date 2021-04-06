package us.dontcareabout.gxt.client.draw;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.DrawComponent;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent.HasSpriteOutHandlers;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent.SpriteOutHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent.HasSpriteOverHandlers;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent.SpriteOverHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.HasSpriteSelectionHandlers;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.SpriteSelectionHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteUpEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteUpEvent.HasSpriteUpHandlers;
import com.sencha.gxt.chart.client.draw.sprite.SpriteUpEvent.SpriteUpHandler;

import us.dontcareabout.gxt.client.draw.container.SimpleLayerContainer;

/**
 * 特化版的 {@link Layer}。
 * 使其可以如 {@link LTextSprite} 等 sprite 一樣加在 {@link Layer} 上。
 * <p>
 * 有別於單純作為 sprite 集合的 {@link Layer}，
 * LayerSprite 內建一個 {@link LRectangleSprite} 作為 background，
 * 因此有實際的大小與可視範圍（但是並沒有防止 member sprite 超出範圍 XD）。
 * caller 可在 {@link DrawComponent} onResize() 時
 * 呼叫 {@link #resize(double, double)} 來調整 LayerSprite 的大小，
 * 並 override {@link #adjustMember()} 以實作各 member sprite 的對應調整。
 * <p>
 * 如果要調整 background 的樣式，請使用 setBg*() 系列 method，
 * 例如 {@link #setBgRadius(double)}。
 * <p>
 * 為了預防沒有設定 background 底色而導致 {@link SpriteOverEvent} / {@link SpriteOutEvent} 的觸發狀況不如預期，
 * 因此 background 預設 opacity 為 0（完全透明）、顏色為 {@link RGB#RED}。
 * 這不會影響 {@link #setBgColor(Color)} / {@link #setBgOpacity(double)} 等 method 的行為，
 * 也就是說，在還沒有設定過 background 底色的預設狀況下，{@link #getBgColor()} 的值會是 {@link Color#NONE}。
 */
public class LayerSprite extends Layer
	implements LSprite, IsWidget, HasSpriteOutHandlers, HasSpriteOverHandlers, HasSpriteSelectionHandlers, HasSpriteUpHandlers {

	private HandlerManager handlerManager;
	private boolean stopPropagation = true;

	private Parameter parameter = new Parameter();
	private Layer layer;
	private LRectangleSprite bg = new LRectangleSprite();
	private Cursor cursor;

	/**
	 * 利用這個 flag 來紀錄底色是否為 {@link Color#NONE}。
	 * 若是 true，則 {@link #setBgOpacity(double)} / {@link #getBgOpacity()}
	 * 實際對應的是 {@link #normalBgOpacity}，而非 bg 的值。
	 */
	private boolean isBgColorNone;
	private double normalBgOpacity;

	/** {@link #asWidget()} 必須回傳同一個 instance，所以只好開成 field */
	private SimpleLayerContainer widget;

	public LayerSprite() {
		add(bg);

		//其實什麼顏色都可以，用（這麼醜的）紅色確保不會忘記他的存在 XD
		bg.setFill(RGB.RED);
		setBgColor(Color.NONE);
	}

	public boolean isStopPropagation() {
		return stopPropagation;
	}

	/**
	 * 設定 event 是否不交由 member sprite 中的 {@link LayerSprite} 處理。
	 * 如果設定為 true 表示不會，預設值為 true。
	 * <p>
	 * <b>注意</b>：如果 Layer 沒有掛載 handler，則會忽略此設定值。
	 *
	 * @see LayerContainer
	 */
	public void setStopPropagation(boolean stopPropagation) {
		this.stopPropagation = stopPropagation;
	}

	public final void resize(double width, double height) {
		if (width < 0 || height < 0) { return; }

		//如果 widget 的大小跟要調整的大小一樣，就不用調整了
		//widget.setPixelSize() 最終會再次呼叫 LayerSprite.resize()
		//這樣也可以避免潛在的效能問題
		if (widget != null && (widget.getOffsetWidth() != width || widget.getOffsetHeight() != height)) {
			widget.setPixelSize((int)width, (int)height);
			return;
			//不用作後面調整 bg、adjustMember() 的事情
			//因為 widget.setPixelSize() 裡頭會再次呼叫 LayerSprite.resize()
			//那時候再作就好
		}

		bg.setWidth(width);
		bg.setHeight(height);
		adjustMember();
	}

	public double getWidth() {
		return bg.getWidth();
	}

	public double getHeight() {
		return bg.getHeight();
	}

	public void setBgColor(Color color) {
		if (Color.NONE.equals(color)) {
			//不是真的設定顏色，只是讓 bg 透明度為 0（等於看不見）
			isBgColorNone = true;
			normalBgOpacity = bg.getOpacity();
			bg.setOpacity(0);
			return;
		}

		isBgColorNone = false;
		bg.setFill(color);
		bg.setOpacity(normalBgOpacity);
	}

	public Color getBgColor() {
		return isBgColorNone ? Color.NONE : bg.getFill();
	}

	public void setBgOpacity(double opacity) {
		if (isBgColorNone) {
			normalBgOpacity = opacity;
			return;
		}

		bg.setOpacity(opacity);
	}

	public double getBgOpacity() {
		return isBgColorNone ? normalBgOpacity : bg.getOpacity();
	}

	public void setBgRadius(double radius) {
		bg.setRadius(radius);
	}

	public double getBgRadius() {
		return bg.getRadius();
	}

	@Override
	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	@Override
	public void setLX(double value) {
		parameter.x = value;
		parameter.lock = false;
		setX((layer == null ? 0 : layer.getX()) + parameter.x);
		parameter.lock = true;
	}

	@Override
	public void setLY(double value) {
		parameter.y = value;
		parameter.lock = false;
		setY((layer == null ? 0 : layer.getY()) + parameter.y);
		parameter.lock = true;
	}

	@Override
	public void setLZIndex(int value) {
		parameter.zIndex = value;
		parameter.lock = false;
		setZIndex((layer == null ? 0 : layer.getZIndex()) + parameter.zIndex);
		parameter.lock = true;
	}

	@Override
	public double getLX() {
		return parameter.x;
	}

	@Override
	public double getLY() {
		return parameter.y;
	}

	@Override
	public int getLZIndex() {
		return parameter.zIndex;
	}

	@Override
	public void setX(double value) {
		//不會有 parent class 呼叫 setX() 的問題，所以只判斷 lock
		if (parameter.lock) {
			throw new UnsupportedOperationException("Use setLX() instead.");
		}

		super.setX(value);
	}

	@Override
	public void setY(double value) {
		if (parameter.lock) {
			throw new UnsupportedOperationException("Use setLY() instead.");
		}

		super.setY(value);
	}

	@Override
	public void setZIndex(int value) {
		if (parameter.lock) {
			throw new UnsupportedOperationException("Use setLZIndex() instead.");
		}

		super.setZIndex(value);
	}

	@Override
	public boolean isHidden() {
		//不管 member，只以 background 的 hidden 為基準
		return bg.isHidden();
	}

	@Override
	public void setCursor(Cursor cursor) {
		this.cursor = cursor;

		if (bg.getSurface() != null) {
			bg.setCursor(cursor == null ? (String)null : cursor.getName());
		}
	}

	@Override
	public Cursor getCursor() {
		return cursor;
	}

	@Override
	public Widget asWidget() {
		if (widget == null) {
			widget = new SimpleLayerContainer(this);
		}

		return widget;
	}

	/**
	 * <b>注意：</b>需搭配 {@link LayerContainer#addLayer(LayerSprite)} 使用。
	 */
	@Override
	public HandlerRegistration addSpriteOutHandler(SpriteOutHandler handler) {
		return ensureHandler().addHandler(SpriteOutEvent.getType(), handler);
	}

	/**
	 * <b>注意：</b>需搭配 {@link LayerContainer#addLayer(LayerSprite)} 使用。
	 */
	@Override
	public HandlerRegistration addSpriteOverHandler(SpriteOverHandler handler) {
		return ensureHandler().addHandler(SpriteOverEvent.getType(), handler);
	}

	/**
	 * <b>注意：</b>需搭配 {@link LayerContainer#addLayer(LayerSprite)} 使用。
	 */
	@Override
	public HandlerRegistration addSpriteSelectionHandler(SpriteSelectionHandler handler) {
		return ensureHandler().addHandler(SpriteSelectionEvent.getType(), handler);
	}

	/**
	 * <b>注意：</b>需搭配 {@link LayerContainer#addLayer(LayerSprite)} 使用。
	 */
	@Override
	public HandlerRegistration addSpriteUpHandler(SpriteUpHandler handler) {
		return ensureHandler().addHandler(SpriteUpEvent.getType(), handler);
	}

	/**
	 * 在 {@link #resize(double, double)} 時提供 child class 調整 member sprite 的時機點。
	 */
	protected void adjustMember() {}

	//比照 DrawComponent 用 protected
	//其實 GWT / GXT 對 HandlerManager 的建立寫法有點混亂... ＝＝"
	//（參見神奇的 ComponentHelper.ensureHandlers()）
	protected HandlerManager ensureHandler() {
		if (handlerManager == null) {
			handlerManager = new HandlerManager(this);
		}

		return handlerManager;
	}

	/**
	 * @return 是否有觸發任何 handler（包含 member），若有觸發回傳 true。
	 */
	//理論上只有 LayerContainer 會呼叫，所以用 default access level
	boolean handleEvent(GwtEvent<?> event, Sprite source) {
		boolean flag = false;

		if (handlerManager != null && handlerManager.getHandlerCount(event.getAssociatedType()) > 0) {
			handlerManager.fireEvent(event);
			flag = true;
		}

		if (flag && stopPropagation) { return flag; }

		for (LSprite sprite : getMembers()) {
			if (sprite instanceof LayerSprite) {
				LayerSprite ls = (LayerSprite)sprite;

				if (ls.hasSprite(source)) {
					flag = flag | ls.handleEvent(event, source);
					break;	//理論上一個 sprite 只會出現在一個 layer 上
				}
			}
		}

		return flag;
	}
}
