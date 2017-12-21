package org.team484.api.sensor;

import org.team484.api.motion.ShifterSolenoid;

import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.Encoder;

/**
 * Class to read quadrature encoders attached to a shifting gearbox. The purpose of this class is to
 * calculate not only the distance traveled and speed of the robot using a set distance per puse, but to
 * also report the RPM of the motors attached to the shifting gearbox so that automatic shifting can
 * be performed based on the current RPM and motor power-band.
 *
 * <p>All encoders will immediately start counting - reset() them if you need them to be zeroed
 * before use.
 */
public class ShifterEncoder extends Encoder {
	
	/*
	 * The RPP values (Rotations Per Pulse) are defined as the gear ratio between the motors and
	 * the encoder divided by the pulses per rotation for the encoder. The default values used below
	 * are for the Vex ball shifter with a 3.68 spread and 256 PPR encoders.
	 */
	private double lowGearRPP = 10.42/256.0/3.0, highGearRPP = 2.83/256.0/3.0, distancePerPulse = 1;
	private ShifterSolenoid shifter;
	
	/**
	   * Shifter Encoder constructor. Construct a Shifter Encoder given a and b channels and a shifter
	   * solenoid.
	   *
	   * <p>The encoder will start counting immediately.
	   *
	   * @param channelA         The a channel DIO channel. 0-9 are on-board, 10-25 are on the MXP port
	   * @param channelB         The b channel DIO channel. 0-9 are on-board, 10-25 are on the MXP port
	   * @param reverseDirection represents the orientation of the encoder and inverts the output values
	   *                         if necessary so forward represents positive values.
	   * @param shifterSolenoid	 The ShifterSolenoid object that controls the high/low gear state of the
	   *                         gearbox.
	   */
	  public ShifterEncoder(final int channelA, final int channelB, boolean reverseDirection,
			  ShifterSolenoid shifterSolenoid) {
	    super(channelA, channelB, reverseDirection);
	    shifter = shifterSolenoid;
	  }

	  /**
	   * Shifter Encoder constructor. Construct a Shifter Encoder given a and b channels and a shifter
	   * solenoid.
	   *
	   * <p>The encoder will start counting immediately.
	   *
	   * @param channelA The a channel digital input channel.
	   * @param channelB The b channel digital input channel.
	   * @param shifterSolenoid	 The ShifterSolenoid object that controls the high/low gear state of the
	   *                         gearbox.
	   * 
	   */
	  public ShifterEncoder(final int channelA, final int channelB, ShifterSolenoid shifterSolenoid) {
	    super(channelA, channelB);
	    shifter = shifterSolenoid;
	  }

	  /**
	   * Shifter Encoder constructor. Construct a Shifter Encoder given a and b channels and a shifter
	   * solenoid.
	   *
	   * <p>The encoder will start counting immediately.
	   *
	   * @param channelA         The a channel digital input channel.
	   * @param channelB         The b channel digital input channel.
	   * @param reverseDirection represents the orientation of the encoder and inverts the output values
	   *                         if necessary so forward represents positive values.
	   * @param encodingType     either k1X, k2X, or k4X to indicate 1X, 2X or 4X decoding. If 4X is
	   *                         selected, then an encoder FPGA object is used and the returned counts
	   *                         will be 4x the encoder spec'd value since all rising and falling edges
	   *                         are counted. If 1X or 2X are selected then a m_counter object will be
	   *                         used and the returned value will either exactly match the spec'd count
	   *                         or be double (2x) the spec'd count.
	   * @param shifterSolenoid	 The ShifterSolenoid object that controls the high/low gear state of the
	   *                         gearbox.
	   */
	  public ShifterEncoder(final int channelA, final int channelB, boolean reverseDirection,
	                 final EncodingType encodingType, ShifterSolenoid shifterSolenoid) {
	    super(channelA, channelB, reverseDirection, encodingType);
	    shifter = shifterSolenoid;
	  }

	  /**
	   * Shifter Encoder constructor. Construct a Shifter Encoder given a and b channels and a shifter
	   * solenoid. Using an index pulse forces 4x encoding
	   *
	   * <p>The encoder will start counting immediately.
	   *
	   * @param channelA         The a channel digital input channel.
	   * @param channelB         The b channel digital input channel.
	   * @param indexChannel     The index channel digital input channel.
	   * @param reverseDirection represents the orientation of the encoder and inverts the output values
	   *                         if necessary so forward represents positive values.
	   * @param shifterSolenoid	 The ShifterSolenoid object that controls the high/low gear state of the
	   *                         gearbox.                        
	   */
	  public ShifterEncoder(final int channelA, final int channelB, final int indexChannel,
	                 boolean reverseDirection, ShifterSolenoid shifterSolenoid) {
	    super(channelA, channelB, indexChannel, reverseDirection);
	    shifter = shifterSolenoid;
	  }

