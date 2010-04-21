import javax.swing.event.EventListenerList;
import java.util.HashMap;
import java.awt.Color;
import java.util.Vector;
import javax.sound.midi.*;
import java.util.prefs.Preferences;

class Configuration
{
	// new variables
	private static Integer _currentMatrixSize;
	private LanguageTable _languageTable;
	private Preset _presets[];
	
	// old variables
	private String m_ConfigurationRoot;
	private int m_GroupSize;
	private int m_MatrixPadding;
	private int m_IdentifierFieldWidth;
	private int m_NameFieldWidth;
	private int m_Matrix[];
	private int m_FixedHoverSource;
	private int m_FixedHoverDestination;
	private int m_HoverSource;
	private int m_HoverDestination;
	private int m_IDNumber;
	private int m_BatchLevel;
	private boolean m_TransmitManually;
	private boolean m_MatrixChanged;
	private boolean m_DevicesChanged;
	private boolean m_PresetsChanged;
	private String m_ActiveWindow;
	private String m_MIDIDeviceString;
	private Device m_Devices[];
	private EventListenerList m_ConnectionListeners;
	private EventListenerList m_MetricListeners;
	private EventListenerList m_DeviceListeners;
	private EventListenerList m_HoverListeners;
	private EventListenerList m_BatchListeners;
	private EventListenerList m_MIDIListeners;
	private EventListenerList m_MatrixListeners;
	private EventListenerList m_TransmitModeListeners;
	private Color m_BackgroundColor;
	private int m_LoadedProgramIndex;
	
	// MIDI Devices
	Vector m_MIDIDevices;
	MidiDevice m_MIDIDevice;
	Receiver m_MIDIReceiver;
	
	// Hacks
	MatrixControllerPanel _matrixControllerPanel;
	
	public Configuration(String ConfigurationRoot)
	{
		_currentMatrixSize = 32;
		// initialize the language map
		_languageTable = new LanguageTable();
		initializeLanguages();
		_languageTable.setLanguage("en");
		// other initialization
		m_ConnectionListeners = new EventListenerList();
		m_MetricListeners = new EventListenerList();
		m_DeviceListeners = new EventListenerList();
		m_HoverListeners = new EventListenerList();
		m_BatchListeners = new EventListenerList();
		m_MIDIListeners = new EventListenerList();
		m_MatrixListeners = new EventListenerList();
		m_TransmitModeListeners = new EventListenerList();
		m_GroupSize = 4;
		m_MatrixPadding = 8;
		m_IdentifierFieldWidth = 100;
		m_NameFieldWidth = 80;
		m_Matrix = new int[getCurrentMatrixSize()];
		m_FixedHoverSource = -1;
		m_FixedHoverDestination = -1;
		m_HoverSource = -1;
		m_HoverDestination = -1;
		m_BatchLevel = 0;
		m_IDNumber = 0;
		m_TransmitManually = false;
		m_ActiveWindow = "";
		m_LoadedProgramIndex = -1;
		m_ConfigurationRoot = ConfigurationRoot;
		
		// changed flags
		m_MatrixChanged = false;
		m_DevicesChanged = false;
		m_PresetsChanged = false;
		for(int Column = 0; Column < getCurrentMatrixSize(); ++Column)
		{
			m_Matrix[Column] = -1;
		}
		m_Devices = new Device[2 * getCurrentMatrixSize()];
		for(int Device = 0; Device < 2 * getCurrentMatrixSize(); ++Device)
		{
			m_Devices[Device] = new Device();
		}
		_presets = new Preset[StaticConfiguration.getNumberOfPresets()];
		for(Integer presetIndex = 0; presetIndex < StaticConfiguration.getNumberOfPresets(); ++presetIndex)
		{
			_presets[presetIndex] = new Preset();
		}
		m_BackgroundColor = new Color(0.36f, 0.40f, 0.43f);
		m_MIDIDevices = new Vector();
		m_MIDIDevices.insertElementAt(getString("None"), 0);
		
		MidiDevice.Info MIDIDeviceInfos[] = MidiSystem.getMidiDeviceInfo();
		
		for(int MIDIDeviceNumber = 0; MIDIDeviceNumber < MIDIDeviceInfos.length; ++MIDIDeviceNumber)
		{
			MidiDevice MIDIDevice = null;
			
			try
			{
				MIDIDevice = MidiSystem.getMidiDevice(MIDIDeviceInfos[MIDIDeviceNumber]);
			}
			catch(MidiUnavailableException Exception)
			{
				System.out.println("WARNING: Unable to retrieve device \"" + MIDIDeviceInfos[MIDIDeviceNumber] + "\"");
				continue;
			}
			if(MIDIDevice.getMaxReceivers() != 0)
			{
				m_MIDIDevices.add(MIDIDeviceInfos[MIDIDeviceNumber]);
			}
		}
	}
	
