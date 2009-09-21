class ConnectionEvent
{
	private int m_Source;
	private int m_Destination;
	private boolean m_Connected;
	private boolean m_PartOfBatch;
	
	public ConnectionEvent(int Source, int Destination, boolean Connected, boolean PartOfBatch)
	{
		m_Source = Source;
		m_Destination = Destination;
		m_Connected = Connected;
		m_PartOfBatch = PartOfBatch;
	}
	
	public boolean isConnected()
	{
		return m_Connected;
	}
	
	public int getSource()
	{
		return m_Source;
	}
	
	public int getDestination()
	{
		return m_Destination;
	}
	
	public boolean isPartOfBatch()
	{
		return m_PartOfBatch;
	}
}
