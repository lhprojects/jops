package jops;

public interface Handler {
	int jopsHandleOp(String sn, String ln, int value);
	int jopsHandleOp(String sn, String ln, boolean value);
	int jopsHandleOp(String sn, String ln, String value);
}
