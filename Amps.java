package meter;

/**
 * CLASS:				This class will handle the amperage values being passed in from the Arduino.
 * 
 */
public class Amps 
{
	//Global variables
	
	//Raw amperage being passed in
	private String rawAmperage;
	
	//Display amperage for GUI
	private String displayAmperage;
	
	//Initial upper amperage limit
	private static double ampLimit = 5.00;
	
	/**
	 * Default Constructor
	 */
	public Amps()
	{
		//Default constructor.
	}
	
	/**
	 * CONSTRUCTOR:				Creates an Amps object with 1 value as a parameter.
	 * 
	 * @param amperage			Amperage value being passed in.
	 */
	public Amps(String amperage)
	{
		rawAmperage = amperage;	
	}


	/**
	 * METHOD:					getAmperage()
	 * 
	 * Getter for the raw amperage value.
	 * 
	 * @return : Returns the raw amperage value.
	 */
	public String getAmperage() 
	{
		return rawAmperage;
	}
	
	/**
	 * METHOD:					setAmpLimit()
	 * 
	 * Sets the upper limit for the amperage readings.
	 * 
	 * @param newAmpLimit : New upper limit value.
	 */
	public void setAmpLimit(double newAmpLimit)
	{
		ampLimit = newAmpLimit;
	}
	
	/**
	 * METHOD:					getAmpLimit()
	 * 
	 * Getter for the upper amp limit
	 * 
	 * @return : Returns the upper amp limit value.
	 */
	public double getAmpLimit()
	{
		return ampLimit;
	}
	
	/**
	 * METHOD:					checkAmperage()
	 * 
	 * Checks the amperage of the values being passed in.
	 * This method wille evaluate if the amp exceeds the upper limit.
	 * 
	 * @param amperage : Amperage value to be checked.
	 * @return : Returns true if the amps are too high.
	 */
	public boolean checkAmperage(String amperage)
	{		
		//Parse the string into a double.
		double ampDouble = Double.parseDouble(amperage);
		
		//Check to see if the amps are too high.
		if(ampDouble > ampLimit)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * METHOD					checkInput()
	 * 
	 * This method will check the input from the Arduino.
	 * If the data from the serial port on the Arduino doesn't
	 * start with an 's' and end with an 'f' the data is not 
	 * used to create an Amp object.  The data will be discarded
	 * to prevent bad data.
	 * 
	 * @param ampInput : data being sent from the Arduino to be checked.
	 */
	public void checkInput(String ampInput)
	{		
		//Check the start and ending characters.
		if(ampInput.charAt(0) != 's' || ampInput.charAt(ampInput.length() - 1) != 'f')
		{
			throw new NumberFormatException("Error with start or finish.");
		}
		//Check that the string is not empty.
		else if(ampInput.isEmpty())
		{
			throw new StringIndexOutOfBoundsException("Error with data sent from Arduino");
		}
	}
	
	/**
	 * METHOD:					ampDisplay()
	 * 
	 * This method returns the amps that the GUI will display.  Drops the
	 * starting and ending characters for display purposes and returns it.
	 * 
	 * @return
	 */
	public String ampDisplay()
	{
		displayAmperage = getAmperage().substring(1, getAmperage().length() - 1);
		
		return displayAmperage;
	}
}
