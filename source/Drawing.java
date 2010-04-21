import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.Integer;

class Drawing
{
	static public void draw(Configuration configuration, Graphics Graphics, int X, int Y, boolean TextHorizontal)
	{
		int CellSize = Configuration.getCurrentCellSize();
		
		Graphics.setColor(new Color(0.78f, 0.79f, 0.80f));
		for(int Block = 0; Block <= Configuration.getCurrentMatrixSize() / (2 * StaticConfiguration.getCellGroupSize()); ++Block)
		{
			if(TextHorizontal == true)
			{
				Graphics.fillRect(X, Y + 1 + 2 * Block * StaticConfiguration.getCellGroupSize() * (CellSize + 1), configuration.getIdentifierFieldWidth() - configuration.getNameFieldWidth() - 2, StaticConfiguration.getCellGroupSize() * (CellSize + 1));
				Graphics.fillRect(X + configuration.getIdentifierFieldWidth() - configuration.getNameFieldWidth(), Y + 1 + 2 * Block * StaticConfiguration.getCellGroupSize() * (CellSize + 1), configuration.getNameFieldWidth(), StaticConfiguration.getCellGroupSize() * (CellSize + 1));
			}
			else
			{
				Graphics.fillRect(X + 1 + 2 * Block * StaticConfiguration.getCellGroupSize() * (CellSize + 1), Y, StaticConfiguration.getCellGroupSize() * (CellSize + 1), configuration.getIdentifierFieldWidth() - configuration.getNameFieldWidth() - 2);
				Graphics.fillRect(X + 1 + 2 * Block * StaticConfiguration.getCellGroupSize() * (CellSize + 1), Y + configuration.getIdentifierFieldWidth() - configuration.getNameFieldWidth(), StaticConfiguration.getCellGroupSize() * (CellSize + 1), configuration.getNameFieldWidth());
			}
		}
		Graphics.setColor(new Color(0.70f, 0.72f, 0.73f));
		for(int Block = 0; Block <= Configuration.getCurrentMatrixSize() / (2 * StaticConfiguration.getCellGroupSize()); ++Block)
		{
			if(TextHorizontal == true)
			{
				Graphics.fillRect(X, Y + 1 + (2 * Block + 1) * StaticConfiguration.getCellGroupSize() * (CellSize + 1), configuration.getIdentifierFieldWidth() - configuration.getNameFieldWidth() - 2, StaticConfiguration.getCellGroupSize() * (CellSize + 1));
				Graphics.fillRect(X + configuration.getIdentifierFieldWidth() - configuration.getNameFieldWidth(), Y + 1 + (2 * Block + 1) * StaticConfiguration.getCellGroupSize() * (CellSize + 1), configuration.getNameFieldWidth(), StaticConfiguration.getCellGroupSize() * (CellSize + 1));
			}
			else
			{
				Graphics.fillRect(X + 1 + (2 * Block + 1) * StaticConfiguration.getCellGroupSize() * (CellSize + 1), Y, StaticConfiguration.getCellGroupSize() * (CellSize + 1), configuration.getIdentifierFieldWidth() - configuration.getNameFieldWidth() - 2);
				Graphics.fillRect(X + 1 + (2 * Block + 1) * StaticConfiguration.getCellGroupSize() * (CellSize + 1), Y + configuration.getIdentifierFieldWidth() - configuration.getNameFieldWidth(), StaticConfiguration.getCellGroupSize() * (CellSize + 1), configuration.getNameFieldWidth());
			}
		}
		Graphics.setColor(new Color(0.26f, 0.28f, 0.29f));
		for(int i = 0; i <= Configuration.getCurrentMatrixSize(); ++i)
		{
			if(TextHorizontal == true)
			{
				Graphics.drawLine(X, Y + i * (CellSize + 1), X + configuration.getIdentifierFieldWidth(), Y + i * (CellSize + 1));
			}
			else
			{
				Graphics.drawLine(X + i * (CellSize + 1), Y, X + i * (CellSize + 1), Y + configuration.getIdentifierFieldWidth());
			}
		}
		
		Graphics2D Graphics2D = (Graphics2D)Graphics;
		
		Graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Graphics2D.setFont(new Font("SansSerif", Font.PLAIN, 9));
		if(TextHorizontal == true)
		{
			for(int Name = 0; Name < Configuration.getCurrentMatrixSize(); ++Name)
			{
				Graphics2D.drawString(Integer.toString(Name + 1), X + 2, Y + 1 + (Name + 1) * (CellSize + 1) - 1 - Configuration.getCurrentTextOffset());
			}
		}
		else
		{
			Graphics2D.rotate(Math.PI / -2.0);
			for(int Name = 0; Name < Configuration.getCurrentMatrixSize(); ++Name)
			{
				Graphics2D.drawString(Integer.toString(Name + 1), Y + 2 - configuration.getIdentifierFieldWidth() + configuration.getNameFieldWidth() + 2, X + 1 + (Name + 1) * (CellSize + 1) - 1 - Configuration.getCurrentTextOffset());
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
