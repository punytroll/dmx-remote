import java.awt.event.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.Insets;
import java.util.HashMap;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class MatrixControllerPanel extends JPanel implements HoverListener, PresetListener, ConnectionListener, TransmitModeListener, MetricListener
{
	private JLabel m_PresetNumberLabel;
	private JLabel m_PresetNameLabel;
	private JLabel m_SourceNumberLabel;
	private JLabel m_SourceNameLabel;
	private JLabel m_DestinationNumberLabel;
	private JLabel m_DestinationNameLabel;
	private JButton m_Transmit;
	private Configuration m_Configuration;
	private Program m_Preset;
	private Component m_Strut;
	private Box m_VBox;
	
	public MatrixControllerPanel(Configuration Configuration)
	{
		m_Configuration = Configuration;
		m_Configuration.addHoverListener(this);
		m_Configuration.addConnectionListener(this);
		m_Configuration.addTransmitModeListener(this);
		m_Configuration.addMetricListener(this);
		setBackground(m_Configuration.getBackgroundColor());
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
				PresetPanel.setBackground(m_Configuration.getBackgroundColor());
				
				JPanel PresetNumberPanel = new JPanel();
				
				PresetNumberPanel.setBackground(new Color(0.0f, 0.0f, 0.0f));
				PresetNumberPanel.setPreferredSize(new Dimension(20, 25));
				m_PresetNumberLabel = new JLabel("");
				m_PresetNumberLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				m_PresetNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
				PresetNumberPanel.add(m_PresetNumberLabel);
				
				JPanel PresetNamePanel = new JPanel();
				
				PresetNamePanel.setBackground(new Color(0.0f, 0.0f, 0.0f));
				PresetNamePanel.setPreferredSize(new Dimension(160, 25));
				m_PresetNameLabel = new JLabel("");
				m_PresetNameLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				m_PresetNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
				PresetNamePanel.add(m_PresetNameLabel);
				
				PresetPanel.add(PresetNumberPanel);
				PresetPanel.add(Box.createRigidArea(new Dimension(15, 25)));
				PresetPanel.add(PresetNamePanel);
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
				SourcePanel.setBackground(m_Configuration.getBackgroundColor());
				
				JPanel SourceNumberPanel = new JPanel();
				
				SourceNumberPanel.setBackground(new Color(0.27f, 0.3f, 0.535f));
				SourceNumberPanel.setPreferredSize(new Dimension(20, 25));
				m_SourceNumberLabel = new JLabel();
				m_SourceNumberLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				m_SourceNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
				SourceNumberPanel.add(m_SourceNumberLabel);
				
				JPanel SourceNamePanel = new JPanel();
				
				SourceNamePanel.setBackground(new Color(0.27f, 0.3f, 0.535f));
				SourceNamePanel.setPreferredSize(new Dimension(160, 25));
				m_SourceNameLabel = new JLabel();
				m_SourceNameLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				m_SourceNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
				DestinationPanel.setBackground(m_Configuration.getBackgroundColor());
				
				JPanel DestinationNumberPanel = new JPanel();
				
				DestinationNumberPanel.setBackground(new Color(0.5f, 0.14f, 0.145f));
				DestinationNumberPanel.setPreferredSize(new Dimension(20, 25));
				m_DestinationNumberLabel = new JLabel();
				m_DestinationNumberLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				m_DestinationNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
				DestinationNumberPanel.add(m_DestinationNumberLabel);
				
				JPanel DestinationNamePanel = new JPanel();
				
				DestinationNamePanel.setBackground(new Color(0.5f, 0.14f, 0.145f));
				DestinationNamePanel.setPreferredSize(new Dimension(160, 25));
				m_DestinationNameLabel = new JLabel();
				m_DestinationNameLabel.setForeground(new Color(0.85f, 0.85f, 0.85f));
				m_DestinationNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
	
	void setPresetIndex(int PresetIndex)
	{
		if(m_Preset != null)
		{
			m_Preset.removePresetListener(this);
		}
		if(PresetIndex != -1)
		{
			m_Preset = m_Configuration.getPreset(PresetIndex);
			m_PresetNumberLabel.setText(String.valueOf(PresetIndex + 1));
			m_PresetNameLabel.setText(m_Preset.getName());
			m_Preset.addPresetListener(this);
		}
		else
		{
			m_Preset = null;
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
		
		int iSize = m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding();
		
		iSize += StaticConfiguration.getStrutHeight(m_Configuration.getSize());
		m_Strut = Box.createVerticalStrut(iSize);
		m_VBox.add(m_Strut, 0);
		validate();
	}
}
