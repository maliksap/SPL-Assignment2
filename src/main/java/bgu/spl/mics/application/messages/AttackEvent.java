package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

/**
 * A message that represents an attack to be performed.
 */

public class AttackEvent implements Event<Boolean> {
    final private Attack attack;

    public Attack getAttack() {
        return attack;
    }

    public AttackEvent(Attack att){
        attack = att;
    }
    }
