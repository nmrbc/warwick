package pt.ncastro.csvprocessor.util.domainHelpers.interfaces;

import org.json.JSONException;



/**
 * This interface defines the functions needed for a specific class to implement
 * in order to be converted into a JSON string and to be built from a JSON
 * string. I know that there are libs that already do this kind of work for us,
 * but well, I wanted to keep it simple...
 * 
 * @author Nuno de Castro
 *
 */
public interface IExchangeableByJSON {

	/**
	 * Function called when we want to convert the object to a JSON string.
	 * 
	 * @return
	 * @throws JSONException
	 */
	public String toJSON() throws JSONException;


	/**
	 * Function called when we want to fill the object from its JSON
	 * representation string.
	 * 
	 * @param json
	 * @throws JSONException
	 */
	public void fromJSON(String json) throws JSONException;

}
