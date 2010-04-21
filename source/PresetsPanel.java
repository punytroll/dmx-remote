import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import javax.swing.event.EventListenerList;
import javax.swing.JPanel;

class PresetsPanel extends JPanel implements MetricListener, PresetListener
{
	private Configuration m_Configuration;
	private Image m_Background;
	private int m_Hover;
	private int m_Select;
	private EventListenerList m_SelectionListeners;
	
	public PresetsPanel(Configuration Configuration)
	{
		m_Configuration = Configuration;
		m_Configuration.addMetricListener(this);
		setBackground(m_Configuration.getBackgroundColor());
		m_SelectionListeners = new EventListenerList();
		m_Hover = -1;
		m_Select = -1;
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent Event)
			{
				mouseHover(Event.getX(), Event.getY());
			}
			
			public void mouseMoved(MouseEvent Event)
			{
				mouseHover(Event.getX(), Event.getY());
			}
		}
		);
		addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent Event)
			{
				mouseHover(Event.getX(), Event.getY());
			}
			
			public void mouseExited(MouseEvent Event)
			{
				setHover(-1);
			}
			
			public void mouseClicked(MouseEvent Event)
			{
				setSelect(m_Hover);
			}
		}
		);
		setPreferredSize(new Dimension(StaticConfiguration.getCellBoxPadding() + 2 * m_Configuration.getIdentifierFieldWidth(), (StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1)));
		setMinimumSize(new Dimension(StaticConfiguration.getCellBoxPadding() + 2 * m_Configuration.getIdentifierFieldWidth(), (StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1)));
		for(int presetIndex = 0; presetIndex < StaticConfiguration.getNumberOfPresets(); ++presetIndex)
		{
			m_Configuration.getPreset(presetIndex).addPresetListener(this);
		}
	}
	
	public void mouseHover(int MouseX, int MouseY)
	{
		double HoverX = Math.floor((double)(MouseX - 1) / (m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding()));
		double HoverY = Math.floor((double)(MouseY - 2) / (Configuration.getCurrentCellSize() + 1));
		
		if((HoverY >= 0) && (HoverY < StaticConfiguration.getNumberOfPresets() / 2) && (MouseX - ((int)(HoverX) * (StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth())) <= m_Configuration.getIdentifierFieldWidth()))
		{
			int Hover = (int)HoverY + (int)HoverX * StaticConfiguration.getNumberOfPresets() / 2;
			
			if((Hover > -1) && (Hover <= StaticConfiguration.getNumberOfPresets()))
			{
				setHover(Hover);
			}
			else
			{
				setHover(-1);
			}
		}
		else
		{
			setHover(-1);
		}
	}
	
	public void setHover(int Hover)
	{
		if(m_Hover != Hover)
		{
			m_Hover = Hover;
			repaint();
		}
	}
	
	public void setSelect(int Select)
	{
		if(m_Select != Select)
		{
			m_Select = Select;
			fireSelectionChanged(m_Select);
			repaint();
		}
	}
	
	public void prepareBackground()
	{
		m_Background = createImage(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1));
		
		Graphics OffscreenGraphics = m_Background.getGraphics();
		
		OffscreenGraphics.setColor(m_Configuration.getBackgroundColor());
		OffscreenGraphics.fillRect(0, 0, m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1));
		Drawing.drawListBackground(OffscreenGraphics, StaticConfiguration.getNumberOfPresets() / 2, 1, m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), Configuration.getCurrentCellSize());
		OffscreenGraphics.translate(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding(), 0);
		Drawing.drawListBackground(OffscreenGraphics, StaticConfiguration.getNumberOfPresets() / 2, 1, m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), Configuration.getCurrentCellSize());
	}
	
	public void paintComponent(Graphics Graphics)
	{
		super.paintComponent(Graphics);
		if(m_Background == null)
		{
			prepareBackground();
		}
		Graphics.drawImage(m_Background, 0, 0, this);
		
		Graphics2D Graphics2D = (Graphics2D)Graphics;
		Composite Original = Graphics2D.getComposite();
		
		if(m_Hover != -1)
		{
			Graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
			Graphics2D.setPaint(Color.white);
			if(m_Hover < StaticConfiguration.getNumberOfPresets() / 2)
			{
				Graphics2D.fillRect(0, 1 + m_Hover * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
			else
			{
				Graphics2D.fillRect(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Hover - StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
		}
		if(m_Select != -1)
		{
			Graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
			Graphics2D.setPaint(Color.green);
			if(m_Select < StaticConfiguration.getNumberOfPresets() / 2)
			{
				Graphics2D.fillRect(0, 1 + m_Select * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
			else
			{
				Graphics2D.fillRect(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Select - StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
		}
		Graphics2D.setComposite(Original);
		Graphics2D.setPaint(new Color(0.0f, 0.0f, 0.0f));
		
		Shape OldClip = Graphics2D.getClip();
		
		Graphics2D.clip(new Rectangle(0, 1, m_Configuration.getIdentifierFieldWidth() - 2, (Configuration.getCurrentCellSize() + 1) * StaticConfiguration.getNumberOfPresets()));
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 9));
		for(int Name = 0; Name < StaticConfiguration.getNumberOfPresets() / 2; ++Name)
		{
			Graphics2D.drawString(m_Configuration.getPreset(Name).getName(), StaticConfiguration.getNumberFieldWidth() + Configuration.getCurrentTextOffset(), 1 + (Name + 1) * (Configuration.getCurrentCellSize() + 1) - 3);
		}
		Graphics2D.setClip(OldClip);
		Graphics2D.clip(new Rectangle(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1, m_Configuration.getIdentifierFieldWidth() - 2, (Configuration.getCurrentCellSize() + 1) * StaticConfiguration.getNumberOfPresets()));
		for(int Name = 0; Name < StaticConfiguration.getNumberOfPresets() / 2; ++Name)
		{
			Graphics2D.drawString(m_Configuration.getPreset(StaticConfiguration.getNumberOfPresets() / 2 + Name).getName(), StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth() + StaticConfiguration.getCellBoxPadding() + StaticConfiguration.getNumberFieldWidth() + Configuration.getCurrentTextOffset(), 1 + (Name + 1) * (Configuration.getCurrentCellSize() + 1) - 3);
		}
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		Graphics2D.setClip(OldClip);
	}
	
	public void addSelectionListener(SelectionListener Listener)
	{
		m_SelectionListeners.add(SelectionListener.class, Listener);
	}
	
	public void fireSelectionChanged(int PresetIndex)
	{
		Object[] Listeners = m_SelectionListeners.getListenerList();
		SelectionEvent Event = null;
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == SelectionListener.class)
			{
				if(Event == null)
				{
					Event = new SelectionEvent(PresetIndex);
				}
				((SelectionListener)Listeners[Listener + 1]).selectionChanged(Event);
			}
		}
	}
	
	public void nameChanged(NameChangedEvent Event)
	{
		repaint();
	}
	
	public void metricChanged(int WhatChanged)
	{
		setPreferredSize(new Dimension(StaticConfiguration.getCellBoxPadding() + 2 * m_Configuration.getIdentifierFieldWidth(), 1 + (StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1)));
		setMinimumSize(new Dimension(StaticConfiguration.getCellBoxPadding() + 2 * m_Configuration.getIdentifierFieldWidth(), 1 + (StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1)));
		getParent().doLayout();
		prepareBackground();
	}
}
