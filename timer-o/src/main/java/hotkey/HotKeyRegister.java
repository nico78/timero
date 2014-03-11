package hotkey;
import javax.swing.KeyStroke;


import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import core.TimeroAction;

public class HotKeyRegister {

	public HotKeyRegister() {
		super();
	}

	public void registerHotKey(String keyStroke, final TimeroAction hotkeyAction) {
		Provider provider = Provider.getCurrentProvider(false);
		KeyStroke switchTaskKeyStroke = KeyStroke.getKeyStroke(keyStroke);
		HotKeyListener switchTaskListener = new HotKeyListener() {
			
			@Override
			public void onHotKey(HotKey hotKey) {
				hotkeyAction.doAction();
			}
		};
		provider.register(switchTaskKeyStroke, switchTaskListener);
	}

}