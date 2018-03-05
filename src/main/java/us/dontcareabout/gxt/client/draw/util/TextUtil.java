package us.dontcareabout.gxt.client.draw.util;

import java.util.HashMap;

import com.sencha.gxt.chart.client.draw.sprite.TextSprite;

/**
 * {@link TextSprite} 的相關 util。
 *
 * <h1>修正 Y 軸偏移量</h1>
 * {@link TextSprite#setY(double)} 的結果很容易與預期有出入，
 * 這可能是因為 setY() 是以字型的 top 為基準，
 * 而實務上會期望是以 ascent 為基準（尤其是處理中國字垂直至中的時候）。
 * <p>
 * 在無法用 native 方式取得 font matrix 的前提下，
 * 這裡做了另一個假設：top 到 ascent 的偏移量與字體大小成正比。
 * 在這個前提假設下，
 * 設計了 {@link #getYOffset(TextSprite)} 等 method 來取得 Y 軸偏移量的修正值。
 */
public class TextUtil {
	// ==== 字型 Y 軸偏移量 ==== //
	private static final HashMap<String, Double> Y_OFFSETS = new HashMap<>();
	static {
		//沒有設定 font name，TextSprite.getFont() 會得到 null
		//這個在沒有調過字型的 Chrome 上看起來是正確的，其他 browser 不確定...... [逃]
		Y_OFFSETS.put(null, -0.1);
	}

	/**
	 * @param fontName	{@link TextSprite#setFont(String)} 的設定值。
	 * 	若傳入 null 表示未設定 font 的 default 值。
	 * @param y	該字型的 Y 軸偏移量比例
	 */
	public static void setYOffsetRatio(String fontName, double y) {
		Y_OFFSETS.put(fontName, y);
	}

	/**
	 * @param fontName {@link TextSprite#getFont()} 的回傳值，允許 null。
	 * @return 該字型的 Y 軸偏移量比例。若找不到該字型，則回傳 0。
	 */
	public static double getYOffsetRatio(String fontName) {
		Double result = Y_OFFSETS.get(fontName);
		return result == null ? 0 : result;
	}

	/**
	 * @return 該 sprite 的 Y 軸偏移量
	 * @see #getYOffsetRatio(String)
	 */
	public static double getYOffset(TextSprite sprite) {
		return sprite.getFontSize() * getYOffsetRatio(sprite.getFont());
	}
	// ======== //
}
