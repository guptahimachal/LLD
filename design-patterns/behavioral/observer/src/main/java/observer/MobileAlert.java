package observer;

import observables.Observable;

public class MobileAlert implements Observer {

    private Observable observable;
    private String phoneNumber;

    public MobileAlert(Observable observable, String phoneNumber) {
        this.observable = observable;
        this.phoneNumber = phoneNumber;
        observable.addObserver(this);
    }

    @Override
    public void update() {
        System.out.printf("Sending message [%s] to [%s]%n", observable.getData(), phoneNumber);
    }
}