	  /**
	   * Shifter Encoder constructor. Construct a Shifter Encoder given a and b channels and a shifter
	   * solenoid. Using an index pulse forces 4x encoding
	   *
	   * <p>The encoder will start counting immediately.
	   *
	   * @param channelA     The a channel digital input channel.
	   * @param channelB     The b channel digital input channel.
	   * @param indexChannel The index channel digital input channel.
	   * @param shifterSolenoid	 The ShifterSolenoid object that controls the high/low gear state of the
	   *                         gearbox.
	   */
	  public ShifterEncoder(final int channelA, final int channelB, final int indexChannel,
			  ShifterSolenoid shifterSolenoid) {
	    super(channelA, channelB, indexChannel);
	    shifter = shifterSolenoid;
	  }

	  /**
	   * Shifter Encoder constructor. Construct a Shifter Encoder given a and b channels as digital inputs
	   * and a shifter solenoid. This is used in the case where the digital inputs are shared. The Encoder
	   * class will not allocate the digital inputs and assume that they already are counted.
	   *
	   * <p>The encoder will start counting immediately.
	   *
	   * @param sourceA          The source that should be used for the a channel.
	   * @param sourceB          the source that should be used for the b channel.
	   * @param reverseDirection represents the orientation of the encoder and inverts the output values
	   *                         if necessary so forward represents positive values.
	   * @param shifterSolenoid	 The ShifterSolenoid object that controls the high/low gear state of the
	   *                         gearbox.
	   */
	  public ShifterEncoder(DigitalSource sourceA, DigitalSource sourceB, boolean reverseDirection,
			  ShifterSolenoid shifterSolenoid) {
	    super(sourceA, sourceB, reverseDirection);
	    shifter = shifterSolenoid;
	  }

	  /**
	   * Shifter Encoder constructor. Construct a Shifter Encoder given a and b channels as digital inputs
	   * and a shifter solenoid. This is used in the case where the digital inputs are shared. The Encoder
	   * class will not allocate the digital inputs and assume that they already are counted.
	   *
	   * <p>The encoder will start counting immediately.
	   *
	   * @param sourceA The source that should be used for the a channel.
	   * @param sourceB the source that should be used for the b channel.
	   * @param shifterSolenoid	 The ShifterSolenoid object that controls the high/low gear state of the
	   *                         gearbox.
	   */
	  public ShifterEncoder(DigitalSource sourceA, DigitalSource sourceB, ShifterSolenoid shifterSolenoid) {
	    super(sourceA, sourceB);
	    shifter = shifterSolenoid;
	  }

	  /**
	   * Shifter Encoder constructor. Construct a Shifter Encoder given a and b channels as digital inputs
	   * and a shifter solenoid. This is used in the case where the digital inputs are shared. The Encoder
	   * class will not allocate the digital inputs and assume that they already are counted.
	   *
	   * <p>The encoder will start counting immediately.
	   *
	   * @param sourceA          The source that should be used for the a channel.
	   * @param sourceB          the source that should be used for the b channel.
	   * @param reverseDirection represents the orientation of the encoder and inverts the output values
	   *                         if necessary so forward represents positive values.
	   * @param encodingType     either k1X, k2X, or k4X to indicate 1X, 2X or 4X decoding. If 4X is
	   *                         selected, then an encoder FPGA object is used and the returned counts
	   *                         will be 4x the encoder spec'd value since all rising and falling edges
	   *                         are counted. If 1X or 2X are selected then a m_counter object will be
	   *                         used and the returned value will either exactly match the spec'd count
	   *                         or be double (2x) the spec'd count.
	   * @param shifterSolenoid	 The ShifterSolenoid object that controls the high/low gear state of the
	   *                         gearbox.
	   */
	  public ShifterEncoder(DigitalSource sourceA, DigitalSource sourceB, boolean reverseDirection,
	                 final EncodingType encodingType, ShifterSolenoid shifterSolenoid) {
	    super(sourceA, sourceB, reverseDirection, encodingType);
	    shifter = shifterSolenoid;
	  }

