package us.dontcareabout.java.common;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 以 fluent style 取代 {@link java.nio.file.Paths}。
 */
public class Paths {
	private ArrayList<String> children = new ArrayList<>();
	private boolean existFolder;

	public Paths(String root) {
		children.add(root);
	}

	public Paths append(String child) {
		children.add(child);
		return this;
	}

	public Paths append(String... childArray) {
		children.addAll(Arrays.asList(childArray));
		return this;
	}

	public Paths append(List<String> childList) {
		children.addAll(childList);
		return this;
	}

	/**
	 * 在 {@link #toFile()} / {@link #toPath()} 時會檢查該目錄是否存在，
	 * 如果不存在會作 {@link File#mkdirs()}
	 */
	public Paths existFolder() {
		existFolder = true;
		return this;
	}

	public File toFile() {
		if (children.size() == 0) { return new File(""); }

		StringBuilder sb = new StringBuilder(children.get(0));

		for (int i = 1; i < children.size(); i++) {
			sb.append(File.separator);
			sb.append(children.get(i));
		}

		File result = new File(sb.toString());

		if (existFolder && !result.exists()) { result.mkdirs(); }

		return result;
	}

	public Path toPath() {
		return toFile().toPath();
	}
}
