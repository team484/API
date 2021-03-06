package org.team484.api.motion;

import org.team484.api.sensor.ShifterEncoder;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * The ShifterDrive class is an alternative to RobotDrive that is designed to be used with a pair of
 * shifting gearboxes. The class takes in a pair of ShifterEncoder objects and will use the enocder values to
 * automatically tell the ShifterSolenoids when to shift gears. The gear can be set manually as well by
 * running setShifterMode().
 * <p>
 * For this class, high gear is the gear that moves faster but has less pushing power, and low gear is the
 * gear that moves more slowly but has more pushing power.
 */
public class ShifterDrive {
	
	/**
	 * An enumeration to specific the applicable side of the robot. Either left or right.
	 */
	public enum Side {
		LEFT,
		RIGHT
	}
	
	/**
	 * An enumeration to specify the applicable shifter mode. High refers to maintaining high gear, low
	 * refers to maintaining low gear, and auto refers to autmatically shifting between high and low
	 * gears depending on the robot's speed.
	 */
	public enum ShifterMode {
		/**
		 * The high (fast) gearing
		 */
		HIGH,
		
		/**
		 * The low (slow) gearing
		 */
		LOW,
		
		/**
		 * Automatically switch back and fourth between high and low depending on the situation
		 */
		AUTO
	}
	
	private DifferentialDrive drive;
	private SpeedController leftSC, rightSC;
	
	private ShifterMode shifterMode = ShifterMode.AUTO;
	private boolean isInLowGear = true;
	
	private ShifterEncoder leftShifterEncoder;
	private ShifterEncoder rightShifterEncoder;
	
	//The robot shifts to high gear when its speed >shiftingSpeed + deadband
	//The robot shifts to low gear when its speed <shiftingSpeed - deadband
	private double shiftingSpeed = 70;
	private double shiftingDeadband = 5;
	
	private long lastShiftTime = 0; //The last time (UNIX time) a shift occured
	private int shiftingWaitTime = 500; //A cooldown time (in ms)
	
	/**
	 * Creates a new ShifterDrive object using a Speed Controller Group for each side of the robot as well as
	 * a Shifter Encoder for each side. The solenoids in the shifter encoder objects are used for shifting.
	 * @param left - The group of speed controllers that power the left gearbox.
	 * @param right - The group of speed controllers that power the left gearbox.
	 * @param leftShifterEncoder - The encoder object for the left gearbox.
	 * @param rightShifterEncoder - The encoder object for the right gearbox.
	 */
	public ShifterDrive(SpeedControllerGroup left, SpeedControllerGroup right, ShifterEncoder leftShifterEncoder,
			ShifterEncoder rightShifterEncoder) {
		drive = new DifferentialDrive(left, right);
		leftSC = left;
		rightSC = right;
		this.leftShifterEncoder = leftShifterEncoder;
		this.rightShifterEncoder = rightShifterEncoder;
	}
	
	/**
	 * Sets the speed at which the shifter will change back and fourth between high and low gears.
	 * @param speed - speed in units per seconds.
	 */
	public void setShiftingSpeed(double speed) {
		shiftingSpeed = speed;
	}
	
	/**
	 * Sets the deadband for shifting (in units per second)
	 * For the shifter to auto shift up, it must pass a speed of shiftingSpeed + deadband.
	 * To shift down, the speed must be less than shiftingSpeed - deadband.
	 * @param deadband - the deadband in the shifter
	 */
	public void setShiftingDeadband(double deadband) {
		shiftingDeadband = deadband;
	}
	
	/**
	 * Sets the cooldown time between shifts. Will wait to shift again until so many milliseconds after the
	 * last shift event. This number is in milliseconds.
	 * @param ms - Cooldown time in milliseconds (default 500)
	 */
	public void setShiftingCooldown(int ms) {
		shiftingWaitTime = ms;
	}
	
	/**
	 * Sets if the motors on one side of the robot should be inverted or not. This is useful if a flipped
	 * gearbox results in one (or both) side of the robot moving in the wrong direction.
	 * @param motor - The side of the robot to consider inversion on.
	 * @param isInverted - If the side in question should be inverted.
	 */
	public void setInversionOfSide(Side motor, boolean isInverted) {
		switch(motor) {
		case LEFT:
			leftSC.setInverted(isInverted);
			break;
		case RIGHT:
			rightSC.setInverted(isInverted);
			break;
		default:
			System.err.println("Unknown motor type: " + motor.name());
		}
	}
	
