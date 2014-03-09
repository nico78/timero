package lockedstatus.jna;

import com.sun.jna.Library;

public interface User32
    extends Library
{

    public abstract int OpenDesktopA(String s, int i, boolean flag, int j);

    public abstract boolean SwitchDesktop(int i);

    public abstract boolean CloseDesktop(int i);
}
