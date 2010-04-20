import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class SelectProgramDialog extends JDialog
{
	private Configuration m_Configuration;
	private JComboBox m_Combo;
	private JTextField m_Name;
	private JButton m_OK;
	private JButton m_Cancel;
	
	public SelectProgramDialog(Configuration Configuration, Frame Owner, boolean Modal)
	{
		super(Owner, Configuration.getString("Save to Preset:"), Modal);
		m_Configuration = Configuration;
		setLocation(PersistentConfiguration.getWindowLeft("save-matrix-to-preset"), PersistentConfiguration.getWindowTop("save-matrix-to-preset"));
		setSize(PersistentConfiguration.getWindowWidth("save-matrix-to-preset"), PersistentConfiguration.getWindowHeight("save-matrix-to-preset"));
		getContentPane().setBackground(m_Configuration.getBackgroundColor());
		
		JPanel Buttons = new JPanel();
		
		Buttons.setLayout(new BoxLayout(Buttons, BoxLayout.LINE_AXIS));
		Buttons.add(Box.createHorizontalGlue());
		m_Cancel = new JButton("Cancel");
		m_Cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent Event)
			{
				setVisible(false);
				m_Combo.setSelectedIndex(-1);
			}
		}
		);
		Buttons.add(m_Cancel);
		Buttons.add(Box.createRigidArea(new Dimension(10, 0)));
		m_OK = new JButton("OK");
		m_OK.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent Event)
			{
				setVisible(false);
			}
		}
		);
		Buttons.add(m_OK);
		Buttons.setBackground(m_Configuration.getBackgroundColor());
		
		JPanel Settings = new JPanel();
		
		Settings.setLayout(new BoxLayout(Settings, BoxLayout.PAGE_AXIS));
		Settings.add(new JLabel("Select Preset"));
		Settings.add(Box.createRigidArea(new Dimension(0,5)));
		Settings.setBackground(m_Configuration.getBackgroundColor());
		
		Vector Items = new Vector();
		
		for(int I = 0; I < m_Configuration.getNumberOfPresets(); ++I)
		{
			Items.add(String.valueOf(I + 1) + ". " + m_Configuration.getProgram(I).getName());
		}
		m_Name = new JTextField();
		m_Name.setAlignmentX(LEFT_ALIGNMENT);
		m_Name.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
		m_Name.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent Event)
			{
				if(Event.getKeyCode() == KeyEvent.VK_ENTER)
				{
					m_OK.doClick();
					Event.consume();
				}
				else if(Event.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					m_Cancel.doClick();
					Event.consume();
				}
			}
		}
		);
		m_Combo = new JComboBox(Items);
		m_Combo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent Event)
			{
				if(m_Combo.getSelectedIndex() > -1)
				{
					m_Name.setText(m_Configuration.getProgram(m_Combo.getSelectedIndex()).getName());
					m_Name.requestFocus();
					m_Name.setCaretPosition(0);
					m_Name.moveCaretPosition(m_Name.getText().length());
				}
			}
		}
		);
		m_Combo.setAlignmentX(LEFT_ALIGNMENT);
		m_Combo.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
		if(m_Configuration.getLoadedProgramIndex() == -1)
		{
			m_Combo.setSelectedIndex(0);
		}
		else
		{
			m_Combo.setSelectedIndex(m_Configuration.getLoadedProgramIndex());
		}
		Settings.add(m_Combo);
		Settings.add(Box.createRigidArea(new Dimension(0, 15)));
		Settings.add(new JLabel("Edit Name"));
		Settings.add(Box.createRigidArea(new Dimension(0, 10)));
		Settings.add(m_Name);
		Settings.add(Box.createVerticalGlue());
		Settings.add(Box.createRigidArea(new Dimension(0, 15)));
		Settings.add(Buttons);
		Settings.setBorder(new EmptyBorder(20, 20, 20, 20));
		Buttons.setAlignmentX(LEFT_ALIGNMENT);
		getContentPane().add(Settings);
		addComponentListener(new ComponentAdapter()
		{
			public void componentMoved(ComponentEvent Event)
			{
				PersistentConfiguration.setWindowLeft("save-matrix-to-preset", Event.getComponent().getLocation().x);
				PersistentConfiguration.setWindowTop("save-matrix-to-preset", Event.getComponent().getLocation().y);
			}
			
			public void componentResized(ComponentEvent Event)
			{
				PersistentConfiguration.setWindowWidth("save-matrix-to-preset", Event.getComponent().getSize().width);
				PersistentConfiguration.setWindowHeight("save-matrix-to-preset", Event.getComponent().getSize().height);
			}
		}
		);
	}
	
	public void addNotify()
	{
		super.addNotify();
		m_Name.requestFocus();
		m_Name.setCaretPosition(0);
		m_Name.moveCaretPosition(m_Name.getText().length());
	}
	
	public int getSelection()
	{
		return m_Combo.getSelectedIndex();
	}
	
	public String getName()
	{
		return m_Name.getText();
	}
}