	public void initializeLanguages()
	{
		HashMap<String, String> english = new HashMap<String, String>();
		
		_languageTable.put("en", english);
		english.put("Enter Device Names", "Enter Device Names");
		english.put("Open Matrix", "Open Matrix");
		english.put("Open Preset List", "Open Preset List");
		english.put("Device Configuration", "Device Configuration");
		english.put("MIDI Device", "MIDI Device");
		english.put("Load from File", "Load from File");
		english.put("Save to File", "Save to File");
		english.put("Save to Preset", "Save to Preset");
		english.put("None", "None");
		english.put("Source", "Source");
		english.put("Destination", "Destination");
		english.put("DefaultSourceName", "No Name");
		english.put("DefaultDestinationName", "No Name");
		english.put("Dump", "Dump");
		english.put("ID Number", "ID Number");
		english.put("Clear Matrix", "Clear Matrix");
		english.put("Matrix", "Matrix");
		english.put("Preset", "Preset");
		english.put("Presets", "Presets");
		english.put("Devices", "Devices");
		english.put("Clear Preset List", "Clear Preset List");
		english.put("Copy Sources to Destinations", "Copy Sources to Destinations");
		english.put("Clear Device List", "Clear Device List");
		english.put("Load Presets", "Load Presets");
		english.put("Save Presets", "Save Presets");
		english.put("MIDI", "MIDI");
		english.put("Select Preset to overwrite", "Select Preset to overwrite");
		english.put("Transmit", "Transmit");
		english.put("Transmit manually", "Transmit manually");
		english.put("Transmit immediately", "Transmit immediately");
		english.put("Save to Preset:", "Save to Preset:");
		english.put("Windows", "Windows");
		english.put("Configuration", "Configuration");
		english.put("Matrix Size", "Matrix Size");
		english.put("System Size", "System Size");
		english.put("Reset Positions", "Reset Positions");
		english.put("Edit Preset Names", "Edit Preset Names");
		english.put("Load from Preset", "Load from Preset");
		english.put("Edit Device Names", "Edit Device Names");
		english.put("Edit Device Name", "Edit Device Name");
	}
	
	public String getString(String stringIdentifier)
	{
		return _languageTable.getString(stringIdentifier);
	}
	
	public String getCapitalizedString(String stringIdentifier)
	{
		return (new String(_languageTable.getString(stringIdentifier))).toUpperCase();
	}
	
	public void setSize(int Size)
	{
		if(Size == getCurrentMatrixSize())
		{
			return;
		}
		_currentMatrixSize = Size;
		m_Matrix = new int[getCurrentMatrixSize()];
		for(int Column = 0; Column < getCurrentMatrixSize(); ++Column)
		{
			m_Matrix[Column] = -1;
		}
		m_Devices = new Device[2 * getCurrentMatrixSize()];
		for(int Device = 0; Device < 2 * getCurrentMatrixSize(); ++Device)
		{
			m_Devices[Device] = new Device();
		}
		clearPresets();
		fireMetricChanged(MetricListener.SIZE_CHANGED | MetricListener.MATRIX_CELL_SIZE_CHANGED);
		setMatrixChanged();
		setDevicesChanged();
		setPresetsChanged();
	}
	
	public void setMatrixControllerPanel(MatrixControllerPanel matrixControllerPanel)
	{
		_matrixControllerPanel = matrixControllerPanel;
	}
	
	public void closeMIDIDevice()
	{
		if(m_MIDIDevice != null)
		{
			m_MIDIDevice.close();
			m_MIDIDevice = null;
			m_MIDIDeviceString = "";
			fireMIDIDeviceChanged();
		}
	}
	
