/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public final class Net_gpii_AndroidPersistentConfiguration {

	private static Object[] __args = new Object[2];

	public static Object[] __getArgs() { return __args; }

	static Object __invoke(net.gpii.AndroidPersistentConfiguration inst, int opIdx, Object[] args) {
		Object result = null;
		switch(opIdx) {
		case 0: /* get */
			result = inst.get(
				(String)args[0]
			);
			break;
		case 1: /* set */
			result = inst.set(
				(String)args[0],
				(String)args[1]
			);
			break;
		default:
		}
		return result;
	}

}
