package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

public class CheckIn extends JFrame {
    private Conn conn;
    private JTextField pnrField;
    private JTextField ticketField;
    private JButton checkInButton;
    private static String username;

    public CheckIn(Conn conn, String username) {
        this.username = username;
        this.conn = conn;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Check-In");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel pnrLabel = new JLabel("PNR Number:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(pnrLabel, gbc);

        pnrField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(pnrField, gbc);

        JLabel ticketLabel = new JLabel("Ticket Number:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(ticketLabel, gbc);

        ticketField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(ticketField, gbc);

        checkInButton = new JButton("Check-In");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(checkInButton, gbc);

        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pnrNumber = pnrField.getText().trim();
                String ticketNumber = ticketField.getText().trim();

                if (pnrNumber.isEmpty() || ticketNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both PNR number and Ticket number.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int response = JOptionPane.showConfirmDialog(null, "Do you want to check-in?", "Confirm Check-In", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.OK_OPTION) {
                    performCheckIn(pnrNumber, ticketNumber);
                }
            }
        });

        add(panel);
    }

    private void performCheckIn(String pnrNumber, String ticketNumber) {
        String userCheckQuery = "SELECT pnr, ticket FROM reservation WHERE username = ?";
        String checkQuery = "SELECT status, travel_date FROM reservation WHERE pnr = ? AND ticket = ?";
        String updateQuery = "UPDATE reservation SET check_in = TRUE WHERE pnr = ? AND ticket = ?";

        try (PreparedStatement userCheckPst = conn.c.prepareStatement(userCheckQuery);
             PreparedStatement checkPst = conn.c.prepareStatement(checkQuery);
             PreparedStatement updatePst = conn.c.prepareStatement(updateQuery)) {

            // Verify if the PNR and ticket number belong to the user
            userCheckPst.setString(1, username);
            ResultSet userRs = userCheckPst.executeQuery();

            boolean validUserTicket = false;
            while (userRs.next()) {
                String dbPnr = userRs.getString("pnr");
                String dbTicket = userRs.getString("ticket");
                if (dbPnr.equals(pnrNumber) && dbTicket.equals(ticketNumber)) {
                    validUserTicket = true;
                    break;
                }
            }

            if (!validUserTicket) {
                JOptionPane.showMessageDialog(null, "The PNR and Ticket number do not match your records. Please check your details.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check the status and flight date
            checkPst.setString(1, pnrNumber);
            checkPst.setString(2, ticketNumber);
            ResultSet rs = checkPst.executeQuery();

            if (rs.next()) {
                boolean isCanceled = rs.getBoolean("status");
                Date flightDate = rs.getDate("travel_date");

                // Check if the ticket is canceled
                if (isCanceled) {
                    JOptionPane.showMessageDialog(null, "This ticket has been cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if the current date is the flight date
                LocalDate currentDate = LocalDate.now();
                LocalDate flightLocalDate = flightDate.toLocalDate();
                if (!currentDate.equals(flightLocalDate)) {
                    JOptionPane.showMessageDialog(null, "You can check in on the day of your flight (" + flightLocalDate + ")", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Proceed with check-in
                updatePst.setString(1, pnrNumber);
                updatePst.setString(2, ticketNumber);
                int rowsAffected = updatePst.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Check-in successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to check in. Please enter correct PNR and ticket details.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Failed to check in. Please enter correct PNR and ticket details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while checking in.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        Conn conn = new Conn();  // Assuming your Conn class has a default constructor
        SwingUtilities.invokeLater(() -> {
            CheckIn checkInFrame = new CheckIn(conn, username);
            checkInFrame.setVisible(true);
        });
    }
}