	  /**
	   * Shifter Encoder constructor. Construct a Shifter Encoder given a, b and index channels as digital
	   * inputs and a shifter solenoid. This is used in the case where the digital inputs are shared. The
	   * Encoder class will not allocate the digital inputs and assume that they already are counted.
	   *
	   * <p>The encoder will start counting immediately.
	   *
	   * @param sourceA          The source that should be used for the a channel.
	   * @param sourceB          the source that should be used for the b channel.
	   * @param indexSource      the source that should be used for the index channel.
	   * @param reverseDirection represents the orientation of the encoder and inverts the output values
	   *                         if necessary so forward represents positive values.
	   * @param shifterSolenoid	 The ShifterSolenoid object that controls the high/low gear state of the
	   *                         gearbox.
	   */
	  public ShifterEncoder(DigitalSource sourceA, DigitalSource sourceB, DigitalSource indexSource,
	                 boolean reverseDirection, ShifterSolenoid shifterSolenoid) {
	    super(sourceA, sourceB, indexSource, reverseDirection);
	    shifter = shifterSolenoid;
	  }

	  /**
	   * Shifter Encoder constructor. Construct a Shifter Encoder given a, b and index channels as digital
	   * inputs and a shifter solenoid. This is used in the case where the digital inputs are shared. The
	   * Encoder class will not allocate the digital inputs and assume that they already are counted.
	   *
	   * <p>The encoder will start counting immediately.
	   *
	   * @param sourceA     The source that should be used for the a channel.
	   * @param sourceB     the source that should be used for the b channel.
	   * @param indexSource the source that should be used for the index channel.
	   * @param shifterSolenoid	 The ShifterSolenoid object that controls the high/low gear state of the
	   *                         gearbox.
	   */
	  public ShifterEncoder(DigitalSource sourceA, DigitalSource sourceB, DigitalSource indexSource,
			  ShifterSolenoid shifterSolenoid) {
	    super(sourceA, sourceB, indexSource);
	    shifter = shifterSolenoid;
	  }
	  
	  /**
	   * Sets the number of rotations per pulse for this encoder when the shifter is in high gear. Since
	   * most encoders have a large number of pulses, this number is most likely less than 1.
	   * 
	   * @param rpp - the rotations per pulse.
	   */
	  public void setHighGearRPP(double rpp) {
		  highGearRPP = rpp;
	  }
	 
	  /**
	   * Sets the number of rotations per pulse for this encoder when the shifter is in low gear. Since
	   * most encoders have a large number of pulses, this number is most likely less than 1. This value
	   * is used when getting the current motor RPM for the shifting gearbox associated with this encoder.
	   * 
	   * @param rpp - the rotations per pulse.
	   */
	  public void setLowGearRPP(double rpp) {
		  lowGearRPP = rpp;
	  }
	  
	  /**
	   * Set the distance per pulse for this encoder. This sets the multiplier used to determine the
	   * distance driven based on the count value from the encoder. Do not include the decoding type in
	   * this scale. The library already compensates for the decoding type. Set this value based on the
	   * encoder's rated Pulses per Revolution and factor in gearing reductions following the encoder
	   * shaft. This distance can be in any units you like, linear or angular.
	   *
	   * @param distancePerPulse The scale factor that will be used to convert pulses to useful units.
	   */
	  @Override
	  public void setDistancePerPulse(double distancePerPulse) {
		  this.distancePerPulse = distancePerPulse;
		  super.setDistancePerPulse(distancePerPulse);
	  }
	
	  /**
	   * Gets the raw rate for this encoder in pulses per second. Since the unit is pulses, the value set
	   * for the distance per pulse has no bearing on the output of this method.
	   * @return the rate the encoder is rotating in pulses per second.
	   */
	  public double getRawRate() {
		  return getRate() / distancePerPulse;
	  }
	  
	  /**
	   * Gets the RPM of the motors powering the shifting gearbox this encoder is attached to. This is
	   * found by multiplying the raw rate of this encoder (the pulses per second) by the set rotations
	   * per puse for the gearbox. That value is then multiplied by 60 to covert the units to RPM.
	   * @return the rate the attached motors are spinning in RPM.
	   */
	  public double getRPM() {
		  double rpp = shifter.isLowGear() ? lowGearRPP : highGearRPP;
		  return Math.abs(getRawRate() * rpp * 60.0);
	  }
	  
	  /**
	   * Gets the encoder's ShifterSolenoid object. This object is used by the encoder to determine if the
	   * gearbox is in low or high gear.
	   * @return the ShifterSolenoid.
	   */
	  public ShifterSolenoid getShifterSolenoid() {
		  return shifter;
	  }
	  
	  /**
	   * Sets the encoder's ShifterSolenoid object. This object is used by the encoder to determine if the
	   * gearbox is in low or high gear.
	   * @param shifterSolenoid - The ShifterSolenoid object for the encoder to use.
	   */
	  public void getShifterSolenoid(ShifterSolenoid shifterSolenoid) {
		  shifter = shifterSolenoid;
	  }
}
