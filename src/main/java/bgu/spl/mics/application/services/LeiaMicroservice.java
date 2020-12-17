package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as {@link AttackEvent}.
 * Leia is in charge of sending {@link DeactivationEvent} when all {@link AttackEvent} are handled, and
 * also in charge of sending {@link BombDestroyerEvent} when {@link DeactivationEvent} is completed.
 */
public class LeiaMicroservice extends MicroService {
	final Attack[] attacks;
    Future[] eventsFollowUp;


    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		eventsFollowUp = new Future[attacks.length];
    }

    /**
     * Subscribe the microservice to the messages that are of it's interest
     * and send all {@link AttackEvent}.
     */
    @Override
    protected void initialize() {
        Callback<AttackFinishBroadcast> AttBroadcastCallback = new Callback<AttackFinishBroadcast>() {
            /**
             * A callback that defines instructions for microservice to handle an {@link AttackFinishBroadcast}.
             */
            @Override
            public void call(AttackFinishBroadcast c) {
                boolean attacksDone = true;
                for (Future future : eventsFollowUp) {
                    if (!future.isDone()) {
                        attacksDone = false;
                        break;
                    }
                }
                if (attacksDone)
                    sendEvent(new DeactivationEvent());
            }
        };
        subscribeBroadcast(AttackFinishBroadcast.class ,AttBroadcastCallback );
        Callback<DeactivationFinishBroadcast> DecBroadcastCallback = new Callback<DeactivationFinishBroadcast>() {
            /**
             * A callback that defines instructions for microservice to handle a {@link DeactivationFinishBroadcast}.
             */
            @Override
            public void call(DeactivationFinishBroadcast c) {
                sendEvent(new BombDestroyerEvent());
            }
        };
        subscribeBroadcast(DeactivationFinishBroadcast.class, DecBroadcastCallback);

        Callback<BombFinishBroadcast> BombBroadcastCallback = new Callback<BombFinishBroadcast>() {
            /**
             * A callback that defines instructions for microservice to handle a {@link BombFinishBroadcast}.
             */
            @Override
            public void call(BombFinishBroadcast c) {
                Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
                terminate();
            }
        };
        subscribeBroadcast(BombFinishBroadcast.class, BombBroadcastCallback);

        for (int i = 0 ; i<eventsFollowUp.length ; i++){
            eventsFollowUp[i] = sendEvent(new AttackEvent(attacks[i]));
        }
    }
}
