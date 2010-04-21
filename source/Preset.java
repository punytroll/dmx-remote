import javax.swing.event.EventListenerList;

class Preset
{
	private int [] _matrix;
	private String _name;
	private EventListenerList _presetListeners;
	
	public Preset()
	{
		_matrix = null;
		_name = "";
		_presetListeners = new EventListenerList();
	}
	
	public void clear()
	{
		_matrix = null;
		setName("");
	}
	
	public int getSize()
	{
		if(_matrix != null)
		{
			return _matrix.length;
		}
		else
		{
			return 0;
		}
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int [] getMatrix()
	{
		return _matrix;
	}
	
	public void setName(String name)
	{
		if(name.equals(_name) == false)
		{
			_name = name;
			fireNamedChanged();
		}
	}
	
	public void setMatrix(int [] matrix)
	{
		_matrix = new int[matrix.length];
		for(int index = 0; index < matrix.length; ++index)
		{
			_matrix[index] = matrix[index];
		}
	}
	
	public boolean hasMatrixInformation()
	{
		if(_matrix != null)
		{
			for(int index = 0; index < _matrix.length; ++index)
			{
				if(_matrix[index] != -1)
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
				event = new NameChangedEvent(_name);
			}
			presetListener.nameChanged(event);
		}
	}
}
