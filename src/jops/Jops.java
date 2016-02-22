package jops;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Jops {

	static final public int TYPE_BOOLEAN = 0;
	static final public int TYPE_INT = 1;
	static final public int TYPE_STRING = 2;
	Set<Op> ops;
	String tn;
	String des;
	String oth;
	Handler handler;

	public Jops() {
		ops = new HashSet<Op>();
	}

	public void addOp(String short_name, String long_name, int argument_type,
			String abstract_, String description) {
		addOp(new Op(short_name, long_name, argument_type, abstract_,
				description));
	}

	void addOp(Op op) {
		if (op.sn.length() == 0) {
			op.sn = null;
		}
		if (op.ln.length() == 0) {
			op.ln = null;
		}

		if (op.sn == null && op.ln == null) {
			throw new Error("short name and long name should not be both zero");
		}
		if (op.type < TYPE_BOOLEAN || op.type > TYPE_STRING) {
			throw new Error("unknow option type");
		}
		ops.add(op);
	}

	public void setToolName(String tn) {
		this.tn = tn;
	}

	public void setToolDescription(String des) {
		this.des = des;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setOthersName(String others_name) {
		this.oth = others_name;
	}

	public String getHelp() {
		StringBuilder sb = new StringBuilder();
		String tn = this.tn;
		if (tn == null) {
			tn = "<toolname>";
		}
		String oth = this.oth;
		if (oth == null) {
			oth = "<others>";
		}
		sb.append(tn);
		sb.append(" -<option>:<argument>|--<option>:<argument> ").append(oth)
				.append("\n");
		if (des != null) {
			sb.append(des);
			sb.append("\n");
		}

		if (ops != null) {
			for (Op op : ops) {
				if (op.sn != null) {
					sb.append("-");
					sb.append(op.sn);
				}
				if (op.ln != null && op.ln.length() != 0) {
					if (op.sn != null) {
						sb.append(' ');
					}
					sb.append("--");
					sb.append(op.ln);
				}
				sb.append('\n');
				if (op.abs != null) {
					sb.append(op.abs);
					sb.append('\n');
				}
				if (op.des != null) {
					sb.append(op.des);
					sb.append('\n');
				}
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	public Set<Op> getOps() {
		return ops;
	}

	void handleOp(char sn, String ln, int v) {

	}

	void handleOp(char sn, String ln, String v) {

	}

	public Result parse(String[] args) {
		Result result = new Result();
		StringBuilder err = new StringBuilder();
		Set<String> others = new HashSet<String>();
		for (String arg : args) {
			if (arg.charAt(0) != '-') {
				others.add(arg);
				continue;
			}
			String sn = null;
			String ln = null;
			String argument = null;
			if (arg.length() >= 2 && arg.charAt(1) == '-') {
				if (arg.length() == 2) {
					err.append("bad option '--'\n");
					break;
				}
				int e = arg.indexOf(':', 2);
				if (e < 0) {
					ln = arg.substring(2);
				} else {
					ln = arg.substring(2, e);
					argument = arg.substring(e + 1, arg.length());
				}
				if (ln.length() == 0) {
					ln = null;
				}

			} else if (arg.length() >= 1) {
				if (arg.length() == 1) {
					err.append("bad option '-'\n");
					break;
				}
				int e = arg.indexOf(':', 1);
				if (e < 0) {
					sn = arg.substring(2);
				} else {
					sn = arg.substring(2, e);
					argument = arg.substring(e + 1, arg.length());
				}
				if (sn.length() == 0) {
					sn = null;
				}
			}
			if (argument.length() == 0) {
				argument = null;
			}
			for (Op op : ops) {
				if ((sn != null && op.sn != null && sn.compareTo(op.sn) == 0)
						|| (ln != null && op.ln != null && ln.compareTo(op.ln) == 0)) {
					if (op.type != TYPE_BOOLEAN && argument == null) {
						err.append("only option with boolean type may have default argument!\n");
						break;
					}
					int r = 0;
					switch (op.type) {
					case TYPE_BOOLEAN:
						boolean a = false;
						if (argument == null) {
							a = true;
						}
						a = Boolean.valueOf(argument);
						r = handler.jopsHandleOp(op.sn, op.ln, a);
						break;
					case TYPE_STRING:
						r = handler.jopsHandleOp(op.sn, op.ln, argument);
						break;
					case TYPE_INT:
						int ai = Integer.valueOf(argument);
						r = handler.jopsHandleOp(op.sn, op.ln, ai);
						break;
					default:
						throw new Error("Internal Error!");
					}
					if (r != 0) {
						err.append("can't handle `").append(arg).append("'");
						break;
					}

				}
			}
			if (err.length() != 0) {
				break;
			}

		}
		if (err.length() != 0) {
			result.error = err.toString();
		}
		if (others.size() != 0) {
			result.others = others.toArray(new String[0]);
		}
		return result;
	}
}
