package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Home extends JFrame implements ActionListener {

    private JMenuItem boardingPass, checkin;
    private JMenuItem modifyFlight;
    private String userRole; // Variable to store user role
    private int passengerId; // Variable to store user role
 private static  String username;
    public Home(String userRole, int passengerId, String username) {
        this.userRole = userRole; // Initialize user role
        this.passengerId = passengerId;
        this.username = username;
        System.out.print("username ="+username);
        // Set up JFrame
        setLayout(null);

        // Load and set background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("flightmanagementsystem/icons/front.jpg"));
        JLabel image = new JLabel(i1);
        image.setBounds(0, 0, 1600, 800);
        add(image);

        // Set up heading label
        JLabel heading = new JLabel("AIR INDIA WELCOMES YOU");
        heading.setBounds(500, 40, 1000, 40);
        heading.setForeground(Color.BLUE);
        heading.setFont(new Font("Tahoma", Font.PLAIN, 36));
        image.add(heading);

        // Set up menu bar
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        JMenu details = new JMenu("Details");
        menubar.add(details);

        // Add menu items based on user role
        addMenuItems(details);

        JMenu ticket = new JMenu("Ticket");
        menubar.add(ticket);

        if ("USER".equalsIgnoreCase(userRole) && passengerId != 0) {
        checkin = new JMenuItem("Boarding Pass");
        checkin.addActionListener(this);
        ticket.add(checkin);
        }
        if ("ADMIN".equalsIgnoreCase(userRole) && passengerId != 0) {
        	checkin = new JMenuItem("Boarding Pass.");
        	checkin.addActionListener(this);
        	ticket.add(checkin);
        }
        if ("USER".equalsIgnoreCase(userRole) && passengerId != 0) {
            boardingPass = new JMenuItem("Check In");
            boardingPass.addActionListener(this);
            ticket.add(boardingPass);
        }
        
        

        if ("ADMIN".equalsIgnoreCase(userRole) && passengerId != 0) {
            modifyFlight = new JMenuItem("Flight Management");
            modifyFlight.addActionListener(this);
            details.add(modifyFlight);
        }

        // Frame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit the application when the main window is closed
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void addMenuItems(JMenu details) {
        if ("ADMIN".equalsIgnoreCase(userRole) && passengerId != 0) {
            JMenuItem customerDetails = new JMenuItem("Add Passenger Details");
            customerDetails.addActionListener(this);
            details.add(customerDetails);

            JMenuItem flightDetails = new JMenuItem("Flight Details");
            flightDetails.addActionListener(this);
            details.add(flightDetails);
        }

        if ("USER".equalsIgnoreCase(userRole) && passengerId != 0) {
            JMenuItem rescheduleFlight = new JMenuItem("Reschedule Flight");
            rescheduleFlight.addActionListener(this);
            details.add(rescheduleFlight);
        }
        
        if ("USER".equalsIgnoreCase(userRole) && passengerId != 0) {
        	boardingPass = new JMenuItem("History");
        	boardingPass.addActionListener(this);
        	details.add(boardingPass);
        }

        if ("ADMIN".equalsIgnoreCase(userRole) && passengerId != 0) {
            JMenuItem bookFlight = new JMenuItem("Book Flight");
            bookFlight.addActionListener(this);
            details.add(bookFlight);

            JMenuItem ticketCancellation = new JMenuItem("Cancel Ticket");
            ticketCancellation.addActionListener(this);
            details.add(ticketCancellation);
        }
        JMenuItem journeneyDetils = new JMenuItem("Journey Details");
        journeneyDetils.addActionListener(this);
        details.add(journeneyDetils);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String text = ae.getActionCommand();
        System.out.println("Action Command: " + text); // Debug statement

        if (text.equals("Add Passenger Details")) {
            new AddCustomer();
        } else if (text.equals("Flight Details")) {
            new FlightInfo();
        } else if (text.equals("Book Flight")) {
            new BookFlight(passengerId);
        } else if (text.equals("Journey Details")) {
            new JourneyDetails();
        } else if (text.equals("Cancel Ticket")) {
            new Cancel(passengerId);
        }
        else if (text.equals("Boarding Pass")) {
            new BoardingPass(username);
        }
        else if (text.equals("Boarding Pass.")) {
        	new AdminBoardingPass();
        }
        else if (text.equals("Flight Management")) {
            new FlightManagement();
        }
        else if (text.equals("History")) {
        	new TravelHistory(username);
        }
        else if (text.equals("Check In")) {
            System.out.println("Check In Clicked"); // Debug statement
            try {
            	Conn conn = new Conn();
                new CheckIn(conn,username).setVisible(true); // Open Check In window
            } catch (Exception ex) {
                ex.printStackTrace(); // Print any exceptions
            }
        }  else if (text.equals("Reschedule Flight")) {
            try {
                // Creating a new RescheduleFlight window without affecting Home window
            	RescheduleFlight reschedule = new RescheduleFlight(username);
            	reschedule.setVisible(true);
            	 System.out.println("Reschedule Flight Clicked"); // Debug statement
            } catch (Exception ex) {
                ex.printStackTrace(); // Print any exceptions
            }
        }
    }

    public static void main(String[] args) {
        String role = getUserRole(); // Fetch the user role from the database or session
        int passengerId = getPassengerId(); // Fetch the passenger ID from the database or session
        new Home(role, passengerId,username);
    }

    private static String getUserRole() {
        String role = "USER"; // Default role
        try {
            Conn conn = new Conn();
            Statement stmt = conn.c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT role FROM passenger WHERE username = 'Krish'"); // Change 'Krish' to the actual username

            if (rs.next()) {
                role = rs.getString("role");
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    private static int getPassengerId() {
        int id = 0; // Default ID
        try {
            Conn conn = new Conn();
            Statement stmt = conn.c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM passenger WHERE username = 'Krish'"); // Change 'Krish' to the actual username

            if (rs.next()) {
                id = rs.getInt("id");
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
}
