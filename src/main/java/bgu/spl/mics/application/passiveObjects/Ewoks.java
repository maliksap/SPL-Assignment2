package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MessageBusImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static Ewoks instance = null;
    ConcurrentHashMap<Integer,Ewok> ewoksMap=new ConcurrentHashMap<Integer,Ewok>();
    volatile ConcurrentHashMap<Integer, AtomicBoolean > lockMap=new ConcurrentHashMap<Integer,AtomicBoolean>();

    private Ewoks() {
    }

    public static Ewoks getInstance() {
        if(instance == null) {
            instance = new Ewoks();
        }
        return instance;
    }

    public void setter(int n)
    {
        for(int j=1 ; j<=n ; j++)
        {
            ewoksMap.put(j, new Ewok(j,true));
            lockMap.put(j,new AtomicBoolean(true));
        }
    }

    public boolean acquireEwoks(List<Integer> ewoksToAcquire)
    {
        Collections.sort(ewoksToAcquire);
        for(int serial: ewoksToAcquire)
        {
             while (!(lockMap.get(serial)).compareAndSet(true, false))
             {
                 try{
                     this.wait();
                 }
                 catch (InterruptedException e){
                 }
             }
             ewoksMap.get(serial).acquire();
        }
        return true;
    }
    public void releaseEwoks(List<Integer> ewoksToRelease)
    {
        for (int serial : ewoksToRelease)
        {
            ewoksMap.get(serial).release();
            lockMap.get(serial).compareAndSet(false,true);
            this.notifyAll();
        }

    }
}
