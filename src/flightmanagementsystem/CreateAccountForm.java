package flightmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateAccountForm extends JFrame {

    private JComboBox<String> roleComboBox;
    private JTextField usernameField, nameField;
    private JPasswordField passwordField;
    private JButton createButton, backButton;

    public CreateAccountForm() {
        setTitle("Create Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300); // Increased size
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Role Label and ComboBox
        JLabel roleLabel = new JLabel("Role:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(roleLabel, gbc);

        String[] roles = {"ADMIN" };
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setPreferredSize(new Dimension(300, 30)); // Larger dropdown
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(roleComboBox, gbc);

        // Name Label and TextField
        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setPreferredSize(new Dimension(300, 30)); // Larger text field
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(nameField, gbc);

        // Username Label and TextField
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(300, 30)); // Larger text field
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(usernameField, gbc);

        // Password Label and PasswordField
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(300, 30)); // Larger text field
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(passwordField, gbc);

        // Create and Back Buttons
        createButton = new JButton("Create");
        backButton = new JButton("Back");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createButton);
        buttonPanel.add(backButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners to buttons
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle create button click
                String role = (String) roleComboBox.getSelectedItem();
                String name = nameField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || name.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled out.");
                    return;
                }

                // Hash the password before storing
                String hashedPassword = hashPassword(password);

                // Check if the username already exists
                try {
                    Conn c = new Conn();
                    String checkQuery = "SELECT COUNT(*) FROM signup WHERE username = ?";
                    PreparedStatement checkPstmt = c.c.prepareStatement(checkQuery);
                    checkPstmt.setString(1, username);
                    ResultSet rs = checkPstmt.executeQuery();
                    
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different username.");
                    } else {
                        // Insert data into the database
                        String insertQuery = "INSERT INTO signup (name, username, password, role) VALUES (?, ?, ?, ?)";
                        PreparedStatement insertPstmt = c.c.prepareStatement(insertQuery);
                        insertPstmt.setString(1, name);
                        insertPstmt.setString(2, username);
                        insertPstmt.setString(3, hashedPassword);
                        insertPstmt.setString(4, role);
                        insertPstmt.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Account created successfully.");
                        
                        dispose();
                        new Login().setVisible(true);
                    }
                    
                    checkPstmt.close();
                    c.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle back button click
            	setVisible(false);
                new Login(); // Close the form and open the login form
            }
        });

        setVisible(true);
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

    public static void main(String[] args) {
        new CreateAccountForm();
    }
}
