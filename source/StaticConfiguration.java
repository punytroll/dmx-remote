import java.util.Vector;

/**
 * StaticConfiguration is the interface to getting the static setup of the program.
 * The values in this class are considered constant for the program.
 **/
class StaticConfiguration
{
	private static final Integer _cellBoxPadding = 8;
	private static final Integer _cellGroupSize = 4;
	private static final String _configurationApplication = "dmx-remote";
	private static final String _configurationVersion = "configuration-version-1.0";
	private static final MatrixConfiguration[] _matrixConfigurations = { 
		new MatrixConfiguration(12, 23, 145, 8),
		new MatrixConfiguration(16, 19, 175, 6),
		new MatrixConfiguration(18, 19, 215, 6),
		new MatrixConfiguration(32, 11, 240, 2),
		new MatrixConfiguration(64, 11, 250, 2)
	};
	private static final Integer _nameFieldWidth = 80;
	private static final Integer _numberFieldWidth = 18;
	private static final Integer _numberOfPresets = 50;
	
	public static Integer getCellBoxPadding()
	{
		return _cellBoxPadding;
	}
	
	public static Integer getCellGroupSize()
	{
		return _cellGroupSize;
	}
	
	public static Integer getCellSize(Integer matrixSize)
	{
		for(MatrixConfiguration matrixConfiguration : _matrixConfigurations)
		{
			if(matrixConfiguration.getMatrixSize() == matrixSize)
			{
				return matrixConfiguration.getCellSize();
			}
		}
		
		throw new IllegalArgumentException("The matrix size \"" + matrixSize.toString() + "\" is not defined.");
	}
	
	public static String getConfigurationApplication()
	{
		return _configurationApplication;
	}
	
	public static String getConfigurationVersion()
	{
		return _configurationVersion;
	}
	
	public static Vector< Integer > getMatrixSizes()
	{
		Vector< Integer > result = new Vector< Integer >();
		
		for(MatrixConfiguration matrixConfiguration : _matrixConfigurations)
		{
			result.add(matrixConfiguration.getMatrixSize());
		}
		
		return result;
	}
	
	public static Integer getNameFieldWidth()
	{
		return _nameFieldWidth;
	}
	
	public static Integer getNumberFieldWidth()
	{
		return _numberFieldWidth;
	}
	
	public static Integer getNumberOfPresets()
	{
		return _numberOfPresets;
	}
	
	public static Integer getStrutHeight(Integer matrixSize)
	{
		for(MatrixConfiguration matrixConfiguration : _matrixConfigurations)
		{
			if(matrixConfiguration.getMatrixSize() == matrixSize)
			{
				return matrixConfiguration.getStrutHeight();
			}
		}
		
		throw new IllegalArgumentException("The matrix size \"" + matrixSize.toString() + "\" is not defined.");
	}
	
	public static Integer getTextOffset(Integer matrixSize)
	{
		for(MatrixConfiguration matrixConfiguration : _matrixConfigurations)
		{
			if(matrixConfiguration.getMatrixSize() == matrixSize)
			{
				return matrixConfiguration.getTextOffset();
			}
		}
		
		throw new IllegalArgumentException("The matrix size \"" + matrixSize.toString() + "\" is not defined.");
	}
}

/**
 * MatrixConfiguration stores a combination of matrix size and other metrics respectively.
 **/
class MatrixConfiguration
{
	private final Integer _cellSize;
	private final Integer _matrixSize;
	private final Integer _strutHeight;
	private final Integer _textOffset;
	
	public MatrixConfiguration(Integer matrixSize, Integer cellSize, Integer strutHeight, Integer textOffset)
	{
		_cellSize = cellSize;
		_matrixSize = matrixSize;
		_strutHeight = strutHeight;
		_textOffset = textOffset;
	}
	
	public Integer getCellSize()
	{
		return _cellSize;
	}
	
	public Integer getMatrixSize()
	{
		return _matrixSize;
	}
	
	public Integer getStrutHeight()
	{
		return _strutHeight;
	}
	
	public Integer getTextOffset()
	{
		return _textOffset;
	}
}
