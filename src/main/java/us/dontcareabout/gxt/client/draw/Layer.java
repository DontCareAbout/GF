package us.dontcareabout.gxt.client.draw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.chart.client.draw.DrawComponent;
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

/**
 * 可以一次調整一組 {@link LSprite}（實際上還是 {@link Sprite}）的 X、Y、ZIndex 的 class。
 * <p>
 * caller 透過 {@link #add(LSprite)} 將 sprite 變成 Layer 的 member sprite，
 * 然後用 {@link #deploy(DrawComponent)} 將 member sprite 實際加到 {@link DrawComponent} 上。
 * 此後，只要呼叫對應 setter（例如 {@link #setX(double)}），
 * 就會將所有 member sprite 作對應的調整。
 * <p>
 * <b>注意：{@link Layer} 不負責處理 redraw 時機</b>
 */
public class Layer
	implements HasSpriteOutHandlers, HasSpriteOverHandlers, HasSpriteSelectionHandlers, HasSpriteUpHandlers {

	private HandlerManager handlerManager;
	private boolean stopPropagation = true;

	private ArrayList<LSprite> members = new ArrayList<>();

	private double x;
	private double y;
	private int zIndex = 0;

	private DrawComponent drawComponent;

	public Layer() {
		this(0, 0);
	}

	public Layer(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void add(LSprite sprite) {
		members.add(sprite);
		sprite.setLayer(this);
		sprite.setLX(sprite.getLX());
		sprite.setLY(sprite.getLY());
		sprite.setLZIndex(sprite.getLZIndex());
	}

	/**
	 * 將指定的 sprite 從 {@link Layer} 上移除，同時也會自 {@link DrawComponent} 上移除。
	 * <p>
	 * 若指定的 sprite 是 member 的 member，
	 * remove() 依然可以將其移除。
	 *
	 * @see #undeploy()
	 */
	public void remove(LSprite target) {
		for (LSprite sprite : members) {
			if (sprite == target) {
				if (sprite instanceof LayerSprite) {
					((Layer)sprite).undeploy();
					members.remove(sprite);
				} else {
					members.remove(sprite);
					drawComponent.remove((Sprite)sprite);
				}

				return;
			}
		}

		//第一層找不到，就看看有沒有 LayerSprite 然後往上找
		for (LSprite sprite : members) {
			if (sprite instanceof LayerSprite) {
				((Layer)sprite).remove(target);
			}
		}
	}

	/**
	 * 判斷 member sprite 中是否包含指定的 sprite。
	 * <p>
	 * 若指定的 sprite 是 member 的 member，
	 * 也會回傳 true。
	 */
	public boolean hasSprite(Sprite target) {
		//無法預期 DFS / BFS 哪個比較有效率，所以選擇程式碼比較簡單的 DFS
		for (LSprite sprite : members) {
			if (sprite instanceof LayerSprite) {
				if (((LayerSprite)sprite).hasSprite(target)) {
					return true;
				}
			} else {
				if (sprite == target) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * <b>注意：</b>如果會用到 {@link #addSpriteSelectionHandler(SpriteSelectionHandler)} 等功能，
	 * 請改用 {@link LayerContainer#addLayer(Layer)} 來達到 deploy 的效果。
	 */
	public void deploy(DrawComponent component) {
		this.drawComponent = component;

		for (LSprite sprite : members) {
			if (sprite instanceof LayerSprite) {
				Layer layer = (Layer) sprite;
				layer.deploy(component);
				continue;
			}

			Sprite s = (Sprite)sprite;

			//避免 caller 重複呼叫，所以用 getComponent() 是否為 null 來判斷是否加過了
			if (s.getComponent() != null) { continue; }

			component.addSprite(s);
		}
	}

	/**
	 * 將 {@link Layer}（包含所擁有的 {@link LSprite}）從 {@link DrawComponent} 上移除。
	 * <p>
	 * <b>undeploy() 並不會影響 {@link Layer} 原本的 {@link LSprite} 結構</b>。
	 * 如果要將 {@link LSprite} 從 {@link Layer} 中移除，請使用 {@link #remove(LSprite)}。
	 */
	public void undeploy() {
		for (LSprite sprite : members) {
			if (sprite instanceof LayerSprite) {
				((Layer)sprite).undeploy();
			} else {
				drawComponent.remove((Sprite)sprite);
			}
		}
	}

	public List<LSprite> getMembers() {
		return Collections.unmodifiableList(members);
	}

	/**
	 * 設定所有 member（包含 member 的 member）的 cursor。
	 */
	public void setMemberCursor(Cursor cursor) {
		for (LSprite member : members) {
			if (member instanceof Layer) {
				((Layer) member).setMemberCursor(cursor);
			} else {
				member.setCursor(cursor);
			}
		}
	}

	public void setX(double value) {
		if (value == x) { return; }

		x = value;

		for (LSprite sprite : members) {
			sprite.setLX(sprite.getLX());
		}
	}

	public void setY(double value) {
		if (value == y) { return; }

		y = value;

		for (LSprite sprite : members) {
			sprite.setLY(sprite.getLY());
		}
	}

	public void setZIndex(int value) {
		if (value == zIndex) { return; }

		zIndex = value;

		for (LSprite sprite : members) {
			sprite.setLZIndex(sprite.getLZIndex());
		}
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getZIndex() {
		return zIndex;
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

	/**
	 * <b>注意：</b>需搭配 {@link LayerContainer#addLayer(Layer)} 使用。
	 */
	@Override
	public HandlerRegistration addSpriteOutHandler(SpriteOutHandler handler) {
		return ensureHandler().addHandler(SpriteOutEvent.getType(), handler);
	}

	/**
	 * <b>注意：</b>需搭配 {@link LayerContainer#addLayer(Layer)} 使用。
	 */
	@Override
	public HandlerRegistration addSpriteOverHandler(SpriteOverHandler handler) {
		return ensureHandler().addHandler(SpriteOverEvent.getType(), handler);
	}

	/**
	 * <b>注意：</b>需搭配 {@link LayerContainer#addLayer(Layer)} 使用。
	 */
	@Override
	public HandlerRegistration addSpriteSelectionHandler(SpriteSelectionHandler handler) {
		return ensureHandler().addHandler(SpriteSelectionEvent.getType(), handler);
	}

	/**
	 * <b>注意：</b>需搭配 {@link LayerContainer#addLayer(Layer)} 使用。
	 */
	@Override
	public HandlerRegistration addSpriteUpHandler(SpriteUpHandler handler) {
		return ensureHandler().addHandler(SpriteUpEvent.getType(), handler);
	}

	//比照 DrawComponent 用 protected
	//其實 GWT / GXT 對 HandlerManager 的建立寫法有點混亂... ＝＝"
	//（參見神奇的 ComponentHelper.ensureHandlers()）
	protected HandlerManager ensureHandler() {
		if (handlerManager == null) {
			handlerManager = new HandlerManager(this);
		}

		return handlerManager;
	}

	//理論上只有 LayerContainer 會呼叫，所以用 default access level
	void setContainer(LayerContainer container) {
		this.drawComponent = container;
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

		for (LSprite sprite : members) {
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
