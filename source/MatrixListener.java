import java.util.EventListener;

interface MatrixListener extends EventListener
{
	public void matrixChanged();
	public void matrixSaved();
}
