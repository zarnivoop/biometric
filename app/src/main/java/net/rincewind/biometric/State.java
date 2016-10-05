package net.rincewind.biometric;

/**
 * Created by stefan on 2016-08-01.
 */
public class State {
    public int mockup = 0;

    private static State ourInstance = new State();

    public static State getInstance() {
        return ourInstance;
    }

    private State() {
    }
}
