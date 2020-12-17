package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombFinishBroadcast;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.DeactivationFinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.Main;


/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 */
public class R2D2Microservice extends MicroService {
    long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration=duration;
    }

    /**
     * Subscribe the microservice to the messages that are of it's interest
     */

    @Override
    protected void initialize() {
        Callback<BombFinishBroadcast> BombBroadcastCallback = new Callback<BombFinishBroadcast>() {
            /**
             * A callback that defines instructions for microservice to handle a {@link BombFinishBroadcast}.
             */
            @Override
            public void call(BombFinishBroadcast c) {
                Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
                terminate();
            }
        };
        subscribeBroadcast(BombFinishBroadcast.class, BombBroadcastCallback);

        Callback<DeactivationEvent> deactCallback=new Callback<DeactivationEvent>() {
            /**
             * A callback that defines instructions for microservice to handle a {@link DeactivationEvent}.
             */
            @Override
            public void call(DeactivationEvent c) {
                try{
                    Thread.sleep(duration);
                    complete(c , true);
                    sendBroadcast(new DeactivationFinishBroadcast());
                }catch (InterruptedException e){}
                Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());
            }
        };
        subscribeEvent(DeactivationEvent.class, deactCallback);

        Main.getCountDownLatch().countDown();
    }
}
