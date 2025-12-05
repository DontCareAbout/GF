package us.dontcareabout.gxt.client.util;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;

import us.dontcareabout.gxt.client.model.GetValueProvider;

public class ColumnConfigBuilder<T, V> {
	private final ColumnConfig<T, V> result;

	private Cell<V> cell;
	private String header;
	private HorizontalAlignmentConstant horizontalAlignment;
	private HorizontalAlignmentConstant horizontalHeaderAlignment;
	private Integer width;

	private boolean cellPedding = true;
	private boolean fixed;

	public ColumnConfigBuilder(ValueProvider<T, V> vp) {
		result = new ColumnConfig<>(vp);
	}

	/**
	 * @param constant {@link ValueProvider#getValue(Object)} 的固定回傳值
	 */
	public ColumnConfigBuilder(V constant) {
		this(new GetValueProvider<T, V>() {
			@Override
			public V getValue(T object) {
				return constant;
			}
		});
	}

	public ColumnConfig<T, V> build() {
		if (cell != null) { result.setCell(cell); }
		if (header != null) { result.setHeader(header); }
		if (horizontalAlignment != null) { result.setHorizontalAlignment(horizontalAlignment); }
		if (horizontalHeaderAlignment != null) { result.setHorizontalHeaderAlignment(horizontalHeaderAlignment); }
		if (width != null) { result.setWidth(width); }

		result.setCellPadding(cellPedding);
		result.setFixed(fixed);
		return result;
	}

	// ======== //

	public ColumnConfigBuilder<T, V> setCell(Cell<V> cell) {
		this.cell = cell;
		return this;
	}

	public ColumnConfigBuilder<T, V> setHeader(String header) {
		this.header = header;
		return this;
	}

	public ColumnConfigBuilder<T, V> setHorizontalAlignment(HorizontalAlignmentConstant horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	public ColumnConfigBuilder<T, V> setHorizontalHeaderAlignment(HorizontalAlignmentConstant horizontalHeaderAlignment) {
		this.horizontalHeaderAlignment = horizontalHeaderAlignment;
		return this;
	}

	public ColumnConfigBuilder<T, V> setWidth(int width) {
		this.width = width;
		return this;
	}

	// ======== //

	public ColumnConfigBuilder<T, V> setCellPedding(boolean cellPedding) {
		this.cellPedding = cellPedding;
		return this;
	}

	public ColumnConfigBuilder<T, V> setFixed(boolean fixed) {
		this.fixed = fixed;
		return this;
	}
}
