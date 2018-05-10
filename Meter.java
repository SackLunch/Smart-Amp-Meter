package meter;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.fazecast.jSerialComm.SerialPort;
import java.awt.GridLayout;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.comm.SerialPortEventListener;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import java.awt.SystemColor;
import javax.swing.JCheckBox;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.border.BevelBorder;


/**
 * CLASS:			This class will launch the GUI.
 */
public class Meter extends JFrame implements SerialPortEventListener
{
	//Fields
	/** * FIELD:		Serial version for this class. */
	private static final long serialVersionUID = 1L;
	/** * FIELD:		SerialPort object. */
	private SerialPort comPort;
	/** * FIELD:		The comport that will be used.  Change this field if you're using a different port for your Arduino. */
	private String comPortName = "COM3";
	//private String comPortName = "/dev/ttyACM0";
	/** * FIELD:		Communication rate for the USB port.  If you're Arduino is using a different rate, change this value to match it. */
	private int baudRate = 115200;
	/** * FIELD:		PrintWriter used to write through the USB port. */
	static PrintWriter outPut;
	/** * FIELD:		Main content pane. */
	private JPanel mainContentPane;
	/** * FIELD:		Label for the amperage. */
	private JLabel lblAmperage;
	/** * FIELD:		Data available from the serial port? */
	private static int dataStatus;
	/** * FIELD:		Textfield representation of the amperage readings */
	private JTextField amperageValueText;
	/** * FIELD:		String value for the amperage. */
	private String amperage;
	/** * FIELD:		Default constructor for the email. */
	private Email email = new Email();
	/** * FIELD:		Boolean to see if an email has been sent. */
	private boolean emailSent = false;
	/** * FIELD:		Default Amps() constructor. */
	private Amps amps = new Amps();
	
	/**
	 * Launch the GUI.
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					Meter frame = new Meter();
					frame.setVisible(true);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});		
	}

	/**
	 * Create the frame for the GUI
	 */
	public Meter() 
	{
		//Title of the GUI
		setTitle("Arduino");
		
		//Choose what happens when the 'x' is clicked.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Set the size of the GUI.
		setBounds(100, 100, 449, 290);
		
		//Menu bar at the top of the GUI.
		JMenuBar topMenuBar = new JMenuBar();
		topMenuBar.setBackground(SystemColor.control);
		setJMenuBar(topMenuBar);
		
		//File option in menu
		JMenu mnFile = new JMenu("File");
		mnFile.setBackground(SystemColor.control);
		topMenuBar.add(mnFile);
		
		//Exit option in menu, located under "File
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		//Settings option in menu
		JMenu mnSettings = new JMenu("Settings");
		topMenuBar.add(mnSettings);
		
		//Email Address option, located under settings.
		JMenuItem mntmEmailAddress = new JMenuItem("Email Settings");
		mnSettings.add(mntmEmailAddress);
		
		//Amp settings, located under settings.
		JMenuItem mntmAmpSettings = new JMenuItem("Amp Settings");
		mnSettings.add(mntmAmpSettings);
		
		//Test email option, located under settings.
		JMenuItem mntmTestEmail = new JMenuItem("Test Email");
		mnSettings.add(mntmTestEmail);
		
		//About option
		JMenu mnAbout = new JMenu("About");
		topMenuBar.add(mnAbout);
		
		//Info about the code, located under About settings.
		JMenuItem mntmInfo = new JMenuItem("Info");
		mnAbout.add(mntmInfo);

		//Create a new contentPane and set the borders
		mainContentPane = new JPanel();
		mainContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainContentPane);
		mainContentPane.setLayout(new GridLayout(5, 0, 0, 0));
		
		//Create a new JPanel
		JPanel buttonPanel = new JPanel();
		mainContentPane.add(buttonPanel);
				
		//Add connect button.
		JButton btnConnect = new JButton("Connect");
		buttonPanel.add(btnConnect);
		
