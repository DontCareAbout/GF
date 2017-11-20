package us.dontcareabout.gxt.client.draw;

import java.util.ArrayList;

import com.sencha.gxt.chart.client.draw.DrawComponent;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;

/**
 * 可以一次調整一組 {@link LSprite}（實際上還是 {@link Sprite}）的 X、Y、ZIndex 的 class。
 * <p>
 * caller 透過 {@link #add(LSprite)} 將 sprite 納入此 Layer 的管轄範圍，
 * 然後用 {@link #deploy(DrawComponent)} 將此 Layer 所管轄的 sprite
 * 實際加到 {@link DrawComponent} 上。
 * 此後，只要呼叫對應 setter（例如 {@link #setX(double)}），
 * 就會將所管轄的 sprite 作對應的調整。
 * <p>
 * <b>注意：{@link Layer} 不負責處理 redraw 時機</b>
 */
public class Layer {
	private ArrayList<LSprite> sprites = new ArrayList<>();

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
		sprites.add(sprite);
		sprite.setLayer(this);
		sprite.setLX(sprite.getLX());
		sprite.setLY(sprite.getLY());
		sprite.setLZIndex(sprite.getLZIndex());
	}

	/**
	 * 將指定的 sprite 從 {@link Layer} 上移除，同時也會自 {@link DrawComponent} 上移除。
	 * <p>
	 * 若指定的 sprite 是在 {@link LayerSprite} 之上（或是 {@link LayerSprite} 上的 {@link LayerSprite}），
	 * remove() 依然可以將其移除。
	 *
	 * @see #undeploy()
	 */
	public void remove(LSprite target) {
		for (LSprite sprite : sprites) {
			if (sprite == target) {
				if (sprite instanceof LayerSprite) {
					((Layer)sprite).undeploy();
					sprites.remove(sprite);
				} else {
					sprites.remove(sprite);
					drawComponent.remove((Sprite)sprite);
				}

				return;
			}
		}

		//第一層找不到，就看看有沒有 LayerSprite 然後往上找
		for (LSprite sprite : sprites) {
			if (sprite instanceof LayerSprite) {
				((Layer)sprite).remove(target);
			}
		}
	}

	public void deploy(DrawComponent component) {
		this.drawComponent = component;

		for (LSprite sprite : sprites) {
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
		for (LSprite sprite : sprites) {
			if (sprite instanceof LayerSprite) {
				((Layer)sprite).undeploy();
			} else {
				drawComponent.remove((Sprite)sprite);
			}
		}
	}

	public void setX(double value) {
		if (value == x) { return; }

		x = value;

		for (LSprite sprite : sprites) {
			sprite.setLX(sprite.getLX());
		}
	}

	public void setY(double value) {
		if (value == y) { return; }

		y = value;

		for (LSprite sprite : sprites) {
			sprite.setLY(sprite.getLY());
		}
	}

	public void setZIndex(int value) {
		if (value == zIndex) { return; }

		zIndex = value;

		for (LSprite sprite : sprites) {
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
}
