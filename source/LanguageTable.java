import java.util.HashMap;

class LanguageTable extends HashMap<String, HashMap<String, String>>
{
	private String _languageIdentifier;
	private HashMap<String, String> _languageMap;
	
	public LanguageTable()
	{
		_languageIdentifier = null;
		_languageMap = null;
	}
	
	public String getString(String stringIdentifier)
	{
		if(_languageMap == null)
		{
			System.out.println("No dictionary set while requesting string: '" + stringIdentifier + "'.");
			
			return null;
		}
		
		String result = _languageMap.get(stringIdentifier);
		
		if(result == null)
		{
			System.out.println("Dictionary '" + _languageIdentifier + "' does not contain: '" + stringIdentifier + "'.");
		}
		
		return result;
	}
	
	public void setLanguage(String languageIdentifier)
	{
		_languageIdentifier = languageIdentifier;
		_languageMap = get(_languageIdentifier);
	}
}