		//Add disconnect button.
		JButton btnDisconnect = new JButton("Disconnect");
		buttonPanel.add(btnDisconnect);
		
		//Monitor amperage button.
		JButton btnMonitor = new JButton("Monitor");
		btnMonitor.setEnabled(false);
		buttonPanel.add(btnMonitor);
		
		//Add another panel for the middle row.
		JPanel ampsPanel = new JPanel();
		mainContentPane.add(ampsPanel);
		ampsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 35, 5));
		
		//Label for amperage display
		lblAmperage = new JLabel("Amps: ");
		lblAmperage.setFont(new Font("Tahoma", Font.PLAIN, 25));
		ampsPanel.add(lblAmperage);
		
		//Display amperage values in the GUI.
		amperageValueText = new JTextField();
		amperageValueText.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		amperageValueText.setBackground(SystemColor.control);
		amperageValueText.setFont(new Font("Tahoma", Font.PLAIN, 25));
		ampsPanel.add(amperageValueText);
		amperageValueText.setColumns(5);
		amperageValueText.setBackground(SystemColor.control);
		
		//New GUI panel for the status
		JPanel statusPanel = new JPanel();
		FlowLayout fl_statusPanel = (FlowLayout) statusPanel.getLayout();
		fl_statusPanel.setHgap(35);
		fl_statusPanel.setVgap(7);
		fl_statusPanel.setAlignment(FlowLayout.LEFT);
		mainContentPane.add(statusPanel);

		//Add the status JLabel.
		JLabel lblStatus = new JLabel("Status: Connect to Arduino");
		statusPanel.add(lblStatus);
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		
		//New panel for info
		JPanel infoPanel = new JPanel();
		mainContentPane.add(infoPanel);
		
		//New label to display what email is being used.
		JLabel lblEmail = new JLabel(String.format("%14s", "Email: ") + email.getRecipient());
		lblEmail.setFont(new Font("Tahoma", Font.BOLD, 17));
		infoPanel.add(lblEmail);
		infoPanel.setLayout(new GridLayout(2, 1, 1, 1));
	
		//Label to show what the upper limit is for the amperage.
		JLabel lblAmpLimitLabel = new JLabel(String.format("%18s", "Amp Limit: " ) + amps.getAmpLimit() + " amps");
		lblAmpLimitLabel.setFont(new Font("Tahoma", Font.BOLD, 17));
		infoPanel.add(lblAmpLimitLabel);
		
		//New panel for the email info
		JPanel emailPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) emailPanel.getLayout();
		flowLayout.setHgap(30);
		mainContentPane.add(emailPanel);
		
		//Check box to show an email has been sent.
		JCheckBox chckbxEmailSent = new JCheckBox("Email Sent");
		emailPanel.add(chckbxEmailSent);

		//Check box to select whether emails are sent out or not.
		JCheckBox chckbxEmailAlert = new JCheckBox(String.format("%s", "Email Alert"));
		chckbxEmailAlert.setVerticalAlignment(SwingConstants.BOTTOM);
		chckbxEmailAlert.setHorizontalAlignment(SwingConstants.LEFT);
		emailPanel.add(chckbxEmailAlert);
		

		/**
		 * Listeners and other items that were moved for ease of use.
		 */
		//Disable disconnect buttons if Connection is selected.
		if(btnConnect.isEnabled() == true)
		{
			btnDisconnect.setEnabled(false);
		}
		else
		{
			btnDisconnect.setEnabled(true);
		}
		
		//Listener if the amperage settings are to be changed.
		mntmAmpSettings.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				//Prompt the user to enter new limits.
				String newAmpLimits = JOptionPane.showInputDialog("Enter new amp upper limit.");
				
				//Data validation for new limits.
				if(newAmpLimits == null)
				{
					JOptionPane.showMessageDialog(null, "Amperage limit not changed.");
				}
				else if(!newAmpLimits.equals("") && Double.parseDouble(newAmpLimits) > 0 && Double.parseDouble(newAmpLimits) <= 20)
				{
					amps.setAmpLimit(Double.parseDouble(newAmpLimits));
					lblAmpLimitLabel.setText(String.format("%18s", "Amp Limit: ") + Double.toString(amps.getAmpLimit()) + " amps");
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Please enter an amperage between 0 and 20.");
				}			
			}
		});

		//Listener if Exit is selected.
		mntmExit.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				//Exit GUI.
				System.exit(0);
			}
		});
		
		//Listener if info is selected.
		mntmInfo.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				//Display contact info.
				JPanel infoPanel = new JPanel();
				JOptionPane.showMessageDialog (infoPanel, "Visit www.buildtech.xyz for contact info/support.", "Testing", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		//Listener to change the email address.
		mntmEmailAddress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				//Prompt user to enter new email address.
				String newEmail = JOptionPane.showInputDialog("Enter new email address.");
				
				//Validate entry.
				if(newEmail == null)
				{
					JOptionPane.showMessageDialog(null, "Email address was null.  Email has not been updated.");
				}
				else if(newEmail.equals(""))
				{
					JOptionPane.showMessageDialog(null, "Email address was blank.  Email has not been updated.");
				}
				else
				{
					email.setRecipient(newEmail);
					lblEmail.setText(String.format("%14s", "Email: ") + newEmail);
				}
			}
		});
		
		//Listener to try and send a test email.
		mntmTestEmail.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				//Prompt user to enter test email address.
				String testEmailAddress = JOptionPane.showInputDialog("Enter desired email recipient:");
				
				//Validate email entry.
				if(testEmailAddress == null)
				{
					JOptionPane.showMessageDialog(null, "Email address was null.  No email sent.");
				}
				else if(testEmailAddress.equals(""))
				{
					JOptionPane.showMessageDialog(null, "Email address was blank.  No email sent.");
				}
				//If the email passes validation, send email.
				else
				{
					try 
					{
						email.generateTestEmail();
					} 
					catch (AddressException e1) 
					{
						System.out.println("Error with the address sending email.");
						e1.printStackTrace();
					} 
					catch (MessagingException e1) 
					{
						System.out.println("Error with the message sending email.");
						e1.printStackTrace();
					}
				}
			}
		});
		
		//Listener for monitor button.
		btnMonitor.addActionListener(new ActionListener() 
		{
			@Override
	        public void actionPerformed(ActionEvent e)
	        {
				//Start new scheduled service to keep checking for Arduino data.
	            final ScheduledExecutorService service = 
	            Executors.newSingleThreadScheduledExecutor();
	                
	            //Update GUI status.
	            lblStatus.setText("Status: Monitoring Current");
	                
	            //Disable the monitor button.
	            btnMonitor.setEnabled(false);
	            
	            //Run schedule with a 500 millisecond delay.  Same as Arduino.
	            service.scheduleWithFixedDelay(new Runnable()
	            {	
	                @Override
	                public void run()
                    {
	                	//Check that there is data in the serial port.
                    	while(SerialPort.LISTENING_EVENT_DATA_AVAILABLE == 1)
                    	{
                    		//Grab the string values.
                    		String amperageValues = serialRead();

                    		try
                    		{
                    			//Create new Amps object and pass in recently read string.
                    			amps = new Amps(amperageValues);
                    			
                    			//Validate the input.
                    			amps.checkInput(amps.getAmperage());
                    			
                    			//Save the amp value read.
                    			amperage = amps.ampDisplay();
                    				                 
                    			//Display the value read to the GUI.
                    			amperageValueText.setText(amperage);
                    			
                    			//Check to see if the amperage is greater than 20 amps, if no email has been sent, and if one is required.
                    			//If this is true, that means you have exceeded your upper limit amperage settings and an email needs to be sent.
                    			if(amps.checkAmperage(amperage) == true && amperage != null && chckbxEmailAlert.isSelected() == true && chckbxEmailSent.isSelected() == false)
                    			{
                    				//Send email 
                    				try
                    				{
                    					email.setAmperage(amperage);
										email.generateAmpEmail();
									} 
                    				catch (AddressException e) 
                    				{
										System.out.println("Error with the address sending the email.");
										e.printStackTrace();
									} catch (MessagingException e) {
										System.out.println("Error with the message sending the email.");
										e.printStackTrace();
									}
                    				
                    				//Update the email sent box.
                    				chckbxEmailSent.setSelected(true);
                    			}	                    			
                    		}
                    		//Catch any issues with the email.
                    		catch(NumberFormatException | IndexOutOfBoundsException e)
                    		{
                    			System.err.println("Error reading data." + e);
                    		}
                    	}
                    }
	                    }, 0, 500, TimeUnit.MILLISECONDS);				//Update these fields to change the timing of the schedule.
	            }
		});
		
		//Connect listener.  Connects to the Arduino.
		btnConnect.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				//Set up the values for the USB port.
				comPort = SerialPort.getCommPort(comPortName);
				comPort.setBaudRate(baudRate);

				//If the port is not closed, open the USB port.
				if(comPort.isOpen() == false)
				{	
					try 
					{
						//Open the USB port and initialize the PrintWriter.
						comPort.openPort();
						Thread.sleep(1000);
						outPut = new PrintWriter(comPort.getOutputStream());
					} 
					
					catch(Exception c){}
					
					//Update the console and status.
					btnConnect.setEnabled(false);
					btnDisconnect.setEnabled(true);
					btnMonitor.setEnabled(true);
				}
				
				if(comPort.isOpen() == false)
				{
					//If the port couldn't be opened print out to the console.
					lblStatus.setText("Status: Opening USB failed");
					btnConnect.setEnabled(true);
					btnDisconnect.setEnabled(false);
					btnMonitor.setEnabled(false);
				}
				else
				{
					//System.out.println("Connection to Arduino successful.");
					lblStatus.setText("Status: Connected");
				}
				
				dataStatus = SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			}
		});
		
		//Listener to disconnect from the Arduino.
		btnDisconnect.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				//Update GUI buttons and statuses.
				btnConnect.setEnabled(true);
				btnDisconnect.setEnabled(false);
				btnMonitor.setEnabled(false);
				lblStatus.setText("Status: Disconnected.");
				
				//Blank out the amperage value.  This doesn't always work correctly!  Not sure why!
				amperageValueText.setText("");
				
				//Close the comport.
				comPort.closePort();
			}
		});
		
		//Email Alert checkbox listener.  Check this to have an email sent.
		chckbxEmailAlert.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if(chckbxEmailAlert.isSelected() == true)
				{
					chckbxEmailSent.setSelected(false);
				}
			}
		});
		
	}

	/**
	 * METHOD:		serialRead()
	 * 
	 * Read in the data from the serial port on the Arduino.
	 * 
	 * @return		
	 */
	public String serialRead()
	{
		//Loop through and read the serial port while the comport has not timed out.
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		
		//String to save incoming data to.
		String dataIn="";
		
		//Scanner object to read in through the comport.
		Scanner in = new Scanner(comPort.getInputStream());
		try
		{
			//Grab data if there is a next value.  Sleep for 500 milliseconds after, same as the Arduino is senidng it.
			if(in.hasNext())
			{
				dataIn = in.next();
				Thread.sleep(500);
			}
			
			//Close the scanner.
		   	in.close();
		} 
		catch (Exception e) 
		{
			System.out.println("Error reading the data from serial.");
			e.printStackTrace();
		}
		//Return the 
		return dataIn;
	}

	/**
	 * METHOD:		serialEvent()
	 * 
	 * Uses javax.comm's serial library to grab data from the serial port.
	 * This is an abstract method due to implementing SerialPortEventListener.
	 */
	@Override
	public void serialEvent(javax.comm.SerialPortEvent event) 
	{
	    switch (event.getEventType()) 
	    {
        	case javax.comm.SerialPortEvent.DATA_AVAILABLE:
        		System.out.println("Data available");
        		break;
        }
    }
}
