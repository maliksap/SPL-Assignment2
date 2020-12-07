package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.BombFinishBroadcast;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.DeactivationFinishBroadcast;

import java.util.concurrent.CountDownLatch;

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
    private CountDownLatch countDownLatch;

    public R2D2Microservice(long duration, CountDownLatch countDownLatch) {
        super("R2D2");
        duration=duration;
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


        Callback<DeactivationEvent> deactCallback=new Callback<DeactivationEvent>() {
            @Override
            public void call(DeactivationEvent c) {
                try{
                    this.wait(duration); //sleep???????
                    complete(c , true);
                    sendBroadcast(new DeactivationFinishBroadcast());
                }catch (InterruptedException e){}
                //TODO update diary?
            }
        };
        subscribeEvent(DeactivationEvent.class, deactCallback);
        // TODO subscribe to relevant broadcasts

        countDownLatch.countDown();

    }
}
