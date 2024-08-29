package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TravelHistory extends JFrame {
    private JTable reservationTable;
    private String[] columnNames = {"Name", "Aadhar", "Flight Name", "Flight Code", "Travel Date", "Pnr", "Ticket"};
    private Object[][] data;
    private String passengerName;

    public TravelHistory(String passengerName) {
        this.passengerName = passengerName;
        setTitle("Reservation History");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Fetch reservation data
        data = fetchReservations(passengerName);

        // Check if data is retrieved
        if (data.length == 0) {
            JOptionPane.showMessageDialog(null, "No reservation history found for " + passengerName, "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        // Create JTable with reservation data
        reservationTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private Object[][] fetchReservations(String passengerName) {
        System.out.println("Username is: " + passengerName);
        Object[][] reservations = new Object[0][0];
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Conn conn = new Conn();
            connection = conn.c;

            // Query to get reservation data based on passenger name
            String query = "SELECT * FROM reservation WHERE username = ?";
            pstmt = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setString(1, passengerName);
            rs = pstmt.executeQuery();

            // Check if any rows returned
            if (!rs.isBeforeFirst()) {  // This will check if the result set is empty
                System.out.println("No reservations found for user: " + passengerName);
                return reservations;
            }

            // Move cursor to the last row to count rows
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            reservations = new Object[rowCount][columnNames.length];

            int rowIndex = 0;
            while (rs.next()) {
                reservations[rowIndex][0] = rs.getString("name");
                reservations[rowIndex][1] = rs.getString("aadhar");
                reservations[rowIndex][2] = rs.getString("flightname");
                reservations[rowIndex][3] = rs.getString("flightcode");
                reservations[rowIndex][4] = rs.getDate("travel_date");
                reservations[rowIndex][5] = rs.getInt("PNR");
                reservations[rowIndex][6] = rs.getInt("TICKET");
                rowIndex++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching reservation history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return reservations;
    }

    public static void main(String[] args) {
        // For testing
        new TravelHistory("John Doe");
    }
}
