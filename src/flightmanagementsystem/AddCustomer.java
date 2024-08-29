package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class AddCustomer extends JFrame implements ActionListener {

    JTextField tfname, tfphone, tfaadhar, tfnationality, tfaddress, tfcity, tfusername;
    JPasswordField tfpassword;
    JRadioButton rbmale, rbfemale;
    JComboBox<String> stateDropdown;
    JCheckBox cbresetPassword, cbforgotPassword;

    private static final List<String> STATES = Arrays.asList(
    	    "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", 
    	    "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", 
    	    "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", 
    	    "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", 
    	    "Uttar Pradesh", "Uttarakhand", "West Bengal",
    	    "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu", 
    	    "Lakshadweep", "Delhi", "Puducherry", "Ladakh", "Jammu and Kashmir"
    	);


    public AddCustomer() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel heading = new JLabel("ADD PASSENGER DETAILS");
        heading.setBounds(220, 20, 500, 35);
        heading.setFont(new Font("Tahoma", Font.PLAIN, 32));
        heading.setForeground(Color.BLUE);
        add(heading);

        JLabel lblname = new JLabel("Name");
        lblname.setBounds(60, 80, 150, 25);
        lblname.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblname);

        tfname = new JTextField();
        tfname.setBounds(220, 80, 150, 25);
        add(tfname);

        JLabel lblnationality = new JLabel("Nationality");
        lblnationality.setBounds(60, 130, 150, 25);
        lblnationality.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblnationality);

        tfnationality = new JTextField();
        tfnationality.setBounds(220, 130, 150, 25);
        add(tfnationality);

        JLabel lblaadhar = new JLabel("Aadhar Number");
        lblaadhar.setBounds(60, 180, 150, 25);
        lblaadhar.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblaadhar);

        tfaadhar = new JTextField();
        tfaadhar.setBounds(220, 180, 150, 25);
        add(tfaadhar);

        JLabel lbladdress = new JLabel("Address");
        lbladdress.setBounds(60, 230, 150, 25);
        lbladdress.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lbladdress);

        tfaddress = new JTextField();
        tfaddress.setBounds(220, 230, 150, 25);
        add(tfaddress);

        JLabel lblcity = new JLabel("City");
        lblcity.setBounds(60, 280, 150, 25);
        lblcity.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblcity);

        tfcity = new JTextField();
        tfcity.setBounds(220, 280, 150, 25);
        add(tfcity);

        JLabel lblstate = new JLabel("State");
        lblstate.setBounds(60, 330, 150, 25);
        lblstate.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblstate);

        stateDropdown = new JComboBox<>(STATES.toArray(new String[0]));
        stateDropdown.setBounds(220, 330, 150, 25);
        add(stateDropdown);

        JLabel lblgender = new JLabel("Gender");
        lblgender.setBounds(60, 380, 150, 25);
        lblgender.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblgender);

        ButtonGroup gendergroup = new ButtonGroup();

        rbmale = new JRadioButton("Male");
        rbmale.setBounds(220, 380, 70, 25);
        rbmale.setBackground(Color.WHITE);
        add(rbmale);

        rbfemale = new JRadioButton("Female");
        rbfemale.setBounds(300, 380, 70, 25);
        rbfemale.setBackground(Color.WHITE);
        add(rbfemale);

        gendergroup.add(rbmale);
        gendergroup.add(rbfemale);

        JLabel lblphone = new JLabel("Phone");
        lblphone.setBounds(60, 430, 150, 25);
        lblphone.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblphone);

        tfphone = new JTextField();
        tfphone.setBounds(220, 430, 150, 25);
        add(tfphone);

        JLabel lblusername = new JLabel("Username");
        lblusername.setBounds(60, 480, 150, 25);
        lblusername.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblusername);

        tfusername = new JTextField();
        tfusername.setBounds(220, 480, 150, 25);
        add(tfusername);

        JLabel lblpassword = new JLabel("Password");
        lblpassword.setBounds(60, 530, 150, 25);
        lblpassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
        add(lblpassword);

        tfpassword = new JPasswordField();
        tfpassword.setBounds(220, 530, 150, 25);
        add(tfpassword);

        JButton save = new JButton("SAVE");
        save.setBackground(Color.BLACK);
        save.setForeground(Color.WHITE);
        save.setBounds(220, 580, 150, 30);
        save.addActionListener(this);
        add(save);

        ImageIcon image = new ImageIcon(ClassLoader.getSystemResource("flightmanagementsystem/icons/emp.png"));
        JLabel lblimage = new JLabel(image);
        lblimage.setBounds(450, 80, 280, 400);
        add(lblimage);

        setSize(900, 750);
        setLocation(230, 100);
        setVisible(true);
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // Retrieve form data
        String name = tfname.getText();
        String nationality = tfnationality.getText();
        String phone = tfphone.getText();
        String address = tfaddress.getText();
        String aadhar = tfaadhar.getText();
        String city = tfcity.getText();
        String state = (String) stateDropdown.getSelectedItem();
        String gender = rbmale.isSelected() ? "Male" : "Female";
        String username = tfusername.getText();
        String password = new String(tfpassword.getPassword());
        String role = "USER";

        // Validation
        if (name.isEmpty() || nationality.isEmpty() || phone.isEmpty() || address.isEmpty() ||
            aadhar.isEmpty() || city.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required.");
            return;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(null, "Name should only contain letters.");
            return;
        }
        if (!nationality.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(null, "Nationality should only contain letters.");
            return;
        }
        if (!aadhar.matches("\\d{12}")) {
            JOptionPane.showMessageDialog(null, "Aadhar number should be 12 digits.");
            return;
        }
        if (!city.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(null, "City should only contain letters.");
            return;
        }
        if (!rbmale.isSelected() && !rbfemale.isSelected()) {
            JOptionPane.showMessageDialog(null, "Select Gender.");
            return;
        }
        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(null, "Phone number should be 10 digits.");
            return;
        }

        try {
            Conn conn = new Conn();

            // Check if Aadhar number already exists
            String checkAadharQuery = "SELECT aadhar FROM passenger WHERE aadhar = ?";
            PreparedStatement checkAadharStmt = conn.c.prepareStatement(checkAadharQuery);
            checkAadharStmt.setString(1, aadhar);
            ResultSet rsAadhar = checkAadharStmt.executeQuery();

            if (rsAadhar.next()) {
                JOptionPane.showMessageDialog(null, "User with the mentioned Aadhar number already exists.");
                return;
            }

            // Check if username already exists
            String checkUserQuery = "SELECT username FROM passenger WHERE username = ?";
            PreparedStatement checkUserStmt = conn.c.prepareStatement(checkUserQuery);
            checkUserStmt.setString(1, username);
            ResultSet rsUser = checkUserStmt.executeQuery();

            if (rsUser.next()) {
                JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different username.");
                return;
            }

            // Insert data into the passenger table
            String passengerQuery = "INSERT INTO passenger (name, nationality, aadhar, address, city, state, gender, phone, username, password, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement passengerPst = conn.c.prepareStatement(passengerQuery);
            passengerPst.setString(1, name);
            passengerPst.setString(2, nationality);
            passengerPst.setString(3, aadhar);
            passengerPst.setString(4, address);
            passengerPst.setString(5, city);
            passengerPst.setString(6, state);
            passengerPst.setString(7, gender);
            passengerPst.setString(8, phone);
            passengerPst.setString(9, username);
            passengerPst.setString(10, encryptPassword(password)); // Store encrypted password
            passengerPst.setString(11, role);
            passengerPst.executeUpdate();

            // Insert data into the signup table
            String signupQuery = "INSERT INTO signup (name, username, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement signupPst = conn.c.prepareStatement(signupQuery);
            signupPst.setString(1, name);
            signupPst.setString(2, username);
            signupPst.setString(3, encryptPassword(password)); // Store encrypted password
            signupPst.setString(4, role);
            signupPst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Passenger and Signup information added successfully");

            setVisible(false);
            // new AddCustomer(); // Navigate to login or another relevant action
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to encrypt password (example using Base64, adjust as needed)
    private String encryptPassword1(String password) {
        // Simple Base64 encoding for demonstration purposes
        return Base64.getEncoder().encodeToString(password.getBytes());
    }


    public static void main(String[] args) {
        new AddCustomer();
    }
}
