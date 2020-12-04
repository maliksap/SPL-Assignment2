package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
	int serialNumber;

    public boolean isAvailable() {
        return available;
    }

    boolean available;

	public Ewok(int serNum, boolean some){
	    serialNumber = serNum;
	    available = some;
    }
	
  
    /**
     * Acquires an Ewok
     */
    public void acquire() {
        available = false;
		
    }

    /**
     * release an Ewok
     */
    public void release() {
    	available = true;
    }
}
