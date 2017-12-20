package org.team484.api.motion;

import java.util.ArrayList;
import java.util.List;

import org.team484.api.sensor.ShifterEncoder;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;

/**
 * The ShifterDrive class is an alternative to RobotDrive that is designed to be used with a pair of
 * shifting gearboxes. The class takes in a pair of ShifterSolenoid objects and a pair of ShifterEncoder
 * objects and will use the enocder values to automatically tell the ShifterSolenoids when to shift gears.
 * The gear can be set manually as well by running setShifterMode().
 * <p>
 * For this class, high gear is the gear that moves faster but has less pushing power, and low gear is the
 * gear that moves more slowly but has more pushing power.
 *
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
		HIGH,
		LOW,
		AUTO
	}
	
	// A struct to store RobotDrive objects for each Left-Right motor pair
	private List<RobotDrive> drive = new ArrayList<>();
	
	private ShifterMode shifterMode = ShifterMode.AUTO;
	private boolean isInLowGear = true;
	
	private ShifterEncoder leftShifterEncoder;
	private ShifterEncoder rightShifterEncoder;
	
	//Optimal theoretical shifting RPMs multiplied by the operating efficiency
	private double downshiftRPM = 1140 * 0.81;
	private double upshiftRPM = 4190 * 0.81;
	
	/**
	 * Creates a new ShifterDrive object with each gearbox having one motor channel. This constructor
	 * uses the port numbers given for the motors to create new instances of PWM objects. If the speed
	 * controller is not PWM or the port has already been initialized, use the version of this constructor
	 * that takes in SpeedController objects instead.
	 * @param left - the PWM port the left motor is plugged in to.
	 * @param right - the PWM port the right motor is plugged in to.
	 * @param leftShifterEncoder - the encoder object for the left gearbox.
	 * @param rightShifterEncoder - the encoder object for the right gearbox.
	 */
	public ShifterDrive(int left1, int right1, ShifterEncoder leftShifterEncoder,
			ShifterEncoder rightShifterEncoder) {
		RobotDrive drive1 = new RobotDrive(left1, right1);
		drive.add(drive1);
		this.leftShifterEncoder = leftShifterEncoder;
		this.rightShifterEncoder = rightShifterEncoder;
	}
	
	/**
	 * Creates a new ShifterDrive object with each gearbox having two motor channels. This constructor
	 * uses the port numbers given for the motors to create new instances of PWM objects. If the speed
	 * controller is not PWM or the port has already been initialized, use the version of this constructor
	 * that takes in SpeedController objects instead.
	 * @param left1 - the PWM port one of the left motors is plugged in to.
	 * @param left2 - the PWM port the other left motor is plugged in to.
	 * @param right1 - the PWM port one of the right motors is plugged in to.
	 * @param right2 - the PWM port the other right motor is plugged in to.
	 * @param leftShifterEncoder - the encoder object for the left gearbox.
	 * @param rightShifterEncoder - the encoder object for the right gearbox.
	 */
	public ShifterDrive(int left1, int left2, int right1, int right2, ShifterEncoder leftShifterEncoder,
			ShifterEncoder rightShifterEncoder) {
		RobotDrive drive1 = new RobotDrive(left1, right1);
		RobotDrive drive2 = new RobotDrive(left2, right2);
		drive.add(drive1);
		drive.add(drive2);
		this.leftShifterEncoder = leftShifterEncoder;
		this.rightShifterEncoder = rightShifterEncoder;
	}
	
	/**
	 * Creates a new ShifterDrive object with each gearbox having three motor channels. This constructor
	 * uses the port numbers given for the motors to create new instances of PWM objects. If the speed
	 * controller is not PWM or the port has already been initialized, use the version of this constructor
	 * that takes in SpeedController objects instead.
	 * @param left1 - the PWM port one of the left motors is plugged in to.
	 * @param left2 - the PWM port the second left motor is plugged in to.
	 * @param left3 - the PWM port the third left motor is plugged in to
	 * @param right1 - the PWM port one of the right motors is plugged in to.
	 * @param right2 - the PWM port the second right motor is plugged in to.
	 * @param right3 - the PWM port the third right motor is plugged in to.
	 * @param leftShifterEncoder - the encoder object for the left gearbox.
	 * @param rightShifterEncoder - the encoder object for the right gearbox.
	 */
	public ShifterDrive(int left1, int left2, int left3, int right1, int right2, int right3,
			ShifterEncoder leftShifterEncoder, ShifterEncoder rightShifterEncoder) {
		RobotDrive drive1 = new RobotDrive(left1, right1);
		RobotDrive drive2 = new RobotDrive(left2, right2);
		RobotDrive drive3 = new RobotDrive(left3, right3);
		drive.add(drive1);
		drive.add(drive2);
		drive.add(drive3);
		this.leftShifterEncoder = leftShifterEncoder;
		this.rightShifterEncoder = rightShifterEncoder;
	}
	
	/**
	 * Creates a new ShifterDrive object with each gearbox having one speed controller object.
	 * @param left1 - the speed controller for the left motor.
	 * @param right2 - the speed controller for the right motor.
	 * @param leftShifterEncoder - the encoder object for the left gearbox.
	 * @param rightShifterEncoder - the encoder object for the right gearbox.
	 */
	public ShifterDrive(SpeedController left1, SpeedController right1, ShifterEncoder leftShifterEncoder,
			ShifterEncoder rightShifterEncoder) {
		RobotDrive drive1 = new RobotDrive(left1, right1);
		drive.add(drive1);
		this.leftShifterEncoder = leftShifterEncoder;
		this.rightShifterEncoder = rightShifterEncoder;
	}
	
	/**
	 * Creates a new ShifterDrive object with each gearbox having two speed controller objects.
	 * @param left1 - the speed controller for one of the left motors.
	 * @param left2 - the speed controller for the second left motor.
	 * @param right1 - the speed controller for one of the right motors.
	 * @param right2 - the speed controller for the second right motor.
	 * @param leftShifterEncoder - the encoder object for the left gearbox.
	 * @param rightShifterEncoder - the encoder object for the right gearbox.
	 */
	public ShifterDrive(SpeedController left1, SpeedController left2, SpeedController right1,
			SpeedController right2, ShifterEncoder leftShifterEncoder,
			ShifterEncoder rightShifterEncoder) {
		RobotDrive drive1 = new RobotDrive(left1, right1);
		RobotDrive drive2 = new RobotDrive(left2, right2);
		drive.add(drive1);
		drive.add(drive2);
		this.leftShifterEncoder = leftShifterEncoder;
		this.rightShifterEncoder = rightShifterEncoder;
	}
	
	/**
	 * Creates a new ShifterDrive object with each gearbox having three speed controller objects.
	 * @param left1 - the speed controller for one of the left motors.
	 * @param left2 - the speed controller for the second left motor.
	 * @param left3 - the speed controller for the third left motor.
	 * @param right1 - the speed controller for one of the right motors.
	 * @param right2 - the speed controller for the second right motor.
	 * @param right3 - the speed controller for the third right motor.
	 * @param leftShifterEncoder - the encoder object for the left gearbox.
	 * @param rightShifterEncoder - the encoder object for the right gearbox.
	 */
	public ShifterDrive(SpeedController left1, SpeedController left2, SpeedController left3,
			SpeedController right1, SpeedController right2, SpeedController right3,
			ShifterEncoder leftShifterEncoder, ShifterEncoder rightShifterEncoder) {
		RobotDrive drive1 = new RobotDrive(left1, right1);
		RobotDrive drive2 = new RobotDrive(left2, right2);
		RobotDrive drive3 = new RobotDrive(left3, right3);
		drive.add(drive1);
		drive.add(drive2);
		drive.add(drive3);
		this.leftShifterEncoder = leftShifterEncoder;
		this.rightShifterEncoder = rightShifterEncoder;
	}
	
	/**
	 * Sets the RPM at which the drivetrain will switch to low gear when running in auto mode.
	 * @param rpm - The motor RPM.
	 */
	public void setDownshiftRPM(double rpm) {
		downshiftRPM = rpm;
	}
	
	/**
	 * Sets the RPM at which the drivetrain will switch to high gear when running in auto mode.
	 * @param rpm - The motor RPM.
	 */
	public void setUpshiftRPM(double rpm) {
		upshiftRPM = rpm;
	}
	
	/**
	 * Sets if a particular motor on the drivetrain should be inverted. For most gearboxes, this should
	 * not be necessary as all motors on one side of the robot should have the same inversion state. To
	 * change the inversion for all motors, use setInversionOfSide();
	 * @param motor - The left or right side of the robot.
	 * @param motorNumber - The number assigned to the motor when this object was constructed.
	 * @param isInverted - If the motor in question should be inverted.
	 */
	public void setInverted(Side motor, int motorNumber, boolean isInverted) {
		if (drive.size() <= motorNumber) {
			System.err.println("Cannot set inverted value for motor number " + motorNumber +
					". That motor number is not assigned");
			return;
		}
		if (motorNumber < 0) {
			System.err.println("Cannot set inverted value for negative motor number (" + motorNumber + ")");
		}
		
		switch(motor) {
		case LEFT:
			drive.get(motorNumber).setInvertedMotor(MotorType.kFrontLeft, isInverted);
			break;
		case RIGHT:
			drive.get(motorNumber).setInvertedMotor(MotorType.kFrontRight, isInverted);
			break;
		default:
			System.err.println("Unknown motor type: " + motor.name());
		}
	}
	
	/**
	 * Sets if the motors on one side of the robot should be inverted or not. This is useful if a flipped
	 * gearbox results in one side (or both) of the robot moving in the wrong direction.
	 * @param motor - The side of the robot to consider inversion on.
	 * @param isInverted - If the side in question should be inverted.
	 */
	public void setInversionOfSide(Side motor, boolean isInverted) {
		for(int i = 0; i < drive.size(); i++) {
			setInverted(motor, i, isInverted);
		}
	}
	
	/**
	 * Configure the scaling factor for using RobotDrive with motor controllers in a mode other than
	 * PercentVbus. (For example votage compensation mode on the TalonSRX controllers)
	 *
	 * @param maxOutput Multiplied with the output percentage computed by the drive functions.
	 */
	public void setMaxOutput(double maxOutput) {
		for (RobotDrive motorPair : drive) {
			motorPair.setMaxOutput(maxOutput);
		}
	}
	
	public void setShifterMode(ShifterMode mode) {
		this.shifterMode = mode;
	}
	
	/**
	 * Drives the robot in arcade drive. The first value is how quickly the robot should travel forward/
	 * backward and the second value is how quickly the robot should rotate clockwise/counterclockwise.
	 * These values are squared to improve smoothness when operating the robot using a joystick.
	 * @param speed - The speed to drive forward/backward. (from 1 to -1)
	 * @param rotation - The rate at which to rotate clockwise/counterclockwise. (from 1 to -1)
	 */
	public void arcadeDrive(double speed, double rotation) {
		for (RobotDrive motorPair : drive) {
			motorPair.arcadeDrive(speed, rotation);
		}
		checkShifterGear();
	}
	
	/**
	 * Drive the robot in tank drive. The first value is the speed for the left wheels and the second value
	 * is the speed for the right wheels.
	 * @param leftSpeed - Speed of the left wheels. (from 1 to -1)
	 * @param rightSpeed - Speed of the right wheels. (from 1 to -1)
	 */
	public void tankDrive(double leftSpeed, double rightSpeed) {
		for (RobotDrive motorPair : drive) {
			motorPair.tankDrive(leftSpeed, rightSpeed);
		}
		checkShifterGear();
	}
	
	/**
	 * Drives like arcade drive but without the squared inputs. This is useful for autonomous PID loops.
	 * @param speed - The speed to drive forward/backward. (from 1 to -1)
	 * @param rotation - The rate at which to rotate clockwise/counterclockwise. (from 1 to -1)
	 */
	public void linearDrive(double speed, double rotation) {
		for (RobotDrive motorPair : drive) {
			motorPair.arcadeDrive(speed, rotation, false);
		}
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
		leftShifterEncoder.getShifterSolenoid().shiftToHigh();
		rightShifterEncoder.getShifterSolenoid().shiftToHigh();
		isInLowGear = false;
	}
	
	/**
	 * Puts everything in low gear.
	 */
	private void setShifterLow() {
		leftShifterEncoder.getShifterSolenoid().shiftToLow();
		rightShifterEncoder.getShifterSolenoid().shiftToLow();
		isInLowGear = true;
	}
	
	/**
	 * Automatically shifts between high and low gears based on motor RPM.
	 */
	private void autoShift() {
		double leftRPM = leftShifterEncoder.getRPM();
		double rightRPM = rightShifterEncoder.getRPM();
		
		//Get the slower of the two RPM values (assuming the slower value isn't 0)
		double rpm = leftRPM < rightRPM && leftRPM > 0 || rightRPM == 0 ? leftRPM : rightRPM;
					 
		if (isInLowGear) {
			
			if (rpm > upshiftRPM) {
				setShifterHigh();
			} else {
				setShifterLow();
			}
			
		} else {
			
			if (rpm < downshiftRPM) {
				setShifterLow();
			} else {
				setShifterHigh();
			}
			
		}
	}
}
