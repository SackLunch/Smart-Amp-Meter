package meter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
* CLASS:						This class will handle all of the email properties and actually sending the email.
* 								The body of the email requires HTML tags.
*
*/
public class Email 
{
	/**		* FIELD:	Properties for the gmail server.  These can be found online for most mail servers.		*/
	static Properties mailServerProperties;
	/**		* FIELD:	Mail session with the mailServerProperties.		*/
	static Session getMailSession;
	/**		* FIELD:	Mail message with with a MimeMessage object from the JavaMail class.		*/
	static MimeMessage generateMailMessage;
	/**		* FIELD:	Who the email will be sent to.		*/
	private String recipient = "YOUR EMAIL HERE";
	/**		* FIELD:	Formatter for the date.		*/
	private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	/**		* FIELD:	Instance of the date the email was sent.		*/
	private static Date date = Calendar.getInstance().getTime();
	
	//Default Amps Constructor
	Amps amps = new Amps();
	/**		* FIELD:	Amperage value to send in the email.		*/
	private String amperage;

	/**
	 * CONSTRUCTOR:					Constructs an object that has everything needed to send the emails.
	 * 				
	 * @param recipient				Returns the recipient of the email which will be the sales rep.
	 * @param product				Returns the product from the record.
	 * @param serialNumber				Returns the serial number of the product.
	 * @param returnLocation			Returns where to send the system.
	 * @param deliveryLocation			Returns where the replacement was sent if any.
	 * @param serviceAddress			Returns the address of the service facility.  
	 */

	/**
	 * Default constructor.
	 */
	public Email()
	{

	}
	
	/**
	 * METHOD:				setRecipient()
	 * 
	 * Sets the email address with a string passed in.
	 * 
	 * @param recipient : Email address to send to.
	 */
	public void setRecipient(String recipient)
	{
		this.recipient = recipient;
	}
	
	/**
	 * METHOD:				getRecipient()
	 * 
	 * Getter for the email address
	 * 
	 * @return : returns the email address
	 */
	public String getRecipient() 
	{
		return recipient;
	}
	
	/**
	 * METHOD:				setAmperage()
	 * 
	 * Setter for the amperage value
	 * 
	 * @param amperage : Amperage value
	 */
	public void setAmperage(String amperage)
	{
		this.amperage = amperage;
	}
	
	/**
	 * METHOD:				getAmperage()
	 * 
	 * Getter for the amperage value
	 * 
	 * @return	: returns the amperage value.
	 */
	public String getAmperage()
	{
		return amperage;
	}
	

	/**
	* METHOD:							Send the email if the equipment was replaced.
	* 
	* @throws AddressException			Returns AddressException if the email is not valid syntax wise.
	* @throws MessagingException		Returns message if exception is thrown.
	*/
	public void generateAmpEmail() throws AddressException, MessagingException {
				
		//Set up the mail properties for GMail with TLS.  Port = 587
		mailServerProperties = System.getProperties();
		//Port for GMail utilizing TLS is 587.
		mailServerProperties.put("mail.smtp.port", "587");
		//Use smtp authorization.
		mailServerProperties.put("mail.smtp.auth", "true");
		//Start TLS.
		mailServerProperties.put("mail.smtp.starttls.enable", "true"); 
		//Set the recipient up and anyone that needs to be cc'd.
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		//Create new MimeMessage with mailServerProperties.
		generateMailMessage = new MimeMessage(getMailSession);
		//recipient variable will be passed in through setter. 
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		//Also CC my email to check that it's sending to both.
		//generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("ANOTHER EMAIL HERE"));
		
		//Write the subject line and the email body.
		generateMailMessage.setSubject("Current Monitor " + dateFormat.format(date));
		
		//What will be written for replacement emails.
		String emailBody = 
		
		"<html>Amperage has exceeded limit.  Amperage: " + getAmperage() +"</html>";
		
		//Set the content of the email.
		generateMailMessage.setContent(emailBody, "text/html");

		//System.out.println("\n\n 3rd ===> Get Session and Send mail");
		Transport transport = getMailSession.getTransport("smtp");

		// Enter your correct gmail UserID and Password
		// if you have 2FA enabled then provide App Specific Password
		transport.connect("smtp.gmail.com", "YOUR EMAIL ADDRESS", "PASSWORD TO EMAIL ADDRESSS");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}
	
	public void generateTestEmail() throws AddressException, MessagingException {
		
		//Set up the mail properties for GMail with TLS.  Port = 587
		mailServerProperties = System.getProperties();
		//Port for GMail utilizing TLS is 587.
		mailServerProperties.put("mail.smtp.port", "587");
		//Use smtp authorization.
		mailServerProperties.put("mail.smtp.auth", "true");
		//Start TLS.
		mailServerProperties.put("mail.smtp.starttls.enable", "true"); 
		//Set the recipient up and anyone that needs to be cc'd.
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		//Create new MimeMessage with mailServerProperties.
		generateMailMessage = new MimeMessage(getMailSession);
		//recipient variable will be passed in through setter. 
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		//Also CC my email to check that it's sending to both.
		//generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("ANOTHER EMAIL HERE"));
		
		//Write the subject line and the email body.
		generateMailMessage.setSubject("Current Monitor Test " + dateFormat.format(date));
		
		//What will be written for replacement emails.
		String emailBody = 
		
		"<html>Test email sent from amperage monitor. </html>";
		
		//Set the content of the email.
		generateMailMessage.setContent(emailBody, "text/html");

		Transport transport = getMailSession.getTransport("smtp");

		// Enter your correct gmail UserID and Password
		// if you have 2FA enabled then provide App Specific Password
		transport.connect("smtp.gmail.com", "YOUR EMAIL HERE", "YOUR PASSWORD HERE");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}
}
