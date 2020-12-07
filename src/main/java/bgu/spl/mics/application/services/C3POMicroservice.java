package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.AttackFinishBroadcast;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.BombFinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.concurrent.CountDownLatch;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    private CountDownLatch countDownLatch;

    public C3POMicroservice(CountDownLatch countDownLatch) {
        super("C3PO");
        countDownLatch=countDownLatch;
    }

    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        Callback<BombFinishBroadcast> BombBroadcastCallback = new Callback<BombFinishBroadcast>() {
            @Override
            public void call(BombFinishBroadcast c) {
                terminate();  //we need to check if its good
            }
        };
        subscribeBroadcast(BombFinishBroadcast.class, BombBroadcastCallback);

        Callback<AttackEvent> attEventCallback=new Callback<AttackEvent>() {
            @Override
            public void call(AttackEvent att) { //attEvent
                try{
                    Ewoks.getInstance().acquireEwoks(att.getAttack().getSerials());
                    this.wait(att.getAttack().getDuration());
                    complete(att, true);
                    sendBroadcast(new AttackFinishBroadcast());
                    // TODO: change wait to sleep?? if yes, how?

                }catch (InterruptedException e){}
                //TODO  update diary?
            }
        };
        subscribeEvent(AttackEvent.class, attEventCallback);
        countDownLatch.countDown();
    }
}
