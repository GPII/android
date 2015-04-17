/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public final class Net_gpii_AndroidSensors {

	private static Object[] __args = new Object[1];

	public static Object[] __getArgs() { return __args; }

	static Object __invoke(net.gpii.AndroidSensors inst, int opIdx, Object[] args) {
		Object result = null;
		switch(opIdx) {
		case 0: /* getAmplitudeEMA */
			result = org.meshpoint.anode.js.JSValue.asJSNumber(inst.getAmplitudeEMA());
			break;
		case 1: /* getEndPoint */
			result = inst.getEndPoint();
			break;
		case 2: /* getLightSensor */
			result = inst.getLightSensor();
			break;
		case 3: /* setEndPoint */
			inst.setEndPoint(
				(String)args[0]
			);
			break;
		case 4: /* startLightSensor */
			inst.startLightSensor();
			break;
		case 5: /* startNoiseSensor */
			inst.startNoiseSensor();
			break;
		case 6: /* stopLightSensor */
			inst.stopLightSensor();
			break;
		case 7: /* stopNoiseSensor */
			inst.stopNoiseSensor();
			break;
		default:
		}
		return result;
	}

}
