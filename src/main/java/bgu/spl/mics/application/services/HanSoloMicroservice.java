package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.AttackFinishBroadcast;
import bgu.spl.mics.application.messages.BombFinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.Main;


/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
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
                Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
                terminate();
            }
        };
        subscribeBroadcast(BombFinishBroadcast.class, BombBroadcastCallback);

        Callback<AttackEvent> attEventCallback=new Callback<AttackEvent>() {
            /**
             * A callback that defines instructions for microservice to handle an {@link AttackEvent}.
             */
            @Override
            public void call(AttackEvent att) {
                try{
                    Ewoks.getInstance().acquireEwoks(att.getAttack().getSerials());
                    Thread.sleep(att.getAttack().getDuration());
                    complete(att, true);
                    Ewoks.getInstance().releaseEwoks(att.getAttack().getSerials());
                    sendBroadcast(new AttackFinishBroadcast());

                }catch (InterruptedException e){}
                Diary.getInstance().increaseTotalAttacks();
                Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
            }
        };
        subscribeEvent(AttackEvent.class, attEventCallback);
        Main.getCountDownLatch().countDown();
    }
}
