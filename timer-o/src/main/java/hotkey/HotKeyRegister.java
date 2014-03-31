package hotkey;
import javax.swing.KeyStroke;


import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import core.TimeroAction;

public class HotKeyRegister {

	private Provider provider;

	public HotKeyRegister() {
		provider = Provider.getCurrentProvider(false);

	}

	public void registerHotKey(String keyStroke, final TimeroAction hotkeyAction) {
				KeyStroke switchTaskKeyStroke = KeyStroke.getKeyStroke(keyStroke);
		HotKeyListener switchTaskListener = new HotKeyListener() {
			
			@Override
			public void onHotKey(HotKey hotKey) {
				hotkeyAction.doAction();
			}
		};
		provider.register(switchTaskKeyStroke, switchTaskListener);
	}
	
	public void quit(){
		
		provider.reset();
		
		provider.stop();
		System.out.println("hot key register stopped");
	}

}