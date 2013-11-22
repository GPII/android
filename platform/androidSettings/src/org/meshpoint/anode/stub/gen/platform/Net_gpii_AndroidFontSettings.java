/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public final class Net_gpii_AndroidFontSettings {

	private static Object[] __args = new Object[1];

	public static Object[] __getArgs() { return __args; }

	static Object __invoke(net.gpii.AndroidFontSettings inst, int opIdx, Object[] args) {
		Object result = null;
		switch(opIdx) {
		case 0: /* getFontSize */
			result = org.meshpoint.anode.js.JSValue.asJSNumber(inst.getFontSize());
			break;
		case 1: /* setFontSize */
			inst.setFontSize(
				((org.meshpoint.anode.js.JSValue)args[0]).dblValue
			);
			break;
		default:
		}
		return result;
	}

}
