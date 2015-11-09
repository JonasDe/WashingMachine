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
			if (mach.getTemperature() + 0.2 >= desiredTemp) { // Kolla om tempen f�r h�g
				mach.setHeating(false);

			} else if (mach.getTemperature() + 2 < desiredTemp && mach.getWaterLevel() != 0) { //Annars kolla om f�r l�g
				mach.setHeating(true);

			}
			if (!reachedTempEarlier && mach.getTemperature() + 2 >= desiredTemp) { //om ej tidigare uppe i temp, och temp �ver s� skicka event, s�tt reached true
				source.putEvent(new AckEvent(this));
				reachedTempEarlier = true;
			}
			return;
		}
		
		source = (RTThread) te.getSource();
		desiredTemp = te.getTemperature();
		int mode = te.getMode();
		//Har f�tt event
		if (mode == TemperatureEvent.TEMP_SET
				&& mach.getTemperature() + 1 < te.getTemperature() && mach.getWaterLevel() != 0) { //Kollar om temp f�r l�g i och med nya kravet, isf �ka 
			mach.setHeating(true);
			reachedTempEarlier = false;

		} else { //annars s�nk

			mach.setHeating(false);
		}

	}
}