	/**
	 * Configure the scaling factor for using RobotDrive with motor controllers in a mode other than
	 * PercentVbus. (For example votage compensation mode on the TalonSRX controllers)
	 *
	 * @param maxOutput Multiplied with the output percentage computed by the drive functions.
	 */
	public void setMaxOutput(double maxOutput) {
		drive.setMaxOutput(maxOutput);
	}
	
	/**
	 * Sets which "shifting mode" the drivetrain should be in. High means that the drivetrain should shift
	 * to high gear and remain there. Low means the drivetrain should shift into low gear instead of high.
	 * Auto means the drivetrain should automatically switch back and forth between gears for optimal power.
	 * This method does not need to be repeatedly called. Only once when switching modes.
	 * @param mode - The mode to switch to.
	 */
	public void setShifterMode(ShifterMode mode) {
		this.shifterMode = mode;
	}
	
	/**
	 * Gets which "shifting mode" the drivetrain is currently running in. The three possible modes are low,
	 * high, or auto. Low means the drivetrain is set to operate in low gear, high means the drivetrain is
	 * set to operate in high gear, auto means the drivetrain is set to automatically switch between low and
	 * high gear depending on the situation.
	 * @return the shifting mode
	 */
	public ShifterMode getShifterMode() {
		return this.shifterMode;
	}
	
	/**
	 * Drives the robot in arcade drive. The first value is how quickly the robot should travel forward/
	 * backward and the second value is how quickly the robot should rotate clockwise/counterclockwise.
	 * These values are squared to improve smoothness when operating the robot using a joystick.
	 * @param speed - The speed to drive forward/backward. (from 1 to -1)
	 * @param rotation - The rate at which to rotate clockwise/counterclockwise. (from 1 to -1)
	 */
	public void arcadeDrive(double speed, double rotation) {
		drive.arcadeDrive(speed, rotation);
		checkShifterGear();
	}
	
	/**
	 * Drive the robot in tank drive. The first value is the speed for the left wheels and the second value
	 * is the speed for the right wheels.
	 * @param leftSpeed - Speed of the left wheels. (from 1 to -1)
	 * @param rightSpeed - Speed of the right wheels. (from 1 to -1)
	 */
	public void tankDrive(double leftSpeed, double rightSpeed) {
		drive.tankDrive(leftSpeed, rightSpeed);
		checkShifterGear();
	}
	
	/**
	 * Drives like arcade drive but without the squared inputs. This is useful for autonomous PID loops.
	 * @param speed - The speed to drive forward/backward. (from 1 to -1)
	 * @param rotation - The rate at which to rotate clockwise/counterclockwise. (from 1 to -1)
	 */
	public void linearDrive(double speed, double rotation) {
		drive.arcadeDrive(speed, rotation, false);
		checkShifterGear();
	}
	
	/**
	 * Checks the current ShifterMode to determine which shifter method to use.
	 */
	private void checkShifterGear() {
		switch(shifterMode) {
		case HIGH:
			setShifterHigh();
			break;
		case LOW:
			setShifterLow();
			break;
		case AUTO:
			autoShift();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Puts everything in high gear.
	 */
	private void setShifterHigh() {
		if (Math.abs(System.currentTimeMillis() - lastShiftTime) < shiftingWaitTime) {
			return;
		}
		lastShiftTime = System.currentTimeMillis();
		leftShifterEncoder.getShifterSolenoid().shiftToHigh();
		rightShifterEncoder.getShifterSolenoid().shiftToHigh();
		isInLowGear = false;
	}
	
	/**
	 * Puts everything in low gear.
	 */
	private void setShifterLow() {
		if (Math.abs(System.currentTimeMillis() - lastShiftTime) < shiftingWaitTime) {
			return;
		}
		lastShiftTime = System.currentTimeMillis();
		leftShifterEncoder.getShifterSolenoid().shiftToLow();
		rightShifterEncoder.getShifterSolenoid().shiftToLow();
		isInLowGear = true;
	}
	
	/**
	 * Automatically shifts between high and low gears based on motor RPM.
	 */
	private void autoShift() {
		
		double leftSpeed = Math.abs(leftShifterEncoder.getRate());
		double rightSpeed = Math.abs(rightShifterEncoder.getRate());
		
		double speed;
		if (leftSpeed == 0) {
			speed = rightSpeed;
		} else if (rightSpeed == 0) {
			speed = leftSpeed;
		} else {
			speed = Math.max(leftSpeed, rightSpeed);
		}
		
		if (isInLowGear) {
			if (speed > shiftingSpeed+shiftingDeadband) {
				setShifterHigh();
			} else {
				setShifterLow();
			}
			
		} else {
			
			if (speed < shiftingSpeed - shiftingDeadband) {
				setShifterLow();
			} else {
				setShifterHigh();
			}
			
		}
	}
}
