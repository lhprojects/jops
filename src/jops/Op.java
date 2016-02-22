package jops;

public class Op {
	public Op(String short_name, String ln, int type, String abs, String des) {
		this.sn = short_name;
		this.ln = ln;
		this.type = type;
		this.abs = abs;
		this.des = des;
	}

	String sn;// short name
	String ln;// long name
	int type; //
	String abs; //abstract
	String des; //description
}
