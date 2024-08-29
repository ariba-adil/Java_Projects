package flightmanagementsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;

public class BookFlight extends JFrame implements ActionListener {

	JTextField tfaadhar;
	JLabel tfname, tfnationality, tfaddress, labelgender, lblpnr, lblticket;
	JButton bookflight, fetchButton, searchButton;
	Choice source, destination, seatChoice, foodChoice;
	JDateChooser dcdate;
	String username;
	public static int passenegrId;

	public BookFlight(int passenegrId) {
		this.passenegrId = passenegrId;
		getContentPane().setBackground(Color.WHITE);
		setLayout(null);

		JLabel heading = new JLabel("Book Flight");
		heading.setBounds(420, 20, 500, 35);
		heading.setFont(new Font("Tahoma", Font.PLAIN, 32));
		heading.setForeground(Color.BLUE);
		add(heading);

		JLabel lblaadhar = new JLabel("Aadhar");
		lblaadhar.setBounds(60, 80, 150, 25);
		lblaadhar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblaadhar);

		tfaadhar = new JTextField();
		tfaadhar.setBounds(220, 80, 150, 25);
		add(tfaadhar);

		fetchButton = new JButton("Fetch User");
		fetchButton.setBackground(Color.BLACK);
		fetchButton.setForeground(Color.WHITE);
		fetchButton.setBounds(380, 80, 120, 25);
		fetchButton.addActionListener(this);
		add(fetchButton);

