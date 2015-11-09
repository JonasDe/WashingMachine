package todo;

import se.lth.cs.realtime.RTThread;
import done.*;

public class WashingController implements ButtonListener {
	private AbstractWashingMachine mach;
	private double speed;
	private TemperatureController tempC;
	private SpinController spinC;
	private WaterController waterC;
	private RTThread currentThread = new WashingProgram3(mach, speed, tempC,
			waterC, spinC);

	// private WaterController waterC;
	// private WashingProgram0 zero;
	// private WashingProgram1 one;
	// private WashingProgram2 two;
	// private WashingProgram3 three;

	public WashingController(AbstractWashingMachine theMachine, double theSpeed) {
		this.tempC = new TemperatureController(theMachine, theSpeed);
		this.spinC = new SpinController(theMachine, theSpeed);
		this.waterC = new WaterController(theMachine, theSpeed);
		// this.zero = new WashingProgram0(theMachine, theSpeed, tempC, waterC,
		// spinC);
		// this.one = new WashingProgram1(theMachine, theSpeed, tempC, waterC,
		// spinC);
		// this.two = new WashingProgram2(theMachine, theSpeed, tempC, waterC,
		// spinC);
		// this.three = new WashingProgram3(theMachine, theSpeed, tempC, waterC,
		// spinC);
		mach = theMachine;
		speed = theSpeed;
		tempC.start();
		spinC.start();
		waterC.start();

	}

	public void processButton(int theButton) {
		if(theButton == 0 && currentThread.isAlive()) currentThread.interrupt();

		if(!currentThread.isAlive()){
		switch (theButton) {
		case 0:
			currentThread = new WashingProgram0(mach, speed, tempC,
					waterC, spinC); 
			break;
		case 1:
			currentThread = new WashingProgram1(mach, speed, tempC,
					waterC, spinC);
			break;

		case 2:
			currentThread = new WashingProgram2(mach, speed, tempC,
					waterC, spinC);
			break;

		case 3:
			currentThread = new WashingProgram3(mach, speed, tempC,
					waterC, spinC);
			break;

		default:
			break;

		}
		currentThread.start();
		}

	}
}

// private WashingProgram0 zero;
// private WashingProgram1 one;
// private WashingProgram2 two;
// private WashingProgram3 three;
//
// public WashingController(AbstractWashingMachine theMachine, double theSpeed)
// {
// TemperatureController tempC = new TemperatureController(theMachine,
// theSpeed);
// SpinController spinC = new SpinController(theMachine, theSpeed);
// WaterController waterC = new WaterController(theMachine, theSpeed);
// this.zero = new WashingProgram0(theMachine, theSpeed, tempC, waterC,
// spinC);
// this.one = new WashingProgram1(theMachine, theSpeed, tempC, waterC,
// spinC);
// this.two = new WashingProgram2(theMachine, theSpeed, tempC, waterC,
// spinC);
// this.three = new WashingProgram3(theMachine, theSpeed, tempC, waterC,
// spinC);
// tempC.start();
// spinC.start();
// waterC.start();
//
// }
//
// public void processButton(int theButton) {
//
// System.out.println("Button pressed");
// switch (theButton) {
// case 0:
// zero.start();
// break;
// case 1:
// one.start();
// break;
//
// case 2:
// two.start();
// break;
//
// case 3:
// three.start();
// break;
//
// default:
// break;
//
// }
//
// }
