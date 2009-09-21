import java.util.EventListener;

public interface DeviceListener extends EventListener
{
	public void deviceNameChanged(DeviceEvent Event);
}
