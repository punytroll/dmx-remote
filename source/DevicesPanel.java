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
	
	public DevicesPanel(Configuration Configuration)
	{
		m_Configuration = Configuration;
		m_Configuration.addDeviceListener(this);
		m_Configuration.addMetricListener(this);
		m_SelectionListeners = new EventListenerList();
		setBackground(m_Configuration.getBackgroundColor());
		m_Hover = -1;
		m_Select = -1;
		m_Transform = new AffineTransform();
		m_Transform.translate(m_Configuration.getMatrixPadding(), m_Configuration.getMatrixPadding() + 40);
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
				
				m_InverseTransform.transform(new Point2D.Double(Event.getX() + m_Configuration.getMatrixPadding(), Event.getY()), EventPoint);
				
				double HoverX = Math.floor((EventPoint.getX() - 1) / (m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth()));
				double HoverY = Math.floor((EventPoint.getY() - 1) / (m_Configuration.getCurrentCellSize() + 1));
				
				if((HoverY >= 0) && (HoverY < m_Configuration.getSize()) && (EventPoint.getX() - (int)HoverX * (m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth()) > m_Configuration.getMatrixPadding()))
				{
					int Hover = (int)HoverY + (int)HoverX * m_Configuration.getSize();
					
					if((Hover > -1) && (Hover < m_Configuration.getSize() * 2))
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
				double HoverX = Math.floor((double)(Event.getX() - 1) / (m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth()));
				double HoverY = Math.floor((double)(Event.getY() - m_Configuration.getMatrixPadding() - 1) / (m_Configuration.getCurrentCellSize() + 1));
				
				if((HoverY >= 0) && (HoverY < m_Configuration.getSize()) && (Event.getX() - (int)HoverX * (m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth()) > m_Configuration.getMatrixPadding()))
				{
					int Hover = (int)HoverY + (int)HoverX * m_Configuration.getSize();
					
					if((Hover > -1) && (Hover < m_Configuration.getSize() * 2))
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
			m_Background = createImage(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + m_Configuration.getSize() * (m_Configuration.getCurrentCellSize() + 1));
			
			Graphics OffscreenGraphics = m_Background.getGraphics();
			
			OffscreenGraphics.setColor(m_Configuration.getBackgroundColor());
			OffscreenGraphics.fillRect(0, 0, m_Configuration.getMatrixPadding() + 2 * m_Configuration.getIdentifierFieldWidth(), 1 + m_Configuration.getSize() * (m_Configuration.getCurrentCellSize() + 1));
			Drawing.drawListBackground(OffscreenGraphics, m_Configuration.getSize(), m_Configuration.getGroupSize(), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), m_Configuration.getCurrentCellSize());
			OffscreenGraphics.translate(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding(), 0);
			Drawing.drawListBackground(OffscreenGraphics, m_Configuration.getSize(), m_Configuration.getGroupSize(), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), m_Configuration.getCurrentCellSize());
		}
	}
	
	public void paintComponent(Graphics Graphics)
	{
		super.paintComponent(Graphics);
		if(m_Background == null)
		{
			prepareBackground();
		}
		
		Graphics2D Graphics2D = (Graphics2D)Graphics;
		
		Graphics2D.setPaint(new Color(0.85f, 0.85f, 0.85f));
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		Graphics2D.setFont(new Font("SansSerif", Font.BOLD, 12));
		Graphics2D.drawString("SOURCES", m_Configuration.getMatrixPadding() + 2, 35);
		Graphics2D.drawString("DESTINATIONS", m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 2, 35);
		Graphics2D.transform(m_Transform);
		Graphics.drawImage(m_Background, 0, 0, this);
		
		Composite Original = Graphics2D.getComposite();
		
		if(m_Hover > -1)
		{
			Graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f));
			Graphics2D.setPaint(Color.white);
			if(m_Hover < m_Configuration.getSize())
			{
				Graphics2D.fillRect(0, 1 + m_Hover * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getCurrentCellSize());
			}
			else
			{
				Graphics2D.fillRect(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Hover - m_Configuration.getSize()) * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getCurrentCellSize());
			}
		}
		if(m_Select > -1)
		{
			if(m_Select < m_Configuration.getSize())
			{
				Graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
				Graphics2D.setPaint(Color.blue);
				Graphics2D.fillRect(0, 1 + m_Select * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getCurrentCellSize());
			}
			else
			{
				Graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
				Graphics2D.setPaint(Color.red);
				Graphics2D.fillRect(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth(), 1 + (m_Select - m_Configuration.getSize()) * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getCurrentCellSize());
			}
		}
		Graphics2D.setComposite(Original);
		Graphics2D.setPaint(new Color(0.0f, 0.0f, 0.0f));
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 9));
		
		Shape OldClip = Graphics2D.getClip();
		
		Graphics2D.clip(new Rectangle(0, 0, m_Configuration.getIdentifierFieldWidth() - 2, (m_Configuration.getCurrentCellSize() + 1) * m_Configuration.getSize()));
		for(int Name = 0; Name < m_Configuration.getSize(); ++Name)
		{
			Graphics2D.drawString(m_Configuration.getSourceName(Name), 2 + m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth(), 1 + (Name + 1) * (m_Configuration.getCurrentCellSize() + 1) - 3);
		}
		Graphics2D.setClip(OldClip);
		Graphics2D.clip(new Rectangle(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth(), 0, m_Configuration.getIdentifierFieldWidth() - 2, (m_Configuration.getCurrentCellSize() + 1) * m_Configuration.getSize()));
		for(int Name = 0; Name < m_Configuration.getSize(); ++Name)
		{
			Graphics2D.drawString(m_Configuration.getDestinationName(Name), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth() + 2, 1 + (Name + 1) * (m_Configuration.getCurrentCellSize() + 1) - 3);
		}
		Graphics2D.setClip(OldClip);
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
		setMinimumSize(new Dimension(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding(), 40 + m_Configuration.getMatrixPadding() + m_Configuration.getSize() * (m_Configuration.getCurrentCellSize() + 1) + 1 + m_Configuration.getMatrixPadding()));
		setPreferredSize(new Dimension(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding(), 40 + m_Configuration.getMatrixPadding() + m_Configuration.getSize() * (m_Configuration.getCurrentCellSize() + 1) + 1 + m_Configuration.getMatrixPadding()));
		setMaximumSize(new Dimension(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding(), 40 + m_Configuration.getMatrixPadding() + m_Configuration.getSize() * (m_Configuration.getCurrentCellSize() + 1) + 1 + m_Configuration.getMatrixPadding()));
	}
}
