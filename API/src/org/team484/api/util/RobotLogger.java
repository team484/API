package org.team484.api.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * RobotLogger takes in various WPILIB objects and logs them periodically to a log file on a connected
 * flash drive. RobotLogger will not log to on-board to prevent degredation of local flash memory. Be
 * considerate of what is added to the logger as logging can get verbose quickly. After creating an
 * instance of the logger, call the run method to begin logging. Interrupt the thread to stop logging.
 */
public class RobotLogger extends Thread {

	//----------- Private Declaration of Object Types -----------
	
	/**
	 * A list of all object types the logger supports
	 */
	private enum ObjectType {
		ANALOGINPUT,
		COMPRESSOR,
		DIGITALINPUT,
		DOUBLESOLENOID,
		DRIVERSTATION,
		ENCODER,
		GYRO,
		JOYSTICK,
		PDP,
		RELAY,
		SOLENOID,
		SPEED_CONTROLLER
	}
	
	/**
	 * A struct for storing an added object to the logger
	 */
	private class LoggerObject {
		public ObjectType type;
		public Object obj;
		public String name;
		public LoggerObject(ObjectType type, Object obj, String name) {
			this.type = type;
			this.obj = obj;
			this.name = name;
		}
	}
	
	//------------------------ Constants ------------------------
	private static final String FILE_NAME_PREFIX = "ROBOT_LOG_";
	private static final String FILE_EXTENSION = ".csv";
	private static final String[] saveDirectories = {
			"/U/",
			"/V/",
			"/media/sda",
			"/media/sdb",
	};
	
	
	private ArrayList<LoggerObject> loggerObjects = new ArrayList<>();
	private boolean allowNewObjects = true;
	
	private File activeSaveDirectory;
	private File outputFile;
	private PrintWriter writer;
	
	private long waitTime;
	
	/**
	 * Creates a new RobotLogger instance with a specified time to wait between recording logs. After an
	 * instance is created, call the log method to add items to the logger then the run method to start
	 * logging.
	 * @param msBetweenLogs - Milliseconds between log entries.
	 */
	public RobotLogger(long msBetweenLogs) {
		waitTime = msBetweenLogs;
	}
	
	/**
	 * Adds an analog input to the logger.
	 * @param name - The name to give the analog input.
	 * @param analogInput - The instance of the analog input.
	 */
	public void log(String name, AnalogInput analogInput) {
		addObjectToLogger(ObjectType.ANALOGINPUT, analogInput, name);
	}
	
	/**
	 * Adds a compressor to the logger.
	 * @param name - The name to give the compressor.
	 * @param compressor - The instance of the compressor.
	 */
	public void log(String name, Compressor compressor) {
		addObjectToLogger(ObjectType.COMPRESSOR, compressor, name);
	}
	
	/**
	 * Adds a digital input to the logger.
	 * @param name - The name to give the digital input.
	 * @param digitalInput - The instance of the digital input.
	 */
	public void log(String name, DigitalInput digitalInput) {
		addObjectToLogger(ObjectType.DIGITALINPUT, digitalInput, name);
	}
	
	/**
	 * Adds a double solenoid to the logger.
	 * @param name - The name to give the double solenoid.
	 * @param doubleSolenoid - The instance of the double solenoid.
	 */
	public void log(String name, DoubleSolenoid doubleSolenoid) {
		addObjectToLogger(ObjectType.DOUBLESOLENOID, doubleSolenoid, name);
	}
	
	/**
	 * Adds the driverstation to the logger.
	 * @param name - The name to give the driver station.
	 * @param ds - The instance of the driver station.
	 */
	public void log(String name, DriverStation ds) {
		addObjectToLogger(ObjectType.DRIVERSTATION, ds, name);
	}
	
	/**
	 * Adds an encoder to the logger.
	 * @param name - The name to give the encoder.
	 * @param encoder - The instance of the encoder.
	 */
	public void log(String name, Encoder encoder) {
		addObjectToLogger(ObjectType.ENCODER, encoder, name);
	}
	
	/**
	 * Adds a gyro to the logger.
	 * @param name - The name to give the gyro.
	 * @param gyro - The instance of the gyro.
	 */
	public void log(String name, AnalogGyro gyro) {
		addObjectToLogger(ObjectType.GYRO, gyro, name);
	}
	
	/**
	 * Adds a joystick to the logger.
	 * @param name - The name to give the joystick.
	 * @param joystick - The instance of the joystick.
	 */
	public void log(String name, Joystick joystick) {
		addObjectToLogger(ObjectType.JOYSTICK, joystick, name);
	}
	
	/**
	 * Adds a PDP to the logger.
	 * @param name - The name to give the PDP.
	 * @param pdp - The instance of the PDP.
	 */
	public void log(String name, PowerDistributionPanel pdp) {
		addObjectToLogger(ObjectType.PDP, pdp, name);
	}
	
