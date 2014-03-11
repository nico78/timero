package event;

public class Event<E> {
	private final E object;
	public Event(E object){
		this.object = object;
	}
	public E getObject(){
		return object;
	}
}
