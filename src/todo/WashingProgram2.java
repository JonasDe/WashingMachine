package todo;

import se.lth.cs.realtime.event.RTEvent;
import done.AbstractWashingMachine;

public class WashingProgram2 extends WashingProgram {

	protected WashingProgram2(AbstractWashingMachine mach, double speed,
			TemperatureController tempController,
			WaterController waterController, SpinController spinController) {
			super(mach, speed, tempController, waterController, spinController);
	}

	@Override
	protected void wash() throws InterruptedException {
		
		//Lock the hatch
		myMachine.setLock(true);
		
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));
		
		//Fill with water
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, WaterEvent.WATER_LEVEL));
		mailbox.doFetch();
		
		//Heat to 40 and stay for 15 min
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_SET, 40));
		RTEvent preWash = mailbox.doFetch();
		while((System.currentTimeMillis()/1000) - preWash.getSeconds() < 900/mySpeed) Thread.sleep(1000);
		
		//Main wash in 90
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_SET, 90));
		RTEvent mainWash = mailbox.doFetch();
		// Sleep 30 min?!
		while((System.currentTimeMillis()/1000) - mainWash.getSeconds() < 1800/mySpeed) Thread.sleep(1000);
		
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0));
		mailbox.doFetch();
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_IDLE, 0));
		for(int i = 0; i < 5; i++){
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, WaterEvent.WATER_LEVEL));
			RTEvent waterDone = mailbox.doFetch();
			while((System.currentTimeMillis()/1000) - waterDone.getSeconds() < 120/mySpeed) Thread.sleep(1000);
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0));
			mailbox.doFetch();
		}
		
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_FAST));
		double startTime = System.currentTimeMillis();
		while((System.currentTimeMillis()/1000) - startTime/1000 < 300/mySpeed) Thread.sleep(1000);
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
		
		// Unlock
		myMachine.setLock(false);
	}
	

}