	/**
	 * Adds a relay to the logger.
	 * @param name - The name to give the relay.
	 * @param relay - The instance of the relay.
	 */
	public void log(String name, Relay relay) {
		addObjectToLogger(ObjectType.RELAY, relay, name);
	}
	
	/**
	 * Adds a solenoid to the logger.
	 * @param name - The name to give the solenoid.
	 * @param solenoid - The instance of the solenoid.
	 */
	public void log(String name, Solenoid solenoid) {
		addObjectToLogger(ObjectType.SOLENOID, solenoid, name);
	}
	
	/**
	 * Adds a speed controller to the logger.
	 * @param name - The name to give the speed controller.
	 * @param speedController - The instance of the speed controller.
	 */
	public void log(String name, SpeedController speedController) {
		addObjectToLogger(ObjectType.SPEED_CONTROLLER, speedController, name);
	}
	
	/**
	 * Adds an object to the logger if it is ok do to so.
	 * @param type - The object type.
	 * @param obj - The object that is being added.
	 * @param name - The name given to the obkect.
	 */
	private void addObjectToLogger(ObjectType type, Object obj, String name) {
		if (!allowNewObjects) {
			System.err.println("Cannot add objects to logger while running");
			return;
		}
		loggerObjects.add(new LoggerObject(type, obj, name));
	}
	
