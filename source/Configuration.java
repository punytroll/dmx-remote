import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.sound.midi.*;
import javax.swing.event.EventListenerList;

class Configuration
{
	// new variables
	private LanguageTable _languageTable;
	private static IntegerObject _matrixSize = new IntegerObject(32);
	private static BooleanObject _matrixModified = new BooleanObject(false);
	private static EventListenerList _matrixModifiedListeners = new EventListenerList();
	private static EventListenerList _matrixSizeListeners = new EventListenerList();
	private static List<MidiDevice.Info> _MIDIDevices;
	private Preset _presets[];
	private static Integer _selectedPresetIndex = -1;
	private static EventListenerList _selectedPresetIndexListeners = new EventListenerList();
	
	// old variables
	private String m_ConfigurationRoot;
	private int m_Matrix[];
	private int m_FixedHoverSource;
	private int m_FixedHoverDestination;
	private int m_HoverSource;
	private int m_HoverDestination;
	private int m_IDNumber;
	private int m_BatchLevel;
	private boolean m_TransmitManually;
	private boolean m_DevicesChanged;
	private boolean m_PresetsChanged;
	private String m_ActiveWindow;
	private String m_MIDIDeviceString;
	private Device m_Devices[];
	private EventListenerList m_ConnectionListeners;
	private EventListenerList m_DeviceListeners;
	private EventListenerList m_HoverListeners;
	private EventListenerList m_BatchListeners;
	private EventListenerList m_MIDIListeners;
	private EventListenerList m_TransmitModeListeners;
	
	// MIDI Devices
	MidiDevice m_MIDIDevice;
	Receiver m_MIDIReceiver;
	
