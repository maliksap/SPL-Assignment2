package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackFinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.MessageBusImpl;


import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private CountDownLatch countDownLatch;
    private Attack attack;
    private MessageBusImpl mBus;
    private C3POMicroservice C3PO;
    private AttackEvent attEvent;
    private Broadcast b;

    @BeforeEach
    void setUp() {
        mBus = MessageBusImpl.getInstance();
        C3PO= new C3POMicroservice();
        attEvent=new AttackEvent(attack);
        b=new AttackFinishBroadcast(); //change to Attbroadcast
    }

    @AfterEach
    void clean() {
        mBus.subEventQueues.clear();
        mBus.broadcastQueues.clear();
        mBus.microServicesQueues.clear();
    }

    @Test
    public void subscribeEvent() {
        mBus.register(C3PO);
        mBus.subscribeEvent(attEvent.getClass(), C3PO);
        mBus.sendEvent(attEvent);
        try {
            assertEquals(attEvent, mBus.awaitMessage(C3PO));
        }
        catch (InterruptedException e){}
    }

    @Test
    public void subscribeBroadcast() {
        mBus.register(C3PO);
        mBus.subscribeBroadcast(b.getClass(), C3PO);
        mBus.sendBroadcast(b);
        try {
            assertEquals(b, mBus.awaitMessage(C3PO));
        }
        catch (InterruptedException e){}
    }

    @Test
    void complete() {
        Boolean result = true;
        mBus.register(C3PO);
        mBus.subscribeEvent(attEvent.getClass(),C3PO);
        Future<Boolean> future1 = mBus.sendEvent(attEvent);
        mBus.complete(attEvent, result);
        assertTrue(future1.isDone());
    }

    @Test
    public void sendBroadcast() {
        mBus.register(C3PO);
        mBus.subscribeBroadcast(b.getClass(), C3PO);
        mBus.sendBroadcast(b);
        try{
            assertEquals(b,mBus.awaitMessage(C3PO));
        }
        catch (InterruptedException e){}
    }

    @Test
    public void sendEvent() {
        mBus.register(C3PO);
        mBus.subscribeEvent(attEvent.getClass(),C3PO);
        mBus.sendEvent(attEvent);
        try{
            assertEquals(attEvent,mBus.awaitMessage(C3PO));
        }
        catch (InterruptedException e){}
    }

    @Test
    void register() {
        mBus.register(C3PO);
        mBus.subscribeEvent(attEvent.getClass(),C3PO);
        mBus.sendEvent(attEvent);
        try{
            assertEquals(attEvent,mBus.awaitMessage(C3PO));
        }
        catch (InterruptedException e){}
    }


    @Test
    void awaitMessage() {
        mBus.register(C3PO);
        mBus.subscribeEvent(attEvent.getClass(),C3PO);
        mBus.sendEvent(attEvent);
        try{
            assertEquals(mBus.awaitMessage(C3PO),attEvent);
        }catch(InterruptedException exc) {
            exc.printStackTrace();
        }
    }
}