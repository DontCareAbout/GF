package us.dontcareabout.gxt.client.draw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.sencha.gxt.chart.client.draw.DrawComponent;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;

/**
 * 可以一次調整一組 {@link LSprite}（實際上還是 {@link Sprite}）的 X、Y、ZIndex 的 class。
 * <p>
 * caller 透過 {@link #add(LSprite)} 將 sprite 變成 Layer 的 member sprite，
 * 然後用 {@link #deploy(DrawComponent)} 將 member sprite 實際加到 {@link DrawComponent} 上。
 * 此後，只要呼叫對應 setter（例如 {@link #setX(double)}），
 * 就會將所有 member sprite 作對應的調整。
 * 如果已經 deploy 後又增加 {@link LSprite}，可使用 {@link #redeploy()} 來重新 deploy。
 */
public class Layer {
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
	 * <p>
	 * <b>注意：</b>目前 GXT {@link DrawComponent} 對同一個 {@link Sprite} 作 remove() 再 addSprite()，
	 * 該 {@link Sprite} 並不會出現，這是 GXT 原生的行為 / bug，SSCCE 參見 GF-Test 的 Issue_31。
	 * 這代表目前的版本中，若作過 {@link #remove(LSprite)}，則 {@link #add(LSprite)} 與 {@link #redeploy()} 並不會起作用。
	 *
	 * @see #undeploy()
	 */
	public void remove(LSprite target) {
		if (drawComponent == null) { return; }

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
	 * 移除所有 member（aka 對所有 member 作 {@link #remove(LSprite)}）
	 */
	public void clear() {
		while(members.size() != 0) {
			remove(members.get(0));
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

	public void deploy(DrawComponent component) {
		Preconditions.checkNotNull(component);
		this.drawComponent = component;
		redeploy();
	}

	public void redeploy() {
		if (drawComponent == null) {
			throw new UnsupportedOperationException("Haven't been deployed yet");
		}

		for (LSprite sprite : members) {
			if (sprite instanceof LayerSprite) {
				Layer layer = (Layer) sprite;
				layer.deploy(drawComponent);
				continue;
			}

			Sprite s = (Sprite)sprite;

			//避免 caller 重複呼叫，所以用 getComponent() 是否為 null 來判斷是否加過了
			if (s.getComponent() != null) { continue; }

			drawComponent.addSprite(s);
		}
	}

	/**
	 * 使所屬的 {@link DrawComponent} 作 {@link DrawComponent#redrawSurface()}
	 */
	public void redraw() {
		redraw(false);
	}

	/**
	 * 使所屬的 {@link DrawComponent} 依傳入參數作
	 * {@link DrawComponent#redrawSurface()} 或 {@link DrawComponent#redrawSurfaceForced()}。
	 */
	public void redraw(boolean isForced) {
		if (drawComponent == null) { return; }

		if (isForced) {
			drawComponent.redrawSurfaceForced();
		} else {
			drawComponent.redrawSurface();
		}
	}

	/**
	 * 將 {@link Layer}（包含所擁有的 {@link LSprite}）從 {@link DrawComponent} 上移除。
	 * <p>
	 * <b>undeploy() 並不會影響 {@link Layer} 原本的 {@link LSprite} 結構</b>。
	 * 如果要將 {@link LSprite} 從 {@link Layer} 中移除，請使用 {@link #remove(LSprite)}。
	 * <p>
	 * <b>注意：</b>目前 GXT {@link DrawComponent} 對同一個 {@link Sprite} 作 remove() 再 addSprite()，
	 * 該 {@link Sprite} 並不會出現，這是 GXT 原生的行為 / bug，SSCCE 參見 GF-Test 的 Issue_31。
	 * 這代表目前的版本中，若作過 {@link #undeploy()}，則 {@link #redeploy()} 並不會起作用。
	 */
	public void undeploy() {
		if (drawComponent == null) {
			throw new UnsupportedOperationException("Haven't been deployed yet");
		}

		for (LSprite sprite : members) {
			if (sprite instanceof LayerSprite) {
				((Layer)sprite).undeploy();
			} else {
				Sprite s = (Sprite)sprite;
				drawComponent.remove(s);
				//DrawComponent.remove() 沒有修改 sprite.component 的值
				//這導致 deploy() 時會因為防止重複 deploy() 的機制
				//而根本沒真正作 drawComponent.addSprite()
				//雖然目前證實即使補了這段還是沒用（參見 GF-Test 的 Issue_33）
				//不過還是先補上、使 GF 本身的邏輯是正確的...... Orz
				s.setComponent(null);
			}
		}
	}

	public List<LSprite> getMembers() {
		return Collections.unmodifiableList(members);
	}

	/**
	 * 設定所有 member（包含 member 的 member）的 hidden 狀態。
	 * <p>
	 * 註：由於 {@link Layer} 只是一個 sprite 的集合，本身並無實體，
	 * 所以不提供 isHidden()。
	 */
	public void setHidden(boolean hidden) {
		for (LSprite member : members) {
			if (member instanceof Layer) {
				((Layer) member).setHidden(hidden);;
			} else {
				member.setHidden(hidden);
			}
		}
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
}
