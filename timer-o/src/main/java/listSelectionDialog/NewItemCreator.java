package listSelectionDialog;

public interface NewItemCreator<T> {

	T createItemFor(String text);

}
