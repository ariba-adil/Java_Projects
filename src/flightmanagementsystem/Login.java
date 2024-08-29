package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends JFrame implements ActionListener {
    JButton login, cancel, signUp;
    JTextField tfusername;
    JPasswordField tfpassword;
    JComboBox<String> roleDropdown;
    
    public Login() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        JLabel lblusername = new JLabel("Username");
        lblusername.setBounds(20, 20, 100, 20);
        add(lblusername);
        
        tfusername = new JTextField();
        tfusername.setBounds(130, 20, 200, 20);
        add(tfusername);
        
        JLabel lblpassword = new JLabel("Password");
        lblpassword.setBounds(20, 60, 100, 20);
        add(lblpassword);
        
        tfpassword = new JPasswordField();
        tfpassword.setBounds(130, 60, 200, 20);
        add(tfpassword);
        
        JLabel lblRole = new JLabel("Login as");
        lblRole.setBounds(20, 100, 100, 20);
        add(lblRole);
        
        // Dropdown for selecting role
        String[] roles = {"USER", "ADMIN"};
        roleDropdown = new JComboBox<>(roles);
        roleDropdown.setBounds(130, 100, 200, 20);
        add(roleDropdown);
        
        login = new JButton("Login");
        login.setBounds(40, 150, 120, 20);
        login.addActionListener(this);
        add(login);
        
        cancel = new JButton("Cancel");
        cancel.setBounds(190, 150, 120, 20);
        cancel.addActionListener(this);
        add(cancel);
        
        signUp = new JButton("Sign Up");
        signUp.setBounds(130, 180, 120, 20);
        signUp.addActionListener(this);
        add(signUp);
        
        setSize(400, 250);
        setLocation(450, 250);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == login) {
            String username = tfusername.getText();
            String password = new String(tfpassword.getPassword());
            String hashedPassword = hashPassword(password);
            String selectedRole = (String) roleDropdown.getSelectedItem();
            
            Connection connection = null;
            PreparedStatement verifyPstmt = null;
            ResultSet rs = null;

            try {
                Conn conn = new Conn();
                connection = conn.c;
                
                // Verify user credentials
                String verifyQuery = "SELECT id, role FROM signup WHERE username = ? AND password = ?";
                verifyPstmt = connection.prepareStatement(verifyQuery);
                verifyPstmt.setString(1, username);
                verifyPstmt.setString(2, hashedPassword);
                
                rs = verifyPstmt.executeQuery();
                
                if (rs.next()) {
                    int passengerId = rs.getInt("id"); // Get the passenger ID from the result set
                    String currentRole = rs.getString("role"); // Get the current role from the result set
                    
                    if (!selectedRole.equals(currentRole)) {
                        // Display an error message if the role does not match
                        JOptionPane.showMessageDialog(null, "Role mismatch. Please select the correct role.", "Role Error", JOptionPane.ERROR_MESSAGE);
                        tfusername.setText("");
                        tfpassword.setText("");
                        return;
                    }

                    // Proceed to Home if the role matches
                    new Home(selectedRole, passengerId,username); // Pass the selected role and passenger ID to the Home class
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
                    tfusername.setText("");
                    tfpassword.setText("");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Close resources
                try {
                    if (rs != null) rs.close();
                    if (verifyPstmt != null) verifyPstmt.close();
                    if (connection != null && !connection.isClosed()) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if (ae.getSource() == cancel) {
            setVisible(false);
        } else if (ae.getSource() == signUp) {
        	setVisible(false);
            new CreateAccountForm(); // Open the Sign Up form
        }
    }

    public static void main(String[] args) {
        new Login();
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
