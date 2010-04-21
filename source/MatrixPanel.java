import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import javax.swing.JPanel;

class MatrixPanel extends JPanel implements ConnectionListener, DeviceListener, HoverListener, MetricListener
{
	private Configuration m_Configuration;
	private Image m_Background;
	private AffineTransform m_Transform;
	private AffineTransform m_InverseTransform;
	
	public MatrixPanel(Configuration configuration)
	{
		m_Configuration = configuration;
		m_Configuration.addConnectionListener(this);
		m_Configuration.addDeviceListener(this);
		m_Configuration.addHoverListener(this);
		m_Configuration.addMetricListener(this);
		setBackground(m_Configuration.getBackgroundColor());
		setPreferredSize(new Dimension(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1) + m_Configuration.getMatrixPadding(), m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1) + m_Configuration.getMatrixPadding()));
		m_Transform = new AffineTransform();
		m_Transform.translate(m_Configuration.getMatrixPadding(), m_Configuration.getMatrixPadding());
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
			public void mouseMoved(MouseEvent Event)
			{
				Point2D.Double EventPoint = new Point2D.Double();
				
				m_InverseTransform.transform(new Point2D.Double(Event.getX(), Event.getY()), EventPoint);
				
				m_Configuration.setHoverSource((int)Math.floor((EventPoint.getY() - 2 - m_Configuration.getIdentifierFieldWidth() - m_Configuration.getMatrixPadding()) / (m_Configuration.getCurrentCellSize() + 1)));
				m_Configuration.setHoverDestination((int)Math.floor((EventPoint.getX() - 2 - m_Configuration.getIdentifierFieldWidth() - m_Configuration.getMatrixPadding()) / (m_Configuration.getCurrentCellSize() + 1)));
			}
		}
		);
		addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent Event)
			{
				Point2D.Double EventPoint = new Point2D.Double();
				
				m_InverseTransform.transform(new Point2D.Double(Event.getX(), Event.getY()), EventPoint);
				m_Configuration.setHoverSource((int)Math.floor((EventPoint.getY() - 2 - m_Configuration.getIdentifierFieldWidth() - m_Configuration.getMatrixPadding()) / (m_Configuration.getCurrentCellSize() + 1)));
				m_Configuration.setHoverDestination((int)Math.floor((EventPoint.getX() - 2 - m_Configuration.getIdentifierFieldWidth() - m_Configuration.getMatrixPadding()) / (m_Configuration.getCurrentCellSize() + 1)));
			}
			
			public void mouseExited(MouseEvent Event)
			{
				m_Configuration.setHoverSource(-1);
				m_Configuration.setHoverDestination(-1);
			}
			
			public void mouseClicked(MouseEvent Event)
			{
				if(m_Configuration.getHover() == true)
				{
					m_Configuration.setConnected(m_Configuration.getHoverSource(), m_Configuration.getHoverDestination(), !m_Configuration.isConnected(m_Configuration.getHoverSource(), m_Configuration.getHoverDestination()));
				}
			}
		}
		);
	}
	
	public void prepareBackground()
	{
		if(isDisplayable() == true)
		{
			m_Background = createImage(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1));
			
			Graphics OffscreenGraphics = m_Background.getGraphics();
			
			// clear the matrix area
			OffscreenGraphics.setColor(m_Configuration.getBackgroundColor());
			OffscreenGraphics.fillRect(0, 0, m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1));
			// colorize the light areas of the matrix
			OffscreenGraphics.setColor(new Color(0.70f, 0.70f, 0.70f));
			for(int Row = 0; Row <= Configuration.getCurrentMatrixSize() / (2 * m_Configuration.getGroupSize()); ++Row)
			{
				for(int Column = 0; Column <= Configuration.getCurrentMatrixSize() / (2 * m_Configuration.getGroupSize()); ++Column)
				{
					OffscreenGraphics.fillRect(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + 2 * Column * m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + 2 * Row * m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1));
				}
			}
			// colorize the dark areas of the matrix
			OffscreenGraphics.setColor(new Color(0.55f, 0.55f, 0.55f));
			for(int Row = 0; Row <= Configuration.getCurrentMatrixSize() / (2 * m_Configuration.getGroupSize()); ++Row)
			{
				for(int Column = 0; Column <= Configuration.getCurrentMatrixSize() / (2 * m_Configuration.getGroupSize()); ++Column)
				{
					OffscreenGraphics.fillRect(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + (2 * Column + 1) * m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + (2 * Row + 1) * m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1));
				}
			}
			// colorize the normal areas of the matrix
			OffscreenGraphics.setColor(new Color(0.63f, 0.63f, 0.63f));
			for(int Row = 0; Row <= Configuration.getCurrentMatrixSize() / m_Configuration.getGroupSize(); ++Row)
			{
				for(int Column = 0; Column <= Configuration.getCurrentMatrixSize() / (2 * m_Configuration.getGroupSize()); ++Column)
				{
					OffscreenGraphics.fillRect(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + (2 * Column + ((Row + 1) % 2)) * m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Row * m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getGroupSize() * (m_Configuration.getCurrentCellSize() + 1));
				}
			}
			// draw all the lines
			OffscreenGraphics.setColor(new Color(0.78f, 0.78f, 0.78f));
			for(int i = 0; i <= Configuration.getCurrentMatrixSize(); ++i)
			{
				OffscreenGraphics.drawLine(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding(), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + i * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + i * (m_Configuration.getCurrentCellSize() + 1));
				OffscreenGraphics.drawLine(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + i * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding(), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + i * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1));
			}
			Drawing.draw(m_Configuration, OffscreenGraphics, 0, m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding(), true);
			Drawing.draw(m_Configuration, OffscreenGraphics, m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding(), 0, false);
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
		
		Graphics2D.transform(m_Transform);
		Graphics2D.drawImage(m_Background, 0, 0, this);
		Graphics2D.setPaint(new Color(0.85f, 0.85f, 0.85f));
		Graphics2D.setFont(new Font("SansSerif", Font.BOLD, 12));
		Graphics2D.drawString("SOURCES", 2, m_Configuration.getIdentifierFieldWidth());
		Graphics2D.drawString("DESTINATIONS", 2, m_Configuration.getMatrixPadding() + 2);
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Graphics2D.setPaint(new Color(0.0f, 0.0f, 0.6f));
		for(int Row = 0; Row < Configuration.getCurrentMatrixSize(); ++Row)
		{
			for(int Column = 0; Column < Configuration.getCurrentMatrixSize(); ++Column)
			{
				if(m_Configuration.isConnected(Row, Column) == true)
				{
					Graphics2D.fill(new Ellipse2D.Double(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Column * (m_Configuration.getCurrentCellSize() + 1) + (m_Configuration.getCurrentCellSize() - getMatrixBallWidth()) / 2, m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Row * (m_Configuration.getCurrentCellSize() + 1) + (m_Configuration.getCurrentCellSize() - getMatrixBallWidth()) / 2, getMatrixBallWidth(), getMatrixBallWidth()));
				}
			}
		}
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		if(m_Configuration.getHover() == true)
		{
			Composite Original = Graphics2D.getComposite();
			int hoverBarWidth = getHoverBarWidth();
			
			// highlighting in the matrix
			Graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
			Graphics2D.setPaint(Color.blue);
			Graphics2D.fillRect(m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + m_Configuration.getHoverSource() * (m_Configuration.getCurrentCellSize() + 1) + (m_Configuration.getCurrentCellSize() - hoverBarWidth) / 2, m_Configuration.getMatrixPadding() + 1 + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1), hoverBarWidth);
			Graphics2D.setPaint(Color.red);
			Graphics2D.fillRect(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + m_Configuration.getHoverDestination() * (m_Configuration.getCurrentCellSize() + 1) + (m_Configuration.getCurrentCellSize() - hoverBarWidth) / 2, m_Configuration.getIdentifierFieldWidth(), hoverBarWidth, m_Configuration.getMatrixPadding() + 1 + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1));
			
			// wider highlighting in the names
			Graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
			Graphics2D.setPaint(Color.blue);
			Graphics2D.fillRect(0, m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + m_Configuration.getHoverSource() * (m_Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), m_Configuration.getCurrentCellSize());
			Graphics2D.setPaint(Color.red);
			Graphics2D.fillRect(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + m_Configuration.getHoverDestination() * (m_Configuration.getCurrentCellSize() + 1), 0, m_Configuration.getCurrentCellSize(), m_Configuration.getIdentifierFieldWidth());
			
			Graphics2D.setComposite(Original);
		}
		Graphics2D.setPaint(new Color(0.0f, 0.0f, 0.0f));
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 9));
		
		Shape OldClip = Graphics2D.getClip();
		
		Graphics2D.clip(new Rectangle(0, m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1, m_Configuration.getIdentifierFieldWidth(), (m_Configuration.getCurrentCellSize() + 1) * Configuration.getCurrentMatrixSize()));
		for(int Name = 0; Name < Configuration.getCurrentMatrixSize(); ++Name)
		{
			Graphics2D.drawString(m_Configuration.getSourceName(Name), m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth() + 2, m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + (Name + 1) * (m_Configuration.getCurrentCellSize() + 1) - 1 - m_Configuration.getCurrentTextOffset());
		}
		Graphics2D.setClip(OldClip);
		Graphics2D.clip(new Rectangle(m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1, m_Configuration.getIdentifierFieldWidth() - m_Configuration.getNameFieldWidth() + 2, (m_Configuration.getCurrentCellSize() + 1) * Configuration.getCurrentMatrixSize(), m_Configuration.getNameFieldWidth()));
		Graphics2D.rotate(Math.PI / -2.0);
		for(int Name = 0; Name < Configuration.getCurrentMatrixSize(); ++Name)
		{
			Graphics2D.drawString(m_Configuration.getDestinationName(Name), 2 - m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + (Name + 1) * (m_Configuration.getCurrentCellSize() + 1) - 1 - m_Configuration.getCurrentTextOffset());
		}
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		Graphics2D.transform(m_InverseTransform);
		Graphics2D.setClip(OldClip);
	}
	
	public int getHoverBarWidth()
	{
		if(m_Configuration.getCurrentCellSize() == 23)
		{
			return 13;
		}
		else if(m_Configuration.getCurrentCellSize() == 19)
		{
			return 11;
		}
		else if(m_Configuration.getCurrentCellSize() == 11)
		{
			return 7;
		}
		
		return 7;
	}
	
	public int getMatrixBallWidth()
	{
		if(m_Configuration.getCurrentCellSize() == 23)
		{
			return 17;
		}
		else if(m_Configuration.getCurrentCellSize() == 19)
		{
			return 15;
		}
		else if(m_Configuration.getCurrentCellSize() == 11)
		{
			return 9;
		}
		
		return 9;
	}
	
	public void deviceNameChanged(DeviceEvent Event)
	{
		repaint();
	}
	
	public void hoverChanged(HoverEvent Event)
	{
		repaint();
	}
	
	public void connectionChanged(ConnectionEvent Event)
	{
		repaint();
	}
	
	public void metricChanged(int WhatChanged)
	{
		setPreferredSize(new Dimension(m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1) + m_Configuration.getMatrixPadding(), m_Configuration.getMatrixPadding() + m_Configuration.getIdentifierFieldWidth() + m_Configuration.getMatrixPadding() + 1 + Configuration.getCurrentMatrixSize() * (m_Configuration.getCurrentCellSize() + 1) + m_Configuration.getMatrixPadding()));
		prepareBackground();
		revalidate();
		repaint();
	}
}
