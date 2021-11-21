//imports
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

@SuppressWarnings("serial")
public class JDBCMainWindowContent extends JInternalFrame implements ActionListener
{	
	String cmd = null;

	// DB Connectivity Attributes
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	
	ImageIcon chartIcon = new ImageIcon("chart.png");
	Image img = chartIcon.getImage(); 
	Image scaled_img = img.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
	ImageIcon imageIcon = new ImageIcon(new ImageIcon("chart.png").getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
	
	
	private Container content;
	private JPanel detailsPanel;
	private JPanel exportButtonPanel;
	//private JPanel exportConceptDataPanel;
	private JScrollPane dbContentsPanel;
	private JPanel chartStatisticsPanel;
	
	
	private Border lineBorder;
	//create jlabels for the data for CRUD
	private JLabel IDLabel= new JLabel("ID: 	");
	private JLabel FirstNameLabel=new JLabel("FirstName:               ");
	private JLabel LastNameLabel=new JLabel("LastName:      ");
	private JLabel GenderLabel=new JLabel("Gender:        ");
	private JLabel NationalityLabel=new JLabel("Nationality:                 ");
	private JLabel DateofBirthLabel=new JLabel("Date of Birth:               ");
	private JLabel PassportNumberLabel=new JLabel("Passport Number:      ");
	private JLabel DepartureCityLabel=new JLabel("Departure City:      ");
	private JLabel ArrivalCityLabel=new JLabel("Arrival City:       ");
	private JLabel AirCompanyLabel=new JLabel("Aircraft Company:        ");
	private JLabel AnyStopsLabel=new JLabel("Any Stops:        ");

	////create text fields for the data for CRUD
	private JTextField IDTF= new JTextField(30);
	private JTextField FirstNameTF=new JTextField(30);
	private JTextField LastNameTF=new JTextField(30);
	private JTextField GenderTF=new JTextField(30);
	private JTextField NationalityTF=new JTextField(30);
	private JTextField DateofBirthTF=new JTextField(30);
	private JTextField PassportNumberTF=new JTextField(30);
	private JTextField DepartureCityTF=new JTextField(30);
	private JTextField ArrivalCityTF=new JTextField(30);
	private JTextField AirCompanyTF=new JTextField(30);
	private JTextField AnyStopsTF=new JTextField(30);


	private static QueryTableModel TableModel = new QueryTableModel();
	//Add the models to JTabels
	private JTable TableofDBContents=new JTable(TableModel);
	//Buttons for inserting, and updating members
	//also a clear button to clear details panel
	private JButton updateButton = new JButton("Update");
	private JButton insertButton = new JButton("Insert");
	private JButton exportButton  = new JButton("Export");
	private JButton deleteButton  = new JButton("Delete");
	private JButton clearButton  = new JButton("Clear");
	//buttons forcharts
	private JButton statsButton  = new JButton("DifferentAirCompanies");
	private JButton statsButton2  = new JButton("NationalitiesofCustomers");
	private JButton statsButton3  = new JButton("NoOfFlightsPerCity");
	private JButton statsButton4  = new JButton("PassengerAges");

	private JButton  NumFlights= new JButton("number Of Flights Per Country:");
	private JTextField NumFlightsTF  = new JTextField(22);
	private JButton avgNationality  = new JButton("Average Nationality");
	private JTextField avgNationalityTF  = new JTextField(22);
	private JButton ListAllDepartures  = new JButton("List The Departures");
	private JTextField ListAllDeparturesTF  = new JTextField(22);
	private JButton ListAllAircraft  = new JButton("List The Number of Aircraft Companies:");
	private JTextField ListAllAircraftTF  = new JTextField(22);
	private JButton getAge= new JButton("Get age of the passanger");
	private JTextField getAgeTF  = new JTextField(22);

	
	

	public JDBCMainWindowContent( String aTitle)
	{	
		//setting up the GUI
		super(aTitle, false,false,false,false);
		setEnabled(true);

		initiate_db_conn();
		//add the 'main' panel to the Internal Frame
		content=getContentPane();
		content.setLayout(null);
		content.setBackground(Color.orange);
		lineBorder = BorderFactory.createEtchedBorder(15, Color.DARK_GRAY, Color.DARK_GRAY);

		//setup details panel and add the components to it
		detailsPanel=new JPanel();
		detailsPanel.setLayout(new GridLayout(13,2));
		detailsPanel.setBackground(Color.pink);
		detailsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "CRUD Actions"));
			
