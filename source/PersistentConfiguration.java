import java.util.prefs.Preferences;

/**
 * PersistentConfiguration is the interface to storing configuration items over session boundaries.
 * PersistentConfiguration items are stored using Java's nativ Preferences.
 **/
class PersistentConfiguration
{
	private static final String _applicationString = "dmx-remote";
	private static final String _configurationVersion = "configuration-version-1.0";
	
	private static Preferences _node;
	
	private static Preferences _getNode()
	{
		if(_node == null)
		{
			_node = Preferences.userRoot().node(_applicationString + "/" + _configurationVersion);
		}
		
		return _node;
	}
	
	private static Preferences _getNode(String RelativePath)
	{
		return _getNode().node(RelativePath);
	}
	
	public static int getWindowWidth(String WindowIdentifier)
	{
		return _getNode("window-" + WindowIdentifier).getInt("Width", 800);
	}
	
	public static int getWindowHeight(String WindowIdentifier)
	{
		return _getNode("window-" + WindowIdentifier).getInt("Height", 600);
	}
	
	public static int getWindowLeft(String WindowIdentifier)
	{
		return _getNode("window-" + WindowIdentifier).getInt("Left", 0);
	}
	
	public static int getWindowTop(String WindowIdentifier)
	{
		return _getNode("window-" + WindowIdentifier).getInt("Top", 0);
	}
	
	public static boolean getWindowVisible(String WindowIdentifier)
	{
		return _getNode("window-" + WindowIdentifier).getBoolean("Visible", false);
	}
	
	public static void setWindowWidth(String WindowIdentifier, int Width)
	{
		_getNode("window-" + WindowIdentifier).putInt("Width", Width);
	}
	
	public static void setWindowHeight(String WindowIdentifier, int Height)
	{
		_getNode("window-" + WindowIdentifier).putInt("Height", Height);
	}
	
	public static void setWindowLeft(String WindowIdentifier, int Left)
	{
		_getNode("window-" + WindowIdentifier).putInt("Left", Left);
	}
	
	public static void setWindowTop(String WindowIdentifier, int Top)
	{
		_getNode("window-" + WindowIdentifier).putInt("Top", Top);
	}
	
	public static void setWindowVisible(String WindowIdentifier, boolean Visible)
	{
		_getNode("window-" + WindowIdentifier).putBoolean("Visible", Visible);
	}
}
