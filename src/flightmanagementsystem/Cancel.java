package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Cancel extends JFrame implements ActionListener {

	private JTextField tfpnr;
	private JLabel tfname, cancellationno, lblfcode, lbldateoftravel;
	private JButton fetchButton, flight;
	private boolean ticketCancelled = false;
	private static int passenegrId;

	public Cancel(int passenegrId) {
		this.passenegrId = passenegrId;
		getContentPane().setBackground(Color.WHITE);
		setLayout(null);

		JLabel heading = new JLabel("CANCELLATION");
		heading.setBounds(180, 20, 250, 35);
		heading.setFont(new Font("Tahoma", Font.PLAIN, 32));
		add(heading);

		ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("flightmanagementsystem/icons/cancel.png"));
		Image i2 = i1.getImage().getScaledInstance(250, 250, Image.SCALE_DEFAULT);
		ImageIcon i3 = new ImageIcon(i2);
		JLabel image = new JLabel(i3);
		image.setBounds(470, 120, 250, 250);
		add(image);

		JLabel lblaadhar = new JLabel("PNR Number");
		lblaadhar.setBounds(60, 80, 150, 25);
		lblaadhar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblaadhar);

		tfpnr = new JTextField();
		tfpnr.setBounds(220, 80, 150, 25);
		add(tfpnr);

		fetchButton = new JButton("Show Details");
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

		JLabel lblnationality = new JLabel("Cancellation No");
		lblnationality.setBounds(60, 180, 150, 25);
		lblnationality.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblnationality);

		cancellationno = new JLabel(); // Initially empty
		cancellationno.setBounds(220, 180, 150, 25);
		add(cancellationno);

		JLabel lbladdress = new JLabel("Flight Code");
		lbladdress.setBounds(60, 230, 150, 25);
		lbladdress.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lbladdress);

		lblfcode = new JLabel();
		lblfcode.setBounds(220, 230, 150, 25);
		add(lblfcode);

		JLabel lblgender = new JLabel("Date");
		lblgender.setBounds(60, 280, 150, 25);
		lblgender.setFont(new Font("Tahoma", Font.PLAIN, 16));
		add(lblgender);

		lbldateoftravel = new JLabel();
		lbldateoftravel.setBounds(220, 280, 150, 25);
		add(lbldateoftravel);

		flight = new JButton("Cancel");
		flight.setBackground(Color.BLACK);
		flight.setForeground(Color.WHITE);
		flight.setBounds(220, 330, 120, 25);
		flight.addActionListener(this);
		add(flight);

		setSize(800, 450);
		setLocation(250, 120);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == fetchButton) {
			String pnr = tfpnr.getText();

			try {
				Conn conn = new Conn();
				String query = "SELECT * FROM reservation WHERE PNR = ?";
				PreparedStatement pstmt = conn.c.prepareStatement(query);
				pstmt.setString(1, pnr);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					if (rs.getBoolean("status")) {
						JOptionPane.showMessageDialog(null, "This ticket has already been cancelled.");
						return;
					}

					tfname.setText(rs.getString("name") != null ? rs.getString("name") : "N/A");
					lblfcode.setText(rs.getString("flightcode") != null ? rs.getString("flightcode") : "N/A");
					lbldateoftravel.setText(rs.getString("travel_date") != null ? rs.getString("travel_date") : "N/A");

					// Reset cancellation number field
					cancellationno.setText("");
					ticketCancelled = false;
					JOptionPane.showMessageDialog(null, "Details fetched successfully.");
				} else {
					JOptionPane.showMessageDialog(null, "No reservation found with the provided PNR.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error fetching reservation details.");
			}
		} else if (ae.getSource() == flight) {
			if (ticketCancelled) {
				JOptionPane.showMessageDialog(null, "This ticket has already been cancelled.");
				return;
			}

			int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel this ticket?",
					"Confirm Cancellation", JOptionPane.YES_NO_OPTION);

			if (response == JOptionPane.YES_OPTION) {
				String name = tfname.getText();
				String pnr = tfpnr.getText();
				String fcode = lblfcode.getText();
				String date = lbldateoftravel.getText();

				// Check if cancellation is within 24 hours of departure
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Adjust format as needed
				try {
					if (!date.equals("N/A")) {
						Date departureDate = sdf.parse(date);
						Date currentDate = new Date();
						long diff = departureDate.getTime() - currentDate.getTime();
						long hoursDifference = diff / (1000 * 60 * 60);

						if (hoursDifference < 24) {
							JOptionPane.showMessageDialog(null,
									"Ticket can only be cancelled 24 hours prior to departure.");
							return;
						}
					}

					// Generate cancellation number
					Random random = new Random();
					String cancelno = String.format("%06d", random.nextInt(1000000));
					cancellationno.setText(cancelno);

					Conn conn = new Conn();
					String insertQuery = "INSERT INTO cancel_ticket (PNR, name, cancelno, flight_code, travel_date, passenger_id) VALUES (?, ?, ?, ?, ?, ?)";
					PreparedStatement pstmt = conn.c.prepareStatement(insertQuery);
					pstmt.setString(1, pnr);
					pstmt.setString(2, name);
					pstmt.setString(3, cancelno);
					pstmt.setString(4, fcode);
					pstmt.setString(5, date);
					pstmt.setInt(6, passenegrId);
					pstmt.executeUpdate();

					String updateQuery = "UPDATE reservation SET status = ? WHERE PNR = ?";
					PreparedStatement pstmt1 = conn.c.prepareStatement(updateQuery);
					pstmt1.setBoolean(1, true);
					pstmt1.setString(2, pnr);
					pstmt1.executeUpdate();

					ticketCancelled = true; // Set flag to prevent re-cancellation
					JOptionPane.showMessageDialog(null,
							"Ticket Cancelled Successfully\nCancellation number: " + cancelno);
					setVisible(false);

				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error processing cancellation.");
				}
			}
		}
	}

	public static void main(String[] args) {
		new Cancel(passenegrId);
	}
}
