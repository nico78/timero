package img;

import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.swt.widgets.Display;

public class Icons {

	public static java.awt.Image getAWTImage(String filename) {
		URL imgURL = getImgUrl(filename);
		Toolkit tk = Toolkit.getDefaultToolkit();
		return tk.getImage(imgURL);
	}

	private static URL getImgUrl(String filename) {
		URL resource = Thread.currentThread().getContextClassLoader()
				.getResource("images/" + filename);
		if(resource==null)
			throw new RuntimeException(filename + " not found!");
		return resource;
	}

	// creates the image, and tries really hard to do so
	public static org.eclipse.swt.graphics.Image getSWTImage(String fileName) {
		try {
			InputStream is = getImgUrl(fileName).openStream();
			if (is == null) {
				is = Icons.class.getResourceAsStream(fileName.substring(1));

				if (is == null) {
					return null;
				}
			}

			org.eclipse.swt.graphics.Image img = new org.eclipse.swt.graphics.Image(
					Display.getDefault(), is);
			is.close();

			return img;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
