package bgu.spl.mics.application.passiveObjects;


import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the resource manager.
 * This class is a thread-safe singleton.
 */
public class Ewoks {
    private static class EwoksHolder{
        private static Ewoks instance = new Ewoks();
    }

    ConcurrentHashMap<Integer,Ewok> ewoksMap=new ConcurrentHashMap<Integer,Ewok>();

    private Ewoks() {
    }

    public static Ewoks getInstance() {
        return Ewoks.EwoksHolder.instance;
    }

    public void setter(int n)
    {
        for(int j=1 ; j<=n ; j++)
        {
            ewoksMap.put(j, new Ewok(j,true));
        }
    }

    /**
     * Acquires a group of Ewoks
     */
    public boolean acquireEwoks(List<Integer> ewoksToAcquire)
    {
        Collections.sort(ewoksToAcquire);
        for(int serial: ewoksToAcquire)
        {
            synchronized (ewoksMap.get(serial)){
                while (!(ewoksMap.get(serial).isAvailable()))
                {
                    try{
                        ewoksMap.get(serial).wait();
                    }
                    catch (InterruptedException e){
                    }
                }
                ewoksMap.get(serial).acquire();
            }
        }
        return true;
    }

    /**
     * Release a group of Ewoks
     */
    public void releaseEwoks(List<Integer> ewoksToRelease)
    {
        for (int serial : ewoksToRelease)
        {
            synchronized (ewoksMap.get(serial)){
                ewoksMap.get(serial).release();
                ewoksMap.get(serial).notifyAll();
            }
        }
    }
}
