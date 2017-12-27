package us.dontcareabout.gxt.client.draw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.DrawComponent;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.SpriteSelectionHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteUpEvent;

/**
 * {@link DrawComponent} 的補強、並針對 {@link LSprite} 作延伸，
 * 以解決 {@link DrawComponent} 在實務上遇到的問題：
 * <ul>
 * 	<li>sprite handler 機制</li>
 * 	<li>改變預設行為</li>
 * </ul>
 *
 *
 * <h1>sprite handler 機制</h1>
 *
 * 以「點擊 sprite」為例，
 * 原本 GXT 的設計是使用 {@link DrawComponent#addSpriteSelectionHandler(SpriteSelectionHandler)}，
 * 從 {@link SpriteSelectionEvent#getSprite()} 取得觸發的 sprite，然後才能作對應的處理。
 * 如果會觸發 event 的 sprite 數量很多、那麼 handler 實作程式碼就會糾結一團十分混亂。
 * <p>
 * 導入 {@link Layer} 後，handler 實作改為 {@link Layer#addSpriteSelectionHandler(SpriteSelectionHandler)} 負責，
 * {@link LayerContainer} 只負責將 event 導向至擁有觸發 event 的 sprite 的 {@link Layer}。
 * <p>
 * 參考下圖
 * <pre>
 * +----------------------------+
 * | LayerContainer             |
 * |  +-----------------------+ |
 * |  | layer A               | |
 * |  |  +------------------+ | |
 * |  |  | layer B          | | |
 * |  |  +------------------+ | |
 * |  +-----------------------+ |
 * +----------------------------+
 * </pre>
 * 假設觸發 selection event 的 sprite 在 layer B 上，
 * layer B 有掛載對應 handler。會有下列幾種狀況：
 * <ul>
 * 	<li>
 * 		layer A <b>沒有</b>掛載 handler：觸發 layer B 的 handler
 * 	</li>
 * 	<li>
 * 		layer A <b>有</b>掛載 handler，且 {@link Layer#isStopPropagation()} 為 true：
 * 		只觸發 layer A 的 handler。
 * 	</li>
 * 	<li>
 * 		layer A <b>有</b>掛載 handler，且 {@link Layer#isStopPropagation()} 為 false：
 * 		先觸發 layer A 的 handler，然後觸發 layer B 的 handler。
 * 	</li>
 * </ul>
 *
 *
 * <h1>改變預設行為</h1>
 *
 * <ul>
 * 	<li>
 * 		onLoad()：
 * 		<ol>
 * 			<li>呼叫一次 {@link #redrawSurfaceForced()}</li>
 * 			<li>將所有 memeber sprite （如果有設定 cursor）作 {@link LSprite#setCursor(Cursor)}</li>
 * 		</ol>
 * 	<li>禁止呼叫 {@link #setBackground(Color)}，並拔掉預設的白色 background</li>
 * </ul>
 */
public class LayerContainer extends DrawComponent {
	private ArrayList<Layer> layers = new ArrayList<>();

	public LayerContainer() {
		this(500, 500);
	}

	public LayerContainer(int w, int h) {
		super(w, h);

		//常出現 background 漂到最上面蓋住其他 sprite 的情形（執行期隨機出現）
		//所以直接把 background 拔掉
		//caller 有需要就自己弄個 LayerSprite
		super.setBackground(null);

		addSpriteHandler(new SpriteHandler() {
			@Override
			public void onSpriteSelect(SpriteSelectionEvent event) {
				handleEvent(event, event.getSprite());
			}

			@Override
			public void onSpriteLeave(SpriteOutEvent event) {
				handleEvent(event, event.getSprite());
			}

			@Override
			public void onSpriteOver(SpriteOverEvent event) {
				handleEvent(event, event.getSprite());
			}

			@Override
			public void onSpriteUp(SpriteUpEvent event) {
				handleEvent(event, event.getSprite());
			}
		});
	}

	public void addLayer(Layer layer) {
		layers.add(layer);
		layer.deploy(this);
	}

	public List<Layer> getLayers() {
		return Collections.unmodifiableList(layers);
	}

	public void clear() {
		//不用 Layer.undeploy() 了，直接清空就算了 XD
		layers.clear();
		super.clearSurface();
	}

	@Override
	public void clearSurface() {
		throw new UnsupportedOperationException("Use clear() instead.");
	}

	@Override
	public void setBackground(Color background) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		//很多實務上的邏輯需要 surfaceElement 先存在
		//例如 setCursor()、或是要取得正確的 bbox（在 onResize() 中會用到）
		//所以這裡統一作一次
		redrawSurfaceForced();

		for (Layer layer : layers) {
			//LayerSprite 也可以設定 cursor
			//但是塞不進 processLayerOnLoad()，所以就在這邊搞... (艸
			if (layer instanceof LayerSprite) {
				ensureCursor((LayerSprite) layer);
			}

			processLayerOnLoad(layer);
		}
	}

	/**
	 * 處理在 {@link DrawComponent} attach 進 DOM 後才能作的事情。
	 */
	private void processLayerOnLoad(Layer layer) {
		for (LSprite ls : layer.getSprites()) {
			ensureCursor(ls);

			if (ls instanceof Layer) {
				processLayerOnLoad((Layer) ls);
			}
		}
	}

	private void ensureCursor(LSprite ls) {
		if (ls.getCursor() != null) {
			ls.setCursor(ls.getCursor());
		}
	}

	private void handleEvent(GwtEvent<?> event, Sprite sprite) {
		for (Layer layer : layers) {
			if (layer.hasSprite(sprite)) {
				if (layer.handleEvent(event, sprite)) {
					redrawSurface();
				};

				break;	//理論上一個 sprite 只會出現在一個 layer 上
			}
		}
	}
}
