/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public final class Net_gpii_AndroidSensors {

	private static Object[] __args = new Object[1];

	public static Object[] __getArgs() { return __args; }

	static Object __invoke(net.gpii.AndroidSensors inst, int opIdx, Object[] args) {
		Object result = null;
		switch(opIdx) {
		case 0: /* getLightSensor */
			result = inst.getLightSensor();
			break;
		case 1: /* startLightSensor */
			inst.startLightSensor(
				(String)args[0]
			);
			break;
		case 2: /* stopLightSensor */
			inst.stopLightSensor();
			break;
		default:
		}
		return result;
	}

}
