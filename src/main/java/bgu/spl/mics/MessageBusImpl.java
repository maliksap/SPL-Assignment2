package bgu.spl.mics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServicesQueues;
	ConcurrentHashMap<Class<? extends Message> , BlockingQueue<MicroService>> subEventQueues;
	ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>>  broadcastQueues ;
	ConcurrentHashMap<Event,Future> futureEvents;
	private static MessageBusImpl instance = null;

	private MessageBusImpl()
	{
		microServicesQueues = new ConcurrentHashMap<>();
		subEventQueues = new ConcurrentHashMap<>();
		broadcastQueues = new ConcurrentHashMap<>();
		futureEvents = new ConcurrentHashMap<>();
	}


	public static MessageBusImpl getInstance() {
		if(instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}



	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!subEventQueues.containsKey(type))
		{
			subEventQueues.put(type, new LinkedBlockingQueue<>());
		}
		subEventQueues.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!broadcastQueues.containsKey(type))
		{
			broadcastQueues.put(type, new LinkedBlockingQueue<>());
		}
		broadcastQueues.get(type).add(m);
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		futureEvents.get(e).resolve(result);
//		if(e.getClass().equals(AttackEvent.class))
// 			((AttackEvent)e).getFuture().resolve(result);
//		else if(e.getClass().getName().equals("bgu.spl.mics.application.messages.BombDestroyerEvent"))
//			((BombDestroyerEvent)e).getFuture().resolve(result);
//		else
//			((DeactivationEvent)e).getFuture().resolve(result);
	}


	@Override
	public void sendBroadcast(Broadcast b) {
		for (MicroService m: broadcastQueues.get(b.getClass()))
		{
			microServicesQueues.get(m).add(b);
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> ans = new Future<>();
		MicroService m = subEventQueues.get(e.getClass()).remove();
		microServicesQueues.get(m).add(e);
		subEventQueues.get(e.getClass()).add(m);
		futureEvents.put(e, ans);
		return ans;

//		if(subEventQueues.get(e.getClass()).isEmpty()){
//			ans.resolve(null);
//		}
//		if(e.getClass().equals(AttackEvent.class))
//		{
//			((AttackEvent)e).setFuture(ans);
//			return ans;
//		}
//		else if(e.getClass().getName().equals("bgu.spl.mics.application.messages.BombDestroyerEvent"))
//		{
//			((BombDestroyerEvent)e).setFuture(ans);
//			return ans;
//		}
//		else
//		{
//			((DeactivationEvent)e).setFuture( ans);
//			return ans;
//		}
	}

	@Override
	public void register(MicroService m) {
		microServicesQueues.putIfAbsent(m, new LinkedBlockingQueue<Message>());  //can we assume valid input? or do we need to make sure that m is not already registered?
		}

	@Override
	public void unregister(MicroService m) {   //can we assume valid input? or do we need to make sure that m is registered?
		microServicesQueues.remove(m);
		for (Class<? extends Message> s: broadcastQueues.keySet())
		{
			broadcastQueues.get(s).remove(m);
		}
		for (Class<? extends Message> s: subEventQueues.keySet())
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
