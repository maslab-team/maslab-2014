package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class Gyroscope extends Sensor {
	private static final double CONVERSION_FACTOR = (Math.PI / 180) / 80;
	private static final int ERROR_CODE = -32767;
	private long lastUpdateTime;
	
	byte spiPort;
	byte ssPin;
	double omega;
	double theta;
	
	/*
	 * The first argument is the index of an "SPI port". There are two on the Maple.
	 * One is pins 10-13, and the other is pins 31-34. (Read the bottom of the Maple.)
	 * The SS pin (pin 10 or pin 31) needs to be controlled manually, so you need to
	 * provide a fifth (digital) pin.
	 */
	public Gyroscope(int spiPort, int ssPin) {
		this.spiPort = (byte) spiPort;
		this.ssPin = (byte) ssPin;
		lastUpdateTime = System.nanoTime();
		theta = 0;
	}

	@Override
	public byte getDeviceCode() {
		return 'Y';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[] {spiPort, ssPin};
	}

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) {
	    long currentTime = System.nanoTime();
		byte msb = buff.get();
		byte lsb = buff.get();
		int new_omega = (msb * 256) + ((int) lsb & 0xff);
		if (new_omega != ERROR_CODE) {
			omega = new_omega * CONVERSION_FACTOR;
			if(Math.abs(omega) > .01) {
			    theta += omega*(currentTime - lastUpdateTime)/1000;
			    theta = theta % 2*Math.PI;
			    if(theta < 0) {
			        theta += 2*Math.PI;
			    }
			    lastUpdateTime = currentTime;
			}
		}
	}

	@Override
	public int expectedNumBytesFromMaple() {
		return 2;
	}
	
	// in radians per second
	public double getAngularSpeed() {
		return omega;
	}
	
	public double getTheta() {
	    return theta;
	}
	
	public double getAngleChangeSinceLastUpdate() {
		throw new UnsupportedOperationException();
	}

}
