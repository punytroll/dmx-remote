import java.io.File;
import javax.swing.filechooser.FileFilter;

class MatrixFileFilter extends FileFilter
{
	public boolean accept(File File)
	{
		String FileName = File.toString();
		
		return FileName.substring(FileName.length() - 3).equals("mat");
	}
	
	public String getDescription()
	{
		return "Matrix configuration files. [*.mat]";
	}
	
	public String getExtension()
	{
		return "mat";
	}
}
