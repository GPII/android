/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public final class Net_gpii_AndroidAudioManager {

	private static Object[] __args = new Object[2];

	public static Object[] __getArgs() { return __args; }

	static Object __invoke(net.gpii.AndroidAudioManager inst, int opIdx, Object[] args) {
		Object result = null;
		switch(opIdx) {
		case 0: /* getVolume */
			result = inst.getVolume(
				(String)args[0]
			);
			break;
		case 1: /* setVolume */
			inst.setVolume(
				(String)args[0],
				(Integer)args[1]
			);
			break;
		default:
		}
		return result;
	}

}
