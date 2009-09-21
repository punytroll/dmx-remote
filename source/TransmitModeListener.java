import java.util.EventListener;

interface TransmitModeListener extends EventListener
{
	public void changedToTransmitManually();
	public void changedToTransmitImmediately();
}
