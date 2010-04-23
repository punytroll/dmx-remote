import java.util.EventListener;

public interface BooleanListener extends EventListener
{
	public void booleanSet(Boolean newValue);
	public void booleanChanged(Boolean oldValue, Boolean newValue);
}
