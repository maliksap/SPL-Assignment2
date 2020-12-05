package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    long duration;
    public LandoMicroservice(long duration) {
        super("Lando");
        duration=duration;
    }

    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        Callback<BombDestroyerEvent> bombEventCallback=new Callback() {
            @Override
            public void call(Object c) {
                try{
                    this.wait(duration); //sleep???????
                }catch (InterruptedException e){}
                //TODO sends broadcast and update diary?
            }
        };
        subscribeEvent(BombDestroyerEvent.class, bombEventCallback);
        // TODO subscribe to relevant broadcasts

    }
}