	public Configuration(String ConfigurationRoot)
	{
		// initialize the language map
		_languageTable = new LanguageTable();
		initializeLanguages();
		_languageTable.setLanguage("en");
		// other initialization
		m_ConnectionListeners = new EventListenerList();
		m_DeviceListeners = new EventListenerList();
		m_HoverListeners = new EventListenerList();
		m_BatchListeners = new EventListenerList();
		m_MIDIListeners = new EventListenerList();
		m_TransmitModeListeners = new EventListenerList();
		m_Matrix = new int[getMatrixSize()];
		m_FixedHoverSource = -1;
		m_FixedHoverDestination = -1;
		m_HoverSource = -1;
		m_HoverDestination = -1;
		m_BatchLevel = 0;
		m_IDNumber = 0;
		m_TransmitManually = false;
		m_ActiveWindow = "";
		m_ConfigurationRoot = ConfigurationRoot;
		
		// changed flags
		m_DevicesChanged = false;
		m_PresetsChanged = false;
		for(int Column = 0; Column < getMatrixSize(); ++Column)
		{
			m_Matrix[Column] = -1;
		}
		m_Devices = new Device[2 * getMatrixSize()];
		for(int Device = 0; Device < 2 * getMatrixSize(); ++Device)
		{
			m_Devices[Device] = new Device();
		}
		_presets = new Preset[StaticConfiguration.getNumberOfPresets()];
		for(Integer presetIndex = 0; presetIndex < StaticConfiguration.getNumberOfPresets(); ++presetIndex)
		{
			_presets[presetIndex] = new Preset();
		}
		_MIDIDevices = new ArrayList<MidiDevice.Info>();
		_MIDIDevices.add(null);
		
		MidiDevice.Info MIDIDeviceInfos[] = MidiSystem.getMidiDeviceInfo();
		
		for(int MIDIDeviceNumber = 0; MIDIDeviceNumber < MIDIDeviceInfos.length; ++MIDIDeviceNumber)
		{
			_MIDIDevices.add(MIDIDeviceInfos[MIDIDeviceNumber]);
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
	
	public void setMatrixSize(int matrixSize)
	{
		if(getMatrixSize() != matrixSize)
		{
			m_Matrix = new int[matrixSize];
			for(int Column = 0; Column < matrixSize; ++Column)
			{
				m_Matrix[Column] = -1;
			}
			m_Devices = new Device[2 * matrixSize];
			for(int Device = 0; Device < 2 * matrixSize; ++Device)
			{
				m_Devices[Device] = new Device();
			}
			clearPresets();
			setInteger(_matrixSize, matrixSize, _matrixSizeListeners);
			setMatrixModified(false);
			setDevicesChanged();
			setPresetsChanged();
		}
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
	
	public void openMIDIDevice(String DeviceName)
	{
		MidiDevice.Info MIDIDeviceInfo = _getMIDIDeviceInfo(DeviceName);
		
		closeMIDIDevice();
		try
		{
			m_MIDIDevice = MidiSystem.getMidiDevice(MIDIDeviceInfo);
			m_MIDIDevice.open();
		}
		catch(MidiUnavailableException Exception)
		{
			System.out.println("\"" + MIDIDeviceInfo.getName() + "\" not available for output.");
			
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
			System.out.println("No Receiver for output device \"" + MIDIDeviceInfo.getName() + "\"");
			m_MIDIDevice.close();
			
			return;
		}
		m_MIDIDeviceString = DeviceName;
		fireMIDIDeviceChanged();
	}
	
	private MidiDevice.Info _getMIDIDeviceInfo(String DeviceName)
	{
		for(int MIDIDeviceNumber = 1; MIDIDeviceNumber < _MIDIDevices.size(); ++MIDIDeviceNumber)
		{
			MidiDevice.Info MIDIDeviceInfo = _MIDIDevices.get(MIDIDeviceNumber);
			
			if(MIDIDeviceInfo == null)
			{
				if(DeviceName.equals(getString("None")) == true)
				{
					return null;
				}
			}
			else
			{
				if(DeviceName.equals(MIDIDeviceInfo.getName()) == true)
				{
					return MIDIDeviceInfo;
				}
			}
		}
		
		return null;
	}
	
	public int numberOfMIDIDevices()
	{
		return _MIDIDevices.size();
	}
	
	public String getMIDIDeviceName(int DeviceNumber)
	{
		MidiDevice.Info MIDIDeviceInfo = _MIDIDevices.get(DeviceNumber);
		
		if(MIDIDeviceInfo == null)
		{
			return getString("None");
		}
		else
		{
			return MIDIDeviceInfo.getName();
		}
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
		if(m_MIDIDeviceString != null)
		{
			openMIDIDevice(m_MIDIDeviceString);
		}
	}
	
	public void sendMIDI(MidiMessage message)
	{
		System.out.println("Sending with MIDI ...");
		
		StringBuilder messageString = new StringBuilder();
		
		for(int index = 0; index < message.getLength(); ++index)
		{
			messageString.append(String.format("%02x ", message.getMessage()[index]));
		}
		System.out.println("Message: " + messageString.toString());
		System.out.println("Length: " + message.getLength());
		m_MIDIReceiver.send(message, -1L);
		System.out.println("Sent.");
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
		if((Source >= 0) && (Source < getMatrixSize()))
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
		if((Destination >= 0) && (Destination < getMatrixSize()))
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
		return (m_HoverSource >= 0) && (m_HoverDestination >= 0) && (m_HoverSource < getMatrixSize()) && (m_HoverDestination < getMatrixSize());
	}
	
	public static Integer getMatrixSize()
	{
		return _matrixSize.get();
	}
	
	public static Integer getCurrentCellSize()
	{
		return StaticConfiguration.getCellSize(getMatrixSize());
	}
	
	public static Integer getCurrentTextOffset()
	{
		return StaticConfiguration.getTextOffset(getMatrixSize());
	}
	
	public int getIdentifierFieldWidth()
	{
		return StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth();
	}
	
	public int getNameFieldWidth()
	{
		return StaticConfiguration.getNameFieldWidth();
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
		_selectedPresetIndex = -1;
		fireSelectedPresetIndexChanged();
	}
	
	public void setSelectedPresetIndex(Integer presetIndex)
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("CurrentMatrixType", "Preset");
		Preferences.userRoot().node(m_ConfigurationRoot).putInt("CurrentMatrix", presetIndex);
		_selectedPresetIndex = presetIndex;
		fireSelectedPresetIndexChanged();
	}
	
	public void setCurrentMatrixIsEmpty()
	{
		Preferences.userRoot().node(m_ConfigurationRoot).put("CurrentMatrixType", "Empty");
		_selectedPresetIndex = -1;
		fireSelectedPresetIndexChanged();
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
		for(int I = 0; I < getMatrixSize(); ++I)
		{
			if(getDestinationName(I).equals("") == true)
			{
				setDestinationName(I, getSourceName(I));
			}
		}
	}
	
	public boolean isConnected(int Source, int Destination)
	{
		if(Source >= getMatrixSize() || Destination >= getMatrixSize())
		{
			return false;
		}
		
		return m_Matrix[Destination] == Source;
	}
	
	public void clearNames()
	{
		for(int Name = 0; Name < getMatrixSize() * 2; ++Name)
		{
			setName(Name, "");
		}
	}
	
	public void clearMatrix()
		throws MatrixNotSavedException
	{
		if(getMatrixModified() == true)
		{
			throw new MatrixNotSavedException();
		}
		enterBatch();
		for(int Destination = 0; Destination < getMatrixSize(); ++Destination)
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
	
	public static void setMatrixModified(Boolean matrixModified)
	{
		setBoolean(_matrixModified, matrixModified, _matrixModifiedListeners);
	}
	
	private static Boolean setBoolean(BooleanObject destination, Boolean newValue, EventListenerList listeners)
	{
		Boolean result = false;
		
		if(destination.get() != newValue)
		{
			Boolean oldValue = destination.get();
			
			destination.set(newValue);
			_fireBooleanChanged(listeners, oldValue, newValue);
			result = true;
		}
		_fireBooleanSet(listeners, newValue);
		
		return result;
	}
	
	private static Boolean setInteger(IntegerObject destination, Integer newValue, EventListenerList listeners)
	{
		Boolean result = false;
		
		if(destination.get() != newValue)
		{
			Integer oldValue = destination.get();
			
			destination.set(newValue);
			_fireIntegerChanged(listeners, oldValue, newValue);
			result = true;
		}
		_fireIntegerSet(listeners, newValue);
		
		return result;
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
	
	public static Boolean getMatrixModified()
	{
		return _matrixModified.get();
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
		return getMatrixModified() || isDevicesChanged() || isPresetsChanged();
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
			fireConnectionChanged(Source, Destination, false);
			setMatrixModified(true);
		}
	}
	
	public void setConnected(int Source, int Destination, boolean Connected)
	{
		if((Source >= 0) && (Destination >= 0) && (Source < getMatrixSize()) && (Destination < getMatrixSize()) && (isConnected(Source, Destination) != Connected))
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
			setMatrixModified(true);
			leaveBatch();
		}
	}
	
	public int getConnectedSource(int Destination)
	{
		if((Destination >= 0) && (Destination < getMatrixSize()))
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
	
	public Vector< String > getDestinationNames()
	{
		Vector< String > result = new Vector< String >();
		
		for(Integer deviceIndex = 0; deviceIndex < getMatrixSize(); ++deviceIndex)
		{
			result.add(m_Devices[deviceIndex + getMatrixSize()].getName());
		}
		
		return result;
	}
	
	public Vector< String > getSourceNames()
	{
		Vector< String > result = new Vector< String >();
		
		for(Integer deviceIndex = 0; deviceIndex < getMatrixSize(); ++deviceIndex)
		{
			result.add(m_Devices[deviceIndex].getName());
		}
		
		return result;
	}
	
	public Vector< String > getPresetNames()
	{
		Vector< String > result = new Vector< String >();
		
		for(Preset preset : _presets)
		{
			result.add(preset.getName());
		}
		
		return result;
	}
	
	public String getSourceName(int Index)
	{
		if((Index < getMatrixSize()) && (Index >= 0))
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
		if((Index < getMatrixSize()) && (Index >= 0))
		{
			return m_Devices[Index + getMatrixSize()].getName();
		}
		else
		{
			return "";
		}
	}
	
	public void setSourceName(int Index, String Name)
	{
		if(Index < getMatrixSize())
		{
			setName(Index, Name);
		}
	}
	
	public void setDestinationName(int Index, String Name)
	{
		if(Index < getMatrixSize())
		{
			Index += getMatrixSize();
			setName(Index, Name);
		}
	}
	
	public void setName(int Index, String Name)
	{
		if(Index < getMatrixSize() * 2)
		{
			if(m_Devices[Index].getName().equals(Name) == false)
			{
				m_Devices[Index].setName(Name);
				fireDeviceNameChanged(Index, Index > getMatrixSize(), m_Devices[Index]);
				setDevicesChanged();
			}
		}
	}
	
	public void loadProgramToMatrix(int ProgramIndex)
		throws MatrixNotSavedException
	{
		if(getMatrixModified() == true)
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
				if(Matrix.length != getMatrixSize())
				{
					System.out.println("\tloadProgramToMatrix: Dimensions don't match.");
					
					return;
				}
				enterBatch();
				clearMatrix();
				for(int Destination = 0; Destination < getMatrixSize(); ++Destination)
				{
					setConnected(Matrix[Destination], Destination, true);
				}
				leaveBatch();
			}
		}
		setMatrixModified(false);
		setSelectedPresetIndex(ProgramIndex);
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
		setMatrixModified(false);
		setPresetsChanged();
		setSelectedPresetIndex(PresetIndex);
	}
	
	public static Integer getSelectedPresetIndex()
	{
		return _selectedPresetIndex;
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
	
	private static void _fireBooleanSet(EventListenerList eventListeners, Boolean newValue)
	{
		for(BooleanListener booleanListener : eventListeners.getListeners(BooleanListener.class))
		{
			booleanListener.booleanSet(newValue);
		}
	}
	
	private static void _fireBooleanChanged(EventListenerList eventListeners, Boolean oldValue, Boolean newValue)
	{
		for(BooleanListener booleanListener : eventListeners.getListeners(BooleanListener.class))
		{
			booleanListener.booleanChanged(oldValue, newValue);
		}
	}
	
	private static void _fireIntegerSet(EventListenerList eventListeners, Integer newValue)
	{
		for(IntegerListener integerListener : eventListeners.getListeners(IntegerListener.class))
		{
			integerListener.integerSet(newValue);
		}
	}
	
	private static void _fireIntegerChanged(EventListenerList eventListeners, Integer oldValue, Integer newValue)
	{
		for(IntegerListener integerListener : eventListeners.getListeners(IntegerListener.class))
		{
			integerListener.integerChanged(oldValue, newValue);
		}
	}
	
	private static void fireSelectedPresetIndexChanged()
	{
		SelectionEvent event = null;
		
		for(SelectionListener selectionListener : _selectedPresetIndexListeners.getListeners(SelectionListener.class))
		{
			if(event == null)
			{
				event = new SelectionEvent(_selectedPresetIndex);
			}
			selectionListener.selectionChanged(event);
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
	
	public void addTransmitModeListener(TransmitModeListener Listener)
	{
		m_TransmitModeListeners.add(TransmitModeListener.class, Listener);
	}
	
	public static void addMatrixModifiedListener(BooleanListener listener)
	{
		_matrixModifiedListeners.add(BooleanListener.class, listener);
	}
	
	public static void addMatrixSizeListener(IntegerListener listener)
	{
		_matrixSizeListeners.add(IntegerListener.class, listener);
	}
	
	public static void addSelectedPresetIndexListener(SelectionListener listener)
	{
		_selectedPresetIndexListeners.add(SelectionListener.class, listener);
	}
}
