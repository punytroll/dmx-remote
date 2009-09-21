import java.util.EventListener;

public interface ProgramListener extends EventListener
{
	public void programNameChanged(NameChangedEvent Event);
}
