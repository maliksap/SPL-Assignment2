package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.BombFinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.Main;


import java.util.concurrent.CountDownLatch;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    long duration;
//    private CountDownLatch countDownLatch;

//    public LandoMicroservice(long duration, CountDownLatch countDownLatch) {
//        super("Lando");
//        this.duration=duration;
//        this.countDownLatch=countDownLatch;
//    }

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration=duration;
    }

    @Override
    protected void initialize() {
//        MessageBusImpl.getInstance().register(this);

        Callback<BombFinishBroadcast> BombBroadcastCallback = new Callback<BombFinishBroadcast>() {
            @Override
            public void call(BombFinishBroadcast c) {
                Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
                System.out.println("terminate lando :" + System.currentTimeMillis());
                terminate();  //we need to check if its good
            }
        };
        subscribeBroadcast(BombFinishBroadcast.class, BombBroadcastCallback);

        Callback<BombDestroyerEvent> bombEventCallback=new Callback<BombDestroyerEvent>() {
            @Override
            public void call(BombDestroyerEvent c) {
                try{
//                    this.wait(duration); //sleep??????? -original do not change
                    System.out.println("lando go to sleep:" + System.currentTimeMillis());
                    Thread.sleep(duration);  //sapir's change
                    System.out.println("lando wakes up:" + System.currentTimeMillis());

                    complete(c, true);
                    sendBroadcast(new BombFinishBroadcast());
                }catch (InterruptedException e){}
            }
        };
        subscribeEvent(BombDestroyerEvent.class, bombEventCallback);

        Main.getCountDownLatch().countDown();
    }
}

