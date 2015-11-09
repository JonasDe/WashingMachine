package todo;

import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;

public class TemperatureController extends PeriodicThread {
	// TODO: add suitable attributes
	private double desiredTemp;
	private boolean reachedTempEarlier = true;
	private RTThread source;

	private AbstractWashingMachine mach;

	public TemperatureController(AbstractWashingMachine mach, double speed) {
		super((long) (1000 / speed)); // TODO: replace with suitable period
		this.mach = mach;
	}

	public void perform() {
		if (mach.getWaterLevel() == 0) {
			mach.setHeating(false);
			return;
		}
		TemperatureEvent te = (TemperatureEvent) mailbox.tryFetch();
		if (te == null) { // Om inget nytt event
			if (mach.getTemperature() + 0.2 >= desiredTemp) { // Kolla om tempen för hög
				mach.setHeating(false);

			} else if (mach.getTemperature() + 2 < desiredTemp && mach.getWaterLevel() != 0) { //Annars kolla om för låg
				mach.setHeating(true);

			}
			if (!reachedTempEarlier && mach.getTemperature() + 2 >= desiredTemp) { //om ej tidigare uppe i temp, och temp över så skicka event, sätt reached true
				source.putEvent(new AckEvent(this));
				reachedTempEarlier = true;
			}
			return;
		}
		
		source = (RTThread) te.getSource();
		desiredTemp = te.getTemperature();
		int mode = te.getMode();
		//Har fått event
		if (mode == TemperatureEvent.TEMP_SET
				&& mach.getTemperature() + 1 < te.getTemperature() && mach.getWaterLevel() != 0) { //Kollar om temp för låg i och med nya kravet, isf öka 
			mach.setHeating(true);
			reachedTempEarlier = false;

		} else { //annars sänk

			mach.setHeating(false);
		}

	}
}
