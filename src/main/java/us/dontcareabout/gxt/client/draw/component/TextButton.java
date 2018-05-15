package us.dontcareabout.gxt.client.draw.component;

import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.SpriteSelectionHandler;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.PreciseRectangle;

import us.dontcareabout.gxt.client.draw.Cursor;
import us.dontcareabout.gxt.client.draw.LTextSprite;
import us.dontcareabout.gxt.client.draw.LayerSprite;
import us.dontcareabout.gxt.client.draw.util.TextUtil;

/**
 * GF-draw 版的 GXT TextButton，但有幾點不同：
 * <ul>
 * 	<li>無法設定 icon</li>
 * 	<li>click handler 是 {@link #addSpriteSelectionHandler(SpriteSelectionHandler)}</li>
 * 	<li>可設定 margin，按鈕字體會自動調整為符合剩餘空間的大小並雙向置中</li>
 * </ul>
 */
public class TextButton extends LayerSprite {
	//直接開成 protected，讓 child class 要作一些進階動作才有辦法動得到
	//但是不直接開成 public、也不給 public getter
	//這樣設法保持封閉性，以免 caller 作 setFontSize() 等動作造成混淆。
	//child class 要亂搞就... 自己負責 XD
	protected LTextSprite textSprite = new LTextSprite();

	private Margins margins = new Margins(5);

	public TextButton() {
		add(textSprite);
		setMemberCursor(Cursor.POINTER);
	}

	public TextButton(String text) {
		this();
		setText(text);
	}

	public void setText(String text) {
		textSprite.setText(text);
	}

	public void setTextColor(Color color) {
		textSprite.setFill(color);
	}

	public void setMargin(int value) {
		setMargins(new Margins(value));
	}

	public void setMargins(Margins margins) {
		this.margins = margins;
	}

	public String getText() {
		return textSprite.getText();
	}

	public Margins getMargins() {
		return margins;
	}

	@Override
	protected void adjustMember() {
		TextUtil.autoResize(
			textSprite,
			getWidth() - margins.getLeft() - margins.getRight(),
			getHeight() - margins.getTop() - margins.getBottom()
		);

		PreciseRectangle textBox = textSprite.getBBox();
		textSprite.setLX((getWidth() + margins.getLeft() - margins.getRight() - textBox.getWidth()) / 2.0);
		textSprite.setLY(
			(getHeight() + margins.getTop() - margins.getBottom() - textBox.getHeight()) / 2.0
			+ TextUtil.getYOffset(textSprite)
		);
	}
}
