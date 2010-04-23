import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
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

class EditPresetNamesWindow extends JInternalFrame
{
	private Configuration m_Configuration;
	private PresetsPanel m_PresetsPanel;
	private JTextField m_IdentifierField;
	private Integer _selectedPresetIndex;
	
	public EditPresetNamesWindow(Configuration Configuration)
	{
		super(Configuration.getString("Edit Preset Names"), true, true, true, false);
		m_Configuration = Configuration;
		setBackground(StaticConfiguration.getWindowBackgroundColor());
		m_PresetsPanel = new PresetsPanel(Configuration);
		
		FlowLayout Layout = new FlowLayout();
		
		Layout.setAlignment(FlowLayout.LEADING);
		getContentPane().setLayout(Layout);
		getContentPane().add(m_PresetsPanel, BorderLayout.WEST);
		m_IdentifierField = new JTextField(15);
		
		JPanel Editor = new JPanel();
		
		Editor.setLayout(new GridBagLayout());
		
		Box EditorBox = Box.createVerticalBox();
		JLabel Label = new JLabel("Edit Preset Name");
		
		Label.setForeground(new Color(0.85f, 0.85f, 0.85f));
		Label.setFont(Label.getFont().deriveFont(Font.BOLD));
		EditorBox.add(Label);
		EditorBox.add(Box.createVerticalStrut(20));
		EditorBox.add(m_IdentifierField);
		Editor.setOpaque(false);
		getContentPane().add(Editor, BorderLayout.EAST);
		
		GridBagConstraints Constraints = new GridBagConstraints();
		
		Constraints.insets = new Insets(20, 20, 20, 20);
		Editor.add(EditorBox, Constraints);
		m_IdentifierField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void update(DocumentEvent Event)
			{
				if((_selectedPresetIndex > -1) && (_selectedPresetIndex < StaticConfiguration.getNumberOfPresets()))
				{
					m_Configuration.getPreset(_selectedPresetIndex).setName(m_IdentifierField.getText());
				}
			}
			
			public void insertUpdate(DocumentEvent Event)
			{
				update(Event);
			}
			
			public void removeUpdate(DocumentEvent Event)
			{
				update(Event);
			}
			
			public void changedUpdate(DocumentEvent Event)
			{
			}
		}
		);
		m_PresetsPanel.addSelectedPresetIndexListener(new IntegerListener()
		{
			public void integerSet(Integer newValue)
			{
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
		_selectedPresetIndex =  selectedPresetIndex;
		if(selectedPresetIndex >= 0)
		{
			m_IdentifierField.setText(m_Configuration.getPreset(selectedPresetIndex).getName());
		}
		else
		{
			m_IdentifierField.setText("");
		}
	}
}
