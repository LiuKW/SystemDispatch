package observer;

import event.Event;

public interface Observer {
    void onAction(Event event);
}