import java.awt.event.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.font.TextAttribute;
import java.awt.Insets;
import java.util.HashMap;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class MatrixControllerPanel extends JPanel implements HoverListener, PresetListener, ConnectionListener, TransmitModeListener, MetricListener
{
	private JPanel _presetNamePanel;
	private JPanel _presetNumberPanel;
	private JLabel m_PresetNumberLabel;
	private JLabel m_PresetNameLabel;
	private JLabel m_SourceNumberLabel;
	private JLabel m_SourceNameLabel;
	private JLabel m_DestinationNumberLabel;
	private JLabel m_DestinationNameLabel;
	private JButton m_Transmit;
	private Configuration m_Configuration;
	private Integer _selectedPresetIndex;
	private Component m_Strut;
	private Box m_VBox;
	
	public MatrixControllerPanel(Configuration configuration)
	{
		_selectedPresetIndex = -1;
		m_Configuration = configuration;
		m_Configuration.addHoverListener(this);
		m_Configuration.addConnectionListener(this);
		m_Configuration.addTransmitModeListener(this);
		m_Configuration.addMetricListener(this);
		setOpaque(false);
		setLayout(new FlowLayout(FlowLayout.LEFT, 30, 10));
		m_VBox = Box.createVerticalBox();
		
		Font NameFont = new Font(null, Font.PLAIN, 14);
		
		m_Strut = null;
		metricChanged(MetricListener.ALL_CHANGED);
		{
			{
				JLabel PresetLabel = new JLabel(m_Configuration.getCapitalizedString("Preset"));
				
				PresetLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				PresetLabel.setFont(PresetLabel.getFont().deriveFont(Font.BOLD));
				m_VBox.add(PresetLabel);
			}
			m_VBox.add(Box.createVerticalStrut(10));
			{
				JPanel PresetPanel = new JPanel();
				
				PresetPanel.setAlignmentX(LEFT_ALIGNMENT);
				PresetPanel.setLayout(new BoxLayout(PresetPanel, BoxLayout.LINE_AXIS));
				PresetPanel.setOpaque(false);
				_presetNumberPanel = new JPanel(new GridLayout(1, 1));
				_presetNumberPanel.setBackground(StaticConfiguration.getPresetPanelColor());
				_presetNumberPanel.setPreferredSize(new Dimension(25, 25));
				m_PresetNumberLabel = new JLabel("", JLabel.CENTER);
				m_PresetNumberLabel.setForeground(new Color(1.0f, 1.0f, 1.0f));
				_presetNumberPanel.add(m_PresetNumberLabel);
				
				_presetNamePanel = new JPanel(new GridLayout(1, 1));
				_presetNamePanel.setBackground(StaticConfiguration.getPresetPanelColor());
				_presetNamePanel.setPreferredSize(new Dimension(160, 25));
				m_PresetNameLabel = new JLabel("", JLabel.LEFT);
				m_PresetNameLabel.setForeground(new Color(1.0f, 1.0f, 1.0f));
				m_PresetNameLabel.setBorder(BorderFactory.createMatteBorder(0, 8, 0, 8, StaticConfiguration.getPresetPanelColor()));
				_presetNamePanel.add(m_PresetNameLabel);
				
				PresetPanel.add(_presetNumberPanel);
				PresetPanel.add(Box.createRigidArea(new Dimension(15, 25)));
				PresetPanel.add(_presetNamePanel);
				m_VBox.add(PresetPanel);
			}
		}
		m_VBox.add(Box.createVerticalStrut(10));
		{
			{
				JLabel SourceLabel = new JLabel(m_Configuration.getCapitalizedString("Source"));
				
				SourceLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				SourceLabel.setFont(SourceLabel.getFont().deriveFont(Font.BOLD));
				m_VBox.add(SourceLabel);
			}
			m_VBox.add(Box.createVerticalStrut(10));
			{
				JPanel SourcePanel = new JPanel();
				
				SourcePanel.setAlignmentX(LEFT_ALIGNMENT);
				SourcePanel.setLayout(new BoxLayout(SourcePanel, BoxLayout.LINE_AXIS));
				SourcePanel.setOpaque(false);
				
				JPanel SourceNumberPanel = new JPanel(new GridLayout(1, 1));
				
				SourceNumberPanel.setBackground(StaticConfiguration.getSourcePanelColor());
				SourceNumberPanel.setBorder(BorderFactory.createLineBorder(Color.white, 1));
				SourceNumberPanel.setPreferredSize(new Dimension(25, 25));
				m_SourceNumberLabel = new JLabel("", JLabel.CENTER);
				m_SourceNumberLabel.setForeground(Color.white);
				SourceNumberPanel.add(m_SourceNumberLabel);
				
				JPanel SourceNamePanel = new JPanel(new GridLayout(1, 1));
				
				SourceNamePanel.setBackground(StaticConfiguration.getSourcePanelColor());
				SourceNamePanel.setBorder(BorderFactory.createLineBorder(Color.white, 1));
				SourceNamePanel.setPreferredSize(new Dimension(160, 25));
				m_SourceNameLabel = new JLabel("", JLabel.LEFT);
				m_SourceNameLabel.setForeground(Color.white);
				m_SourceNameLabel.setBorder(BorderFactory.createMatteBorder(0, 8, 0, 8, StaticConfiguration.getSourcePanelColor()));
				SourceNamePanel.add(m_SourceNameLabel);
				
				SourcePanel.add(SourceNumberPanel);
				SourcePanel.add(Box.createRigidArea(new Dimension(15, 25)));
				SourcePanel.add(SourceNamePanel);
				m_VBox.add(SourcePanel);
			}
		}
		m_VBox.add(Box.createVerticalStrut(10));
		{
			{
				JLabel DestinationLabel = new JLabel(m_Configuration.getCapitalizedString("Destination"));
				
				DestinationLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				DestinationLabel.setFont(DestinationLabel.getFont().deriveFont(Font.BOLD));
				m_VBox.add(DestinationLabel);
			}
			m_VBox.add(Box.createVerticalStrut(10));
			{
				JPanel DestinationPanel = new JPanel();
				
				DestinationPanel.setAlignmentX(LEFT_ALIGNMENT);
				DestinationPanel.setLayout(new BoxLayout(DestinationPanel, BoxLayout.LINE_AXIS));
				DestinationPanel.setOpaque(false);
				
				JPanel DestinationNumberPanel = new JPanel(new GridLayout(1, 1));
				
				DestinationNumberPanel.setBackground(StaticConfiguration.getDestinationPanelColor());
				DestinationNumberPanel.setBorder(BorderFactory.createLineBorder(Color.white, 1));
				DestinationNumberPanel.setPreferredSize(new Dimension(25, 25));
				m_DestinationNumberLabel = new JLabel("", JLabel.CENTER);
				m_DestinationNumberLabel.setForeground(Color.white);
				DestinationNumberPanel.add(m_DestinationNumberLabel);
				
				JPanel DestinationNamePanel = new JPanel(new GridLayout(1, 1));
				
				DestinationNamePanel.setBackground(StaticConfiguration.getDestinationPanelColor());
				DestinationNamePanel.setBorder(BorderFactory.createLineBorder(Color.white, 1));
				DestinationNamePanel.setPreferredSize(new Dimension(160, 25));
				m_DestinationNameLabel = new JLabel("", JLabel.LEFT);
				m_DestinationNameLabel.setForeground(Color.white);
				m_DestinationNameLabel.setBorder(BorderFactory.createMatteBorder(0, 8, 0, 8, StaticConfiguration.getDestinationPanelColor()));
				DestinationNamePanel.add(m_DestinationNameLabel);
				
				DestinationPanel.add(DestinationNumberPanel);
				DestinationPanel.add(Box.createRigidArea(new Dimension(15, 25)));
				DestinationPanel.add(DestinationNamePanel);
				m_VBox.add(DestinationPanel);
			}
		}
		m_VBox.add(Box.createVerticalStrut(30));
		m_Transmit = new JButton("Transmit");
		m_Transmit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent Event)
			{
				m_Configuration.transmitNow();
				transmitted();
			}
		}
		);
		m_Transmit.setFocusable(false);
		
		JLabel TransmitLabel = new JLabel(m_Configuration.getCapitalizedString("Transmit"));
		
		TransmitLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
		TransmitLabel.setFont(TransmitLabel.getFont().deriveFont(Font.BOLD));
		TransmitLabel.setDisplayedMnemonicIndex(0);
		
		m_VBox.add(TransmitLabel);
		m_VBox.add(Box.createVerticalStrut(10));
		m_VBox.add(m_Transmit);
		add(m_VBox);
		if(m_Configuration.getTransmitManually() == true)
		{
			changedToTransmitManually();
		}
		else
		{
			changedToTransmitImmediately();
		}
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
	}
	
	public void hoverChanged(HoverEvent Event)
	{
		if(Event.getSource() >= 0)
		{
			m_SourceNumberLabel.setText(String.valueOf(Event.getSource() + 1));
			m_SourceNameLabel.setText(m_Configuration.getSourceName(Event.getSource()));
		}
		else
		{
			m_SourceNumberLabel.setText("");
			m_SourceNameLabel.setText("");
		}
		if(Event.getDestination() >= 0)
		{
			m_DestinationNumberLabel.setText(String.valueOf(Event.getDestination() + 1));
			m_DestinationNameLabel.setText(m_Configuration.getDestinationName(Event.getDestination()));
		}
		else
		{
			m_DestinationNumberLabel.setText("");
			m_DestinationNameLabel.setText("");
		}
	}
	
	void setPresetIndex(Integer selectPresetIndex)
	{
		if(_selectedPresetIndex != -1)
		{
			m_Configuration.getPreset(_selectedPresetIndex).removePresetListener(this);
		}
		_selectedPresetIndex = selectPresetIndex;
		if(_selectedPresetIndex != -1)
		{
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
	
	public void changedToTransmitManually()
	{
		// nothing to do here! or is there?
	}
	
	public void changedToTransmitImmediately()
	{
		m_Transmit.setBackground(null);
	}
	
	public void transmitted()
	{
		m_Transmit.setBackground(null);
	}
	
	public void connectionChanged(ConnectionEvent Event)
	{
		if(m_Configuration.getTransmitManually() == true)
		{
			m_Transmit.setBackground(new Color(0.672f, 0.0f, 0.0f));
		}
	}
	
	public void nameChanged(NameChangedEvent event)
	{
		m_PresetNameLabel.setText(event.getName());
	}
	
	public void metricChanged(int WhatChanged)
	{
		if(m_Strut != null)
		{
			m_VBox.remove(m_Strut);
		}
		m_Strut = Box.createVerticalStrut(StaticConfiguration.getStrutHeight(Configuration.getCurrentMatrixSize()));
		m_VBox.add(m_Strut, 0);
		validate();
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
}
