import java.util.EventListener;

interface MIDIListener extends EventListener
{
	public void MIDIDeviceChanged();
	public void IDNumberChanged();
}
