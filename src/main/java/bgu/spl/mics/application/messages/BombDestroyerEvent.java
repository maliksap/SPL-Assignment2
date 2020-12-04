package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class BombDestroyerEvent implements Event<Boolean> {
    private Future<Boolean> future;

    public BombDestroyerEvent() {}

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future<Boolean> future) {
        this.future = future;
    }

}
