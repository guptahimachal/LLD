package observer;

import observables.Observable;

public class EmailAlert implements Observer {

    private Observable observable;
    private String emailId;

    public EmailAlert(Observable observable, String emailId) {
        this.observable = observable;
        this.emailId = emailId;
        observable.addObserver(this);
    }

    @Override
    public void update() {
        System.out.printf("Sending email [%s] to [%s]%n", observable.getData(), emailId);
    }
}
