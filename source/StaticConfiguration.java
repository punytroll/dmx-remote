/**
 * StaticConfiguration is the interface to getting the static setup of the program.
 * The values in this class are considered constant for the program.
 **/
class StaticConfiguration
{
	private static final String _configurationApplication = "dmx-remote";
	private static final String _configurationVersion = "configuration-version-1.0";
	
	public static String getConfigurationApplication()
	{
		return _configurationApplication;
	}
	
	public static String getConfigurationVersion()
	{
		return _configurationVersion;
	}
}
