package us.dontcareabout.gxt.client.draw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.chart.client.draw.DrawComponent;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.SpriteSelectionHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteUpEvent;

/**
 * {@link DrawComponent} 的延伸，解決 {@link DrawComponent} 處理 sprite handler 的缺陷。
 * <p>
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
 */
public class LayerContainer extends DrawComponent {
	private ArrayList<Layer> layers = new ArrayList<>();

	public LayerContainer() {
		this(500, 500);
	}

	public LayerContainer(int w, int h) {
		super(w, h);

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

	@Override
	protected void onLoad() {
		super.onLoad();

		for (Layer layer : layers) {
			processLayerOnLoad(layer);
		}
	}

	/**
	 * 處理在 {@link DrawComponent} attach 進 DOM 後才能作的事情。
	 */
	private void processLayerOnLoad(Layer layer) {
		for (LSprite ls : layer.getSprites()) {
			//在 SVG（Surface）下，只有在 attach 之後設定 cursor 才會起作用
			//（因為這時候才找得到對應的 Element）
			//所以在這裡統一檢查 / 補作 setCursor()
			if (ls.getCursor() != null) {
				ls.setCursor(ls.getCursor());
			}

			if (ls instanceof Layer) {
				processLayerOnLoad((Layer) ls);
			}
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
