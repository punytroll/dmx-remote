import javax.swing.event.EventListenerList;

class Program
{
	private int [] m_Matrix;
	private String m_Name;
	private EventListenerList _presetListeners;
	
	public Program()
	{
		m_Name = "";
		_presetListeners = new EventListenerList();
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
	
	public void addPresetListener(PresetListener Listener)
	{
		_presetListeners.add(PresetListener.class, Listener);
	}
	
	public void removePresetListener(PresetListener Listener)
	{
		_presetListeners.remove(PresetListener.class, Listener);
	}
	
	public void fireNamedChanged()
	{
		NameChangedEvent event = null;
		
		for(PresetListener presetListener : _presetListeners.getListeners(PresetListener.class))
		{
			if(event == null)
			{
				event = new NameChangedEvent(m_Name);
			}
			presetListener.nameChanged(event);
		}
	}
}
