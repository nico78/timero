package event;

public interface EventPublisher<T> {
	void publishEvent(Event<T> event);
}
