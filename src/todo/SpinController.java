package todo;


import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;


public class SpinController extends PeriodicThread {
	private AbstractWashingMachine mach;
	public static double SPIN_PERIOD = 10.0;
	private double lastSpin;
	private boolean rightSpin;
	private int spinning;
	public SpinController(AbstractWashingMachine mach, double speed) {
		super((long) (1000/speed)); // TODO: replace with suitable period
		this.mach = mach;
	}

	public void perform() {
		SpinEvent se = (SpinEvent) mailbox.tryFetch();
		if(se == null && spinning == SpinEvent.SPIN_SLOW){
			spin();
			return;
		}else if (se == null){
			return;
		}
		
		int mode = se.getMode();
		
		if(mode == SpinEvent.SPIN_FAST	&& mach.getWaterLevel() == 0){
			mach.setSpin(AbstractWashingMachine.SPIN_FAST);
			spinning = SpinEvent.SPIN_FAST;
			
		}else if(mode == SpinEvent.SPIN_SLOW){
			spin();
			spinning = SpinEvent.SPIN_SLOW;
		}else{
			mach.setSpin(AbstractWashingMachine.SPIN_OFF);
			spinning = SpinEvent.SPIN_OFF;
		}
		
//		((RTThread) se.getSource()).putEvent(new AckEvent(this));
//		mailbox.doPost(new AckEvent(se.getSource()));

		
	}
	
	private void spin(){
		double time = System.currentTimeMillis()/1000;
		
		if(time - lastSpin > SPIN_PERIOD){
			
			rightSpin = !rightSpin;
			
			if(rightSpin){
				System.out.println("Spinnig right");
				mach.setSpin(AbstractWashingMachine.SPIN_RIGHT);
			}else{
				System.out.println("Spinnig left");
				mach.setSpin(AbstractWashingMachine.SPIN_LEFT);
			}
			lastSpin = time;
		}
		
		
	}
}
