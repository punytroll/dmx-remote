import java.util.EventListener;

public interface PresetListener extends EventListener
{
	public void nameChanged(NameChangedEvent Event);
}
