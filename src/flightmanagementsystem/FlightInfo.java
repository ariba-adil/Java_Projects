package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.proteanit.sql.DbUtils;

public class FlightInfo extends JFrame implements ActionListener {

    private JTextField tfSource;
    private JTextField tfDestination;
    private JButton btnSearch;
    private JTable table;

    public FlightInfo() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblSource = new JLabel("Source:");
        lblSource.setBounds(20, 20, 100, 30);
        add(lblSource);

        tfSource = new JTextField();
        tfSource.setBounds(120, 20, 150, 30);
        add(tfSource);

        JLabel lblDestination = new JLabel("Destination:");
        lblDestination.setBounds(290, 20, 100, 30);
        add(lblDestination);

        tfDestination = new JTextField();
        tfDestination.setBounds(400, 20, 150, 30);
        add(tfDestination);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(570, 20, 100, 30);
        btnSearch.addActionListener(this);
        add(btnSearch);

        table = new JTable();
        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(0, 70, 800, 400);
        add(jsp);

        setSize(800, 500);
        setLocation(280, 150);
        setVisible(true);

        loadTableData(null, null);  // Load all data initially
    }

    private void loadTableData(String source, String destination) {
        try {
            Conn conn = new Conn();
            StringBuilder query = new StringBuilder("SELECT flight_number, arrival_airport, departure_airport, status FROM flight_details WHERE 1=1");
            if (source != null && !source.isEmpty()) {
                query.append(" AND arrival_airport = ?");
            }
            if (destination != null && !destination.isEmpty()) {
                query.append(" AND departure_airport = ?");
            }

            PreparedStatement pst = conn.c.prepareStatement(query.toString());
            int paramIndex = 1;
            if (source != null && !source.isEmpty()) {
                pst.setString(paramIndex++, source);
            }
            if (destination != null && !destination.isEmpty()) {
                pst.setString(paramIndex++, destination);
            }

            ResultSet rs = pst.executeQuery();
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnSearch) {
            try {
                String source = tfSource.getText();
                String destination = tfDestination.getText();
                loadTableData(source, destination);  // Load data based on search criteria
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error processing search: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FlightInfo::new);
    }
}
