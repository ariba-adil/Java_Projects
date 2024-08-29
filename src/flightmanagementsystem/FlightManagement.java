package flightmanagementsystem;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FlightManagement extends JFrame implements ActionListener {

    private JTextField flightNumberField;
    private JTextField flightCodeField;
    private JTextField sourceField;
    private JTextField destinationField;
    private JComboBox<String> statusField;
    private JButton modifyButton;
    private JButton addButton;
    private JButton deleteButton;
    private JTable flightTable;
    private DefaultTableModel tableModel;

    public FlightManagement() {
        setTitle("Flight Management");
        setLayout(new BorderLayout());

        // Initialize components
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2)); // Adjusted to 5 rows

        JLabel flightNumberLabel = new JLabel("Flight Number:");
        flightNumberField = new JTextField(15);
        inputPanel.add(flightNumberLabel);
        inputPanel.add(flightNumberField);

        JLabel flightCodeLabel = new JLabel("Flight Code:");
        flightCodeField = new JTextField(15);
        inputPanel.add(flightCodeLabel);
        inputPanel.add(flightCodeField);

        JLabel sourceLabel = new JLabel("Source:");
        sourceField = new JTextField(15);
        inputPanel.add(sourceLabel);
        inputPanel.add(sourceField);

        JLabel destinationLabel = new JLabel("Destination:");
        destinationField = new JTextField(15);
        inputPanel.add(destinationLabel);
        inputPanel.add(destinationField);

        JLabel statusLabel = new JLabel("Status:");
        statusField = new JComboBox<>(new String[]{"On Time", "Delayed", "Rescheduled", "Cancelled"});
        inputPanel.add(statusLabel);
        inputPanel.add(statusField);

        modifyButton = new JButton("Modify Flight");
        modifyButton.addActionListener(this);
        addButton = new JButton("Add Flight");
        addButton.addActionListener(this);
        deleteButton = new JButton("Delete Flight");
        deleteButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(modifyButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Table to display flight details
        tableModel = new DefaultTableModel();
        flightTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        tableModel.addColumn("Flight Number");
        tableModel.addColumn("Flight Code");
        tableModel.addColumn("Source");
        tableModel.addColumn("Destination");
        tableModel.addColumn("Status");

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadFlightDetails(); // Load flight details when frame is created

        setupTableSelectionListener(); // Add selection listener to the table

        setSize(600, 300); // Adjusted size
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void loadFlightDetails() {
        try {
            Conn c = new Conn();
            Statement stmt = c.c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT flight_number, flight_ID, arrival_airport, departure_airport, status FROM flight_details");

            // Clear existing data
            tableModel.setRowCount(0);

            while (rs.next()) {
                String flightNumber = rs.getString("flight_number");
                String flightCode = rs.getString("flight_ID");
                String source = rs.getString("arrival_airport");
                String destination = rs.getString("departure_airport");
                String status = rs.getString("status");

                tableModel.addRow(new Object[]{flightNumber, flightCode, source, destination, status});
            }

            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String flightNumber = flightNumberField.getText().trim();
        String flightCode = flightCodeField.getText().trim();
        String source = sourceField.getText().trim();
        String destination = destinationField.getText().trim();
        String status = (String) statusField.getSelectedItem();

        if (ae.getSource() == deleteButton) {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(null, "Please select a flight to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(null, "You want to delete this flight?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String flightNumberToDelete = (String) flightTable.getValueAt(selectedRow, 0);
                deleteFlight(flightNumberToDelete);
            }
        } else {
            if (flightNumber.isEmpty() || flightCode.isEmpty() || source.isEmpty() || destination.isEmpty() || status.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled out.");
                return;
            }

            if (ae.getSource() == modifyButton) {
                int selectedRow = flightTable.getSelectedRow();
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(null, "Please select a flight to update.");
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to modify this flight?", "Confirm Modification", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    modifyFlight(flightNumber, flightCode, source, destination, status);
                }
            } else if (ae.getSource() == addButton) {
                addFlight(flightNumber, flightCode, source, destination, status);
            }
        }
    }

    private void modifyFlight(String flightNumber, String flightCode, String source, String destination, String status) {
        try {
            Conn c = new Conn();
            // Update flight details for the specified flight number
            String query = "UPDATE flight_details SET flight_ID = ?, arrival_airport = ?, departure_airport = ?, status = ? WHERE flight_number = ?";
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, flightCode);
            pstmt.setString(2, source);
            pstmt.setString(3, destination);
            pstmt.setString(4, status);
            pstmt.setString(5, flightNumber);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Flight details updated successfully.");
                loadFlightDetails(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(null, "Flight Number cannot Change.");
            }

            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addFlight(String flightNumber, String flightCode, String source, String destination, String status) {
        try {
            Conn c = new Conn();
            String query = "INSERT INTO flight_details (flight_number, flight_ID, arrival_airport, departure_airport, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, flightNumber);
            pstmt.setString(2, flightCode);
            pstmt.setString(3, source);
            pstmt.setString(4, destination);
            pstmt.setString(5, status);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Flight added successfully.");
            loadFlightDetails(); // Refresh table

            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteFlight(String flightNumber) {
        try {
            Conn c = new Conn();
            String query = "DELETE FROM flight_details WHERE flight_number = ?";
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, flightNumber);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Flight deleted successfully.");
                loadFlightDetails(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(null, "Flight not found.");
            }

            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTableSelectionListener() {
        flightTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow >= 0) {
                flightNumberField.setText((String) flightTable.getValueAt(selectedRow, 0));
                flightCodeField.setText((String) flightTable.getValueAt(selectedRow, 1));
                sourceField.setText((String) flightTable.getValueAt(selectedRow, 2));
                destinationField.setText((String) flightTable.getValueAt(selectedRow, 3));
                statusField.setSelectedItem((String) flightTable.getValueAt(selectedRow, 4));
            }
        });
    }

    public static void main(String[] args) {
        new FlightManagement();
    }
}
