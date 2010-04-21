import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.Spring;
import javax.swing.SpringLayout;

class EditDeviceNamesWindow extends JInternalFrame implements SelectionListener
{
	private Configuration m_Configuration;
	private JTextField m_DeviceNameTextField;
	private DevicesPanel m_DevicesPanel;
	
	public EditDeviceNamesWindow(Configuration Configuration)
	{
		super(Configuration.getString("Edit Device Names"), true, true, true, false);
		m_Configuration = Configuration;
		setBackground(m_Configuration.getBackgroundColor());
		m_DevicesPanel = new DevicesPanel(m_Configuration);
		m_DevicesPanel.addSelectionListener(this);
		m_DeviceNameTextField = new JTextField(15);
		m_DeviceNameTextField.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent Event)
			{
				if(Event.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if(m_DevicesPanel.getSelectedDevice() < m_Configuration.getSize() * 2 - 1)
					{
						m_DevicesPanel.setSelectedDevice(m_DevicesPanel.getSelectedDevice() + 1);
					}
					else
					{
						m_DevicesPanel.setSelectedDevice(0);
					}
					Event.consume();
				}
			}
		}
		);
		m_DeviceNameTextField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void update(DocumentEvent Event)
			{
				if(m_DevicesPanel.getSelectedDevice() > -1)
				{
					int SelectedDevice = m_DevicesPanel.getSelectedDevice();
					
					if(SelectedDevice < m_Configuration.getSize())
					{
						m_Configuration.setSourceName(SelectedDevice, m_DeviceNameTextField.getText());
					}
					else
					{
						SelectedDevice -= m_Configuration.getSize();
						m_Configuration.setDestinationName(SelectedDevice, m_DeviceNameTextField.getText());
					}
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
		
		JScrollPane scrollPane = new JScrollPane(m_DevicesPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		JLabel label = new JLabel("Edit Device Name");
		
		label.setForeground(new Color(0.85f, 0.85f, 0.85f));
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		
		SpringLayout layout = new SpringLayout();
		
		getContentPane().setLayout(layout);
		getContentPane().add(label);
		getContentPane().add(m_DeviceNameTextField);
		getContentPane().add(scrollPane);
		
		// set up the constraints
		// - for the scroll pane
		SpringLayout.Constraints scrollPaneConstraints = layout.getConstraints(scrollPane);
		
		scrollPaneConstraints.setWidth(Spring.sum(Spring.constant(20), Spring.width(m_DevicesPanel)));
		
		// - for the label
		SpringLayout.Constraints labelConstraints = layout.getConstraints(label);
		
		labelConstraints.setX(Spring.sum(Spring.constant(20), scrollPaneConstraints.getConstraint(SpringLayout.EAST)));
		labelConstraints.setY(Spring.constant(200));
		
		// - for the text field
		SpringLayout.Constraints textFieldConstraints = layout.getConstraints(m_DeviceNameTextField);
		
		textFieldConstraints.setX(Spring.sum(Spring.constant(20), scrollPaneConstraints.getConstraint(SpringLayout.EAST)));
		textFieldConstraints.setY(Spring.sum(Spring.constant(20), labelConstraints.getConstraint(SpringLayout.SOUTH)));
		
		// - for the content panel
		SpringLayout.Constraints contentPaneConstraints = layout.getConstraints(getContentPane());
		
		contentPaneConstraints.setHeight(scrollPaneConstraints.getConstraint(SpringLayout.SOUTH));
		contentPaneConstraints.setWidth(Spring.sum(Spring.constant(20), textFieldConstraints.getConstraint(SpringLayout.EAST)));
		// done with setting up the constraints
	}
	
	public void selectionChanged(SelectionEvent Event)
	{
		if(Event.getSelection() > -1)
		{
			int SelectedDevice = Event.getSelection();
			
			if(SelectedDevice < m_Configuration.getSize())
			{
				m_DeviceNameTextField.setText(m_Configuration.getSourceName(SelectedDevice));
			}
			else
			{
				SelectedDevice -= m_Configuration.getSize();
				m_DeviceNameTextField.setText(m_Configuration.getDestinationName(SelectedDevice));
			}
		}
	}
}
