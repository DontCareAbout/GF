package us.dontcareabout.gxt.client.model;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * 提供一個已經實做空白 setValue() 與 getPath() 的 {@link ValueProvider} adapter。
 */
public abstract class GetValueProvider<T, V> implements ValueProvider<T, V> {
	@Override public void setValue(T object, V value) {}

	@Override public String getPath() { return null; }
}
