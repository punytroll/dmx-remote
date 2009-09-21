class NameChangedEvent
{
	private String m_Name;
	
	NameChangedEvent(String Name)
	{
		m_Name = Name;
	}
	
	String getName()
	{
		return m_Name;
	}
}
