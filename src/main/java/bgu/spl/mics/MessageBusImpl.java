package bgu.spl.mics;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	HashMap<MicroService, BlockingQueue<Message>>  microServicesQueues;
	HashMap<String, BlockingQueue<MicroService>> subEventQueues;
	HashMap<String, BlockingQueue<MicroService>>  broadcastQueues ;
	private static MessageBusImpl instance = null;

	private MessageBusImpl()
	{
		microServicesQueues = new HashMap<>();
		subEventQueues = new HashMap<>();
		broadcastQueues = new HashMap<>();
	}


	public static MessageBusImpl getInstance() {
		if(instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}


	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!subEventQueues.containsKey(type.getName()))
		{
			subEventQueues.put(type.getName(), new LinkedBlockingQueue<MicroService>());
		}
		subEventQueues.get(type.getName()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!broadcastQueues.containsKey(type.getName()))
		{
			broadcastQueues.put(type.getName(), new LinkedBlockingQueue<MicroService>());
		}
		broadcastQueues.get(type.getName()).add(m);
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		if(e.getClass().getName().equals("bgu.spl.mics.application.messages.AttackEvent"))
			((AttackEvent)e).getFuture().resolve(result);
		else if(e.getClass().getName().equals("bgu.spl.mics.application.messages.BombDestroyerEvent"))
			((BombDestroyerEvent)e).getFuture().resolve(result);
		else
			((DeactivationEvent)e).getFuture().resolve(result);
	}


	@Override
	public void sendBroadcast(Broadcast b) {
		for (MicroService m: broadcastQueues.get(b.getClass().getName()))
		{
			microServicesQueues.get(m).add(b);
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> ans = new Future<>();
		if(subEventQueues.get(e.getClass().getName()).isEmpty()){
			ans.resolve(null);
		}
		MicroService m = subEventQueues.get(e.getClass().getName()).remove();
		microServicesQueues.get(m).add(e);
		subEventQueues.get(e.getClass().getName()).add(m);
		if(e.getClass().getName().equals("bgu.spl.mics.application.messages.AttackEvent"))
		{
			((AttackEvent)e).setFuture( ans);
			return ans;
		}
		else if(e.getClass().getName().equals("bgu.spl.mics.application.messages.BombDestroyerEvent"))
		{
			((BombDestroyerEvent)e).setFuture(ans);
			return ans;
		}
		else
		{
			((DeactivationEvent)e).setFuture( ans);
			return ans;
		}

	}

	@Override
	public void register(MicroService m) {
		microServicesQueues.putIfAbsent(m, new LinkedBlockingQueue<Message>());  //can we assume valid input? or do we need to make sure that m is not already registered?
		}

	@Override
	public void unregister(MicroService m) {   //can we assume valid input? or do we need to make sure that m is registered?
		microServicesQueues.remove(m);
		for (String s: broadcastQueues.keySet())
		{
			broadcastQueues.get(s).remove(m);
		}
		for (String s: subEventQueues.keySet())
		{
			subEventQueues.get(s).remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message ans;
		try{
			ans = microServicesQueues.get(m).take();
			return ans;
		}
		catch (InterruptedException e) {}
		return null;
	}
}
