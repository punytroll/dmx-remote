import java.util.EventListener;

interface BatchListener extends EventListener
{
	public void enterBatch();
	public void leaveBatch();
}
