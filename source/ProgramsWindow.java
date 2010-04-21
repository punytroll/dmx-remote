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

class ProgramsWindow extends JInternalFrame implements SelectionListener, PresetListener
{
	private Configuration m_Configuration;
	private PresetsPanel m_PresetsPanel;
	private JPanel m_NamePanel;
	private JLabel m_PresetNumberLabel;
	private JLabel m_PresetNameLabel;
	private int _selectedPresetIndex;
	
	public ProgramsWindow(Configuration Configuration)
	{
		super(Configuration.getString("Presets"), true, true, true, false);
		setBackground(StaticConfiguration.getWindowBackgroundColor());
		m_Configuration = Configuration;
		_selectedPresetIndex = -1;
		getContentPane().setLayout(new BorderLayout());
		
		// create the Presets Panel
		m_PresetsPanel = new PresetsPanel(Configuration);
		m_PresetsPanel.addSelectionListener(this);
		
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
				
				JPanel PresetNumberPanel = new JPanel();
				
				PresetNumberPanel.setBackground(new Color(0.0f, 0.0f, 0.0f));
				PresetNumberPanel.setPreferredSize(new Dimension(20, 25));
				m_PresetNumberLabel = new JLabel("");
				m_PresetNumberLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				m_PresetNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
				PresetNumberPanel.add(m_PresetNumberLabel);
				
				JPanel PresetNamePanel = new JPanel();
				
				PresetNamePanel.setBackground(new Color(0.0f, 0.0f, 0.0f));
				PresetNamePanel.setPreferredSize(new Dimension(156, 25));
				m_PresetNameLabel = new JLabel("");
				m_PresetNameLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				m_PresetNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
				PresetNamePanel.add(m_PresetNameLabel);
				
				PresetPanel.add(PresetNumberPanel);
				PresetPanel.add(Box.createRigidArea(new Dimension(15, 25)));
				PresetPanel.add(PresetNamePanel);
				VBox.add(PresetPanel);
			}
		}
		m_NamePanel.add(VBox);
		getContentPane().add(m_NamePanel, BorderLayout.NORTH);
	}
	
	public void selectionChanged(SelectionEvent Event)
	{
		if(_selectedPresetIndex > -1)
		{
			m_Configuration.getPreset(_selectedPresetIndex).removePresetListener(this);
		}
		_selectedPresetIndex = Event.getSelection();
		try
		{
			m_Configuration.loadProgramToMatrix(Event.getSelection());
		}
		catch(MatrixNotSavedException Exception)
		{
			if(JOptionPane.showConfirmDialog(null, "The matrix is not saved yet.\nDo you really want to override the matrix' content?", "Matrix unsaved ...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				m_Configuration.setMatrixSaved();
				try
				{
					m_Configuration.loadProgramToMatrix(Event.getSelection());
				}
				catch(MatrixNotSavedException Exception2)
				{
				}
			}
		}
		if(_selectedPresetIndex > -1)
		{
			m_PresetNumberLabel.setText(String.valueOf(_selectedPresetIndex + 1));
			m_PresetNameLabel.setText(m_Configuration.getPreset(_selectedPresetIndex).getName());
			m_Configuration.getPreset(_selectedPresetIndex).addPresetListener(this);
		}
	}
	
	public void nameChanged(NameChangedEvent event)
	{
		m_PresetNameLabel.setText(event.getName());
	}
}
