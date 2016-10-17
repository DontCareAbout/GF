package us.dontcareabout.gxt.client.model;

/**
 * 原本 {@link #getValue(Object)} 的邏輯改在 {@link #extract(Object)} 當中實作。
 * 如果 {@link #extract(Object)} 產生任何 exception，則 {@link #getValue(Object)} 會回傳 {@link #defaultValue}。
 * <p>
 * 注意：NoExceptionValueProvider 是繼承 {@link GetValueProvider}，沒有處理 {@link #getPath()} 以及 {@link #setValue(Object, Object)}。
 * <p>
 * 特別感謝：ybon3 (GitHub)、Gaduo (GitHub)
 */
public abstract class NoExceptionValueProvider<T, V> extends GetValueProvider<T, V> {
	private final V defaultValue;

	public NoExceptionValueProvider(V defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public final V getValue(T entity) {
		try {
			return extract(entity);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	protected abstract V extract(T entity) throws Throwable;
}