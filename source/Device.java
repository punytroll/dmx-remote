class Device
{
	public static final int DONTCARE = 1;
	public static final int PROFESSIONAL = 2;
	public static final int CONSUMER = 3;
	public static final int ADAT = 4;
	
	private String m_Name;
	private int m_Format;
	
	public Device()
	{
		m_Name = new String();
		m_Format = DONTCARE;
	}
	
	public void setName(String Name)
	{
		m_Name = Name;
	}
	
	public String getName()
	{
		return m_Name;
	}
	
	public void setName(int Format)
	{
		m_Format = Format;
	}
	
	public int getFormat()
	{
		return m_Format;
	}
}
