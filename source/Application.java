import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class Application extends JFrame implements ActionListener, MIDIListener, MetricListener
{
	private Configuration m_Configuration;
	// the pane that holds all the embedded windows
	private JDesktopPane m_Desktop;
	
	// the patch matrix
	private JInternalFrame m_MatrixWindow;
	private MatrixNamePanel m_MatrixNamePanel;
	private MatrixPanel m_MatrixPanel;
	// its popup menu
	private JPopupMenu m_MatrixPopupMenu;
	
	// the device configuration window
	private EditDeviceNamesWindow m_DevicesWindow;
	
	// the edit preset names window
	private EditPresetNamesWindow m_EditPresetNamesWindow;
	
	// the programs window
	private ProgramsWindow m_PresetsWindow;
	
	// the protocol implementer
	private DMXProtocol m_DMXProtocol;
	
	private ButtonGroup m_MIDIDeviceButtonGroup;
	private ButtonGroup m_IDNumberButtonGroup;
	private ButtonGroup m_SystemSizeButtonGroup;
	
	private String m_CurrentMatrixFile;
	private String m_CurrentPresetsFile;
	
	public Application(String ConfigurationRoot)
	{
		super("FRIEND-CHIP  DMX REMOTE");
		
		m_Configuration = new Configuration(ConfigurationRoot);
		m_DMXProtocol = new DMXProtocol(m_Configuration);
		m_Configuration.addBatchListener(m_DMXProtocol);
		m_Configuration.addMIDIListener(this);
		m_Configuration.addMetricListener(this);
		m_CurrentMatrixFile = "";
		m_CurrentPresetsFile = "";
		
		addComponentListener(new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent Event)
			{
				m_Configuration.saveMainWindowX(Event.getComponent().getLocation().x);
				m_Configuration.saveMainWindowY(Event.getComponent().getLocation().y);
			}
			
			public void componentResized(ComponentEvent Event)
			{
				m_Configuration.saveMainWindowWidth(Event.getComponent().getSize().width);
				m_Configuration.saveMainWindowHeight(Event.getComponent().getSize().height);
			}
		}
		);
		
		JMenuBar MenuBar = new JMenuBar();
		{
			m_MatrixPopupMenu = new JPopupMenu(m_Configuration.getString("Matrix"));
			{
				JMenuItem LoadFromPreset = new JMenuItem(m_Configuration.getString("Load from Preset"));
				JMenuItem LoadFromFile = new JMenuItem(m_Configuration.getString("Load from File"));
				JMenuItem SaveToFile = new JMenuItem(m_Configuration.getString("Save to File"));
				JMenuItem SaveToPreset = new JMenuItem(m_Configuration.getString("Save to Preset"));
				JMenuItem ClearMatrix = new JMenuItem(m_Configuration.getString("Clear Matrix"));
				
				LoadFromPreset.addActionListener(this);
				LoadFromFile.addActionListener(this);
				SaveToFile.addActionListener(this);
				SaveToPreset.addActionListener(this);
				ClearMatrix.addActionListener(this);
				m_MatrixPopupMenu.add(LoadFromPreset);
				m_MatrixPopupMenu.add(LoadFromFile);
				m_MatrixPopupMenu.add(SaveToFile);
				m_MatrixPopupMenu.add(SaveToPreset);
				m_MatrixPopupMenu.add(ClearMatrix);
			}
			
			JMenu MatrixMenu = new JMenu(m_Configuration.getString("Matrix"));
			{
				JMenuItem OpenMatrix = new JMenuItem(m_Configuration.getString("Open Matrix"));
				JMenuItem LoadFromFile = new JMenuItem(m_Configuration.getString("Load from File"));
				JMenuItem SaveToFile = new JMenuItem(m_Configuration.getString("Save to File"));
				JMenuItem SaveToPreset = new JMenuItem(m_Configuration.getString("Save to Preset"));
				JMenuItem ClearMatrix = new JMenuItem(m_Configuration.getString("Clear Matrix"));
				
				OpenMatrix.addActionListener(this);
				LoadFromFile.addActionListener(this);
				SaveToFile.addActionListener(this);
				SaveToPreset.addActionListener(this);
				ClearMatrix.addActionListener(this);
				MatrixMenu.add(OpenMatrix);
				MatrixMenu.add(LoadFromFile);
				MatrixMenu.add(SaveToFile);
				MatrixMenu.add(SaveToPreset);
				MatrixMenu.add(ClearMatrix);
			}
			
			JMenu PresetsMenu = new JMenu(m_Configuration.getString("Presets"));
			{
				JMenuItem OpenPresetList = new JMenuItem(m_Configuration.getString("Open Preset List"));
				JMenuItem LoadPresets = new JMenuItem(m_Configuration.getString("Load Presets"));
				JMenuItem SavePresets = new JMenuItem(m_Configuration.getString("Save Presets"));
				JMenuItem ClearPresetList = new JMenuItem(m_Configuration.getString("Clear Preset List"));
				JMenuItem EditPresetNames = new JMenuItem(m_Configuration.getString("Edit Preset Names"));
				
				OpenPresetList.addActionListener(this);
				LoadPresets.addActionListener(this);
				SavePresets.addActionListener(this);
				ClearPresetList.addActionListener(this);
				EditPresetNames.addActionListener(this);
				PresetsMenu.add(OpenPresetList);
				PresetsMenu.add(LoadPresets);
				PresetsMenu.add(SavePresets);
				PresetsMenu.add(ClearPresetList);
				PresetsMenu.add(EditPresetNames);
			}
			
			JMenu DevicesMenu = new JMenu(m_Configuration.getString("Devices"));
			{
				JMenuItem EnterDeviceNames = new JMenuItem(m_Configuration.getString("Enter Device Names"));
				JMenuItem CopySourcesToDestinations = new JMenuItem(m_Configuration.getString("Copy Sources to Destinations"));
				JMenuItem ClearDeviceList = new JMenuItem(m_Configuration.getString("Clear Device List"));
				
				EnterDeviceNames.addActionListener(this);
				CopySourcesToDestinations.addActionListener(this);
				ClearDeviceList.addActionListener(this);
				DevicesMenu.add(EnterDeviceNames);
				DevicesMenu.add(CopySourcesToDestinations);
				DevicesMenu.add(ClearDeviceList);
			}
			
			JMenu MIDIMenu = new JMenu(m_Configuration.getString("MIDI"));
			{
				JMenu MIDIDeviceMenu = new JMenu(m_Configuration.getString("MIDI Device"));
				{
					m_MIDIDeviceButtonGroup = new ButtonGroup();
					ActionListener SelectMIDIDevice = new ActionListener()
					{
						public void actionPerformed(ActionEvent Event)
						{
							String Action = Event.getActionCommand();
							
							m_Configuration.closeMIDIDevice();
							if(Action.equals(m_Configuration.getString("None")) == false)
							{
								m_Configuration.openMIDIDevice(Action);
							}
						}
					};
					
					for(int MIDIDeviceNumber = 0; MIDIDeviceNumber < m_Configuration.numberOfMIDIDevices(); ++MIDIDeviceNumber)
					{
						JRadioButtonMenuItem RadioButton = new JRadioButtonMenuItem(m_Configuration.getMIDIDeviceName(MIDIDeviceNumber));
						
						RadioButton.addActionListener(SelectMIDIDevice);
						m_MIDIDeviceButtonGroup.add(RadioButton);
						MIDIDeviceMenu.add(RadioButton);
						if(RadioButton.getText().equals(m_Configuration.getString("None")) == true)
						{
							m_MIDIDeviceButtonGroup.setSelected(RadioButton.getModel(), true);
						}
					}
				}
				
				JMenu IDNumberMenu = new JMenu(m_Configuration.getString("ID Number"));
				{
					m_IDNumberButtonGroup = new ButtonGroup();
					ActionListener SelectSoftwareChannel = new ActionListener()
					{
						public void actionPerformed(ActionEvent Event)
						{
							String Action = Event.getActionCommand();
							
							m_Configuration.setIDNumber(Integer.valueOf(Action).intValue() - 1);
						}
					};
					for(int ID = 0; ID < 16; ++ID)
					{
						JRadioButtonMenuItem ChannelChoice = new JRadioButtonMenuItem(String.valueOf(ID + 1));
						
						ChannelChoice.addActionListener(SelectSoftwareChannel);
						m_IDNumberButtonGroup.add(ChannelChoice);
						IDNumberMenu.add(ChannelChoice);
						if(ID == m_Configuration.getIDNumber())
						{
							m_IDNumberButtonGroup.setSelected(ChannelChoice.getModel(), true);
						}
					}
				}
				
				JMenuItem Dump = new JMenuItem(m_Configuration.getString("Dump"));
				
				Dump.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent Event)
					{
						if(m_Configuration.getSize() < 64)
						{
							m_DMXProtocol.dumpPresets();
						}
						else
						{
							m_DMXProtocol.dumpC();
						}
					}
				}
				);
				
				//~ JMenuItem DumpC = new JMenuItem(m_Configuration.getString("Dump Protocol C"));
				
				//~ DumpC.addActionListener(new ActionListener()
				//~ {
					//~ public void actionPerformed(ActionEvent Event)
					//~ {
					//~ }
				//~ }
				//~ );
				
				JMenu TransmitMenu = new JMenu(m_Configuration.getString("Transmit"));
				{
					final ButtonGroup TransmitButtonGroup = new ButtonGroup();
					ActionListener SelectTransmit = new ActionListener()
					{
						public void actionPerformed(ActionEvent Event)
						{
							if(Event.getActionCommand().equals(m_Configuration.getString("Transmit manually")) == true)
							{
								m_Configuration.setTransmitManually();
							}
							else
							{
								m_Configuration.setTransmitImmediately();
							}
						}
					};
					
					final JRadioButtonMenuItem TransmitManually = new JRadioButtonMenuItem(m_Configuration.getString("Transmit manually"));
					
					TransmitManually.addActionListener(SelectTransmit);
					TransmitButtonGroup.add(TransmitManually);
					TransmitMenu.add(TransmitManually);
					
					final JRadioButtonMenuItem TransmitImmediately = new JRadioButtonMenuItem(m_Configuration.getString("Transmit immediately"));
					
					TransmitImmediately.addActionListener(SelectTransmit);
					TransmitButtonGroup.add(TransmitImmediately);
					TransmitMenu.add(TransmitImmediately);
					if(m_Configuration.getTransmitManually() == true)
					{
						TransmitButtonGroup.setSelected(TransmitManually.getModel(), true);
					}
					else
					{
						TransmitButtonGroup.setSelected(TransmitImmediately.getModel(), true);
					}
					m_Configuration.addTransmitModeListener(new TransmitModeListener()
					{
						public void changedToTransmitManually()
						{
							TransmitButtonGroup.setSelected(TransmitManually.getModel(), true);
						}
						
						public void changedToTransmitImmediately()
						{
							TransmitButtonGroup.setSelected(TransmitImmediately.getModel(), true);
						}
					}
					);
				}
				
				MIDIMenu.add(MIDIDeviceMenu);
				MIDIMenu.add(IDNumberMenu);
				MIDIMenu.add(Dump);
				//~ MIDIMenu.add(DumpC);
				MIDIMenu.add(TransmitMenu);
			}
			
			JMenu SystemSizeMenu = new JMenu(m_Configuration.getString("System Size"));
			{
				m_SystemSizeButtonGroup = new ButtonGroup();
				ActionListener SelectSystemSize = new ActionListener()
				{
					public void actionPerformed(ActionEvent Event)
					{
						String Action = Event.getActionCommand();
						
						m_Configuration.setSize(Integer.valueOf(Action).intValue());
					}
				};
				
				JRadioButtonMenuItem RadioButton = new JRadioButtonMenuItem("12");
				
				RadioButton.addActionListener(SelectSystemSize);
				m_SystemSizeButtonGroup.add(RadioButton);
				SystemSizeMenu.add(RadioButton);
				if(m_Configuration.getSize() == 12)
				{
					m_SystemSizeButtonGroup.setSelected(RadioButton.getModel(), true);
				}
				RadioButton = new JRadioButtonMenuItem("18");
				RadioButton.addActionListener(SelectSystemSize);
				m_SystemSizeButtonGroup.add(RadioButton);
				SystemSizeMenu.add(RadioButton);
				if(m_Configuration.getSize() == 18)
				{
					m_SystemSizeButtonGroup.setSelected(RadioButton.getModel(), true);
				}
				RadioButton = new JRadioButtonMenuItem("32");
				RadioButton.addActionListener(SelectSystemSize);
				m_SystemSizeButtonGroup.add(RadioButton);
				SystemSizeMenu.add(RadioButton);
				if(m_Configuration.getSize() == 32)
				{
					m_SystemSizeButtonGroup.setSelected(RadioButton.getModel(), true);
				}
				RadioButton = new JRadioButtonMenuItem("64");
				RadioButton.addActionListener(SelectSystemSize);
				m_SystemSizeButtonGroup.add(RadioButton);
				SystemSizeMenu.add(RadioButton);
				if(m_Configuration.getSize() == 64)
				{
					m_SystemSizeButtonGroup.setSelected(RadioButton.getModel(), true);
				}
			}
			
			JMenu WindowsMenu = new JMenu(m_Configuration.getString("Windows"));
			{
				JMenuItem Matrix = new JMenuItem(m_Configuration.getString("Matrix"));
				JMenuItem Devices = new JMenuItem(m_Configuration.getString("Devices"));
				JMenuItem Presets = new JMenuItem(m_Configuration.getString("Presets"));
				JMenuItem EditPresetNames = new JMenuItem(m_Configuration.getString("Edit Preset Names"));
				JMenuItem ResetPositions = new JMenuItem(m_Configuration.getString("Reset Positions"));
				
				Matrix.addActionListener(this);
				Devices.addActionListener(this);
				Presets.addActionListener(this);
				EditPresetNames.addActionListener(this);
				ResetPositions.addActionListener(this);
				WindowsMenu.add(Matrix);
				WindowsMenu.add(Devices);
				WindowsMenu.add(Presets);
				WindowsMenu.add(EditPresetNames);
				WindowsMenu.add(ResetPositions);
			}
			MenuBar.add(SystemSizeMenu);
			MenuBar.add(MatrixMenu);
			MenuBar.add(PresetsMenu);
			MenuBar.add(DevicesMenu);
			MenuBar.add(MIDIMenu);
			MenuBar.add(WindowsMenu);
		}
		m_Desktop = new JDesktopPane();
		m_Desktop.setBackground(new Color(0.18f, 0.20f, 0.22f));
		setJMenuBar(MenuBar);
		setContentPane(m_Desktop);
		createWindows();
		if(m_Configuration.getCurrentPresetsFile().equals("") == false)
		{
			loadPresetsFromFile(m_Configuration.getCurrentPresetsFile(), true);
		}
		//~ if(m_Configuration.isCurrentMatrixSet() == true)
		//~ {
			//~ if(m_Configuration.isCurrentMatrixFile() == true)
			//~ {
				//~ matrixLoadFromFile(m_Configuration.getCurrentMatrixFile());
			//~ }
			//~ else if(m_Configuration.isCurrentMatrixPreset() == true)
			//~ {
				//~ try
				//~ {
					//~ m_Configuration.loadProgramToMatrix(m_Configuration.getCurrentMatrixPreset());
				//~ }
				//~ catch(MatrixNotSavedException Exception)
				//~ {
				//~ }
			//~ }
		//~ }
		setLocation(m_Configuration.getMainWindowX(), m_Configuration.getMainWindowY());
		setSize(m_Configuration.getMainWindowWidth(), m_Configuration.getMainWindowHeight());
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent Event)
	{
		String Action = Event.getActionCommand();
		
		if((Action.equals(m_Configuration.getString("Open Matrix")) == true) || (Action.equals(m_Configuration.getString("Matrix")) == true))
		{
			matrixOpenMatrix();
		}
		else if(Action.equals(m_Configuration.getString("Load from File")) == true)
		{
			matrixLoadFromFile();
		}
		else if(Action.equals(m_Configuration.getString("Save to File")) == true)
		{
			matrixSaveToFile();
		}
		else if(Action.equals(m_Configuration.getString("Save to Preset")) == true)
		{
			matrixSaveToPreset();
		}
		else if(Action.equals(m_Configuration.getString("Clear Matrix")) == true)
		{
			matrixClearMatrix();
		}
		else if((Action.equals(m_Configuration.getString("Open Preset List")) == true) || (Action.equals(m_Configuration.getString("Presets")) == true) || (Action.equals(m_Configuration.getString("Load from Preset")) == true))
		{
			presetsShowPresetList();
		}
		else if(Action.equals(m_Configuration.getString("Load Presets")) == true)
		{
			presetsShowPresetList();
			presetsLoadPresets();
		}
		else if(Action.equals(m_Configuration.getString("Save Presets")) == true)
		{
			presetsShowPresetList();
			presetsSavePresets();
		}
		else if(Action.equals(m_Configuration.getString("Clear Preset List")) == true)
		{
			presetsClearPresetList();
		}
		else if(Action.equals(m_Configuration.getString("Edit Preset Names")) == true)
		{
			presetsEditPresetNames();
		}
		else if((Action.equals(m_Configuration.getString("Enter Device Names")) == true) || (Action.equals(m_Configuration.getString("Devices")) == true))
		{
			devicesEnterDeviceNames();
		}
		else if(Action.equals(m_Configuration.getString("Copy Sources to Destinations")) == true)
		{
			devicesCopySourcesToDestinations();
		}
		else if(Action.equals(m_Configuration.getString("Clear Device List")) == true)
		{
			devicesClearDeviceList();
		}
		else if(Action.equals(m_Configuration.getString("Reset Positions")) == true)
		{
			setLocation(0, 0);
			m_MatrixWindow.setLocation(0, 0);
			m_DevicesWindow.setLocation(0, 0);
			m_PresetsWindow.setLocation(0, 0);
			m_EditPresetNamesWindow.setLocation(0, 0);
		}
		else
		{
			System.out.println("Unhandled action \"" + Action + "\".");
		}
	}
	
	private void createWindows()
	{
		{
			if(m_MatrixPanel == null)
			{
				m_MatrixPanel = new MatrixPanel(m_Configuration);
				m_MatrixPanel.addMouseListener(new MouseAdapter()
				{
					public void mousePressed(MouseEvent event)
					{
						maybeShowPopup(event);
					}
					
					public void mouseReleased(MouseEvent event)
					{
						maybeShowPopup(event);
					}
					
					private void maybeShowPopup(MouseEvent event)
					{
						if(event.isPopupTrigger() == true)
						{
							m_MatrixPopupMenu.show(m_MatrixPanel, event.getX(), event.getY());
						}
					}
				}
				);
			}
			if(m_MatrixNamePanel == null)
			{
				m_MatrixNamePanel = new MatrixNamePanel(m_Configuration);
				m_MatrixNamePanel.setMaximumSize(new Dimension(300, 600));
				m_Configuration.setMatrixNamePanel(m_MatrixNamePanel);
			}
			if(m_MatrixWindow == null)
			{
				m_MatrixWindow = new JInternalFrame(m_Configuration.getString("Matrix"), true, true, true, false);
				m_MatrixWindow.addComponentListener(new ComponentListener()
				{
					public void componentHidden(ComponentEvent Event)
					{
						m_Configuration.saveMatrixWindowVisible(false);
					}
					
					public void componentMoved(ComponentEvent Event)
					{
						m_Configuration.saveMatrixWindowX(Event.getComponent().getLocation().x);
						m_Configuration.saveMatrixWindowY(Event.getComponent().getLocation().y);
					}
					
					public void componentResized(ComponentEvent Event)
					{
						m_Configuration.saveMatrixWindowWidth(Event.getComponent().getSize().width);
						m_Configuration.saveMatrixWindowHeight(Event.getComponent().getSize().height);
					}
					
					public void componentShown(ComponentEvent Event)
					{
						m_Configuration.saveMatrixWindowVisible(true);
					}
				}
				);
				m_MatrixWindow.addInternalFrameListener(new InternalFrameAdapter()
				{
					public void internalFrameActivated(InternalFrameEvent Event)
					{
						m_Configuration.setActiveWindow("Matrix");
					}
					
					public void internalFrameDeactivated(InternalFrameEvent Event)
					{
						m_Configuration.setActiveWindow("");
					}
				}
				);
				m_MatrixWindow.getContentPane().setLayout(new BorderLayout());
				
				JScrollPane scrollPane = new JScrollPane(m_MatrixPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
				scrollPane.setBorder(BorderFactory.createEmptyBorder());
				m_MatrixWindow.getContentPane().add(m_MatrixNamePanel, BorderLayout.WEST);
				m_MatrixWindow.getContentPane().add(scrollPane, BorderLayout.CENTER);
				m_MatrixWindow.setSize(m_Configuration.getMatrixWindowWidth(), m_Configuration.getMatrixWindowHeight());
				m_MatrixWindow.setLocation(m_Configuration.getMatrixWindowX(), m_Configuration.getMatrixWindowY());
				m_MatrixWindow.setVisible(m_Configuration.getMatrixWindowVisible());
				m_MatrixWindow.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
				m_MatrixWindow.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), "transmit");
				m_MatrixWindow.getActionMap().put("transmit", new AbstractAction()
				{
					public void actionPerformed(ActionEvent Event)
					{
						m_Configuration.transmitNow();
						m_MatrixNamePanel.transmitted();
					}
				}
				);
			}
		}
		{
			if(m_PresetsWindow == null)
			{
				m_PresetsWindow = new ProgramsWindow(m_Configuration);
				m_PresetsWindow.addComponentListener(new ComponentListener()
				{
					public void componentHidden(ComponentEvent Event)
					{
						m_Configuration.savePresetsWindowVisible(false);
					}
					
					public void componentMoved(ComponentEvent Event)
					{
						m_Configuration.savePresetsWindowX(Event.getComponent().getLocation().x);
						m_Configuration.savePresetsWindowY(Event.getComponent().getLocation().y);
					}
					
					public void componentResized(ComponentEvent Event)
					{
						m_Configuration.savePresetsWindowWidth(Event.getComponent().getSize().width);
						m_Configuration.savePresetsWindowHeight(Event.getComponent().getSize().height);
					}
					
					public void componentShown(ComponentEvent Event)
					{
						m_Configuration.savePresetsWindowVisible(true);
					}
				}
				);
				m_PresetsWindow.addInternalFrameListener(new InternalFrameAdapter()
				{
					public void internalFrameActivated(InternalFrameEvent Event)
					{
						m_Configuration.setActiveWindow("Presets");
					}
					
					public void internalFrameDeactivated(InternalFrameEvent Event)
					{
						m_Configuration.setActiveWindow("");
					}
				}
				);
				m_PresetsWindow.setSize(m_Configuration.getPresetsWindowWidth(), m_Configuration.getPresetsWindowHeight());
				m_PresetsWindow.setLocation(m_Configuration.getPresetsWindowX(), m_Configuration.getPresetsWindowY());
				m_PresetsWindow.setVisible(m_Configuration.getPresetsWindowVisible());
				m_PresetsWindow.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
			}
		}
		{
			if(m_EditPresetNamesWindow == null)
			{
				m_EditPresetNamesWindow = new EditPresetNamesWindow(m_Configuration);
				m_EditPresetNamesWindow.addComponentListener(new ComponentListener()
				{
					public void componentHidden(ComponentEvent Event)
					{
						m_Configuration.saveEditPresetNamesWindowVisible(false);
					}
					
					public void componentMoved(ComponentEvent Event)
					{
						m_Configuration.saveEditPresetNamesWindowX(Event.getComponent().getLocation().x);
						m_Configuration.saveEditPresetNamesWindowY(Event.getComponent().getLocation().y);
					}
					
					public void componentResized(ComponentEvent Event)
					{
						m_Configuration.saveEditPresetNamesWindowWidth(Event.getComponent().getSize().width);
						m_Configuration.saveEditPresetNamesWindowHeight(Event.getComponent().getSize().height);
					}
					
					public void componentShown(ComponentEvent Event)
					{
						m_Configuration.saveEditPresetNamesWindowVisible(true);
					}
				}
				);
				m_EditPresetNamesWindow.addInternalFrameListener(new InternalFrameAdapter()
				{
					public void internalFrameActivated(InternalFrameEvent Event)
					{
						m_Configuration.setActiveWindow("EditPresetNames");
					}
					
					public void internalFrameDeactivated(InternalFrameEvent Event)
					{
						m_Configuration.setActiveWindow("");
					}
				}
				);
				m_EditPresetNamesWindow.setSize(m_Configuration.getEditPresetNamesWindowWidth(), m_Configuration.getEditPresetNamesWindowHeight());
				m_EditPresetNamesWindow.setLocation(m_Configuration.getEditPresetNamesWindowX(), m_Configuration.getEditPresetNamesWindowY());
				m_EditPresetNamesWindow.setVisible(m_Configuration.getEditPresetNamesWindowVisible());
				m_EditPresetNamesWindow.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
			}
		}
		{
			if(m_DevicesWindow == null)
			{
				m_DevicesWindow = new EditDeviceNamesWindow(m_Configuration);
				m_DevicesWindow.addComponentListener(new ComponentListener()
				{
					public void componentHidden(ComponentEvent Event)
					{
						m_Configuration.saveDeviceWindowVisible(false);
					}
					
					public void componentMoved(ComponentEvent Event)
					{
						m_Configuration.saveDeviceWindowX(Event.getComponent().getLocation().x);
						m_Configuration.saveDeviceWindowY(Event.getComponent().getLocation().y);
					}
					
					public void componentResized(ComponentEvent Event)
					{
						m_Configuration.saveDeviceWindowWidth(Event.getComponent().getSize().width);
						m_Configuration.saveDeviceWindowHeight(Event.getComponent().getSize().height);
					}
					
					public void componentShown(ComponentEvent Event)
					{
						m_Configuration.saveDeviceWindowVisible(true);
					}
				}
				);
				m_DevicesWindow.addInternalFrameListener(new InternalFrameAdapter()
				{
					public void internalFrameActivated(InternalFrameEvent Event)
					{
						m_Configuration.setActiveWindow("EditDeviceNames");
					}
					
					public void internalFrameDeactivated(InternalFrameEvent Event)
					{
						m_Configuration.setActiveWindow("");
					}
				}
				);
				m_DevicesWindow.setSize(m_Configuration.getDeviceWindowWidth(), m_Configuration.getDeviceWindowHeight());
				m_DevicesWindow.setLocation(m_Configuration.getDeviceWindowX(), m_Configuration.getDeviceWindowY());
				m_DevicesWindow.setVisible(m_Configuration.getDeviceWindowVisible());
				m_DevicesWindow.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
			}
		}
		
		// add the windows to the desktop window
		// the order may change
		m_Desktop.add(m_PresetsWindow);
		m_Desktop.add(m_MatrixWindow);
		m_Desktop.add(m_DevicesWindow);
		m_Desktop.add(m_EditPresetNamesWindow);
		
		String ActiveWindow = m_Configuration.getActiveWindow();
		
		if(ActiveWindow.equals("Matrix") == true)
		{
			matrixOpenMatrix();
		}
		else if(ActiveWindow.equals("Presets") == true)
		{
			presetsShowPresetList();
		}
		else if(ActiveWindow.equals("EditPresetNames") == true)
		{
			presetsEditPresetNames();
		}
		else if(ActiveWindow.equals("EditDeviceNames") == true)
		{
			devicesEnterDeviceNames();
		}
	}
	
	private void matrixOpenMatrix()
	{
		m_MatrixWindow.setVisible(true);
		m_MatrixWindow.toFront();
		try
		{
			m_MatrixWindow.setSelected(true);
		}
		catch(PropertyVetoException Exception)
		{
		}
	}
	
	public void matrixLoadFromFile(String Path)
	{
		FileInputStream File;
		
		try
		{
			File = new FileInputStream(Path);
		}
		catch(FileNotFoundException Exception)
		{
			return;
		}
		
		DataInputStream Data = new DataInputStream(File);
		
		try
		{
			int IntData;
			String UTFData;
			boolean BooleanData;
			
			IntData = Data.readInt();
			if(IntData != m_Configuration.getSize())
			{
				JOptionPane.showMessageDialog(null, "The matrix you try to read from the file is of size " + IntData + " but your current matrix' size is " + m_Configuration.getSize() + ".\nPlease adjust the current size before loading.", "Sizes differ", JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			if(m_Configuration.isMatrixChanged() == true)
			{
				if(JOptionPane.showConfirmDialog(null, "The matrix is not saved yet.\nDo you really want to override the matrix' content?", "Matrix unsaved ...", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				{
					return;
				}
				m_Configuration.setMatrixSaved();
			}
			m_Configuration.enterBatch();
			try
			{
				m_Configuration.clearNames();
				m_Configuration.clearMatrix();
			}
			catch(MatrixNotSavedException Exception)
			{
				// leave batch here if this fails
				m_Configuration.leaveBatch();
				// this will not happen for the matrix has already been saved
				return;
			}
			for(int Name = 0; Name < m_Configuration.getSize(); ++Name)
			{
				UTFData = Data.readUTF();
				m_Configuration.setSourceName(Name, UTFData);
			}
			for(int Name = 0; Name < m_Configuration.getSize(); ++Name)
			{
				UTFData = Data.readUTF();
				m_Configuration.setDestinationName(Name, UTFData);
			}
			IntData = Data.readInt();
			if(IntData != 1)
			{
				System.out.println("More than one data set in file.");
				
				return;
			}
			UTFData = Data.readUTF();
			for(int Destination = 0; Destination < m_Configuration.getSize(); ++Destination)
			{
				IntData = Data.readInt();
				if(IntData != -1)
				{
					m_Configuration.setConnected(IntData, Destination, true);
				}
			}
			BooleanData = Data.readBoolean();
			if(BooleanData == true)
			{
				UTFData = Data.readUTF();
				m_Configuration.openMIDIDevice(UTFData);
				IntData = Data.readInt();
				m_Configuration.setIDNumber(IntData);
			}
			matrixOpenMatrix();
			m_Configuration.leaveBatch();
		}
		catch(IOException Exception)
		{
			System.out.println("Error reading data.");
			
			return;
		}
		m_Configuration.setCurrentMatrixIsFile(Path);
		m_Configuration.setDevicesSaved();
		m_Configuration.setMatrixSaved();
		setTitle("FRIEND-CHIP  DMX REMOTE - Matrix: " + Path);
		m_CurrentMatrixFile = Path;
	}
	
	public void matrixLoadFromFile()
	{
		JFileChooser FileChooser = new JFileChooser();
		
		if(m_Configuration.getMatrixLoadFromFileCurrentDirectory().equals("") == false)
		{
			FileChooser.setCurrentDirectory(new File(m_Configuration.getMatrixLoadFromFileCurrentDirectory()));
		}
		FileChooser.setFileFilter(new MatrixFileFilter());
		if(FileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			m_Configuration.setMatrixLoadFromFileCurrentDirectory(FileChooser.getCurrentDirectory().getAbsolutePath());
			matrixLoadFromFile(FileChooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	public void matrixSaveToFile()
	{
		JFileChooser FileChooser = new JFileChooser();
		
		if(m_Configuration.getMatrixSaveToFileCurrentDirectory().equals("") == false)
		{
			FileChooser.setCurrentDirectory(new File(m_Configuration.getMatrixSaveToFileCurrentDirectory()));
		}
		if(m_CurrentMatrixFile.equals("") == false)
		{
			FileChooser.setSelectedFile(new File(m_CurrentMatrixFile));
		}
		
		MatrixFileFilter Filter = new MatrixFileFilter();
		
		FileChooser.setFileFilter(Filter);
		if(FileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			m_Configuration.setMatrixSaveToFileCurrentDirectory(FileChooser.getCurrentDirectory().getAbsolutePath());
			
			File SelectedFile = FileChooser.getSelectedFile();
			
			if(Filter.accept(SelectedFile) == false)
			{
				SelectedFile = new File(SelectedFile.toString() + "." + Filter.getExtension());
			}
			
			FileOutputStream File;
			
			try
			{
				File = new FileOutputStream(SelectedFile);
			}
			catch(FileNotFoundException Exception)
			{
				return;
			}
			
			DataOutputStream Data = new DataOutputStream(File);
			
			try
			{
				Data.writeInt(m_Configuration.getSize());
				for(int Name = 0; Name < m_Configuration.getSize(); ++Name)
				{
					Data.writeUTF(m_Configuration.getSourceName(Name));
				}
				for(int Name = 0; Name < m_Configuration.getSize(); ++Name)
				{
					Data.writeUTF(m_Configuration.getDestinationName(Name));
				}
				Data.writeInt(1);
				Data.writeUTF("Name");
				for(int Destination = 0; Destination < m_Configuration.getSize(); ++Destination)
				{
					Data.writeInt(m_Configuration.getConnectedSource(Destination));
				}
				Data.writeBoolean(true);
				Data.writeUTF(m_Configuration.getMIDIDeviceString());
				Data.writeInt(m_Configuration.getIDNumber());
				Data.close();
			}
			catch(IOException Exception)
			{
				return;
			}
			m_Configuration.setCurrentMatrixIsFile(SelectedFile.getPath());
			m_Configuration.setMatrixSaved();
			setTitle("FRIEND-CHIP  DMX REMOTE - Matrix: " + SelectedFile.getPath());
			m_CurrentMatrixFile = SelectedFile.getPath();
		}
	}
	
	public void matrixSaveToPreset()
	{
		SelectProgramDialog Dialog = new SelectProgramDialog(m_Configuration, this, true);
		
		Dialog.setVisible(true);
		if(Dialog.getSelection() != -1)
		{
			m_Configuration.saveMatrixToPreset(Dialog.getSelection());
			m_Configuration.getProgram(Dialog.getSelection()).setName(Dialog.getName());
		}
	}
	
	public void matrixClearMatrix()
	{
		try
		{
			m_Configuration.clearMatrix();
			m_Configuration.setMatrixSaved();
			m_Configuration.setCurrentMatrixIsEmpty();
		}
		catch(MatrixNotSavedException Exception)
		{
			if(JOptionPane.showConfirmDialog(null, "The matrix is not saved yet.\nDo you really want to clear the matrix?", "Matrix unsaved ...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				m_Configuration.setMatrixSaved();
				try
				{
					m_Configuration.clearMatrix();
					m_Configuration.setMatrixSaved();
					m_Configuration.setCurrentMatrixIsEmpty();
				}
				catch(MatrixNotSavedException Exception2)
				{
				}
			}
		}
	}
	
	public void presetsSavePresets()
	{
		JFileChooser FileChooser = new JFileChooser();
		
		if(m_Configuration.getPresetsSaveToFileCurrentDirectory().equals("") == false)
		{
			FileChooser.setCurrentDirectory(new File(m_Configuration.getPresetsSaveToFileCurrentDirectory()));
		}
		if(m_CurrentPresetsFile.equals("") == false)
		{
			FileChooser.setSelectedFile(new File(m_CurrentPresetsFile));
		}
		
		PresetsFileFilter Filter = new PresetsFileFilter();
		
		FileChooser.setFileFilter(Filter);
		if(FileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			m_Configuration.setPresetsSaveToFileCurrentDirectory(FileChooser.getCurrentDirectory().getAbsolutePath());
			
			File SelectedFile = FileChooser.getSelectedFile();
			
			if(Filter.accept(SelectedFile) == false)
			{
				SelectedFile = new File(SelectedFile.toString() + "." + Filter.getExtension());
			}
			
			FileOutputStream File;
			
			try
			{
				File = new FileOutputStream(SelectedFile);
			}
			catch(FileNotFoundException Exception)
			{
				return;
			}
			
			DataOutputStream Data = new DataOutputStream(File);
			
			try
			{
				Data.writeInt(m_Configuration.getSize());
				for(int Name = 0; Name < m_Configuration.getSize(); ++Name)
				{
					Data.writeUTF(m_Configuration.getSourceName(Name));
				}
				for(int Name = 0; Name < m_Configuration.getSize(); ++Name)
				{
					Data.writeUTF(m_Configuration.getDestinationName(Name));
				}
				Data.writeInt(50);
				for(int ProgramIndex = 0; ProgramIndex < m_Configuration.getNumberOfPresets(); ++ProgramIndex)
				{
					Program Program = m_Configuration.getProgram(ProgramIndex);
					
					Data.writeUTF(Program.getName());
					
					int [] Matrix = Program.getMatrix();
					
					for(int Destination = 0; Destination < m_Configuration.getSize(); ++Destination)
					{
						if(Matrix == null)
						{
							Data.writeInt(-1);
						}
						else
						{
							Data.writeInt(Matrix[Destination]);
						}
					}
				}
				Data.writeBoolean(true);
				Data.writeUTF(m_Configuration.getMIDIDeviceString());
				Data.writeInt(m_Configuration.getIDNumber());
			}
			catch(IOException Exception)
			{
				return;
			}
			m_Configuration.setCurrentPresetsFile(SelectedFile.getPath());
			m_Configuration.setPresetsSaved();
			setTitle("FRIEND-CHIP  DMX REMOTE - Preset: " + SelectedFile.getPath());
			m_CurrentPresetsFile = SelectedFile.getPath();
		}
	}
	
	public void loadPresetsFromFile(String Path, boolean Startup)
	{
		FileInputStream File;
		
		try
		{
			File = new FileInputStream(Path);
		}
		catch(FileNotFoundException Exception)
		{
			return;
		}
		
		DataInputStream Data = new DataInputStream(File);
		
		try
		{
			int IntData;
			String UTFData;
			boolean BooleanData;
			
			IntData = Data.readInt();
			if(Startup == true)
			{
				m_Configuration.setSize(IntData);
			}
			if(IntData != m_Configuration.getSize())
			{
				JOptionPane.showMessageDialog(null, "The matrix you try to read from the file is of size " + IntData + " but your current matrix' size is " + m_Configuration.getSize() + ".\nPlease adjust the current size before loading.", "Sizes differ", JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			m_Configuration.clearNames();
			for(int Name = 0; Name < m_Configuration.getSize(); ++Name)
			{
				UTFData = Data.readUTF();
				m_Configuration.setSourceName(Name, UTFData);
			}
			for(int Name = 0; Name < m_Configuration.getSize(); ++Name)
			{
				UTFData = Data.readUTF();
				m_Configuration.setDestinationName(Name, UTFData);
			}
			IntData = Data.readInt();
			if(IntData > m_Configuration.getNumberOfPresets())
			{
				System.out.println("More than " + m_Configuration.getNumberOfPresets() + " data sets in file, so I will truncate at " + m_Configuration.getNumberOfPresets() + ".");
			}
			
			int [] Matrix = new int[m_Configuration.getSize()];
			
			for(int ProgramIndex = 0; ProgramIndex < m_Configuration.getNumberOfPresets(); ++ProgramIndex)
			{
				Program Program = m_Configuration.getProgram(ProgramIndex);
				
				UTFData = Data.readUTF();
				Program.setName(UTFData);
				for(int Destination = 0; Destination < m_Configuration.getSize(); ++Destination)
				{
					Matrix[Destination] = Data.readInt();
				}
				Program.setMatrix(Matrix);
			}
			BooleanData = Data.readBoolean();
			if(BooleanData == true)
			{
				UTFData = Data.readUTF();
				m_Configuration.openMIDIDevice(UTFData);
				IntData = Data.readInt();
				m_Configuration.setIDNumber(IntData);
			}
		}
		catch(IOException Exception)
		{
			return;
		}
		m_Configuration.setCurrentPresetsFile(Path);
		m_Configuration.setMatrixSaved();
		m_Configuration.setDevicesSaved();
		m_Configuration.setPresetsSaved();
		setTitle("FRIEND-CHIP  DMX REMOTE - Preset: " + Path);
		m_CurrentPresetsFile = Path;
	}
	
	public void presetsLoadPresets()
	{
		JFileChooser FileChooser = new JFileChooser();
		
		if(m_Configuration.getPresetsLoadFromFileCurrentDirectory().equals("") == false)
		{
			FileChooser.setCurrentDirectory(new File(m_Configuration.getPresetsLoadFromFileCurrentDirectory()));
		}
		FileChooser.setFileFilter(new PresetsFileFilter());
		if(FileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			m_Configuration.setPresetsLoadFromFileCurrentDirectory(FileChooser.getCurrentDirectory().getAbsolutePath());
			loadPresetsFromFile(FileChooser.getSelectedFile().getAbsolutePath(), false);
		}
	}
	
	public void presetsShowPresetList()
	{
		m_PresetsWindow.setVisible(true);
		m_PresetsWindow.toFront();
		try
		{
			m_PresetsWindow.setSelected(true);
		}
		catch(PropertyVetoException Exception)
		{
		}
	}
	
	public void presetsClearPresetList()
	{
		m_Configuration.clearPresets();
	}
	
	public void presetsEditPresetNames()
	{
		m_EditPresetNamesWindow.setVisible(true);
		m_EditPresetNamesWindow.toFront();
		try
		{
			m_EditPresetNamesWindow.setSelected(true);
		}
		catch(PropertyVetoException Exception)
		{
		}
	}
	
	public void devicesEnterDeviceNames()
	{
		m_DevicesWindow.setVisible(true);
		m_DevicesWindow.toFront();
		try
		{
			m_DevicesWindow.setSelected(true);
		}
		catch(PropertyVetoException Exception)
		{
		}
	}
	
	public void devicesCopySourcesToDestinations()
	{
		m_Configuration.copySourcesToDestinations();
	}
	
	public void devicesClearDeviceList()
	{
		m_Configuration.clearNames();
	}
	
	public void MIDIDeviceChanged()
	{
		Enumeration Buttons = m_MIDIDeviceButtonGroup.getElements();
		
		while(Buttons.hasMoreElements() == true)
		{
			AbstractButton Button = (AbstractButton)Buttons.nextElement();
			
			if(m_Configuration.getMIDIDeviceString().equals("") == true)
			{
				if(Button.getText().equals("None") == true)
				{
					m_MIDIDeviceButtonGroup.setSelected(Button.getModel(), true);
					
					return;
				}
			}
			else
			{
				if(Button.getText().equals(m_Configuration.getMIDIDeviceString()) == true)
				{
					m_MIDIDeviceButtonGroup.setSelected(Button.getModel(), true);
					
					return;
				}
			}
		}
	}
	
	public void IDNumberChanged()
	{
		Enumeration Buttons = m_IDNumberButtonGroup.getElements();
		
		while(Buttons.hasMoreElements() == true)
		{
			AbstractButton Button = (AbstractButton)Buttons.nextElement();
			
			if(Button.getText().equals(String.valueOf(m_Configuration.getIDNumber() + 1)) == true)
			{
				m_IDNumberButtonGroup.setSelected(Button.getModel(), true);
				
				return;
			}
		}
	}
	
	public void metricChanged(int WhatChanged)
	{
		if((WhatChanged & MetricListener.SIZE_CHANGED) == MetricListener.SIZE_CHANGED)
		{
			Enumeration Buttons = m_SystemSizeButtonGroup.getElements();
			
			while(Buttons.hasMoreElements() == true)
			{
				AbstractButton Button = (AbstractButton)Buttons.nextElement();
				
				if(Button.getText().equals(String.valueOf(m_Configuration.getSize())) == true)
				{
					m_SystemSizeButtonGroup.setSelected(Button.getModel(), true);
					
					return;
				}
			}
		}
	}
	
	public static void main(String [] Arguments)
	{
		String JarFile = Application.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		final Application Application = new Application(JarFile.substring(JarFile.lastIndexOf(System.getProperty("file.separator")) + 1, JarFile.lastIndexOf(".")));
		
		Application.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Application.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent WindowEvent)
			{
				if(Application.m_Configuration.isChanged() == true)
				{
					if(JOptionPane.showConfirmDialog(null, "There is unsaved data.\nDo you really want to close without saving?", "Unsaved data ...", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
					{
						return;
					}
				}
				System.exit(0);
			}
		}
		);
	}
}
