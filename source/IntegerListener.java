import java.util.EventListener;

public interface IntegerListener extends EventListener
{
	public void integerSet(Integer newValue);
	public void integerChanged(Integer oldValue, Integer newValue);
}
