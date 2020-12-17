package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 */
public class Ewok {
	int serialNumber;
    boolean available;

    /**
     * check if an Ewok is available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * constructor
     */
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
