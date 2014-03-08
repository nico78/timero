import event.Event;
import event.EventPublisher;


final class SysoutEventPublisher implements
		EventPublisher {
	@Override
	public void publishEvent(Event<?> event) {
		System.out.println(event.getObject().toString());
	}
}