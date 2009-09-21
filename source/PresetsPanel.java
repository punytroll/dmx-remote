import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import javax.swing.event.EventListenerList;
import javax.swing.JPanel;

class PresetsPanel extends JPanel implements ProgramsListener, MetricListener
{
	private Configuration m_Configuration;
	private Image m_Background;
	private int m_Hover;
	private int m_Select;
	private EventListenerList m_SelectionListeners;
	private String m_WindowName;
	
	public PresetsPanel(Configuration Configuration, String WindowName)
	{
		m_Configuration = Configuration;
		m_Configuration.addProgramsListener(this);
		m_Configuration.addMetricListener(this);
		m_WindowName = WindowName;
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
				if(m_Configuration.getActiveWindow().equals(m_WindowName) == true)
				{
					setHover(-1);
				}
			}
			
			public void mouseClicked(MouseEvent Event)
			{
				setSelect(m_Hover);
			}
		}
		);
		setPreferredSize(new Dimension(m_Configuration.getMatrixPadding() + 2 * m_Configuration.getIdentifierFieldWidth(), (m_Configuration.getNumberOfPresets() / 2) * (m_Configuration.getCellSize() + 1)));
		setMinimumSize(new Dimension(m_Configuration.getMatrixPadding() + 2 * m_Configuration.getIdentifierFieldWidth(), (m_Configuration.getNumberOfPresets() / 2) * (m_Configuration.getCellSize() + 1)));
	}
	
	public void mouseHover(int MouseX, int MouseY)
	{
		if(m_Configuration.getActiveWindow().equals(m_WindowName) == true)
		{
			double HoverX = Math.floor((double)(MouseX - 1) / (m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding()));
			double HoverY = Math.floor((double)(MouseY - 2) / (m_Configuration.getCellSize() + 1));
			
			if((HoverY >= 0) && (HoverY < m_Configuration.getNumberOfPresets() / 2) && (MouseX - ((int)(HoverX) * (m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth())) <= m_Configuration.getIdentifierFieldWidth()))
			{
				int Hover = (int)HoverY + (int)HoverX * m_Configuration.getNumberOfPresets() / 2;
				
				if((Hover > -1) && (Hover <= m_Configuration.getNumberOfPresets()))
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
		m_Background = createImage(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Configuration.getNumberOfPresets() / 2) * (m_Configuration.getCellSize() + 1));
		
		Graphics OffscreenGraphics = m_Background.getGraphics();
		
		OffscreenGraphics.setColor(m_Configuration.getBackgroundColor());
		OffscreenGraphics.fillRect(0, 0, m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Configuration.getNumberOfPresets() / 2) * (m_Configuration.getCellSize() + 1));
		Drawing.drawListBackground(OffscreenGraphics, m_Configuration.getNumberOfPresets() / 2, 1, m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), m_Configuration.getCellSize());
		OffscreenGraphics.translate(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding(), 0);
		Drawing.drawListBackground(OffscreenGraphics, m_Configuration.getNumberOfPresets() / 2, 1, m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), m_Configuration.getCellSize());
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
		
		if((m_Hover != -1) && (m_Configuration.getActiveWindow().equals(m_WindowName) == true))
		{
			Graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
			Graphics2D.setPaint(Color.white);
			if(m_Hover < m_Configuration.getNumberOfPresets() / 2)
			{
				Graphics2D.fillRect(0, 1 + m_Hover * (m_Configuration.getCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getCellSize());
			}
			else
			{
				Graphics2D.fillRect(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Hover - m_Configuration.getNumberOfPresets() / 2) * (m_Configuration.getCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getCellSize());
			}
		}
		if(m_Select != -1)
		{
			Graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
			Graphics2D.setPaint(Color.green);
			if(m_Select < m_Configuration.getNumberOfPresets() / 2)
			{
				Graphics2D.fillRect(0, 1 + m_Select * (m_Configuration.getCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getCellSize());
			}
			else
			{
				Graphics2D.fillRect(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Select - m_Configuration.getNumberOfPresets() / 2) * (m_Configuration.getCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getCellSize());
			}
		}
		Graphics2D.setComposite(Original);
		Graphics2D.setPaint(new Color(0.0f, 0.0f, 0.0f));
		
		Shape OldClip = Graphics2D.getClip();
		
		Graphics2D.clip(new Rectangle(0, 1, m_Configuration.getIdentifierFieldWidth() - 2, (m_Configuration.getCellSize() + 1) * m_Configuration.getNumberOfPresets()));
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 9));
		for(int Name = 0; Name < m_Configuration.getNumberOfPresets() / 2; ++Name)
		{
			Graphics2D.drawString(m_Configuration.getProgram(Name).getName(), 2 + m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), 1 + (Name + 1) * (m_Configuration.getCellSize() + 1) - 3);
		}
		Graphics2D.setClip(OldClip);
		Graphics2D.clip(new Rectangle(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth(), 1, m_Configuration.getIdentifierFieldWidth() - 2, (m_Configuration.getCellSize() + 1) * m_Configuration.getNumberOfPresets()));
		for(int Name = 0; Name < m_Configuration.getNumberOfPresets() / 2; ++Name)
		{
			Graphics2D.drawString(m_Configuration.getProgram(m_Configuration.getNumberOfPresets() / 2 + Name).getName(), m_Configuration.getMatrixPadding() + 2 + 2 * m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), 1 + (Name + 1) * (m_Configuration.getCellSize() + 1) - 3);
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
	
	public void programNameChanged(NameChangedEvent Event)
	{
		repaint();
	}
	
	public void metricChanged(int WhatChanged)
	{
		if((WhatChanged & MetricListener.CELL_SIZE_CHANGED) == MetricListener.CELL_SIZE_CHANGED)
		{
			prepareBackground();
		}
	}
}
