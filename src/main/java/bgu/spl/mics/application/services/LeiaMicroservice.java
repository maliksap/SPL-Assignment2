package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;

import bgu.spl.mics.MicroService;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	final Attack[] attacks;
    Future[] eventsFollowUp;


    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		eventsFollowUp = new Future[attacks.length];

//		int counter=1;
//		for(int i=0; i<attacks.length; i++){
//		    AttackEvent counter = new AttackEvent(attacks[i]);
//		    eventFollowUp.put(new)
//        }
    }

    @Override
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        Callback<AttackFinishBroadcast> AttBroadcastCallback = new Callback<AttackFinishBroadcast>() {
            @Override
            public void call(AttackFinishBroadcast c) {
                boolean attacksDone = true;
                for (Future future : eventsFollowUp) {
                    if (!future.isDone()) {
                        attacksDone = false;
                        break;
                    }
                }
                if (!attacksDone)
                    sendEvent(new DeactivationEvent());
            }
        };
        subscribeBroadcast(AttackFinishBroadcast.class ,AttBroadcastCallback );
        Callback<DeactivationFinishBroadcast> DecBroadcastCallback = new Callback<DeactivationFinishBroadcast>() {
            @Override
            public void call(DeactivationFinishBroadcast c) {
                sendEvent(new BombDestroyerEvent());
            }
        };
        subscribeBroadcast(DeactivationFinishBroadcast.class, DecBroadcastCallback);

        Callback<BombFinishBroadcast> BombBroadcastCallback = new Callback<BombFinishBroadcast>() {
            @Override
            public void call(BombFinishBroadcast c) {
                terminate();  //we need to check if its good
            }
        };

        subscribeBroadcast(BombFinishBroadcast.class, BombBroadcastCallback);


        try{
        wait(600);
        }catch (InterruptedException e){}

        for (int i = 0 ; i<eventsFollowUp.length ; i++){
            eventsFollowUp[i] = sendEvent(new AttackEvent(attacks[i]));
        }
    }
}
