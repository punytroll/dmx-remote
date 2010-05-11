import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import javax.swing.JPanel;

class MatrixPanel extends JPanel implements ConnectionListener, DeviceListener, HoverListener
{
	private Configuration m_Configuration;
	private Image m_Background;
	private AffineTransform m_Transform;
	
	public MatrixPanel(Configuration configuration)
	{
		m_Configuration = configuration;
		m_Configuration.addConnectionListener(this);
		m_Configuration.addDeviceListener(this);
		m_Configuration.addHoverListener(this);
		setBackground(StaticConfiguration.getWindowBackgroundColor());
		setPreferredSize(new Dimension(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1) + StaticConfiguration.getCellBoxPadding(), StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1) + StaticConfiguration.getCellBoxPadding()));
		m_Transform = new AffineTransform();
		m_Transform.translate(StaticConfiguration.getCellBoxPadding(), StaticConfiguration.getCellBoxPadding());
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent Event)
			{
				Point2D.Double EventPoint = new Point2D.Double(Event.getX(), Event.getY());
				
				try
				{
					m_Transform.inverseTransform(EventPoint, EventPoint);
				}
				catch(NoninvertibleTransformException exception)
				{
				}
				m_Configuration.setHoverSource((int)Math.floor((EventPoint.getY() - 2 - m_Configuration.getIdentifierFieldWidth() - StaticConfiguration.getCellBoxPadding()) / (Configuration.getCurrentCellSize() + 1)));
				m_Configuration.setHoverDestination((int)Math.floor((EventPoint.getX() - 2 - m_Configuration.getIdentifierFieldWidth() - StaticConfiguration.getCellBoxPadding()) / (Configuration.getCurrentCellSize() + 1)));
			}
		});
		addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent Event)
			{
				Point2D.Double EventPoint = new Point2D.Double(Event.getX(), Event.getY());
				
				try
				{
					m_Transform.inverseTransform(EventPoint, EventPoint);
				}
				catch(NoninvertibleTransformException exception)
				{
				}
				m_Configuration.setHoverSource((int)Math.floor((EventPoint.getY() - 2 - m_Configuration.getIdentifierFieldWidth() - StaticConfiguration.getCellBoxPadding()) / (Configuration.getCurrentCellSize() + 1)));
				m_Configuration.setHoverDestination((int)Math.floor((EventPoint.getX() - 2 - m_Configuration.getIdentifierFieldWidth() - StaticConfiguration.getCellBoxPadding()) / (Configuration.getCurrentCellSize() + 1)));
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
		});
		Configuration.addMatrixSizeListener(new IntegerListener()
		{
			public void integerSet(Integer newValue)
			{
			}
			
			public void integerChanged(Integer oldValue, Integer newValue)
			{
				setPreferredSize(new Dimension(StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1) + StaticConfiguration.getCellBoxPadding(), StaticConfiguration.getCellBoxPadding() + m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1) + StaticConfiguration.getCellBoxPadding()));
				prepareBackground();
				revalidate();
				repaint();
			}
		});
	}
	
	public void prepareBackground()
	{
		if(isDisplayable() == true)
		{
			m_Background = createImage(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1));
			
			Graphics2D graphics = (Graphics2D)m_Background.getGraphics();
			
			// clear the matrix area
			graphics.setColor(StaticConfiguration.getWindowBackgroundColor());
			graphics.fillRect(0, 0, m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1));
			// colorize the light areas of the matrix
			graphics.setColor(new Color(0.70f, 0.70f, 0.70f));
			for(int Row = 0; Row <= Configuration.getMatrixSize() / (2 * StaticConfiguration.getCellGroupSize()); ++Row)
			{
				for(int Column = 0; Column <= Configuration.getMatrixSize() / (2 * StaticConfiguration.getCellGroupSize()); ++Column)
				{
					graphics.fillRect(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + 2 * Column * StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + 2 * Row * StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1), StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1), StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1));
				}
			}
			// colorize the dark areas of the matrix
			graphics.setColor(new Color(0.55f, 0.55f, 0.55f));
			for(int Row = 0; Row <= Configuration.getMatrixSize() / (2 * StaticConfiguration.getCellGroupSize()); ++Row)
			{
				for(int Column = 0; Column <= Configuration.getMatrixSize() / (2 * StaticConfiguration.getCellGroupSize()); ++Column)
				{
					graphics.fillRect(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + (2 * Column + 1) * StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + (2 * Row + 1) * StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1), StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1), StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1));
				}
			}
			// colorize the normal areas of the matrix
			graphics.setColor(new Color(0.63f, 0.63f, 0.63f));
			for(int Row = 0; Row <= Configuration.getMatrixSize() / StaticConfiguration.getCellGroupSize(); ++Row)
			{
				for(int Column = 0; Column <= Configuration.getMatrixSize() / (2 * StaticConfiguration.getCellGroupSize()); ++Column)
				{
					graphics.fillRect(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + (2 * Column + ((Row + 1) % 2)) * StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Row * StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1), StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1), StaticConfiguration.getCellGroupSize() * (Configuration.getCurrentCellSize() + 1));
				}
			}
			// draw all the lines
			graphics.setColor(new Color(0.78f, 0.78f, 0.78f));
			for(int i = 0; i <= Configuration.getMatrixSize(); ++i)
			{
				graphics.drawLine(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding(), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + i * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + i * (Configuration.getCurrentCellSize() + 1));
				graphics.drawLine(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + i * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding(), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + i * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1));
			}
			graphics.translate(0, StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth() + StaticConfiguration.getCellBoxPadding());
			Drawing.drawListBackground(graphics, Configuration.getMatrixSize(), StaticConfiguration.getCellGroupSize(), StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth(), StaticConfiguration.getNumberFieldWidth(), Configuration.getCurrentCellSize());
			Drawing.drawListIndices(graphics, 1, Configuration.getMatrixSize(), Configuration.getCurrentCellSize());
			graphics.translate(StaticConfiguration.getNumberFieldWidth(), 0);
			Drawing.drawListSeparatorLine(graphics, Configuration.getMatrixSize(), Configuration.getCurrentCellSize());
			graphics.setTransform(graphics.getDeviceConfiguration().getDefaultTransform());
			graphics.rotate(Math.PI / -2.0);
			graphics.translate(-(StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth()), StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth() + StaticConfiguration.getCellBoxPadding());
			Drawing.drawListBackground(graphics, Configuration.getMatrixSize(), StaticConfiguration.getCellGroupSize(), StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth(), StaticConfiguration.getNumberFieldWidth(), Configuration.getCurrentCellSize());
			graphics.translate(StaticConfiguration.getNameFieldWidth(), 0);
			Drawing.drawListIndices(graphics, 1, Configuration.getMatrixSize(), Configuration.getCurrentCellSize());
			Drawing.drawListSeparatorLine(graphics, Configuration.getMatrixSize(), Configuration.getCurrentCellSize());
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
		Shape saveClip = graphics.getClip();
		AffineTransform saveTransform = graphics.getTransform();
		
		graphics.transform(m_Transform);
		graphics.drawImage(m_Background, 0, 0, this);
		graphics.setPaint(new Color(0.85f, 0.85f, 0.85f));
		graphics.setFont(new Font("SansSerif", Font.BOLD, 12));
		graphics.drawString("SOURCES", 2, m_Configuration.getIdentifierFieldWidth());
		graphics.drawString("DESTINATIONS", 2, StaticConfiguration.getCellBoxPadding() + 2);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setPaint(StaticConfiguration.getMatrixConnectionDotColor());
		for(int Row = 0; Row < Configuration.getMatrixSize(); ++Row)
		{
			for(int Column = 0; Column < Configuration.getMatrixSize(); ++Column)
			{
				if(m_Configuration.isConnected(Row, Column) == true)
				{
					graphics.fill(new Ellipse2D.Double(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Column * (Configuration.getCurrentCellSize() + 1) + (Configuration.getCurrentCellSize() - getMatrixBallWidth()) / 2, m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + Row * (Configuration.getCurrentCellSize() + 1) + (m_Configuration.getCurrentCellSize() - getMatrixBallWidth()) / 2, getMatrixBallWidth(), getMatrixBallWidth()));
				}
			}
		}
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		if(m_Configuration.getHover() == true)
		{
			Composite Original = graphics.getComposite();
			int hoverBarWidth = getHoverBarWidth();
			
			// highlighting in the matrix
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
			graphics.setPaint(Color.blue);
			graphics.fillRect(m_Configuration.getIdentifierFieldWidth(), m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + m_Configuration.getHoverSource() * (Configuration.getCurrentCellSize() + 1) + (Configuration.getCurrentCellSize() - hoverBarWidth) / 2, StaticConfiguration.getCellBoxPadding() + 1 + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1), hoverBarWidth);
			graphics.setPaint(Color.red);
			graphics.fillRect(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + m_Configuration.getHoverDestination() * (Configuration.getCurrentCellSize() + 1) + (Configuration.getCurrentCellSize() - hoverBarWidth) / 2, m_Configuration.getIdentifierFieldWidth(), hoverBarWidth, StaticConfiguration.getCellBoxPadding() + 1 + Configuration.getMatrixSize() * (Configuration.getCurrentCellSize() + 1));
			
			// wider highlighting in the names
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
			graphics.setPaint(Color.blue);
			graphics.fillRect(0, m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + m_Configuration.getHoverSource() * (Configuration.getCurrentCellSize() + 1), m_Configuration.getIdentifierFieldWidth(), Configuration.getCurrentCellSize());
			graphics.setPaint(Color.red);
			graphics.fillRect(m_Configuration.getIdentifierFieldWidth() + StaticConfiguration.getCellBoxPadding() + 1 + m_Configuration.getHoverDestination() * (Configuration.getCurrentCellSize() + 1), 0, Configuration.getCurrentCellSize(), m_Configuration.getIdentifierFieldWidth());
			
			graphics.setComposite(Original);
		}
		graphics.setPaint(new Color(0.0f, 0.0f, 0.0f));
		graphics.translate(StaticConfiguration.getNumberFieldWidth() + Configuration.getCurrentTextOffset(), StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth() + StaticConfiguration.getCellBoxPadding());
		graphics.clip(new Rectangle(0, 1, StaticConfiguration.getNameFieldWidth() - Configuration.getCurrentTextOffset() - Configuration.getCurrentTextOffset(), (Configuration.getCurrentCellSize() + 1) * Configuration.getMatrixSize()));
		Drawing.drawListItems(graphics, m_Configuration.getSourceNames(), 0, Configuration.getMatrixSize(), Configuration.getCurrentCellSize());
		graphics.setTransform(saveTransform);
		graphics.setClip(saveClip);
		graphics.transform(m_Transform);
		graphics.rotate(Math.PI / -2.0);
		graphics.translate(-(StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth()) + Configuration.getCurrentTextOffset(), StaticConfiguration.getNumberFieldWidth() + StaticConfiguration.getNameFieldWidth() + StaticConfiguration.getCellBoxPadding());
		graphics.clip(new Rectangle(0, 1, StaticConfiguration.getNameFieldWidth() - Configuration.getCurrentTextOffset() - Configuration.getCurrentTextOffset(), (Configuration.getCurrentCellSize() + 1) * Configuration.getMatrixSize()));
		Drawing.drawListItems(graphics, m_Configuration.getDestinationNames(), 0, Configuration.getMatrixSize(), Configuration.getCurrentCellSize());
		graphics.setTransform(saveTransform);
		graphics.setClip(saveClip);
	}
	
	public int getHoverBarWidth()
	{
		if(Configuration.getCurrentCellSize() == 23)
		{
			return 13;
		}
		else if(Configuration.getCurrentCellSize() == 19)
		{
			return 11;
		}
		else if(Configuration.getCurrentCellSize() == 11)
		{
			return 7;
		}
		
		return 7;
	}
	
	public int getMatrixBallWidth()
	{
		if(Configuration.getCurrentCellSize() == 23)
		{
			return 17;
		}
		else if(Configuration.getCurrentCellSize() == 19)
		{
			return 15;
		}
		else if(Configuration.getCurrentCellSize() == 11)
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
}
