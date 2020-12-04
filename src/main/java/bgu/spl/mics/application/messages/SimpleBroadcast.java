package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

public class SimpleBroadcast implements Broadcast{
    private String msg;

    public SimpleBroadcast(String massage){
        msg = massage;
    }
}
