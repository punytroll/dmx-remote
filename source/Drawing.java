import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Vector;

class Drawing
{
	static public void drawListBackground(Graphics2D graphics, int count, int blockSize, int width, int numberWidth, int cellSize)
	{
		// draw highlighted fields
		graphics.setColor(new Color(0.78f, 0.79f, 0.80f));
		for(int block = 0; block <= count / (2 * blockSize); ++block)
		{
			graphics.fillRect(0, (2 * block) * blockSize * (cellSize + 1), width, blockSize * (cellSize + 1));
		}
		// draw lowlighted fields
		graphics.setColor(new Color(0.70f, 0.72f, 0.73f));
		for(int block = 0; block <= count / (2 * blockSize); ++block)
		{
			graphics.fillRect(0, (2 * block + 1) * blockSize * (cellSize + 1), width, blockSize * (cellSize + 1));
		}
		// draw horizontal lines
		graphics.setColor(new Color(0.26f, 0.28f, 0.29f));
		for(int lineIndex = 0; lineIndex <= count; ++lineIndex)
		{
			graphics.drawLine(0, lineIndex * (cellSize + 1), width, lineIndex * (cellSize + 1));
		}
	}
	
	static public void drawListSeparatorLine(Graphics2D graphics, int count, int cellSize)
	{
		// draw vertical lines
		graphics.drawLine(0, 0, 0, 1 + count * (cellSize + 1));
		graphics.drawLine(1, 0, 1, 1 + count * (cellSize + 1));
	}
	
	static public void drawListIndices(Graphics2D graphics, int begin, int count, int cellSize)
	{
		// draw indices
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setFont(new Font("SansSerif", Font.PLAIN, 9));
		for(int indexIndex = 0; indexIndex < count; ++indexIndex)
		{
			graphics.drawString(Integer.toString(indexIndex + begin), 3, (indexIndex + 1) * (cellSize + 1) - Configuration.getCurrentTextOffset());
		}
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	static public void drawListItems(Graphics2D graphics, Vector< String > items, int begin, int end, int cellSize)
	{
		// draw strings
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setFont(new Font("SansSerif", Font.PLAIN, 9));
		for(int itemIndex = 0; itemIndex < (end - begin); ++itemIndex)
		{
			graphics.drawString(items.elementAt(itemIndex + begin), Configuration.getCurrentTextOffset(), (itemIndex + 1) * (cellSize + 1) - Configuration.getCurrentTextOffset());
		}
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
}
