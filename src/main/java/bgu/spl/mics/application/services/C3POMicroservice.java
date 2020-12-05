package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
	
    public C3POMicroservice() {
        super("C3PO");
    }

    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        Callback<AttackEvent> attEventCallback=new Callback() {
            @Override
            public void call(Object c) {
                try{

                   //it receives an Attack and try to own the resources it needs.
                    //then the thread will go to sleep for the amount of time specified in
                    // the field member "duration" of the Attack it received
                }catch (InterruptedException e){}
                //TODO sends broadcast and update diary?
            }
        };
        subscribeEvent(AttackEvent.class, attEventCallback);
        // TODO subscribe to relevant broadcasts?


    }
}
