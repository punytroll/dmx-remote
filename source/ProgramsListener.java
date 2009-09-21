import java.util.EventListener;

public interface ProgramsListener extends EventListener
{
	public void programNameChanged(NameChangedEvent Event);
}
