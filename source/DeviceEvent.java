class DeviceEvent
{
	private int m_DeviceNumber;
	private boolean m_SourceDevice;
	private Device m_Device;
	
	public DeviceEvent(int DeviceNumber, boolean SourceDevice, Device Device)
	{
		m_DeviceNumber = DeviceNumber;
		m_SourceDevice = SourceDevice;
		m_Device = Device;
	}
}
