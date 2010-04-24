import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import javax.swing.event.EventListenerList;
import javax.swing.JPanel;

class PresetsPanel extends JPanel implements MetricListener, PresetListener
{
	private Configuration m_Configuration;
	private Image m_Background;
	private int m_Hover;
	private IntegerObject _selectedPresetIndex;
	private EventListenerList _selectedPresetIndexListeners;
	
	public PresetsPanel(Configuration Configuration)
	{
		m_Configuration = Configuration;
		m_Configuration.addMetricListener(this);
		setBackground(StaticConfiguration.getWindowBackgroundColor());
		_selectedPresetIndexListeners = new EventListenerList();
		_selectedPresetIndex = new IntegerObject(-1);
		m_Hover = -1;
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
				setSelectedPresetIndex(m_Hover);
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
	
	public void setSelectedPresetIndex(Integer selectedPresetIndex)
	{
		if(setInteger(_selectedPresetIndex, selectedPresetIndex, _selectedPresetIndexListeners) == true)
		{
			repaint();
		}
	}
	
	private static Boolean setInteger(IntegerObject destination, Integer newValue, EventListenerList listeners)
	{
		Boolean result = false;
		
		if(destination.get() != newValue)
		{
			Integer oldValue = destination.get();
			
			destination.set(newValue);
			fireIntegerChanged(listeners, oldValue, newValue);
			result = true;
		}
		fireIntegerSet(listeners, newValue);
		
		return result;
	}
	
	public void prepareBackground()
	{
		m_Background = createImage(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1));
		
		Graphics2D graphics = (Graphics2D)m_Background.getGraphics();
		
		graphics.setColor(StaticConfiguration.getWindowBackgroundColor());
		graphics.fillRect(0, 0, m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1));
		Drawing.drawListBackground(graphics, StaticConfiguration.getNumberOfPresets() / 2, 1, m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), Configuration.getCurrentCellSize());
		Drawing.drawListIndices(graphics, 1, StaticConfiguration.getNumberOfPresets() / 2, Configuration.getCurrentCellSize());
		graphics.translate(StaticConfiguration.getNumberFieldWidth(), 0);
		Drawing.drawListSeparatorLine(graphics, StaticConfiguration.getNumberOfPresets() / 2, Configuration.getCurrentCellSize());
		graphics.translate(StaticConfiguration.getNameFieldWidth() + StaticConfiguration.getCellBoxPadding(), 0);
		Drawing.drawListBackground(graphics, StaticConfiguration.getNumberOfPresets() / 2, 1, m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), Configuration.getCurrentCellSize());
		Drawing.drawListIndices(graphics, StaticConfiguration.getNumberOfPresets() / 2 + 1, StaticConfiguration.getNumberOfPresets() / 2, Configuration.getCurrentCellSize());
		graphics.translate(StaticConfiguration.getNumberFieldWidth(), 0);
		Drawing.drawListSeparatorLine(graphics, StaticConfiguration.getNumberOfPresets() / 2, Configuration.getCurrentCellSize());
	}
	
	public void paintComponent(Graphics Graphics)
	{
		super.paintComponent(Graphics);
		if(m_Background == null)
		{
			prepareBackground();
		}
		Graphics.drawImage(m_Background, 0, 0, this);
		
		Graphics2D graphics = (Graphics2D)Graphics;
		Shape saveClip = graphics.getClip();
		AffineTransform saveTransform = graphics.getTransform();
		Composite saveComposite = graphics.getComposite();
		
		if(m_Hover != -1)
		{
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
			graphics.setPaint(Color.white);
			if(m_Hover < StaticConfiguration.getNumberOfPresets() / 2)
			{
				graphics.fillRect(0, 1 + m_Hover * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
			else
			{
				graphics.fillRect(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Hover - StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
		}
		if(_selectedPresetIndex.get() != -1)
		{
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
			graphics.setPaint(Color.green);
			if(_selectedPresetIndex.get() < StaticConfiguration.getNumberOfPresets() / 2)
			{
				graphics.fillRect(0, 1 + _selectedPresetIndex.get() * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
			else
			{
				graphics.fillRect(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (_selectedPresetIndex.get() - StaticConfiguration.getNumberOfPresets() / 2) * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
		}
		graphics.setComposite(saveComposite);
		graphics.setPaint(new Color(0.0f, 0.0f, 0.0f));
		graphics.translate(StaticConfiguration.getNumberFieldWidth() + Configuration.getCurrentTextOffset(), 0);
		graphics.clip(new Rectangle(0, 1, StaticConfiguration.getNameFieldWidth() - Configuration.getCurrentTextOffset() - Configuration.getCurrentTextOffset(), (StaticConfiguration.getNumberOfPresets() / 2 ) * (Configuration.getCurrentCellSize() + 1)));
		Drawing.drawListItems(graphics, m_Configuration.getPresetNames(), 0, StaticConfiguration.getNumberOfPresets() / 2, Configuration.getCurrentCellSize());
		graphics.setTransform(saveTransform);
		graphics.setClip(saveClip);
		graphics.translate(StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth() + StaticConfiguration.getCellBoxPadding() + StaticConfiguration.getNumberFieldWidth() + Configuration.getCurrentTextOffset(), 0);
		graphics.clip(new Rectangle(0, 1, StaticConfiguration.getNameFieldWidth() - Configuration.getCurrentTextOffset() - Configuration.getCurrentTextOffset(), (StaticConfiguration.getNumberOfPresets() / 2 ) * (Configuration.getCurrentCellSize() + 1)));
		Drawing.drawListItems(graphics, m_Configuration.getPresetNames(), StaticConfiguration.getNumberOfPresets() / 2, StaticConfiguration.getNumberOfPresets(), Configuration.getCurrentCellSize());
		graphics.setTransform(saveTransform);
		graphics.setClip(saveClip);
	}
	
	public void addSelectedPresetIndexListener(IntegerListener listener)
	{
		_selectedPresetIndexListeners.add(IntegerListener.class, listener);
	}
	
	private static void fireIntegerSet(EventListenerList eventListeners, Integer newValue)
	{
		for(IntegerListener integerListener : eventListeners.getListeners(IntegerListener.class))
		{
			integerListener.integerSet(newValue);
		}
	}
	
	private static void fireIntegerChanged(EventListenerList eventListeners, Integer oldValue, Integer newValue)
	{
		for(IntegerListener integerListener : eventListeners.getListeners(IntegerListener.class))
		{
			integerListener.integerChanged(oldValue, newValue);
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
