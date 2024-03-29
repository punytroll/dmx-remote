import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.event.*;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

class ProgramsWindow extends JInternalFrame implements PresetListener
{
	private Configuration m_Configuration;
	private PresetsPanel m_PresetsPanel;
	private JPanel _presetNamePanel;
	private JPanel _presetNumberPanel;
	private JPanel m_NamePanel;
	private JLabel m_PresetNumberLabel;
	private JLabel m_PresetNameLabel;
	private int _selectedPresetIndex;
	
	public ProgramsWindow(Configuration configuration)
	{
		super(configuration.getString("Presets"), true, true, true, false);
		setBackground(StaticConfiguration.getWindowBackgroundColor());
		m_Configuration = configuration;
		_selectedPresetIndex = -1;
		getContentPane().setLayout(new BorderLayout());
		// create the Presets Panel
		m_PresetsPanel = new PresetsPanel(configuration);
		Configuration.addSelectedPresetIndexListener(new SelectionListener()
		{
			public void selectionChanged(SelectionEvent event)
			{
				m_PresetsPanel.setSelectedPresetIndex(event.getSelection());
			}
		});
		
		JPanel PresetsPanel = new JPanel();
		
		PresetsPanel.setOpaque(false);
		PresetsPanel.setLayout(new GridBagLayout());
		PresetsPanel.add(m_PresetsPanel);
		getContentPane().add(PresetsPanel, BorderLayout.CENTER);
		
		// create the Name Panel
		m_NamePanel = new JPanel();
		m_NamePanel.setOpaque(false);
		m_NamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2 * StaticConfiguration.getCellBoxPadding(), 10));
		
		Box VBox = Box.createVerticalBox();
		Font NameFont = new Font(null, Font.PLAIN, 14);
		{
			{
				JLabel PresetLabel = new JLabel(m_Configuration.getCapitalizedString("Preset"));
				
				PresetLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				PresetLabel.setFont(PresetLabel.getFont().deriveFont(Font.BOLD));
				VBox.add(PresetLabel);
			}
			VBox.add(Box.createVerticalStrut(10));
			{
				JPanel PresetPanel = new JPanel();
				
				PresetPanel.setAlignmentX(LEFT_ALIGNMENT);
				PresetPanel.setLayout(new BoxLayout(PresetPanel, BoxLayout.LINE_AXIS));
				PresetPanel.setOpaque(false);
				
				_presetNumberPanel = new JPanel(new GridLayout(1, 1));
				_presetNumberPanel.setBackground(StaticConfiguration.getPresetPanelColor());
				_presetNumberPanel.setPreferredSize(new Dimension(25, 25));
				m_PresetNumberLabel = new JLabel("", JLabel.CENTER);
				m_PresetNumberLabel.setForeground(Color.white);
				_presetNumberPanel.add(m_PresetNumberLabel);
				_presetNamePanel = new JPanel(new GridLayout(1, 1));
				_presetNamePanel.setBackground(StaticConfiguration.getPresetPanelColor());
				_presetNamePanel.setPreferredSize(new Dimension(160, 25));
				m_PresetNameLabel = new JLabel("", JLabel.LEFT);
				m_PresetNameLabel.setForeground(Color.white);
				m_PresetNameLabel.setBorder(BorderFactory.createMatteBorder(0, 8, 0, 8, StaticConfiguration.getPresetPanelColor()));
				_presetNamePanel.add(m_PresetNameLabel);
				PresetPanel.add(_presetNumberPanel);
				PresetPanel.add(Box.createRigidArea(new Dimension(15, 25)));
				PresetPanel.add(_presetNamePanel);
				VBox.add(PresetPanel);
			}
		}
		m_NamePanel.add(VBox);
		getContentPane().add(m_NamePanel, BorderLayout.NORTH);
		Configuration.addMatrixModifiedListener(new BooleanListener()
		{
			public void booleanSet(Boolean newValue)
			{
			}
			
			public void booleanChanged(Boolean oldValue, Boolean newValue)
			{
				_setBorder(newValue);
			}
		});
		_setBorder(Configuration.getMatrixModified());
		m_PresetsPanel.addSelectedPresetIndexListener(new IntegerListener()
		{
			public void integerSet(Integer newValue)
			{
				if((newValue == _selectedPresetIndex) && (Configuration.getMatrixModified() == true))
				{
					_setSelectedPresetIndex(newValue);
				}
			}
			
			public void integerChanged(Integer oldValue, Integer newValue)
			{
				_setSelectedPresetIndex(newValue);
			}
		});
		_setSelectedPresetIndex(-1);
	}
	
	private void _setSelectedPresetIndex(Integer selectedPresetIndex)
	{
		if(_selectedPresetIndex >= 0)
		{
			m_Configuration.getPreset(_selectedPresetIndex).removePresetListener(this);
		}
		_selectedPresetIndex = selectedPresetIndex;
		if(_selectedPresetIndex >= 0)
		{
			try
			{
				m_Configuration.loadProgramToMatrix(_selectedPresetIndex);
			}
			catch(MatrixNotSavedException Exception)
			{
				if(JOptionPane.showConfirmDialog(null, "The matrix is not saved yet.\nDo you really want to override the matrix' content?", "Matrix unsaved ...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				{
					m_Configuration.setMatrixModified(false);
					try
					{
						m_Configuration.loadProgramToMatrix(_selectedPresetIndex);
					}
					catch(MatrixNotSavedException Exception2)
					{
					}
				}
			}
			m_PresetNumberLabel.setText(String.valueOf(_selectedPresetIndex + 1));
			m_PresetNameLabel.setText(m_Configuration.getPreset(_selectedPresetIndex).getName());
			m_Configuration.getPreset(_selectedPresetIndex).addPresetListener(this);
		}
		else
		{
			m_PresetNumberLabel.setText("");
			m_PresetNameLabel.setText("");
		}
	}
	
	private void _setBorder(Boolean modified)
	{
		Border border = null;
		
		if(modified == true)
		{
			border = BorderFactory.createLineBorder(new Color(0.8f, 0.2f, 0.2f), 1);
		}
		else
		{
			border = BorderFactory.createLineBorder(new Color(1.0f, 1.0f, 1.0f), 1);
		}
		_presetNumberPanel.setBorder(border);
		_presetNamePanel.setBorder(border);
	}
	
	public void nameChanged(NameChangedEvent event)
	{
		m_PresetNameLabel.setText(event.getName());
	}
}
