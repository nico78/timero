package notification;

import img.Icons;

import org.eclipse.swt.graphics.Image;

public enum NotificationType {
    ERROR(Icons.getSWTImage("error.png")),
    DELETE(Icons.getSWTImage("delete.png")),
    WARN(Icons.getSWTImage("warn.png")),
    SUCCESS(Icons.getSWTImage("ok.png")),
    INFO(Icons.getSWTImage("info.png")),
    LIBRARY(Icons.getSWTImage("library.png")),
    HINT(Icons.getSWTImage("hint.png")),
    PRINTED(Icons.getSWTImage("printer.png")),
    TIMERO(Icons.getSWTImage("timero.png")),
    CONNECTION_TERMINATED(Icons.getSWTImage("terminated.png")),
    CONNECTED(Icons.getSWTImage("connected.png")),
    DISCONNECTED(Icons.getSWTImage("disconnected.png")),
    TRANSACTION_OK(Icons.getSWTImage("ok.png")),
    PLUS(Icons.getSWTImage("plus.png")),
    LEFT(Icons.getSWTImage("left.png")),
    RIGHT(Icons.getSWTImage("right.png")),
    CLOSE(Icons.getSWTImage("close.png")),
    TRANSFER_1(Icons.getSWTImage("num-1.png")),
    TRANSFER_5(Icons.getSWTImage("num-5.png")),
    TRANSFER_15(Icons.getSWTImage("num-15.png")),
    TRANSFER_60(Icons.getSWTImage("num-60.png")),
    TRANSACTION_FAIL(Icons.getSWTImage("error.png"));

    private Image _image;

    private NotificationType(Image img) {
        _image = img;
    }

    public Image getImage() {
        return _image;
    }
}