	public void enterBatch()
	{
		++m_BatchLevel;
		if(m_BatchLevel == 1)
		{
			fireEnterBatch();
		}
	}
	
	public void leaveBatch()
	{
		if(m_BatchLevel > 0)
		{
			--m_BatchLevel;
			if(m_BatchLevel == 0)
			{
				fireLeaveBatch();
			}
		}
	}
	
	public void openMIDIDevice(String Device)
	{
		for(int MIDIDeviceNumber = 1; MIDIDeviceNumber < m_MIDIDevices.size(); ++MIDIDeviceNumber)
		{
			if(Device.equals(m_MIDIDevices.get(MIDIDeviceNumber).toString()) == true)
			{
				closeMIDIDevice();
				
				MidiDevice.Info MIDIInfo = (MidiDevice.Info)m_MIDIDevices.get(MIDIDeviceNumber);
				
				try
				{
					m_MIDIDevice = MidiSystem.getMidiDevice(MIDIInfo);
					m_MIDIDevice.open();
				}
				catch(MidiUnavailableException Exception)
				{
					System.out.println("\"" + MIDIInfo.getName() + "\" not available for output.");
					
					return;
				}
				if(m_MIDIReceiver != null)
				{
					m_MIDIReceiver.close();
				}
				try
				{
					m_MIDIReceiver = m_MIDIDevice.getReceiver();
				}
				catch(MidiUnavailableException Exception)
				{
					System.out.println("No Receiver for output device \"" + MIDIInfo.getName() + "\"");
					m_MIDIDevice.close();
					
					return;
				}
				m_MIDIDeviceString = Device;
				fireMIDIDeviceChanged();
				
				break;
			}
		}
	}
	
	public int numberOfMIDIDevices()
	{
		return m_MIDIDevices.size();
	}
	
	public String getMIDIDeviceName(int DeviceNumber)
	{
		return m_MIDIDevices.get(DeviceNumber).toString();
	}
	
	public boolean isMIDIDeviceOpen()
	{
		return m_MIDIDevice != null;
	}
	
	public String getMIDIDeviceString()
	{
		if(m_MIDIDeviceString == null)
		{
			return "None";
		}
		else
		{
			return new String(m_MIDIDeviceString);
		}
	}
	
	public void reopenMIDIDevice()
	{
		openMIDIDevice(m_MIDIDeviceString);
	}
	
	public void sendMIDI(MidiMessage Message)
	{
		System.out.println("sending with MIDI.");
		m_MIDIReceiver.send(Message, -1L);
	}
	
	public int getHoverSource()
	{
		return m_HoverSource;
	}
	
	public int getHoverDestination()
	{
		return m_HoverDestination;
	}
	
	public void setHoverSource(int Source)
	{
		if((Source >= 0) && (Source < getCurrentMatrixSize()))
		{
			if(m_HoverSource != Source)
			{
				m_HoverSource = Source;
				fireHoverChanged(m_HoverSource, m_HoverDestination);
			}
		}
		else
		{
			if(m_HoverSource != -1)
			{
				m_HoverSource = -1;
				fireHoverChanged(m_HoverSource, m_HoverDestination);
			}
		}
	}
	
	public void setHoverDestination(int Destination)
	{
		if((Destination >= 0) && (Destination < getCurrentMatrixSize()))
		{
			if(m_HoverDestination != Destination)
			{
				m_HoverDestination = Destination;
				fireHoverChanged(m_HoverSource, m_HoverDestination);
			}
		}
		else
		{
			if(m_HoverDestination != -1)
			{
				m_HoverDestination = -1;
				fireHoverChanged(m_HoverSource, m_HoverDestination);
			}
		}
	}
	
	public boolean getHover()
	{
		return (m_HoverSource >= 0) && (m_HoverDestination >= 0) && (m_HoverSource < getCurrentMatrixSize()) && (m_HoverDestination < getCurrentMatrixSize());
	}
	
	public static Integer getCurrentMatrixSize()
	{
		return _currentMatrixSize;
	}
	
