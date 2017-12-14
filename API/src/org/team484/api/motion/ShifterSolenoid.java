package org.team484.api.motion;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * ShifterSolenoid class for running a pneumatically actuated shifting gearbox. This class adapts the
 * traditional double solenoid class with methods to set the shifter to low/high gear, as well as to
 * get if the shifter is in low or high gear.
 *
 * <p>The ShifterSolenoid class is typically used to actuate a binary pancake actuator that is mounted
 * on a shifting gearbox and is connected to the PCM via two separate digital channels.
 */
public class ShifterSolenoid extends DoubleSolenoid {
	
	/**
	* Constructor. Uses the default PCM ID (defaults to 0).
	*
	* @param lowGearChannel The low gear channel number on the PCM (0..7).
	* @param highGearChannel The high gear channel number on the PCM (0..7).
	*/
	public ShifterSolenoid(int lowGearChannel, int highGearChannel) {
		super(lowGearChannel, highGearChannel);
	}
	
	/**
	* Constructor.
	*
	* @param moduleNumber   The module number of the PCM to use.
	* @param lowGearChannel The low gear channel number on the PCM (0..7).
	* @param highGearChannel The high gear channel number on the PCM (0..7).
	*/
	public ShifterSolenoid(int module, int lowGearChannel, int highGearChannel) {
		super(module, lowGearChannel, highGearChannel);
	}
	
	/**
	 * Switches the shifter into low gear/
	 */
	public void shiftToLow() {
		set(Value.kForward);
	}
	
	/**
	 * Switches the shifter into high gear/
	 */
	public void shiftToHigh() {
		set(Value.kReverse);
	}
	
	/**
	 * Checks if the shifter is currently in low gear/
	 * @return - true only if the shifter is in low gear.
	 */
	public boolean isLowGear() {
		return get().equals(Value.kForward);
	}
	
	/**
	 * Checks if the shifter is currently in high gear/
	 * @return - true only if the shifter is in high gear.
	 */
	public boolean isHighGear() {
		return !isLowGear();
	}
}
