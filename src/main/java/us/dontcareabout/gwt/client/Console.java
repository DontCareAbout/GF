package us.dontcareabout.gwt.client;

public class Console {
	public static void log(boolean value) {
		print("" + value);
	}

	public static void log(char value) {
		print("" + value);
	}

	public static void log(byte value) {
		print("" + value);
	}

	public static void log(short value) {
		print("" + value);
	}

	public static void log(int value) {
		print("" + value);
	}

	public static void log(long value) {
		print("" + value);
	}

	public static void log(float value) {
		print("" + value);
	}

	public static void log(double value) {
		print("" + value);
	}

	public static void log(String string) {
		print(string);
	}

	public static void log(Object object) {
		print(object.toString());
	}

	private static native void print(String string) /*-{
		console.log(string);
	}-*/;
}
