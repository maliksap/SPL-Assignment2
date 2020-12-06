package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        Callback<AttackEvent> attEventCallback=new Callback<AttackEvent>() {
            @Override
            public void call(AttackEvent att) { //attEvent
                try{
                    Ewoks.getInstance().acquireEwoks(att.getAttack().getSerials());
                    this.wait(att.getAttack().getDuration()); // TODO: change wait to sleep?? if yes, how?

                }catch (InterruptedException e){}
                //TODO sends broadcast and update diary?
            }
        };
        subscribeEvent(AttackEvent.class, attEventCallback);
        // TODO subscribe to relevant broadcasts?
    }
}