		JLabel lblname = new JLabel("Name");
		lblname.setBounds(60, 130, 150, 25);
		lblname.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblname);

		tfname = new JLabel();
		tfname.setBounds(220, 130, 150, 25);
		add(tfname);

		JLabel lblnationality = new JLabel("Nationality");
		lblnationality.setBounds(60, 180, 150, 25);
		lblnationality.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblnationality);

		tfnationality = new JLabel();
		tfnationality.setBounds(220, 180, 150, 25);
		add(tfnationality);

		JLabel lbladdress = new JLabel("Address");
		lbladdress.setBounds(60, 230, 150, 25);
		lbladdress.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lbladdress);

		tfaddress = new JLabel();
		tfaddress.setBounds(220, 230, 150, 25);
		add(tfaddress);

		JLabel lblgender = new JLabel("Gender");
		lblgender.setBounds(60, 280, 150, 25);
		lblgender.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblgender);

		labelgender = new JLabel();
		labelgender.setBounds(220, 280, 150, 25);
		add(labelgender);

		JLabel lblsource = new JLabel("Source");
		lblsource.setBounds(60, 330, 150, 25);
		lblsource.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblsource);

		source = new Choice();
		source.setBounds(220, 330, 150, 25);
		add(source);

		JLabel lbldest = new JLabel("Destination");
		lbldest.setBounds(60, 380, 150, 25);
		lbldest.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lbldest);

		destination = new Choice();
		destination.setBounds(220, 380, 150, 25);
		add(destination);

		JLabel lblseat = new JLabel("Seat No");
		lblseat.setBounds(60, 430, 150, 25);
		lblseat.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblseat);

		seatChoice = new Choice();
		seatChoice.setBounds(220, 430, 150, 25);
		add(seatChoice);

		JLabel lblfood = new JLabel("Pre-Booked Food");
		lblfood.setBounds(60, 480, 150, 25);
		lblfood.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblfood);

		foodChoice = new Choice();
		foodChoice.setBounds(220, 480, 150, 25);
		foodChoice.add("None");
		foodChoice.add("Vegetarian");
		foodChoice.add("Non-Vegetarian");
		add(foodChoice);

		JLabel lbldate = new JLabel("Date of Travel");
		lbldate.setBounds(60, 530, 150, 25);
		lbldate.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lbldate);

		dcdate = new JDateChooser();
		dcdate.setBounds(220, 530, 150, 25);
		add(dcdate);

		JLabel lblpnrLabel = new JLabel("PNR No");
		lblpnrLabel.setBounds(60, 580, 150, 25);
		lblpnrLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblpnrLabel);

		lblpnr = new JLabel();
		lblpnr.setBounds(220, 580, 150, 25);
		add(lblpnr);

		JLabel lblticketLabel = new JLabel("Ticket No");
		lblticketLabel.setBounds(60, 630, 150, 25);
		lblticketLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblticketLabel);

		lblticket = new JLabel();
		lblticket.setBounds(220, 630, 150, 25);
		add(lblticket);

		ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("flightmanagementsystem/icons/details.jpg"));
		Image i2 = i1.getImage().getScaledInstance(450, 320, Image.SCALE_DEFAULT);
		ImageIcon image = new ImageIcon(i2);
		JLabel lblimage = new JLabel(image);
		lblimage.setBounds(550, 80, 500, 410);
		add(lblimage);

		bookflight = new JButton("Book Flight");
		bookflight.setBackground(Color.BLACK);
		bookflight.setForeground(Color.WHITE);
		bookflight.setBounds(550, 80, 120, 25);
		bookflight.addActionListener(this);
		add(bookflight);

		searchButton = new JButton("Search Flights");
		searchButton.setBackground(Color.BLACK);
		searchButton.setForeground(Color.WHITE);
		searchButton.setBounds(400, 680, 150, 25);
		searchButton.addActionListener(this);
		add(searchButton);

		setSize(1100, 750);
		setLocation(100, 20);
		setVisible(true);

		populateSeatChoices();
		populateSourceAndDestinationChoices();

	}

	private void populateSeatChoices() {
		for (int i = 1; i <= 30; i++) {
			seatChoice.add(String.valueOf(i));
		}
	}

	private void populateSourceAndDestinationChoices() {
		try {
			Conn conn = new Conn();
			String query = "SELECT DISTINCT arrival_airport FROM flight_details";
			ResultSet rs = conn.s.executeQuery(query);

			while (rs.next()) {
				source.add(rs.getString("arrival_airport"));
			}

			query = "SELECT DISTINCT departure_airport FROM flight_details";
			rs = conn.s.executeQuery(query);

			while (rs.next()) {
				destination.add(rs.getString("departure_airport"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent ae) {
	    if (ae.getSource() == fetchButton) {
	        String aadhar = tfaadhar.getText().trim();

	        if (aadhar.isEmpty()) {
	            JOptionPane.showMessageDialog(null, "Please enter Aadhar number");
	            return;
	        }

	        try {
	            Conn conn = new Conn();
	            String query = "SELECT * FROM passenger WHERE aadhar = ?";
	            PreparedStatement pstmt = conn.c.prepareStatement(query);
	            pstmt.setString(1, aadhar);
	            ResultSet rs = pstmt.executeQuery();

	            if (rs.next()) {
	                tfname.setText(rs.getString("name"));
	                tfnationality.setText(rs.getString("nationality"));
	                tfaddress.setText(rs.getString("address"));
	                labelgender.setText(rs.getString("gender"));
	                username = rs.getString("username");
	            } else {
	                JOptionPane.showMessageDialog(null, "No user found with the given Aadhar number");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    } else if (ae.getSource() == bookflight) {
	        String aadhar = tfaadhar.getText().trim();
	        String sourceLocation = source.getSelectedItem().toString();
	        String destinationLocation = destination.getSelectedItem().toString();
	        String seat = seatChoice.getSelectedItem().toString();
	        String food = foodChoice.getSelectedItem().toString();
	        Date travelDate = dcdate.getDate();

	        if (aadhar.isEmpty()) {
	            JOptionPane.showMessageDialog(null, "Please fetch user details using Aadhar number first");
	            return;
	        }

	        if (travelDate == null || travelDate.before(new java.sql.Date(new Date().getTime()))) {
	            JOptionPane.showMessageDialog(null, "Please select a valid travel date. It should be a future date.");
	            return;
	        }

	        try {
	            Conn conn = new Conn();

	            // First, get flight details to check availability and retrieve flightCode
	            String flightQuery = "SELECT flight_ID, flight_number, status FROM flight_details WHERE arrival_airport = ? AND departure_airport = ?";
	            PreparedStatement flightPstmt = conn.c.prepareStatement(flightQuery);
	            flightPstmt.setString(1, sourceLocation);
	            flightPstmt.setString(2, destinationLocation);
	            ResultSet flightRs = flightPstmt.executeQuery();

	            if (flightRs.next()) {
	                String flightCode = flightRs.getString("flight_ID");
	                String flightNumber = flightRs.getString("flight_number");
	                String status = flightRs.getString("status");

	                // Check flight status
	                if ("Cancelled".equalsIgnoreCase(status)) {
	                    JOptionPane.showMessageDialog(null, "Error: The flight is cancelled. Booking cannot proceed.");
	                    return;
	                }

	                // Check if the seat is already taken for the given flight, date, and booking
	                String seatCheckQuery = "SELECT COUNT(*) FROM reservation WHERE flightcode = ? AND travel_date = ? AND seat_no = ?";
	                PreparedStatement seatCheckPstmt = conn.c.prepareStatement(seatCheckQuery);
	                seatCheckPstmt.setString(1, flightCode);
	                seatCheckPstmt.setDate(2, new java.sql.Date(travelDate.getTime()));
	                seatCheckPstmt.setString(3, seat);
	                ResultSet seatCheckRs = seatCheckPstmt.executeQuery();

	                if (seatCheckRs.next() && seatCheckRs.getInt(1) > 0) {
	                    JOptionPane.showMessageDialog(null, "This seat is already taken. Please select a new seat.");
	                    return;
	                }

	                // Proceed with booking if seat is available
	                int confirm = JOptionPane.showConfirmDialog(null, "Do you really want to book this flight?",
	                        "Confirmation", JOptionPane.YES_NO_OPTION);

	                if (confirm == JOptionPane.YES_OPTION) {
	                    generatePnr();
	                    generateTicket();

	                    String pnr = lblpnr.getText();
	                    String ticket = lblticket.getText();
	                    System.out.println("Generated PNR: " + pnr);
	                    System.out.println("Generated Ticket: " + ticket);

	                    String reservationQuery = "INSERT INTO reservation (aadhar, pnr, ticket, src, des, seat_no, food_choice, travel_date, name, nationality, flightcode, flightname, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	                    PreparedStatement reservationPstmt = conn.c.prepareStatement(reservationQuery);
	                    reservationPstmt.setString(1, aadhar);
	                    reservationPstmt.setString(2, pnr);
	                    reservationPstmt.setString(3, ticket);
	                    reservationPstmt.setString(4, sourceLocation);
	                    reservationPstmt.setString(5, destinationLocation);
	                    reservationPstmt.setString(6, seat);
	                    reservationPstmt.setString(7, food);
	                    reservationPstmt.setDate(8, new java.sql.Date(travelDate.getTime()));
	                    reservationPstmt.setString(9, tfname.getText());
	                    reservationPstmt.setString(10, tfnationality.getText());
	                    reservationPstmt.setString(11, flightCode);
	                    reservationPstmt.setString(12, flightNumber);
	                    reservationPstmt.setString(13, username);

	                    int rowsAffected = reservationPstmt.executeUpdate();

	                    if (rowsAffected > 0) {
	                        JOptionPane.showMessageDialog(null,
	                                "Flight booked successfully\nYour PNR is " + pnr + "\nYour ticket number is " + ticket);
	                    } else {
	                        JOptionPane.showMessageDialog(null, "Failed to book flight. Please try again.");
	                    }
	                }
	            } else {
	                JOptionPane.showMessageDialog(null, "No flight found for the given source and destination");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    } else if (ae.getSource() == searchButton) {
	        JOptionPane.showMessageDialog(null, "Search functionality not implemented yet");
	    }
	}


	private void generatePnr() {
	    Random rand = new Random();
	    int pnr = rand.nextInt(999999);
	    lblpnr.setText(String.format("%06d", pnr));
	}

	private void generateTicket() {
	    Random rand = new Random();
	    int ticket = rand.nextInt(99999999);
	    lblticket.setText(String.format("%08d", ticket));
	}


	public static void main(String[] args) {
		new BookFlight(passenegrId);
	}
}
