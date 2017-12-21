package org.team484.api.motion;

import java.util.ArrayList;
import java.util.Collections;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;

/**
 * SpeedControllerGroup is an implementation of SpeedController that allows for multiple SpeedController
 * objects to be controlled together as if they were a single speed controller. Give the constructor a
 * list of speed controllers (or port numbers) and use the object as if it were a single speed controller.
 * This is useful, for example, in situations where multiple motors are expected to act in unison such as for
 * shooters or multi-motor gearboxes.
 * <p>
 * This class alters the state of the speed controllers given to it. For this reason, it's not recommended to
 * interface with the speed controllers objects given to the constructor from outside the methods of this
 * class. For all getters, the first speed controller object is used to get the values as in most cases all
 * speed controllers should report the same value (unless they're being controlled from outside this object).
 */
public class SpeedControllerGroup implements SpeedController {
	
	/*
	 * A list of all the speed controllers in this group. The first speed controller in this list is
	 * considered the "master" and is used for all getters.
	 */
	private ArrayList<SpeedController> speedControllers = new ArrayList<>();
	
	/**
	 * Creates a new speed controller group with a list of PWM ports. New speed controller objects are
	 * created as Talons here. If you are using a different speed controller, use the other constructor.
	 * @param ports - All the ports that are part of this group.
	 */
	public SpeedControllerGroup(int...ports) {
		for (int port : ports) {
			speedControllers.add(new Talon(port));
		}
	}
	
	/**
	 * Creates a new speed controller group with a list of speed controller objects. This constructor will
	 * also remove all inversions from the speed controllers in order to ensure the speed controllers are
	 * synchronized.
	 * @param controllers - All the controllers that are part of this group.
	 */
	public SpeedControllerGroup(SpeedController...controllers) {
		Collections.addAll(speedControllers, controllers);
		setInverted(false);
	}
	
	/**
	 * Set the output to the value calculated by PIDController.
	 * @param output - The value calculated by PIDController.
	 */
	@Override
	public void pidWrite(double output) {
		for (SpeedController controller : speedControllers) {
			controller.pidWrite(output);
		}
	}

	/**
	 * Method for getting the current set speed of the speed controller group. This getter uses the first
	 * speed controller in the group for its value.
	 * @return The current set speed. Value is between -1.0 and 1.0.
	 */
	@Override
	public double get() {
		if (speedControllers.size() == 0) return 0;
		return speedControllers.get(0).get();
	}

	/**
	 * Method for setting the speed of a speed controller.
	 * @param speed - The speed to set. Value should be between -1.0 and 1.0.
	 */
	@Override
	public void set(double speed) {
		for (SpeedController controller : speedControllers) {
			controller.set(speed);
		}
		
	}

	/**
	 * Method for inverting direction of all speed controllers in the group.
	 * @param isInverted - The state of inversion. True is inverted.
	 */
	@Override
	public void setInverted(boolean isInverted) {
		for (SpeedController controller : speedControllers) {
			controller.setInverted(isInverted);
		}
	}

	/**
	 * Method for returning if the speed controllers in the group are in the inverted state or not. This
	 * getter uses the first speed controller in the group for its value.
	 * @return The state of the inversion. True is inverted.
	 */
	@Override
	public boolean getInverted() {
		if (speedControllers.size() == 0) return false;
		return speedControllers.get(0).getInverted();
	}

	/**
	 * Disable the speed controllers in the group.
	 */
	@Override
	public void disable() {
		for (SpeedController controller : speedControllers) {
			controller.disable();
		}
	}

	/**
	 * Stops motor movement. Motors can be moved again by calling set without having to re-enable the motor.
	 */
	@Override
	public void stopMotor() {
		for (SpeedController controller : speedControllers) {
			controller.stopMotor();
		}
	}

}
