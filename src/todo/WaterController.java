package todo;

import se.lth.cs.realtime.*;
import se.lth.cs.realtime.event.RTEvent;
import done.AbstractWashingMachine;

public class WaterController extends PeriodicThread {
	private AbstractWashingMachine mach;
	private double desiredLevel;
	private boolean reachedLevel;
	private int currentMode = WaterEvent.WATER_IDLE;
	private RTThread source;
	public WaterController(AbstractWashingMachine mach, double speed) {
		super((long) (1000 / speed)); // TODO: replace with suitable period
		this.mach = mach;
	}

	public void perform() {
		WaterEvent we = (WaterEvent) mailbox.tryFetch();

		if (we != null ) {
			source = (RTThread) we.getSource();
			reachedLevel = false;
			desiredLevel = we.getLevel();
			currentMode = we.getMode();
			if (currentMode == WaterEvent.WATER_DRAIN) {
				mach.setFill(false);
				mach.setDrain(true);
			} else if (currentMode == WaterEvent.WATER_FILL
					&& mach.getWaterLevel() < desiredLevel) {
				mach.setFill(true);
				mach.setDrain(false);
			} else if (currentMode == WaterEvent.WATER_IDLE) {
				mach.setFill(false);
				mach.setDrain(false);
			}

		}
		if (mach.getWaterLevel() >= desiredLevel && currentMode == WaterEvent.WATER_FILL) {
			sendEvent();
			mach.setFill(false);
			reachedLevel = true;
		}
		if (mach.getWaterLevel() <= desiredLevel && currentMode == WaterEvent.WATER_DRAIN) {
			sendEvent();
			mach.setDrain(false);
			reachedLevel = true;
		} 
		
		

		// ((RTThread) we.getSource()).putEvent(new AckEvent(this));

	}

	private void sendEvent() {
		if (source != null && reachedLevel == false) {
			source.putEvent(new AckEvent(this));
		}
	}
}