	/**
	 * Runs the logger until the thread is interrupted. To run the logger in a separate thread, use start
	 * instead of run.
	 */
	@Override
	public void run() {
		allowNewObjects = false;
		if (!setActiveSaveDirectory()) return;
		if (!createWriter()) return;
		
		StringBuilder outputString = new StringBuilder();
		for (LoggerObject loggerobj : loggerObjects) {
			writeObjNames(loggerobj, outputString);
		}
		writeLine(outputString);
		
		long loopStart = System.currentTimeMillis();
		while(!Thread.interrupted()) {
			for (LoggerObject loggerobj : loggerObjects) {
				writeObj(loggerobj, outputString);
			}
			writeLine(outputString);
			try {
				long time = System.currentTimeMillis() - loopStart;
				Thread.sleep(Math.max(waitTime - time, 0));
				loopStart = System.currentTimeMillis();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		closeWriter();
		allowNewObjects = true;
	}
	
	/**
	 * Finds the best directory to write the log files to.
	 * @return - If it was successful in finding a flash drive.
	 */
	private boolean setActiveSaveDirectory() {
		activeSaveDirectory = null;
		for (String directory : saveDirectories) {
			File fileDirObj = new File(directory);
			if (fileDirObj.exists()) {
				activeSaveDirectory = fileDirObj;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Creates the print writer object used to write to the log file.
	 * @return - If the creation of the writer was successful.
	 */
	private boolean createWriter() {
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String fileName = FILE_NAME_PREFIX + timeStamp + FILE_EXTENSION;
		outputFile = new File(activeSaveDirectory.getAbsolutePath() + fileName);
		try {
			writer = new PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Writes a line to the writer and clears the string builder cache.
	 * @param line - The line to write.
	 */
	private void writeLine(StringBuilder line) {
		line.setLength(Math.max(line.length() - 1, 0));
		if (writer != null) {
			writer.write(line.toString());
		}
		line.setLength(0);
	}
	
	/**
	 * Flushes and closes the print writer.
	 */
	private void closeWriter() {
		writer.flush();
		writer.close();
	}
	
	/**
	 * Writes the logger object table headers to the string builder.
	 * @param loggerObject - The object that needs to be added to the header.
	 * @param sb - The string builder to write the header to.
	 */
	private static void writeObjNames(LoggerObject loggerObject, StringBuilder sb) {
		switch (loggerObject.type) {
		case ANALOGINPUT:
			sb.append(loggerObject.name + " - voltage,");
			break;
		case COMPRESSOR:
			sb.append(loggerObject.name + " - enabled,");
			sb.append(loggerObject.name + " - current,");
			sb.append(loggerObject.name + " - current too high fault,");
			sb.append(loggerObject.name + " - not connected fault,");
			sb.append(loggerObject.name + " - shorted fault,");
			sb.append(loggerObject.name + " - pressure switch,");
			break;
		case DIGITALINPUT:
			sb.append(loggerObject.name + ",");
			break;
		case DOUBLESOLENOID:
			sb.append(loggerObject.name + " - state,");
			sb.append(loggerObject.name + " - FWD black,");
			sb.append(loggerObject.name + " - REV black,");
			break;
		case DRIVERSTATION:
			sb.append(loggerObject.name + " - time,");
			sb.append(loggerObject.name + " - voltage,");
			sb.append(loggerObject.name + " - enabled,");
			sb.append(loggerObject.name + " - auto,");
			sb.append(loggerObject.name + " - tele,");
			sb.append(loggerObject.name + " - brownout,");
			sb.append(loggerObject.name + " - connection,");
			break;
		case ENCODER:
			sb.append(loggerObject.name + " - distance,");
			sb.append(loggerObject.name + " - speed,");
			break;
		case GYRO:
			sb.append(loggerObject.name + " - angle,");
			sb.append(loggerObject.name + " - rate,");
			break;
		case JOYSTICK:
			Joystick joystick = (Joystick) loggerObject.obj;
			for (int i = 0; i < joystick.getAxisCount(); i++) {
				sb.append(loggerObject.name + "axis " + i + ",");
			}
			for (int i = 1; i < joystick.getButtonCount(); i++) {
				sb.append(loggerObject.name + "button " + i + ",");
			}
			break;
		case PDP:
			sb.append(loggerObject.name + " - temp,");
			sb.append(loggerObject.name + " - current,");
			sb.append(loggerObject.name + " - voltage,");
			for (int i = 0; i < 16; i++) {
				sb.append(loggerObject.name + " - current (" + i + "),");
			}
			break;
		case RELAY:
			sb.append(loggerObject.name + ",");
			break;
		case SOLENOID:
			sb.append(loggerObject.name + ",");
			break;
		case SPEED_CONTROLLER:
			sb.append(loggerObject.name + ",");
			break;
		default:
			break;
		}
	}
	
	/**
	 * Writes the state of a logger object to the string builder.
	 * @param loggerObject - The object used to write the state of.
	 * @param sb - The string builder to write to.
	 */
	private static void writeObj(LoggerObject loggerObject, StringBuilder sb) {
		switch (loggerObject.type) {
		case ANALOGINPUT:
			sb.append(((AnalogInput) loggerObject.obj).getAverageVoltage() + ",");
			break;
		case COMPRESSOR:
			sb.append(((Compressor) loggerObject.obj).enabled() + ",");
			sb.append(((Compressor) loggerObject.obj).getCompressorCurrent() + ",");
			sb.append(((Compressor) loggerObject.obj).getCompressorCurrentTooHighFault() + ",");
			sb.append(((Compressor) loggerObject.obj).getCompressorNotConnectedFault() + ",");
			sb.append(((Compressor) loggerObject.obj).getCompressorShortedFault() + ",");
			sb.append(((Compressor) loggerObject.obj).getPressureSwitchValue() + ",");
			break;
		case DIGITALINPUT:
			sb.append(((DigitalInput) loggerObject.obj).get() + ",");
			break;
		case DOUBLESOLENOID:
			sb.append(((DoubleSolenoid) loggerObject.obj).get().toString() + ",");
			sb.append(((DoubleSolenoid) loggerObject.obj).isFwdSolenoidBlackListed() + ",");
			sb.append(((DoubleSolenoid) loggerObject.obj).isRevSolenoidBlackListed() + ",");
			break;
		case DRIVERSTATION:
			DriverStation ds = (DriverStation) loggerObject.obj;
			sb.append(ds.getMatchTime() + ",");
			sb.append(ds.getBatteryVoltage() + ",");
			sb.append(ds.isEnabled() + ",");
			sb.append(ds.isAutonomous() + ",");
			sb.append(ds.isOperatorControl() + ",");
			sb.append(ds.isBrownedOut() + ",");
			sb.append(ds.isDSAttached() + ",");
			break;
		case ENCODER:
			sb.append(((Encoder) loggerObject.obj).getDistance() + ",");
			sb.append(((Encoder) loggerObject.obj).getRate() + ",");
			break;
		case GYRO:
			sb.append(((AnalogGyro) loggerObject.obj).getAngle() + ",");
			sb.append(((AnalogGyro) loggerObject.obj).getRate() + ",");
			break;
		case JOYSTICK:
			Joystick joystick = (Joystick) loggerObject.obj;
			for (int i = 0; i < joystick.getAxisCount(); i++) {
				sb.append(joystick.getRawAxis(i) + ",");
			}
			for (int i = 1; i < joystick.getButtonCount(); i++) {
				sb.append(joystick.getRawButton(i) + ",");
			}
			break;
		case PDP:
			PowerDistributionPanel pdp = (PowerDistributionPanel) loggerObject.obj;
			sb.append(pdp.getTemperature() + ",");
			sb.append(pdp.getTotalCurrent() + ",");
			sb.append(pdp.getVoltage() + ",");
			for (int i = 0; i < 16; i++) {
				sb.append(pdp.getCurrent(i) + ",");
			}
			break;
		case RELAY:
			sb.append(((Relay) loggerObject.obj).get().toString() + ",");
			break;
		case SOLENOID:
			sb.append(((Solenoid) loggerObject.obj).get() + ",");
			break;
		case SPEED_CONTROLLER:
			sb.append(((SpeedController) loggerObject.obj).get() + ",");
			break;
		default:
			break;
		}
	}
}
