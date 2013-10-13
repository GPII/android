/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public final class Net_gpii_AndroidIntentHandler {

	private static Object[] __args = new Object[2];

	public static Object[] __getArgs() { return __args; }

	static Object __invoke(net.gpii.AndroidIntentHandler inst, int opIdx, Object[] args) {
		Object result = null;
		switch(opIdx) {
		case 0: /* startActivity */
			inst.startActivity(
				(String)args[0],
				(String)args[1]
			);
			break;
		case 1: /* startActivityByPackageName */
			inst.startActivityByPackageName(
				(String)args[0]
			);
			break;
		case 2: /* startMainLauncherActivity */
			inst.startMainLauncherActivity(
				(String)args[0],
				(String)args[1]
			);
			break;
		case 3: /* stopActivityByPackageName */
			inst.stopActivityByPackageName(
				(String)args[0]
			);
			break;
		default:
		}
		return result;
	}

}
