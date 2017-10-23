package us.dontcareabout.gwt.shared.util;

import com.google.common.base.Preconditions;

/**
 * 用一個 int 來紀錄最多 32 個 boolean flag 的 util。
 */
public class BitFlag {
	private static int[] mask = new int[32];
	static {
		mask[0] = 1;

		for (int i = 1; i < 32; i++) {
			mask[i] = mask[i - 1] << 1;
		}
	}

	/**
	 * @param flagInt 用來儲存 flag 的 int
	 * @param index 要設定的 bit index，需介於 0～31 之間
	 * @return 設定完後的新 flagInt 值
	 */
	public static int set(int flagInt, int index, boolean value) {
		Preconditions.checkArgument(index >= 0 && index < 32);
		return value ? flagInt | mask[index] : flagInt & ~mask[index];
	}

	/**
	 * @param flagInt 用來儲存 flag 的 int
	 * @param index 要設定的 bit index，需介於 0～31 之間
	 */
	public static boolean get(int flagInt, int index) {
		Preconditions.checkArgument(index >= 0 && index < 32);
		return (flagInt & mask[index]) != 0;
	}
}