	public Integer getCurrentCellSize()
	{
		return StaticConfiguration.getCellSize(getCurrentMatrixSize());
	}
	
	public Integer getCurrentTextOffset()
	{
		return StaticConfiguration.getTextOffset(getCurrentMatrixSize());
	}
	
	public int getGroupSize()
	{
		return m_GroupSize;
	}
	
	public int getMatrixPadding()
	{
		return m_MatrixPadding;
	}
	
	public int getIdentifierFieldWidth()
	{
		return m_IdentifierFieldWidth;
	}
	
	public int getNameFieldWidth()
	{
		return m_NameFieldWidth;
	}
	
	public int getIDNumber()
	{
		return m_IDNumber;
	}
	
	public void setActiveWindow(String Name)
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("ActiveWindow", Name);
	}
	
	public String getActiveWindow()
	{
		return Preferences.userRoot().node(m_ConfigurationRoot).get("ActiveWindow", "");
	}
	
	public void setMatrixSaveToFileCurrentDirectory(String Path)
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("MatrixSaveToFileCurrentDirectory", Path);
	}
	
	public String getMatrixSaveToFileCurrentDirectory()
	{
		return Preferences.userRoot().node(m_ConfigurationRoot).get("MatrixSaveToFileCurrentDirectory", "");
	}
	
	public void setMatrixLoadFromFileCurrentDirectory(String Path)
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("MatrixLoadFromFileCurrentDirectory", Path);
	}
	
	public String getMatrixLoadFromFileCurrentDirectory()
	{
		return Preferences.userRoot().node(m_ConfigurationRoot).get("MatrixLoadFromFileCurrentDirectory", "");
	}
	
	public void setPresetsSaveToFileCurrentDirectory(String Path)
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("PresetsSaveToFileCurrentDirectory", Path);
	}
	
	public String getPresetsSaveToFileCurrentDirectory()
	{
		return Preferences.userRoot().node(m_ConfigurationRoot).get("PresetsSaveToFileCurrentDirectory", "");
	}
	
	public void setPresetsLoadFromFileCurrentDirectory(String Path)
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("PresetsLoadFromFileCurrentDirectory", Path);
	}
	
	public String getPresetsLoadFromFileCurrentDirectory()
	{
		return Preferences.userRoot().node(m_ConfigurationRoot).get("PresetsLoadFromFileCurrentDirectory", "");
	}
	
	public void setCurrentPresetsFile(String Path)
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("CurrentPresetsFile", Path);
	}
	
	public String getCurrentPresetsFile()
	{
		return Preferences.userRoot().node(m_ConfigurationRoot).get("CurrentPresetsFile", "");
	}
	
	public void setCurrentMatrixIsFile(String filePath)
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("CurrentMatrixType", "File");
		Preferences.userRoot().node(m_ConfigurationRoot).put("CurrentMatrix", filePath);
		_matrixControllerPanel.setPresetIndex(-1);
	}
	
	public void setCurrentMatrixIsPreset(int presetIndex)
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("CurrentMatrixType", "Preset");
		Preferences.userRoot().node(m_ConfigurationRoot).putInt("CurrentMatrix", presetIndex);
		_matrixControllerPanel.setPresetIndex(presetIndex);
	}
	
	public void setCurrentMatrixIsEmpty()
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("CurrentMatrixType", "Empty");
		_matrixControllerPanel.setPresetIndex(-1);
	}
	
	public boolean isCurrentMatrixFile()
	{
		return Preferences.userRoot().node(m_ConfigurationRoot).get("CurrentMatrixType", "").equals("File");
	}
	
	public boolean isCurrentMatrixPreset()
	{
		return Preferences.userRoot().node(m_ConfigurationRoot).get("CurrentMatrixType", "").equals("Preset");
	}
	
	public boolean isCurrentMatrixSet()
	{
		return isCurrentMatrixFile() || isCurrentMatrixPreset();
	}
	
	public String getCurrentMatrixFile()
	{
		return Preferences.userRoot().node(m_ConfigurationRoot).get("CurrentMatrix", "");
	}
	
	public int getCurrentMatrixPreset()
	{
		return Preferences.userRoot().node(m_ConfigurationRoot).getInt("CurrentMatrix", 0);
	}
	
	public void setIDNumber(int IDNumber)
	{
		m_IDNumber = IDNumber;
		fireIDNumberChanged();
	}
	
	public void transmitNow()
	{
		fireLeaveBatch();
	}
	
	public void setTransmitManually()
	{
		if(m_TransmitManually == false)
		{
			m_TransmitManually = true;
			enterBatch();
			fireTransmitModeChanged();
		}
	}
	
	public void setTransmitImmediately()
	{
		if(m_TransmitManually == true)
		{
			m_TransmitManually = false;
			leaveBatch();
			fireTransmitModeChanged();
		}
	}
	
	public boolean getTransmitManually()
	{
		return m_TransmitManually;
	}
	
	public Preset getPreset(int presetIndex)
	{
		if((presetIndex >= 0) && (presetIndex < StaticConfiguration.getNumberOfPresets()))
		{
			return _presets[presetIndex];
		}
		
		return null;
	}
	
	public void copySourcesToDestinations()
	{
		for(int I = 0; I < getCurrentMatrixSize(); ++I)
		{
			if(getDestinationName(I).equals("") == true)
			{
				setDestinationName(I, getSourceName(I));
			}
		}
	}
	
	public boolean isConnected(int Source, int Destination)
	{
		if(Source >= getCurrentMatrixSize() || Destination >= getCurrentMatrixSize())
		{
			return false;
		}
		
		return m_Matrix[Destination] == Source;
	}
	
	public void clearNames()
	{
		for(int Name = 0; Name < getCurrentMatrixSize() * 2; ++Name)
		{
			setName(Name, "");
		}
	}
	
	public void clearMatrix()
		throws MatrixNotSavedException
	{
		if(isMatrixChanged() == true)
		{
			throw new MatrixNotSavedException();
		}
		enterBatch();
		for(int Destination = 0; Destination < getCurrentMatrixSize(); ++Destination)
		{
			setDisconnected(Destination);
		}
		leaveBatch();
	}
	
	public void clearPresets()
	{
		for(Preset preset : _presets)
		{
			preset.clear();
		}
	}
	
	public void setMatrixChanged()
	{
		if(m_MatrixChanged == false)
		{
			m_MatrixChanged = true;
			fireMatrixChanged();
		}
	}
	
	public void setDevicesChanged()
	{
		if(m_DevicesChanged == false)
		{
			m_DevicesChanged = true;
		}
	}
	
	public void setPresetsChanged()
	{
		if(m_PresetsChanged == false)
		{
			m_PresetsChanged = true;
		}
	}
	
	public boolean isMatrixChanged()
	{
		return m_MatrixChanged;
	}
	
	public boolean isDevicesChanged()
	{
		return m_DevicesChanged;
	}
	
	public boolean isPresetsChanged()
	{
		return m_PresetsChanged;
	}
	
	public boolean isChanged()
	{
		return isMatrixChanged() || isDevicesChanged() || isPresetsChanged();
	}
	
	public void setMatrixSaved()
	{
		if(m_MatrixChanged == true)
		{
			m_MatrixChanged = false;
			fireMatrixChanged();
		}
	}
	
	public void setDevicesSaved()
	{
		if(m_DevicesChanged == true)
		{
			m_DevicesChanged = false;
		}
	}
	
	public void setPresetsSaved()
	{
		if(m_PresetsChanged == true)
		{
			m_PresetsChanged = false;
		}
	}
	
	public void setDisconnected(int Destination)
	{
		if(m_Matrix[Destination] != -1)
		{
			int Source = m_Matrix[Destination];
			
			m_Matrix[Destination] = -1;
			setMatrixChanged();
			fireConnectionChanged(Source, Destination, false);
		}
	}
	
	public void setConnected(int Source, int Destination, boolean Connected)
	{
		if((Source >= 0) && (Destination >= 0) && (Source < getCurrentMatrixSize()) && (Destination < getCurrentMatrixSize()) && (isConnected(Source, Destination) != Connected))
		{
			enterBatch();
			if(m_Matrix[Destination] != -1)
			{
				int OldSource = m_Matrix[Destination];
				
				m_Matrix[Destination] = -1;
				fireConnectionChanged(OldSource, Destination, false);
			}
			if(Connected == true)
			{
				m_Matrix[Destination] = Source;
				fireConnectionChanged(m_Matrix[Destination], Destination, true);
			}
			setMatrixChanged();
			leaveBatch();
		}
	}
	
	public int getConnectedSource(int Destination)
	{
		if((Destination >= 0) && (Destination < getCurrentMatrixSize()))
		{
			return m_Matrix[Destination];
		}
		else
		{
			return -1;
		}
	}
	
	public void setHover(int Source, int Destination)
	{
		if(m_FixedHoverSource == -1)
		{
			m_HoverSource = Source;
		}
		if(m_FixedHoverDestination == -1)
		{
			m_HoverDestination = Destination;
		}
	}
	
	public String getSourceName(int Index)
	{
		if((Index < getCurrentMatrixSize()) && (Index >= 0))
		{
			return m_Devices[Index].getName();
		}
		else
		{
			return "";
		}
	}
	
	public String getDestinationName(int Index)
	{
		if((Index < getCurrentMatrixSize()) && (Index >= 0))
		{
			return m_Devices[Index + getCurrentMatrixSize()].getName();
		}
		else
		{
			return "";
		}
	}
	
	public void setSourceName(int Index, String Name)
	{
		if(Index < getCurrentMatrixSize())
		{
			setName(Index, Name);
		}
	}
	
	public void setDestinationName(int Index, String Name)
	{
		if(Index < getCurrentMatrixSize())
		{
			Index += getCurrentMatrixSize();
			setName(Index, Name);
		}
	}
	
	public void setName(int Index, String Name)
	{
		if(Index < getCurrentMatrixSize() * 2)
		{
			if(m_Devices[Index].getName().equals(Name) == false)
			{
				m_Devices[Index].setName(Name);
				fireDeviceNameChanged(Index, Index > getCurrentMatrixSize(), m_Devices[Index]);
				setDevicesChanged();
			}
		}
	}
	
	public void loadProgramToMatrix(int ProgramIndex)
		throws MatrixNotSavedException
	{
		if(isMatrixChanged() == true)
		{
			throw new MatrixNotSavedException();
		}
		if((ProgramIndex >= 0) && (ProgramIndex < StaticConfiguration.getNumberOfPresets()))
		{
			int [] Matrix = _presets[ProgramIndex].getMatrix();
			
			if(Matrix == null)
			{
				clearMatrix();
			}
			else
			{
				if(Matrix.length != getCurrentMatrixSize())
				{
					System.out.println("\tloadProgramToMatrix: Dimensions don't match.");
					
					return;
				}
				enterBatch();
				clearMatrix();
				for(int Destination = 0; Destination < getCurrentMatrixSize(); ++Destination)
				{
					setConnected(Matrix[Destination], Destination, true);
				}
				leaveBatch();
			}
		}
		setMatrixSaved();
		m_LoadedProgramIndex = ProgramIndex;
		if(_matrixControllerPanel != null)
		{
			_matrixControllerPanel.setPresetIndex(ProgramIndex);
		}
		setCurrentMatrixIsPreset(ProgramIndex);
		if(getTransmitManually() == true)
		{
			setTransmitImmediately();
		}
	}
	
	public void saveMatrixToPreset(int PresetIndex)
	{
		if((PresetIndex < 0) && (PresetIndex >= StaticConfiguration.getNumberOfPresets()))
		{
			return;
		}
		_presets[PresetIndex].setMatrix(m_Matrix);
		setCurrentMatrixIsPreset(PresetIndex);
		setMatrixSaved();
		setPresetsChanged();
	}
	
	public int getLoadedProgramIndex()
	{
		return m_LoadedProgramIndex;
	}
	
	private void fireDeviceNameChanged(int Index, boolean SourceEvent, Device Device)
	{
		Object[] DeviceListeners = m_DeviceListeners.getListenerList();
		DeviceEvent Event = null;
		
		for(int Listener = 0; Listener < DeviceListeners.length; Listener += 2)
		{
			if(DeviceListeners[Listener] == DeviceListener.class)
			{
				if(Event == null)
				{
					Event = new DeviceEvent(Index, true, m_Devices[Index]);
				}
				((DeviceListener)DeviceListeners[Listener + 1]).deviceNameChanged(Event);
			}
		}
	}
	
	private void fireConnectionChanged(int Source, int Destination, boolean Connected)
	{
		Object[] Listeners = m_ConnectionListeners.getListenerList();
		ConnectionEvent Event = null;
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == ConnectionListener.class)
			{
				if(Event == null)
				{
					Event = new ConnectionEvent(Source, Destination, Connected, m_BatchLevel > 0);
				}
				((ConnectionListener)Listeners[Listener + 1]).connectionChanged(Event);
			}
		}
	}
	
	private void fireHoverChanged(int Source, int Destination)
	{
		Object[] Listeners = m_HoverListeners.getListenerList();
		HoverEvent Event = null;
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == HoverListener.class)
			{
				if(Event == null)
				{
					Event = new HoverEvent(Source, Destination);
				}
				((HoverListener)Listeners[Listener + 1]).hoverChanged(Event);
			}
		}
	}
	
	private void fireEnterBatch()
	{
		Object[] Listeners = m_BatchListeners.getListenerList();
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == BatchListener.class)
			{
				((BatchListener)Listeners[Listener + 1]).enterBatch();
			}
		}
	}
	
	private void fireLeaveBatch()
	{
		Object[] Listeners = m_BatchListeners.getListenerList();
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == BatchListener.class)
			{
				((BatchListener)Listeners[Listener + 1]).leaveBatch();
			}
		}
	}
	
	private void fireMIDIDeviceChanged()
	{
		Object[] Listeners = m_MIDIListeners.getListenerList();
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == MIDIListener.class)
			{
				((MIDIListener)Listeners[Listener + 1]).MIDIDeviceChanged();
			}
		}
	}
	
	private void fireIDNumberChanged()
	{
		Object[] Listeners = m_MIDIListeners.getListenerList();
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == MIDIListener.class)
			{
				((MIDIListener)Listeners[Listener + 1]).IDNumberChanged();
			}
		}
	}
	
	private void fireMatrixChanged()
	{
		Object[] Listeners = m_MatrixListeners.getListenerList();
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == MatrixListener.class)
			{
				((MatrixListener)Listeners[Listener + 1]).matrixChanged();
			}
		}
	}
	
	private void fireMetricChanged(int WhatChanged)
	{
		Object[] Listeners = m_MetricListeners.getListenerList();
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == MetricListener.class)
			{
				((MetricListener)Listeners[Listener + 1]).metricChanged(WhatChanged);
			}
		}
	}
	
	private void fireTransmitModeChanged()
	{
		Object[] Listeners = m_TransmitModeListeners.getListenerList();
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == TransmitModeListener.class)
			{
				if(m_TransmitManually == true)
				{
					((TransmitModeListener)Listeners[Listener + 1]).changedToTransmitManually();
				}
				else
				{
					((TransmitModeListener)Listeners[Listener + 1]).changedToTransmitImmediately();
				}
			}
		}
	}
	
	public void addDeviceListener(DeviceListener Listener)
	{
		m_DeviceListeners.add(DeviceListener.class, Listener);
	}
	
	public void addConnectionListener(ConnectionListener Listener)
	{
		m_ConnectionListeners.add(ConnectionListener.class, Listener);
	}
	
	public void addMetricListener(MetricListener Listener)
	{
		m_MetricListeners.add(MetricListener.class, Listener);
	}
	
	public void addHoverListener(HoverListener Listener)
	{
		m_HoverListeners.add(HoverListener.class, Listener);
	}
	
	public void addBatchListener(BatchListener Listener)
	{
		m_BatchListeners.add(BatchListener.class, Listener);
	}
	
	public void addMIDIListener(MIDIListener Listener)
	{
		m_MIDIListeners.add(MIDIListener.class, Listener);
	}
	
	public void addMatrixListener(MatrixListener Listener)
	{
		m_MatrixListeners.add(MatrixListener.class, Listener);
	}
	
	public void addTransmitModeListener(TransmitModeListener Listener)
	{
		m_TransmitModeListeners.add(TransmitModeListener.class, Listener);
	}
	
	public Color getBackgroundColor()
	{
		return m_BackgroundColor;
	}
}
