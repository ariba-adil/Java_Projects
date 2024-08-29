package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.sql.*;

public class BoardingPass extends JFrame implements ActionListener {

    private JTextField tfpnr;
    private JLabel tfname, tfnationality, lblsrc, lbldest, labelfname, labelfcode, labeldate;
    private JButton fetchButton, printButton;
    private String username;

    public BoardingPass(String username) {
        System.out.print("Hello USer");

        this.username = username; // Store the username parameter
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        // Heading
        JLabel heading = new JLabel("AIR INDIA");
        heading.setBounds(380, 10, 450, 35);
        heading.setFont(new Font("Tahoma", Font.PLAIN, 32));
        add(heading);

        // Subheading
        JLabel subheading = new JLabel("Boarding Pass");
        subheading.setBounds(360, 50, 300, 30);
        subheading.setFont(new Font("Tahoma", Font.PLAIN, 24));
        subheading.setForeground(Color.BLUE);
        add(subheading);

        // PNR Details label
        JLabel lblaadhar = new JLabel("PNR DETAILS");
        lblaadhar.setBounds(60, 100, 150, 25);
        lblaadhar.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblaadhar);

        // PNR text field
        tfpnr = new JTextField();
        tfpnr.setBounds(220, 100, 150, 25);
        add(tfpnr);

        // Fetch button
        fetchButton = new JButton("Enter");
        fetchButton.setBackground(Color.BLACK);
        fetchButton.setForeground(Color.WHITE);
        fetchButton.setBounds(380, 100, 120, 25);
        fetchButton.addActionListener(this);
        add(fetchButton);

        // Print button
        printButton = new JButton("Print");
        printButton.setBackground(Color.BLACK);
        printButton.setForeground(Color.WHITE);
        printButton.setBounds(550, 100, 120, 25);
        printButton.addActionListener(this);
        add(printButton);

        // Name label
        JLabel lblname = new JLabel("NAME");
        lblname.setBounds(60, 140, 150, 25);
        lblname.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblname);

        // Name value label
        tfname = new JLabel();
        tfname.setBounds(220, 140, 150, 25);
        add(tfname);

        // Nationality label
        JLabel lblnationality = new JLabel("NATIONALITY");
        lblnationality.setBounds(60, 180, 150, 25);
        lblnationality.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblnationality);

        // Nationality value label
        tfnationality = new JLabel();
        tfnationality.setBounds(220, 180, 150, 25);
        add(tfnationality);

        // Source label
        JLabel lbladdress = new JLabel("SRC");
        lbladdress.setBounds(60, 220, 150, 25);
        lbladdress.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lbladdress);

        // Source value label
        lblsrc = new JLabel();
        lblsrc.setBounds(220, 220, 150, 25);
        add(lblsrc);

        // Destination label
        JLabel lblgender = new JLabel("DEST");
        lblgender.setBounds(380, 220, 150, 25);
        lblgender.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblgender);

        // Destination value label
        lbldest = new JLabel();
        lbldest.setBounds(540, 220, 150, 25);
        add(lbldest);

        // Flight Name label
        JLabel lblfname = new JLabel("Flight Name");
        lblfname.setBounds(60, 260, 150, 25);
        lblfname.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblfname);

        // Flight Name value label
        labelfname = new JLabel();
        labelfname.setBounds(220, 260, 150, 25);
        add(labelfname);

        // Flight Code label
        JLabel lblfcode = new JLabel("Flight Code");
        lblfcode.setBounds(380, 260, 150, 25);
        lblfcode.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblfcode);

        // Flight Code value label
        labelfcode = new JLabel();
        labelfcode.setBounds(540, 260, 150, 25);
        add(labelfcode);

        // Date label
        JLabel lbldate = new JLabel("Date");
        lbldate.setBounds(60, 300, 150, 25);
        lbldate.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lbldate);

        // Date value label
        labeldate = new JLabel();
        labeldate.setBounds(220, 300, 150, 25);
        add(labeldate);

        // Image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("flightmanagementsystem/icons/airindia.png"));
        Image i2 = i1.getImage().getScaledInstance(300, 230, Image.SCALE_DEFAULT);
        ImageIcon image = new ImageIcon(i2);
        JLabel lblimage = new JLabel(image);
        lblimage.setBounds(600, 0, 300, 300);
        add(lblimage);

        // Frame properties
        setSize(1000, 450);
        setLocation(150, 150);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String pnr = tfpnr.getText();

        if (ae.getSource() == fetchButton) {
            try {
                Conn conn = new Conn();
                String query = "SELECT * FROM reservation WHERE PNR = ?";
                PreparedStatement pstmt = conn.c.prepareStatement(query);
                pstmt.setString(1, pnr);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Check if the ticket is canceled
                    if (rs.getBoolean("status")) {
                        JOptionPane.showMessageDialog(null, "This ticket has been canceled");
                        // Clear the fields
                        tfname.setText("");
                        tfnationality.setText("");
                        lblsrc.setText("");
                        lbldest.setText("");
                        labelfname.setText("");
                        labelfcode.setText("");
                        labeldate.setText("");
                        return;
                    }

                    // Check if the username matches
                    String ticketUsername = rs.getString("username"); // Assuming 'username' column exists
                    if (!ticketUsername.equals(username)) {
                        JOptionPane.showMessageDialog(null, "You can only fetch your own ticket");
                        return;
                    }

                    // If everything is okay, display the boarding pass details
                    tfname.setText(rs.getString("name"));
                    tfnationality.setText(rs.getString("nationality"));
                    lblsrc.setText(rs.getString("src"));
                    lbldest.setText(rs.getString("des"));
                    labelfname.setText(rs.getString("flightname"));
                    labelfcode.setText(rs.getString("flightcode"));
                    labeldate.setText(rs.getString("travel_date"));
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a correct PNR");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ae.getSource() == printButton) {
            printBoardingPass();
        }
    }


    private void printBoardingPass() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Print the content of the JFrame
                g2d.scale(0.6, 0.6); // Scale down to fit the page if necessary
                printAll(g);

                return PAGE_EXISTS;
            }
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new BoardingPass("username"); // Pass the actual username here
    }
}
