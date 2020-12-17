package bgu.spl.mics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl} class is the implementation of the {@link MessageBus} interface.
 * This class is a thread-safe singleton.
 */
public class MessageBusImpl implements MessageBus {
	ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServicesQueues;
	ConcurrentHashMap<Class<? extends Message> , BlockingQueue<MicroService>> subEventQueues;
	ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>>  broadcastQueues ;
	ConcurrentHashMap<Event,Future> futureEvents;

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
		return MsgBusHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subEventQueues.putIfAbsent(type, new LinkedBlockingQueue<>());
					subEventQueues.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastQueues.putIfAbsent(type, new LinkedBlockingQueue<>());
			broadcastQueues.get(type).add(m);
		}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		futureEvents.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (broadcastQueues.containsKey(b.getClass())) {
				if (!broadcastQueues.get(b.getClass()).isEmpty()) {
					for (MicroService m : broadcastQueues.get(b.getClass())) {
							microServicesQueues.get(m).add(b);
						}
					}
				}
			}

	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		Future<T> ans = new Future<>();
		futureEvents.put(e, ans);
		if (subEventQueues.containsKey(e.getClass())) {
				if (!subEventQueues.get(e.getClass()).isEmpty()) {
					MicroService m = subEventQueues.get(e.getClass()).remove();
						microServicesQueues.get(m).add(e);
						subEventQueues.get(e.getClass()).add(m);
						return ans;
					}
				}
		return null;
	}

	@Override
	  public void register(MicroService m) {
		microServicesQueues.putIfAbsent(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
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
				try {
					return microServicesQueues.get(m).take();
				} catch (InterruptedException e) {
			}
		return null;
	}
}
