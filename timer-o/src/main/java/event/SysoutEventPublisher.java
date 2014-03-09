package event;


public class SysoutEventPublisher implements
		EventPublisher {
	@Override
	public void publishEvent(Event<?> event) {
		System.out.println(event.getObject().toString());
	}
}