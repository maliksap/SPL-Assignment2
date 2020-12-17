package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.BombFinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.Main;

/**
 * LandoMicroservice is in charge of handling {@link BombDestroyerEvent}.
 */
public class LandoMicroservice  extends MicroService {
    long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
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
                Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
                terminate();
            }
        };
        subscribeBroadcast(BombFinishBroadcast.class, BombBroadcastCallback);

        Callback<BombDestroyerEvent> bombEventCallback=new Callback<BombDestroyerEvent>() {
            /**
             * A callback that defines instructions for microservice to handle a {@link BombDestroyerEvent}.
             */
            @Override
            public void call(BombDestroyerEvent c) {
                try{
                    Thread.sleep(duration);
                    complete(c, true);
                    sendBroadcast(new BombFinishBroadcast());
                }catch (InterruptedException e){}
            }
        };
        subscribeEvent(BombDestroyerEvent.class, bombEventCallback);
        Main.getCountDownLatch().countDown();
    }
}

