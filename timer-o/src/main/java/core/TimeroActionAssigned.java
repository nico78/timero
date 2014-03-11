package core;


public abstract class TimeroActionAssigned implements TimeroAction {
	protected Timero timero;

	public TimeroActionAssigned(Timero timero) {
		this.timero = timero;
	}
}