package lockedstatus;

import lockedstatus.jna.User32;

import com.sun.jna.Native;

public class Win32LockedStatusChecker implements LockedStatusChecker {


	/* (non-Javadoc)
	 * @see lockedstatus.LockedStatusChecker#isLocked()
	 */
	@Override
	public boolean isLocked() {
		int DESKTOP_SWITCHDESKTOP = 256;
		User32 user32 = (User32) Native.loadLibrary("user32", User32.class);
		int hwnd = user32.OpenDesktopA("Default", 0, false,
				DESKTOP_SWITCHDESKTOP);
		if (hwnd != 0) {
			boolean rtn = user32.SwitchDesktop(hwnd);
			if (!rtn) {
				user32.CloseDesktop(hwnd);
				return true;
			}
			user32.CloseDesktop(hwnd);
		}
		return false;
	}

}