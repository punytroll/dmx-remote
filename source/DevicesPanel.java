import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.*;

class DevicesPanel extends JPanel implements DeviceListener, MetricListener
{
	private Configuration m_Configuration;
	private Image m_Background;
	private AffineTransform m_Transform;
	private AffineTransform m_InverseTransform;
	private EventListenerList m_SelectionListeners;
	
	private int m_Hover;
	private int m_Select;
	
	public DevicesPanel(Configuration configuration)
	{
		m_Configuration = configuration;
		m_Configuration.addDeviceListener(this);
		m_Configuration.addMetricListener(this);
		m_SelectionListeners = new EventListenerList();
		setBackground(StaticConfiguration.getWindowBackgroundColor());
		m_Hover = -1;
		m_Select = -1;
		m_Transform = new AffineTransform();
		m_Transform.translate(StaticConfiguration.getCellBoxPadding(), StaticConfiguration.getCellBoxPadding() + 40);
		try
		{
			m_InverseTransform = m_Transform.createInverse();
		}
		catch(NoninvertibleTransformException Exception)
		{
			System.exit(1);
		}
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent Event)
			{
				mouseMoved(Event);
			}
			
			public void mouseMoved(MouseEvent Event)
			{
				Point2D.Double EventPoint = new Point2D.Double();
				
				m_InverseTransform.transform(new Point2D.Double(Event.getX() + StaticConfiguration.getCellBoxPadding(), Event.getY()), EventPoint);
				
				double HoverX = Math.floor((EventPoint.getX() - 1) / (StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth()));
				double HoverY = Math.floor((EventPoint.getY() - 1) / (Configuration.getCurrentCellSize() + 1));
				
				if((HoverY >= 0) && (HoverY < Configuration.getCurrentMatrixSize()) && (EventPoint.getX() - (int)HoverX * (StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth()) > StaticConfiguration.getCellBoxPadding()))
				{
					int Hover = (int)HoverY + (int)HoverX * Configuration.getCurrentMatrixSize();
					
					if((Hover > -1) && (Hover < Configuration.getCurrentMatrixSize() * 2))
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
		);
		addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent Event)
			{
				double HoverX = Math.floor((double)(Event.getX() - 1) / (StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth()));
				double HoverY = Math.floor((double)(Event.getY() - StaticConfiguration.getCellBoxPadding() - 1) / (Configuration.getCurrentCellSize() + 1));
				
				if((HoverY >= 0) && (HoverY < Configuration.getCurrentMatrixSize()) && (Event.getX() - (int)HoverX * (StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth()) > StaticConfiguration.getCellBoxPadding()))
				{
					int Hover = (int)HoverY + (int)HoverX * Configuration.getCurrentMatrixSize();
					
					if((Hover > -1) && (Hover < Configuration.getCurrentMatrixSize() * 2))
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
			
			public void mouseExited(MouseEvent Event)
			{
				setHover(-1);
			}
			
			public void mouseClicked(MouseEvent Event)
			{
				if(m_Hover > -1)
				{
					if(m_Hover != m_Select)
					{
						setSelectedDevice(m_Hover);
					}
				}
			}
		}
		);
		updateDimensions();
	}
	
	public void setHover(int Hover)
	{
		if(m_Hover != Hover)
		{
			m_Hover = Hover;
			repaint();
		}
	}
	
	public void setSelectedDevice(int Select)
	{
		if(m_Select != Select)
		{
			m_Select = Select;
			fireSelectionChanged(m_Select);
			repaint();
		}
	}
	
	public int getSelectedDevice()
	{
		return m_Select;
	}
	
	public void prepareBackground()
	{
		if(isDisplayable() == true)
		{
			m_Background = createImage(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + Configuration.getCurrentMatrixSize() * (Configuration.getCurrentCellSize() + 1));
			
			Graphics2D graphics = (Graphics2D)m_Background.getGraphics();
			
			graphics.setColor(StaticConfiguration.getWindowBackgroundColor());
			graphics.fillRect(0, 0, StaticConfiguration.getCellBoxPadding() + 2 * m_Configuration.getIdentifierFieldWidth(), 1 + Configuration.getCurrentMatrixSize() * (Configuration.getCurrentCellSize() + 1));
			Drawing.drawListBackground(graphics, Configuration.getCurrentMatrixSize(), StaticConfiguration.getCellGroupSize(), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), Configuration.getCurrentCellSize());
			Drawing.drawListIndices(graphics, 1, Configuration.getCurrentMatrixSize(), Configuration.getCurrentCellSize());
			graphics.translate(StaticConfiguration.getNumberFieldWidth(), 0);
			Drawing.drawListSeparatorLine(graphics, Configuration.getCurrentMatrixSize(), Configuration.getCurrentCellSize());
			graphics.translate(StaticConfiguration.getNameFieldWidth() + StaticConfiguration.getCellBoxPadding(), 0);
			Drawing.drawListBackground(graphics, Configuration.getCurrentMatrixSize(), StaticConfiguration.getCellGroupSize(), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), Configuration.getCurrentCellSize());
			Drawing.drawListIndices(graphics, 1, Configuration.getCurrentMatrixSize(), Configuration.getCurrentCellSize());
			graphics.translate(StaticConfiguration.getNumberFieldWidth(), 0);
			Drawing.drawListSeparatorLine(graphics, Configuration.getCurrentMatrixSize(), Configuration.getCurrentCellSize());
		}
	}
	
	public void paintComponent(Graphics Graphics)
	{
		super.paintComponent(Graphics);
		if(m_Background == null)
		{
			prepareBackground();
		}
		
		Graphics2D graphics = (Graphics2D)Graphics;
		AffineTransform saveTransform = graphics.getTransform();
		Shape saveClip = graphics.getClip();
		Composite saveComposite = graphics.getComposite();
		
		graphics.setPaint(new Color(0.85f, 0.85f, 0.85f));
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics.setFont(new Font("SansSerif", Font.BOLD, 12));
		graphics.drawString("SOURCES", StaticConfiguration.getCellBoxPadding() + 2, 35);
		graphics.drawString("DESTINATIONS", StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 2, 35);
		graphics.transform(m_Transform);
		graphics.drawImage(m_Background, 0, 0, this);
		if(m_Hover > -1)
		{
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
			graphics.setPaint(Color.white);
			if(m_Hover < Configuration.getCurrentMatrixSize())
			{
				graphics.fillRect(0, 1 + m_Hover * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
			else
			{
				graphics.fillRect(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Hover - Configuration.getCurrentMatrixSize()) * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
		}
		if(m_Select > -1)
		{
			if(m_Select < Configuration.getCurrentMatrixSize())
			{
				graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
				graphics.setPaint(Color.blue);
				graphics.fillRect(0, 1 + m_Select * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
			else
			{
				graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
				graphics.setPaint(Color.red);
				graphics.fillRect(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Select - Configuration.getCurrentMatrixSize()) * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			}
		}
		graphics.setComposite(saveComposite);
		graphics.setPaint(new Color(0.0f, 0.0f, 0.0f));
		graphics.translate(StaticConfiguration.getNumberFieldWidth(), 0);
		graphics.clip(new Rectangle(0, 1, StaticConfiguration.getNameFieldWidth(), (Configuration.getCurrentCellSize() + 1) * m_Configuration.getCurrentMatrixSize()));
		Drawing.drawListItems(graphics, m_Configuration.getSourceNames(), 0, Configuration.getCurrentMatrixSize(), Configuration.getCurrentCellSize());
		graphics.setClip(saveClip);
		graphics.translate(StaticConfiguration.getNameFieldWidth() + StaticConfiguration.getCellBoxPadding() + StaticConfiguration.getNumberFieldWidth(), 0);
		graphics.clip(new Rectangle(0, 1, StaticConfiguration.getNameFieldWidth(), (Configuration.getCurrentCellSize() + 1) * m_Configuration.getCurrentMatrixSize()));
		Drawing.drawListItems(graphics, m_Configuration.getDestinationNames(), 0, Configuration.getCurrentMatrixSize(), Configuration.getCurrentCellSize());
		graphics.setTransform(saveTransform);
		graphics.setClip(saveClip);
	}
	
	public void deviceNameChanged(DeviceEvent Event)
	{
		repaint();
	}
	
	public void metricChanged(int WhatChanged)
	{
		updateDimensions();
		prepareBackground();
		repaint();
		invalidate();
		getParent().validate();
	}
	
	public void addSelectionListener(SelectionListener Listener)
	{
		m_SelectionListeners.add(SelectionListener.class, Listener);
	}
	
	public void fireSelectionChanged(int Selection)
	{
		Object[] Listeners = m_SelectionListeners.getListenerList();
		SelectionEvent Event = null;
		
		for(int Listener = 0; Listener < Listeners.length; Listener += 2)
		{
			if(Listeners[Listener] == SelectionListener.class)
			{
				if(Event == null)
				{
					Event = new SelectionEvent(Selection);
				}
				((SelectionListener)Listeners[Listener + 1]).selectionChanged(Event);
			}
		}
	}
	
	public void updateDimensions()
	{
		setMinimumSize(new Dimension(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding(), 40 + StaticConfiguration.getCellBoxPadding() + Configuration.getCurrentMatrixSize() * (Configuration.getCurrentCellSize() + 1) + 1 + StaticConfiguration.getCellBoxPadding()));
		setPreferredSize(new Dimension(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding(), 40 + StaticConfiguration.getCellBoxPadding() + Configuration.getCurrentMatrixSize() * (Configuration.getCurrentCellSize() + 1) + 1 + StaticConfiguration.getCellBoxPadding()));
		setMaximumSize(new Dimension(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding(), 40 + StaticConfiguration.getCellBoxPadding() + Configuration.getCurrentMatrixSize() * (Configuration.getCurrentCellSize() + 1) + 1 + StaticConfiguration.getCellBoxPadding()));
	}
}
