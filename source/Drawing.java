import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.Integer;

class Drawing
{
	static public int getMatrixTextOffset(Configuration Configuration)
	{
		int CellSize = Configuration.getCurrentCellSize();
		
		if(CellSize == 23)
		{
			return 8;
		}
		else if(CellSize == 19)
		{
			return 6;
		}
		else if(CellSize == 11)
		{
			return 2;
		}
		
		return 2;
	}
	
	static public void draw(Configuration Configuration, Graphics Graphics, int X, int Y, boolean TextHorizontal)
	{
		int CellSize = Configuration.getCurrentCellSize();
		
		Graphics.setColor(new Color(0.78f, 0.79f, 0.80f));
		for(int Block = 0; Block <= Configuration.getSize() / (2 * Configuration.getGroupSize()); ++Block)
		{
			if(TextHorizontal == true)
			{
				Graphics.fillRect(X, Y + 1 + 2 * Block * Configuration.getGroupSize() * (CellSize + 1), Configuration.getIdentifierFieldWidth() - Configuration.getNameFieldWidth() - 2, Configuration.getGroupSize() * (CellSize + 1));
				Graphics.fillRect(X + Configuration.getIdentifierFieldWidth() - Configuration.getNameFieldWidth(), Y + 1 + 2 * Block * Configuration.getGroupSize() * (CellSize + 1), Configuration.getNameFieldWidth(), Configuration.getGroupSize() * (CellSize + 1));
			}
			else
			{
				Graphics.fillRect(X + 1 + 2 * Block * Configuration.getGroupSize() * (CellSize + 1), Y, Configuration.getGroupSize() * (CellSize + 1), Configuration.getIdentifierFieldWidth() - Configuration.getNameFieldWidth() - 2);
				Graphics.fillRect(X + 1 + 2 * Block * Configuration.getGroupSize() * (CellSize + 1), Y + Configuration.getIdentifierFieldWidth() - Configuration.getNameFieldWidth(), Configuration.getGroupSize() * (CellSize + 1), Configuration.getNameFieldWidth());
			}
		}
		Graphics.setColor(new Color(0.70f, 0.72f, 0.73f));
		for(int Block = 0; Block <= Configuration.getSize() / (2 * Configuration.getGroupSize()); ++Block)
		{
			if(TextHorizontal == true)
			{
				Graphics.fillRect(X, Y + 1 + (2 * Block + 1) * Configuration.getGroupSize() * (CellSize + 1), Configuration.getIdentifierFieldWidth() - Configuration.getNameFieldWidth() - 2, Configuration.getGroupSize() * (CellSize + 1));
				Graphics.fillRect(X + Configuration.getIdentifierFieldWidth() - Configuration.getNameFieldWidth(), Y + 1 + (2 * Block + 1) * Configuration.getGroupSize() * (CellSize + 1), Configuration.getNameFieldWidth(), Configuration.getGroupSize() * (CellSize + 1));
			}
			else
			{
				Graphics.fillRect(X + 1 + (2 * Block + 1) * Configuration.getGroupSize() * (CellSize + 1), Y, Configuration.getGroupSize() * (CellSize + 1), Configuration.getIdentifierFieldWidth() - Configuration.getNameFieldWidth() - 2);
				Graphics.fillRect(X + 1 + (2 * Block + 1) * Configuration.getGroupSize() * (CellSize + 1), Y + Configuration.getIdentifierFieldWidth() - Configuration.getNameFieldWidth(), Configuration.getGroupSize() * (CellSize + 1), Configuration.getNameFieldWidth());
			}
		}
		Graphics.setColor(new Color(0.26f, 0.28f, 0.29f));
		for(int i = 0; i <= Configuration.getSize(); ++i)
		{
			if(TextHorizontal == true)
			{
				Graphics.drawLine(X, Y + i * (CellSize + 1), X + Configuration.getIdentifierFieldWidth(), Y + i * (CellSize + 1));
			}
			else
			{
				Graphics.drawLine(X + i * (CellSize + 1), Y, X + i * (CellSize + 1), Y + Configuration.getIdentifierFieldWidth());
			}
		}
		
		Graphics2D Graphics2D = (Graphics2D)Graphics;
		
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 9));
		if(TextHorizontal == true)
		{
			for(int Name = 0; Name < Configuration.getSize(); ++Name)
			{
				Graphics2D.drawString(Integer.toString(Name + 1), X + 2, Y + 1 + (Name + 1) * (CellSize + 1) - 1 - getMatrixTextOffset(Configuration));
			}
		}
		else
		{
			Graphics2D.rotate(Math.PI / -2.0);
			for(int Name = 0; Name < Configuration.getSize(); ++Name)
			{
				Graphics2D.drawString(Integer.toString(Name + 1), Y + 2 - Configuration.getIdentifierFieldWidth() + Configuration.getNameFieldWidth() + 2, X + 1 + (Name + 1) * (CellSize + 1) - 1 - getMatrixTextOffset(Configuration));
			}
		}
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	static public void drawListBackground(Graphics graphics, int count, int blockSize, int width, int numberWidth, int cellSize)
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
		// draw vertical lines
		graphics.drawLine(numberWidth, 0, numberWidth, 1 + count * (cellSize + 1));
		graphics.drawLine(numberWidth + 1, 0, numberWidth + 1, 1 + count * (cellSize + 1));
		
		// draw indices
		Graphics2D graphics2D = (Graphics2D)graphics;
		
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 9));
		for(int indexIndex = 0; indexIndex < count; ++indexIndex)
		{
			graphics2D.drawString(Integer.toString(indexIndex + 1), 2, 1 + (indexIndex + 1) * (cellSize + 1) - 3);
		}
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
}
