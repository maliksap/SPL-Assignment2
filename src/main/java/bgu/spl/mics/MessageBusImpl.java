package bgu.spl.mics;
import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	HashMap<MicroService, Queue<Message>>  microServicesQueues;
	HashMap<String, Queue<MicroService>> subEventQueues;
	HashMap<String, Queue<MicroService>>  broadcastQueues ;
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
			subEventQueues.put(type.getName(), new LinkedList<MicroService>());
		}
		subEventQueues.get(type.getName()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!broadcastQueues.containsKey(type.getName()))
		{
			broadcastQueues.put(type.getName(), new LinkedList<MicroService>());
		}
		broadcastQueues.get(type.getName()).add(m);
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (MicroService m: broadcastQueues.get(b))
		{
			microServicesQueues.get(m).add(b);
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

        return null;
	}

	@Override
	public void register(MicroService m) {
		microServicesQueues.putIfAbsent(m, new LinkedList<Message>());  //can we assume valid input? or do we need to make sure that m is not already registered?
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
			ans = microServicesQueues.get(m).poll();
			return ans;
		}
		catch (InterruptedException) {}

	}
}