		detailsPanel.add(IDLabel);	
		detailsPanel.add(IDTF);
		detailsPanel.add(FirstNameLabel);		
		detailsPanel.add(FirstNameTF);
		detailsPanel.add(LastNameLabel);		
		detailsPanel.add(LastNameTF);
		detailsPanel.add(GenderLabel);	
		detailsPanel.add(GenderTF);
		detailsPanel.add(NationalityLabel);		
		detailsPanel.add(NationalityTF);
		detailsPanel.add(DateofBirthLabel);
		detailsPanel.add(DateofBirthTF);
		detailsPanel.add(PassportNumberLabel);
		detailsPanel.add(PassportNumberTF);
		detailsPanel.add(DepartureCityLabel);
		detailsPanel.add(DepartureCityTF);
		detailsPanel.add(ArrivalCityLabel);
		detailsPanel.add(ArrivalCityTF);
		detailsPanel.add(AirCompanyLabel);
		detailsPanel.add(AirCompanyTF);
		detailsPanel.add(AnyStopsLabel);
		detailsPanel.add(AnyStopsTF);



		//setup export panel and add the components to it
		exportButtonPanel=new JPanel();
		exportButtonPanel.setLayout(new GridLayout(5,4));
		exportButtonPanel.setBackground(Color.pink);
		exportButtonPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Data"));
		exportButtonPanel.add(NumFlights);
		exportButtonPanel.setSize (120, 55);
		exportButtonPanel.add(NumFlightsTF);
		exportButtonPanel.add(getAge);
		exportButtonPanel.add(getAgeTF);
		exportButtonPanel.add(avgNationality);
		exportButtonPanel.add(avgNationalityTF);
		exportButtonPanel.add(ListAllDepartures);
		exportButtonPanel.add(ListAllDeparturesTF);
		exportButtonPanel.add(ListAllAircraft);
		exportButtonPanel.add(ListAllAircraftTF);
		exportButtonPanel.setSize(400, 350);
		exportButtonPanel.setLocation(45, 340);
		content.add(exportButtonPanel);

		insertButton.setSize(120, 55);
		updateButton.setSize(120, 55);
		exportButton.setSize (120, 55);
		deleteButton.setSize (120, 55);
		clearButton.setSize (120, 55);

		insertButton.setLocation(10, 20);
		insertButton.setBackground(Color.pink);
		insertButton.setBorder(BorderFactory.createTitledBorder(lineBorder));
		updateButton.setLocation(10, 120);
		updateButton.setBackground(Color.pink);
		updateButton.setBorder(BorderFactory.createTitledBorder(lineBorder));
		exportButton.setLocation (10, 170);
		exportButton.setBackground(Color.LIGHT_GRAY);
		exportButton.setBorder(BorderFactory.createTitledBorder(lineBorder));
		deleteButton.setLocation (10, 70);
		deleteButton.setBackground(Color.LIGHT_GRAY);
		deleteButton.setBorder(BorderFactory.createTitledBorder(lineBorder));
		clearButton.setLocation (10, 220);
		clearButton.setBackground(Color.pink);
		clearButton.setBorder(BorderFactory.createTitledBorder(lineBorder));
		
		chartStatisticsPanel = new JPanel();
		chartStatisticsPanel.setLayout(new GridLayout(2,2));
		chartStatisticsPanel.setBackground(Color.pink);
		chartStatisticsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Pie Chart"));
		statsButton.setSize(150,30);
		statsButton2.setSize(100,30);
		statsButton3.setSize(100,30);
		statsButton4.setSize(100,30);
		chartStatisticsPanel.add(statsButton);
		chartStatisticsPanel.add(statsButton2);
		chartStatisticsPanel.add(statsButton3);
		chartStatisticsPanel.add(statsButton4);
		chartStatisticsPanel.setSize(400,200);
		chartStatisticsPanel.setLocation(200,100);
		content.add(chartStatisticsPanel);
		
		chartStatisticsPanel.setSize(400,300);
		chartStatisticsPanel.setLocation(520,350);
		content.add(chartStatisticsPanel);
		
	
	

