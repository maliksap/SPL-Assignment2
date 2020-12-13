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
//		private static MessageBusImpl instance = null;
	private static class MsgBusHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl()
	{
		this.microServicesQueues = new ConcurrentHashMap<>();
		this.subEventQueues = new ConcurrentHashMap<>();
		this.broadcastQueues = new ConcurrentHashMap<>();
		this.futureEvents = new ConcurrentHashMap<>();
	}


	public static MessageBusImpl getInstance() {
//		if(instance == null) {
//			instance = new MessageBusImpl();
//		}
//		return instance;
		return MsgBusHolder.instance;
	}



	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
//		if (!subEventQueues.containsKey(type))
//		{
//			subEventQueues.put(type, new LinkedBlockingQueue<>());
//		}
		subEventQueues.putIfAbsent(type, new LinkedBlockingQueue<>());
//		synchronized (subEventQueues.get(type)){

					subEventQueues.get(type).add(m);
	}
//	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
//		if (!broadcastQueues.containsKey(type))
//		{
//			broadcastQueues.put(type, new LinkedBlockingQueue<>());
//		}
		broadcastQueues.putIfAbsent(type, new LinkedBlockingQueue<>());
//		synchronized (broadcastQueues.get(type)){

			broadcastQueues.get(type).add(m);
		}

//		System.out.println("microservice: " + m.getName() +"	broadcastQueues type: " + type.getName() + "	broadcastQueues keys: " + broadcastQueues.get(type).size());

//	}

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
		if (broadcastQueues.containsKey(b.getClass())) {
//			synchronized (broadcastQueues.get(b.getClass())) {
				if (!broadcastQueues.get(b.getClass()).isEmpty()) {
					for (MicroService m : broadcastQueues.get(b.getClass())) {
//						synchronized (microServicesQueues.get(m)) {
//					System.out.println("microservice: " + m.getName() + "	microservicequeues keys: " + microServicesQueues.size());
							microServicesQueues.get(m).add(b);
//							microServicesQueues.get(m).notifyAll();

						}
					}
				}
			}
//		}
//	}


	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {  //Todo: add synchronize (or not?)
		Future<T> ans = new Future<>();
		futureEvents.put(e, ans);
		if (subEventQueues.containsKey(e.getClass())) {
//			synchronized (subEventQueues.get(e.getClass())) {
				if (!subEventQueues.get(e.getClass()).isEmpty()) {
					MicroService m = subEventQueues.get(e.getClass()).remove();
//					synchronized (microServicesQueues.get(m)) {
						microServicesQueues.get(m).add(e);
						subEventQueues.get(e.getClass()).add(m);

//						microServicesQueues.get(m).notifyAll();
						return ans;
					}
				}
//			}
//		}
		return null;

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
		microServicesQueues.putIfAbsent(m, new LinkedBlockingQueue<Message>());//can we assume valid input? or do we need to make sure that m is not already registered?
//		System.out.println("microservice: " + m.getName() + "	microservicequeues keys: " + microServicesQueues.size());

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
//		System.out.println(m.getName() + " entered Awaitmassage");
		Message ans;
//			synchronized (microServicesQueues.get(m)) {
				try {
//					if (microServicesQueues.get(m).isEmpty()) {
//						microServicesQueues.get(m).wait();
//					}
					return microServicesQueues.get(m).take();
//			System.out.println("await massage microservice: " + m.getName() + "	microservicequeues keys: " + microServicesQueues.size());
//				ans = microServicesQueues.get(m).take();
//			System.out.println("microservice: " + m.getName() +"	recieved message: " + ans.toString());
//				return ans;
				} catch (InterruptedException e) {
//				}
			}
		return null;
	}
}
