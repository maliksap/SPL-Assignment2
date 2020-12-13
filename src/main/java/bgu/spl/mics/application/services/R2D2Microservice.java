package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.BombFinishBroadcast;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.DeactivationFinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.Main;


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
//    private CountDownLatch countDownLatch;

//    public R2D2Microservice(long duration, CountDownLatch countDownLatch) {
//        super("R2D2");
//        this.duration=duration;
//        this.countDownLatch=countDownLatch;
//    }

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration=duration;
    }

    @Override
    protected void initialize() {
//        MessageBusImpl.getInstance().register(this);
        Callback<BombFinishBroadcast> BombBroadcastCallback = new Callback<BombFinishBroadcast>() {
            @Override
            public void call(BombFinishBroadcast c) {
                Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
                System.out.println("terminate r2d2 :" + System.currentTimeMillis());
                terminate();  //we need to check if its good
            }
        };
        subscribeBroadcast(BombFinishBroadcast.class, BombBroadcastCallback);


        Callback<DeactivationEvent> deactCallback=new Callback<DeactivationEvent>() {
            @Override
            public void call(DeactivationEvent c) {
                try{
                    System.out.println("r2d2 goes to sleep:" + System.currentTimeMillis());
                    Thread.sleep(duration);  //sapir's change
                    System.out.println("r2d2 wakes up:" + System.currentTimeMillis());
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
