package us.dontcareabout.gxt.client.draw.layout;

import java.util.ArrayList;

import com.google.common.base.Preconditions;
import com.sencha.gxt.core.client.util.Margins;

import us.dontcareabout.gxt.client.draw.LayerSprite;

/**
 * {@link HorizontalLayoutLayer} 與 {@link VerticalLayoutLayer} 的共通部份。
 * 原則上適用於所有「依照所給 weight 決定 child 尺寸」的 layout，
 * 不過怎麼想好像也就這兩個 case...... XD
 */
abstract class WeightLayoutLayer extends LayerSprite {
	ArrayList<LayerSprite> children = new ArrayList<>();
	ArrayList<Double> weightList = new ArrayList<>();
	Margins margins = new Margins();
	int gap;

	/**
	 * 在 {@link #resize(double, double)} 後可取得視覺上的整體寬度或高度。
	 */
	public abstract double getViewSize();

	/**
	 * @param weight 值大於 1，直接以 weight 值作為 child 的大小。
	 * 		若值大於 0、小於等於 1，child 的大小為「layout 剩餘大小乘上 weight 值」。
	 * 		0 以下為非法值，會 throw {@link IllegalArgumentException}。
	 */
	//LayerSprite 才有 resize 能力，所以無法處理更上層的 Layer / LSprite
	//唯二的例外是 LRectangleSprite / LImageSprite
	//但整體來說加了沒啥特別好處，所以不考慮
	public void addChild(LayerSprite child, double weight) {
		Preconditions.checkArgument(weight > 0);
		children.add(child);
		weightList.add(weight);
		add(child);
	}

	public Margins getMargins() {
		return margins;
	}

	public void setMargins(int value) {
		margins = new Margins(value);
	}

	public void setMargins(Margins margins) {
		Preconditions.checkArgument(margins != null);
		this.margins = margins;
	}

	/**
	 * @return child 之間的間隙
	 */
	public int getGap() {
		return gap;
	}

	/**
	 * 設定 child 之間的間隙，預設值為 0。
	 */
	public void setGap(int gap) {
		this.gap = gap;
	}

	@Override
	public void clear() {
		weightList.clear();

		while(children.size() != 0) {
			remove(children.get(0));
			children.remove(0);
		}
	}
}
