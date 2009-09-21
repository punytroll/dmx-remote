import javax.swing.event.EventListenerList;

class Program
{
	private int [] m_Matrix;
	private String m_Name;
	private EventListenerList m_ProgramListeners;
	
	public Program()
	{
		m_Name = "";
		m_ProgramListeners = new EventListenerList();
	}
	
	public void clear()
	{
		m_Matrix = null;
		setName("");
	}
	
	public int getSize()
	{
		if(m_Matrix != null)
		{
			return m_Matrix.length;
		}
		else
		{
			return 0;
		}
	}
	
	public String getName()
	{
		return m_Name;
	}
	
	public int [] getMatrix()
	{
		return m_Matrix;
	}
	
	public void setName(String Name)
	{
		if(Name.equals(m_Name) == false)
		{
			m_Name = Name;
			fireNamedChanged();
		}
	}
	
	public void setMatrix(int [] Matrix)
	{
		m_Matrix = new int[Matrix.length];
		for(int I = 0; I < Matrix.length; ++I)
		{
			m_Matrix[I] = Matrix[I];
		}
	}
	
	public boolean hasMatrixInformation()
	{
		if(m_Matrix != null)
		{
			for(int I = 0; I < m_Matrix.length; ++I)
			{
				if(m_Matrix[I] != -1)
				{
					return true;
				}
			}
			
			return false;
		}
		else
		{
			return false;
		}
	}
	
	public void addProgramListener(ProgramListener Listener)
	{
		m_ProgramListeners.add(ProgramListener.class, Listener);
	}
	
	public void removeProgramListener(ProgramListener Listener)
	{
		m_ProgramListeners.remove(ProgramListener.class, Listener);
	}
	
	public void fireNamedChanged()
	{
		Object[] Listeners = m_ProgramListeners.getListenerList();
		NameChangedEvent Event = null;
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == ProgramListener.class)
			{
				if(Event == null)
				{
					Event = new NameChangedEvent(m_Name);
				}
				((ProgramListener)Listeners[Listener + 1]).programNameChanged(Event);
			}
		}
	}
}