		insertButton.addActionListener(this);
		updateButton.addActionListener(this);
		exportButton.addActionListener(this);
		deleteButton.addActionListener(this);
		clearButton.addActionListener(this);
		this.statsButton.addActionListener(this);
		this.statsButton2.addActionListener(this);
		this.statsButton3.addActionListener(this);
		this.statsButton4.addActionListener(this);

		this.NumFlights.addActionListener(this);
		this.avgNationality.addActionListener(this);
		this.ListAllDepartures.addActionListener(this);
		this.ListAllAircraft.addActionListener(this);
		this.getAge.addActionListener(this);


		content.add(insertButton);
		content.add(updateButton);
		content.add(exportButton);
		content.add(deleteButton);
		content.add(clearButton);


		TableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));

		dbContentsPanel=new JScrollPane(TableofDBContents,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dbContentsPanel.setBackground(Color.pink);
		dbContentsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder,"Database Content"));

		detailsPanel.setSize(360, 300);
		detailsPanel.setLocation(150,10);
		dbContentsPanel.setSize(650, 300);
		dbContentsPanel.setLocation(520, 10);

		content.add(detailsPanel);
		content.add(dbContentsPanel);

		setSize(1100,100);
		setVisible(true);

		TableModel.refreshFromDB(stmt);
	}

	public void initiate_db_conn()
	{
		try
		{
			// Load the JConnector Driver
			Class.forName("com.mysql.jdbc.Driver");
			// Specify the DB Name
			String url="jdbc:mysql://localhost:3306/assign"; //change the name of the database
			// Connect to DB using DB URL, Username and password
			con = DriverManager.getConnection(url, "root", "Labukasbvb112");
			//Create a generic statement which is passed to the TestInternalFrame1
			stmt = con.createStatement();
		}
		catch(Exception e)
		{
			System.out.println("Error: Failed to connect to database\n"+e.getMessage());
		}
	}

	//event handling 
	public void actionPerformed(ActionEvent e)
	{
		Object target=e.getSource();
		if (target == clearButton)
		{
			IDTF.setText("");
			FirstNameTF.setText("");
			LastNameTF.setText("");
			GenderTF.setText("");
			NationalityTF.setText("");
			DateofBirthTF.setText("");
			PassportNumberTF.setText("");
			DepartureCityTF.setText("");
			ArrivalCityTF.setText("");
			AirCompanyTF.setText("");
			AnyStopsTF.setText("");

		}

		//Insert function
		if (target == insertButton)
		{		 
			try
			{
				String updateTemp ="INSERT INTO details VALUES("+null +",'"+FirstNameTF.getText()+"','"+LastNameTF.getText()+"','"+GenderTF.getText()+"',"
						+ "'"+NationalityTF.getText()+"','"+DateofBirthTF.getText()+"','"+PassportNumberTF.getText()+"',"
						+ "'"+DepartureCityTF.getText()+"','"+ArrivalCityTF.getText()+"','"+AirCompanyTF.getText()+"','"+AnyStopsTF.getText()+"');";
				stmt.executeUpdate(updateTemp);

			}
			catch (SQLException sqle)
			{
				System.err.println("Error with  insert:\n"+sqle.toString());
			}
			finally
			{
				TableModel.refreshFromDB(stmt);
			}
		}
		//Delete function
		if (target == deleteButton)
		{

			try
			{
				String updateTemp ="DELETE FROM details WHERE id = "+IDTF.getText()+";"; 
				stmt.executeUpdate(updateTemp);

			}
			catch (SQLException sqle)
			{
				System.err.println("Error with delete:\n"+sqle.toString());
			}
			finally
			{
				TableModel.refreshFromDB(stmt);
			}
		}
		//update function
		if (target == updateButton)
		{	 	
			try
			{ 			
				String updateTemp ="UPDATE details SET " +
				"firstName = '"+FirstNameTF.getText()+
				"', lastName = '"+LastNameTF.getText()+
				"', gender = '"+GenderTF.getText()+
				"', nationality ='"+NationalityTF.getText()+
				"', dateOfBirth = '"+DateofBirthTF.getText()+
				"', passportNumber = '"+PassportNumberTF.getText()+
				"', departureCity = '"+DepartureCityTF.getText()+
				"', arrivalCity = '"+ArrivalCityTF.getText()+
				"', airCompany = '"+AirCompanyTF.getText()+
				"', anyStops = '"+AnyStopsTF.getText()+
				"' where id = "+IDTF.getText();


				stmt.executeUpdate(updateTemp);
				//these lines do nothing but the table updates when we access the db.
				rs = stmt.executeQuery("SELECT * from details ");
				rs.next();
				rs.close();	
			}
			catch (SQLException sqle){
				System.err.println("Error with  update:\n"+sqle.toString());
			}
			finally{
				TableModel.refreshFromDB(stmt);
			}
		}

		/////////////////////////////////////////////////////////////////////////////////////
		//I have only added functionality of 2 of the button on the lower right of the template
		
		///////////////////////////////////////////////////////////////////////////////////
		if(target == this.NumFlights){

			cmd = "select departureCity, count(*) from details where departureCity = '"+NumFlightsTF.getText()+"';"; 

			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}
		
		if(target == this.ListAllDepartures){

			cmd = "select distinct departureCity from details;";

			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}


		
		if(target == this.avgNationality){

			cmd = "select nationality, count(nationality) from details group by nationality limit 1;";

			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}

		if(target == this.ListAllAircraft){
			String deptName = this.ListAllAircraftTF.getText();

			cmd = "select airCompany, count((airCompany)) from details group by airCompany order by count(*) desc;";

			System.out.println(cmd);
			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		} 
		
		if(target == this.getAge){

			cmd = "call determineAge( '"+getAgeTF.getText()+"') ;"; 

			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}
		
		if(target == this.exportButton){
			String deptName = this.ListAllAircraftTF.getText();

			cmd = "select * from details;";

			System.out.println(cmd);
			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		} 
	

	///////////////////////////////////////////////////////////////////////////

	

	//graphs
	
	if (target.equals(statsButton)){  		
		cmd ="select airCompany, count(airCompany) from details group by airCompany;";
		try {
			rs= stmt.executeQuery(cmd);
			pieGraph(rs, "Different Aircraft Companies");	
		} 
		catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
					
	}
	
	
	if (target.equals(statsButton2)){  		
		cmd ="select nationality, count(nationality) from details group by nationality;";
		try {
			rs= stmt.executeQuery(cmd);
			pieGraph(rs, "Different Flight Passenger Nationalities");	
		} 
		catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	}	
	if (target.equals(statsButton3)){  		
		cmd ="select arrivalCity, count(*) from details group by arrivalCity;";
		try {
			rs= stmt.executeQuery(cmd);
			pieGraph(rs, "No of Flights arriving in Cities");	
		} 
		catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
					
	}
	if (target.equals(statsButton4)){  		
		cmd ="SELECT dateOfBirth, (YEAR(CURDATE())-YEAR(dateOfBirth)) AS age,count(*) FROM details group by dateOfBirth;";
		try {
			rs= stmt.executeQuery(cmd);
			pieGraph(rs, "Passanger Dates of Birth");	
		} 
		catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
					
	}
}
	public void pieGraph(ResultSet rs, String title) {
		try {
			DefaultPieDataset dataset = new DefaultPieDataset();

			while (rs.next()) {
				String category = rs.getString(1);
				String value = rs.getString(2);
				dataset.setValue(category+ " "+value, new Double(value));
			}
			JFreeChart chart = ChartFactory.createPieChart(
					title,  
					dataset,             
					false,              
					true,
					true
			);

			ChartFrame frame = new ChartFrame(title, chart);
			chart.setBackgroundPaint(Color.WHITE);
			frame.pack();
			frame.setVisible(true);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void writeToFile(ResultSet rs){
		try{
			System.out.println("In writeToFile");
			FileWriter outputFile = new FileWriter("FlightData.csv");
			PrintWriter printWriter = new PrintWriter(outputFile);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();

			for(int i=0;i<numColumns;i++){
				printWriter.print(rsmd.getColumnLabel(i+1)+",");
			}
			printWriter.print("\n");
			while(rs.next()){
				for(int i=0;i<numColumns;i++){
					printWriter.print(rs.getString(i+1)+",");
				}
				printWriter.print("\n");
				printWriter.flush();
			}
			printWriter.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
}