package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JourneyDetails extends JFrame implements ActionListener {
    JTable table;
    JTextField pnr;
    JButton show, save;

    public JourneyDetails() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblpnr = new JLabel("PNR Details");
        lblpnr.setFont(new Font("Tahoma", Font.PLAIN, 18)); // Increased font size
        lblpnr.setBounds(50, 50, 150, 30); // Increased size
        add(lblpnr);

        pnr = new JTextField();
        pnr.setBounds(210, 50, 250, 30);  // Increased width and height
        add(pnr);

        show = new JButton("Show Details");
        show.setBackground(Color.BLACK);
        show.setForeground(Color.WHITE);
        show.setBounds(470, 50, 150, 30);  // Increased size
        show.addActionListener(this);
        add(show);

        save = new JButton("Save Ticket");
        save.setBackground(Color.BLACK);
        save.setForeground(Color.WHITE);
        save.setBounds(630, 50, 150, 30);  // Increased size
        save.addActionListener(this);
        add(save);

        table = new JTable();
        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(10, 100, 980, 500);  // Increased height and width
        jsp.setBackground(Color.WHITE);
        add(jsp);

        setSize(1000, 700); // Adjusted frame size
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == show) {
            String pnrText = pnr.getText();
            if (pnrText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: Enter Input");
                return;
            }
            if (isTicketCancelled(pnrText)) {
                JOptionPane.showMessageDialog(null, "Error: Ticket is canceled");
                return;
            }
            try {
                Conn conn = new Conn();
                String query = "SELECT * FROM reservation WHERE PNR = ?";
                PreparedStatement pstmt = conn.c.prepareStatement(query);
                pstmt.setString(1, pnrText);
                ResultSet rs = pstmt.executeQuery();

                if (!rs.isBeforeFirst()) {
                    JOptionPane.showMessageDialog(null, "No Information Found");
                    return;
                }

                // Remove the 'ddate' and 'status' columns from the result set
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Collect indices of columns to include
                java.util.List<Integer> columnIndices = new java.util.ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    if (!columnName.equalsIgnoreCase("ddate") &&
                        !columnName.equalsIgnoreCase("status")) {
                        columnIndices.add(i);
                    }
                }

                // Create a new TableModel without the 'ddate' and 'status' columns
                DefaultTableModel model = new DefaultTableModel();
                for (int i : columnIndices) {
                    model.addColumn(metaData.getColumnName(i));
                }

                // Format date columns
                SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                while (rs.next()) {
                    Object[] rowData = new Object[columnIndices.size()];
                    for (int i = 0; i < columnIndices.size(); i++) {
                        Object value = rs.getObject(columnIndices.get(i));
                        String columnName = metaData.getColumnName(columnIndices.get(i));

                        if (value instanceof Timestamp && "travel_date".equals(columnName)) {
                            value = dateFormat.format(timestampFormat.parse(value.toString())); // Format the date
                        }
                        rowData[i] = value;
                    }
                    model.addRow(rowData);
                }

                table.setModel(model);
                adjustColumnWidths(); // Adjust column widths after setting the table model
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ae.getSource() == save) {
            String pnrText = pnr.getText();
            
            if (isTicketCancelled(pnrText)) {
                JOptionPane.showMessageDialog(null, "Error: No data Found");
                return;
            }
            saveTicket();
        }
    }

    private boolean isTicketCancelled(String pnr) {
        try {
            Conn conn = new Conn();
            String query = "SELECT status FROM reservation WHERE PNR = ?";
            PreparedStatement pstmt = conn.c.prepareStatement(query);
            pstmt.setString(1, pnr);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("status"); // Assuming 'status' column is boolean
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void adjustColumnWidths() {
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            int maxWidth = 0;
            for (int j = 0; j < table.getRowCount(); j++) {
                Object value = table.getValueAt(j, i);
                int width = (value == null) ? 0 : value.toString().length();
                maxWidth = Math.max(maxWidth, width);
            }
            maxWidth = Math.max(maxWidth, table.getColumnName(i).length());
            column.setPreferredWidth(maxWidth * 10); // Adjust multiplier as needed for padding
        }
    }

    private void saveTicket() {
        try {
            // Get table data
            int rowCount = table.getRowCount();
            int columnCount = table.getColumnCount();
            StringBuilder data = new StringBuilder();
            
            // Define column widths for formatting (adjust as needed)
            int[] columnWidths = new int[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnWidths[i] = table.getColumnName(i).length() + 5; // Add extra space for padding
            }

            // Update column widths based on data
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    Object cellValue = table.getValueAt(i, j);
                    int cellLength = (cellValue != null) ? cellValue.toString().length() : 0;
                    columnWidths[j] = Math.max(columnWidths[j], cellLength + 5); // Add extra space for padding
                }
            }

            // Header
            for (int i = 0; i < columnCount; i++) {
                data.append(String.format("%-" + columnWidths[i] + "s", table.getColumnName(i)));
            }
            data.append("\n");
            
            // Data rows
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    Object cellValue = table.getValueAt(row, col);
                    String cellString = (cellValue != null) ? cellValue.toString() : "";
                    data.append(String.format("%-" + columnWidths[col] + "s", cellString));
                }
                data.append("\n");
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(data.toString());
                JOptionPane.showMessageDialog(null, "Ticket saved successfully as TXT at " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving ticket");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error generating ticket data");
        }
    }

    public static void main(String[] args) {
        new JourneyDetails();
    }
}
