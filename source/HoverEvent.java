class HoverEvent
{
	private int m_Source;
	private int m_Destination;
	
	public HoverEvent(int Source, int Destination)
	{
		m_Source = Source;
		m_Destination = Destination;
	}
	
	public int getSource()
	{
		return m_Source;
	}
	
	public int getDestination()
	{
		return m_Destination;
	}
}
