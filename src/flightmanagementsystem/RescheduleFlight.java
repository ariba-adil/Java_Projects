package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

public class RescheduleFlight extends JFrame {
    private JTextField pnrField, flightCodeField, flightNameField, sourceField, destinationField, travelDateField;
    private JButton fetchButton, updateButton;
    private Conn conn;
    private String username;

    public RescheduleFlight(String username) {
        this.username = username; // Store the username
        conn = new Conn();
        setTitle("Reschedule Flight");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel pnrLabel = new JLabel("Enter PNR Number:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(pnrLabel, gbc);

        pnrField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(pnrField, gbc);

        fetchButton = new JButton("Fetch Details");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(fetchButton, gbc);

        JLabel flightCodeLabel = new JLabel("Flight Code:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(flightCodeLabel, gbc);

        flightCodeField = new JTextField(20);
        flightCodeField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(flightCodeField, gbc);

        JLabel flightNameLabel = new JLabel("Flight Name:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(flightNameLabel, gbc);

        flightNameField = new JTextField(20);
        flightNameField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(flightNameField, gbc);

        JLabel sourceLabel = new JLabel("Source:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(sourceLabel, gbc);

        sourceField = new JTextField(20);
        sourceField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sourceField, gbc);

        JLabel destinationLabel = new JLabel("Destination:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(destinationLabel, gbc);

        destinationField = new JTextField(20);
        destinationField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(destinationField, gbc);

        JLabel travelDateLabel = new JLabel("Travel Date:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(travelDateLabel, gbc);

        travelDateField = new JTextField(20);
        travelDateField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(travelDateField, gbc);

        updateButton = new JButton("Update Travel Date");
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(updateButton, gbc);

        fetchButton.addActionListener(e -> {
            String pnrNumber = pnrField.getText().trim();
            if (!pnrNumber.isEmpty()) {
                fetchFlightDetails(pnrNumber);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a PNR number.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            String pnrNumber = pnrField.getText().trim();
            String newDateStr = JOptionPane.showInputDialog(this, "Enter new travel date (YYYY-MM-DD):");
            if (newDateStr != null && !newDateStr.trim().isEmpty()) {
                try {
                    LocalDate newDate = LocalDate.parse(newDateStr);
                    LocalDate currentDate = LocalDate.now();

                    if (newDate.isBefore(currentDate)) {
                        JOptionPane.showMessageDialog(this, "Cannot reschedule to a past date.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        updateTravelDate(pnrNumber, Date.valueOf(newDate));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(panel);
    }

    private void fetchFlightDetails(String pnrNumber) {
        String query = "SELECT flightcode, flightname, src, des, travel_date, status, username FROM reservation WHERE pnr = ?";
        try (PreparedStatement pst = conn.c.prepareStatement(query)) {
            pst.setString(1, pnrNumber);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    boolean isCancelled = rs.getBoolean("status");
                    String reservationUsername = rs.getString("username");
                    
                    if (!username.equals(reservationUsername)) {
                        JOptionPane.showMessageDialog(this, "You can only reschedule your own flights.", "Error", JOptionPane.ERROR_MESSAGE);
                        clearFields();
                        return;
                    }

                    if (isCancelled) {
                        JOptionPane.showMessageDialog(this, "This ticket has been cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                        clearFields();
                    } else {
                        flightCodeField.setText(rs.getString("flightcode"));
                        flightNameField.setText(rs.getString("flightname"));
                        sourceField.setText(rs.getString("src"));
                        destinationField.setText(rs.getString("des"));
                        travelDateField.setText(rs.getDate("travel_date").toString());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No reservation found with the provided PNR number.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching flight details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTravelDate(String pnrNumber, Date newDate) {
        String updateQuery = "UPDATE reservation SET travel_date = ? WHERE pnr = ?";
        try (PreparedStatement pst = conn.c.prepareStatement(updateQuery)) {
            pst.setDate(1, newDate);
            pst.setString(2, pnrNumber);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Travel date updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                fetchFlightDetails(pnrNumber);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update travel date.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating travel date.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        flightCodeField.setText("");
        flightNameField.setText("");
        sourceField.setText("");
        destinationField.setText("");
        travelDateField.setText("");
    }

    public static void main(String[] args) {
        String username = "your_username"; // Replace with actual username
        SwingUtilities.invokeLater(() -> {
            new RescheduleFlight(username).setVisible(true);
        });
    }
}
