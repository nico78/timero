package event;


public class SysoutEventPublisher<T> implements
		EventPublisher<T> {
	@Override
	public void publishEvent(Event<T> event) {
		System.out.println(event.getObject().toString());
	}
}