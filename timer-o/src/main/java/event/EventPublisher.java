package event;

public interface EventPublisher {
	void publishEvent(Event<?> event);
}
