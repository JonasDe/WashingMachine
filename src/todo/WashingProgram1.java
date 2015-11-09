package todo;

import se.lth.cs.realtime.event.RTEvent;
import done.AbstractWashingMachine;

public class WashingProgram1 extends WashingProgram {
	
	private double speed;
	protected WashingProgram1(AbstractWashingMachine mach, double speed,
			TemperatureController tempController,
			WaterController waterController, SpinController spinController) {
		
			super(mach, speed, tempController, waterController, spinController);
			this.speed = speed;
	}

	@Override
	protected void wash() throws InterruptedException {
		
		//Lock the hatch
		myMachine.setLock(true);
		
		
		//Fill with water
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, WaterEvent.WATER_LEVEL));
		mailbox.doFetch();
//		while(myMachine.getWaterLevel() < WaterEvent.WATER_LEVEL) sleep(1000); 
		
		
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));
	
		//Heat to 60 and stay for 30 min
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_SET, 60));
		RTEvent rt = mailbox.doFetch();
//		while(myMachine.getTemperature() < 58) sleep(1000);
		// Sleep 30 min?!
//		System.out.println("RtGetSeconds " + System.currentTimeMillis()/1000));
		while((System.currentTimeMillis()/1000) - rt.getSeconds() < 1800/speed) Thread.sleep(1000);
		
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0));
		mailbox.doFetch();
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_IDLE, 0));
		for(int i = 0; i < 5; i++){
			System.out.println("i " + i + " Rinsing");
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, WaterEvent.WATER_LEVEL));
			RTEvent waterDone = mailbox.doFetch();
			while((System.currentTimeMillis()/1000) - waterDone.getSeconds() < 120/speed) Thread.sleep(1000);
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0));
			mailbox.doFetch();
		}
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_FAST));
//		RTEvent centrifuge = mailbox.doFetch();
		double startTime = System.currentTimeMillis();
		while((System.currentTimeMillis()/1000) - startTime/1000 < 300/speed) Thread.sleep(1000);
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));
		// Unlock
		myMachine.setLock(false);
	}
	

}
