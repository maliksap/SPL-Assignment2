package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    long duration;
    public R2D2Microservice(long duration) {
        super("R2D2");
        duration=duration;
    }

    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        Callback<DeactivationEvent> deactCallback=new Callback() {
            @Override
            public void call(Object c) {
                try{
                    this.wait(duration); //sleep???????
                }catch (InterruptedException e){}
                //TODO sends broadcast and update diary?
            }
        };
        subscribeEvent(DeactivationEvent.class, deactCallback);
        // TODO subscribe to relevant broadcasts
    }
}
