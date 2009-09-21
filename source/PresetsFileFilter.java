import java.io.File;
import javax.swing.filechooser.FileFilter;

class PresetsFileFilter extends FileFilter
{
	public boolean accept(File File)
	{
		String FileName = File.toString();
		
		return FileName.substring(FileName.length() - 3).equals("pre");
	}
	
	public String getDescription()
	{
		return "Presets configuration files. [*.pre]";
	}
	
	public String getExtension()
	{
		return "pre";
	}
}
