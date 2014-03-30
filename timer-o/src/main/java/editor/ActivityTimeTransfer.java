package editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class ActivityTimeTransfer {
	private int timeSecs;
	private int fromIndex;

	public ActivityTimeTransfer(int timeSecs, int fromIndex) {
		this.timeSecs = timeSecs;
		this.fromIndex = fromIndex;
	}


	public synchronized int getTimeSecs() {
		return timeSecs;
	}

	public synchronized int getFromIndex() {
		return fromIndex;
	}

	public synchronized void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}

	public synchronized void setTimeSecs(int timeSecs) {
		this.timeSecs = timeSecs;
	}

	static class ActivityTimeTransferType extends ByteArrayTransfer {

		private static final String TYPENAME = "activity_time_transfer";
		private static final int TYPEID = registerType(TYPENAME);
		private static ActivityTimeTransferType _instance = new ActivityTimeTransferType ();

		public static ActivityTimeTransferType getInstance () {
			return _instance;
		}
		@Override
		protected String[] getTypeNames() {
			return new String[] { TYPENAME };
		}

		@Override
		protected int[] getTypeIds() {
			return new int[] { TYPEID };
		}

		@Override
		public void javaToNative(Object object, TransferData transferData) {
			if (!checkType(object) || !isSupportedType(transferData)) {
				DND.error(DND.ERROR_INVALID_DATA);
			}
			ActivityTimeTransfer activityTimeTransfer = (ActivityTimeTransfer) object;
			try {
				// write data to a byte array and then ask super to convert to
				// pMedium
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream writeOut = new DataOutputStream(out);
				writeOut.writeInt(activityTimeTransfer.getFromIndex());
				writeOut.writeInt(activityTimeTransfer.getTimeSecs());
			
				writeOut.close();
				byte [] buffer = out.toByteArray ();
				super.javaToNative(buffer, transferData);
				System.out.println("wrote "+activityTimeTransfer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		boolean checkType(Object object) {
			return object != null && object instanceof ActivityTimeTransfer;
		}

		@Override
		public Object nativeToJava (TransferData transferData) {
			if (isSupportedType (transferData)) {
				byte [] buffer = (byte []) super.nativeToJava (transferData);
				if (buffer == null) return null;

				ActivityTimeTransfer activityTimeTransfer = null;
				System.out.println("trying to read..");
				try {
					ByteArrayInputStream in = new ByteArrayInputStream (buffer);
					DataInputStream readIn = new DataInputStream (in);
					System.out.println("Available: " + readIn.available());
					while (readIn.available () >0) { //TODO get correct conditional
						
						int tFromIndex = readIn.readInt();
						int tTimeSecs = readIn.readInt();
						activityTimeTransfer = new ActivityTimeTransfer(tTimeSecs, tFromIndex);
						System.out.println("read " + activityTimeTransfer);
					}
					
					readIn.close ();
				}
				catch (IOException ex) {
					ex.printStackTrace();
					return null;
				}
				return activityTimeTransfer;
			}

			return null;
		}

	}

	@Override
	public String toString() {
		return "ActivityTimeTransfer [timeSecs=" + timeSecs + ", fromIndex="
				+ fromIndex + "]";
	}

	
}