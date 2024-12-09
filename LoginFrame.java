import java.awt.*;
import java.io.*;
import javax.swing.*;

class LoginFrame extends JFrame {
    private static final String LOGIN_FILE = "final_project_cs210/Data.txt";
    private static final String TEACHER_FILE = "final_project_cs210/TeacherData.txt";

    // Save credentials to the login file
    private void saveCredentials(String email, String password) {
        try (FileWriter fw = new FileWriter(LOGIN_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            Student student = new Student(Student.generateStudentId(), email, email.split("@")[0], password);
            bw.write(String.format("%s,%s,%s,%d,student,[],()%n", 
                student.getEmail(), student.getPassword(), student.getName(), student.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check credentials for login
    private Object checkCredentials(String email, String password) {
        // First try to read student data
        Student student = Student.readFromFile(email);
        if (student != null && student.getPassword().equals(password)) {
            System.out.println("Student login successful: " + email); // Debug print
            return student;
        }

        // If not a student, check for teacher
        File teacherFile = new File(TEACHER_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(teacherFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(email) && parts[1].equals(password)) {
                    int id = Integer.parseInt(parts[3]);
                    Teacher teacher = new Teacher(id, email, parts[2], password);
                    
                    // Add courses from the list
                    String[] courses = parts[5].split(";");
                    for (String courseName : courses) {
                        teacher.addCourseTeaching(Course.getCourse(courseName.trim()));
                    }
                    System.out.println("Teacher login successful: " + email); // Debug print
                    return teacher;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Login failed for: " + email); // Debug statement
        return null;
    }
    
    // Creates the login frame
    public LoginFrame() {
        setTitle("Login to SDSU Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null); 

        // Create UI components
        JLabel logoLabel = new JLabel();
        ImageIcon logo = new ImageIcon("logo.jpg"); 
        logoLabel.setIcon(logo);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 20));

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 20));

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 50));

        // Layout for form
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);

        // Sign up button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setPreferredSize(new Dimension(100, 50));
        formPanel.add(signUpButton);

        // Add components to the main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(logoLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Login button action
        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                System.out.println("Attempting login for: " + username); // Debug print
                
                Object user = checkCredentials(username, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    if (user instanceof Student) {
                        System.out.println("Creating student panel"); // Debug print
                        StudentPanel studentPanel = new StudentPanel((Student) user);
                        studentPanel.setVisible(true);
                        dispose();
                    } else if (user instanceof Teacher) {
                        System.out.println("Creating teacher panel"); // Debug print
                        TeacherPanel teacherPanel = new TeacherPanel((Teacher) user);
                        teacherPanel.setVisible(true);
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();  // This will show us the actual error
                JOptionPane.showMessageDialog(LoginFrame.this,
                    "Error during login: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Sign up button action
        signUpButton.addActionListener(e -> {
            JDialog signupDialog = new JDialog(this, "Sign Up", true);
            signupDialog.setSize(400, 300);
            signupDialog.setLocationRelativeTo(null);
            
            JPanel signupPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            signupPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Form fields
            JTextField emailField = new JTextField();
            JTextField nameField = new JTextField();
            JPasswordField signupPasswordField = new JPasswordField();
            JPasswordField confirmSignupPasswordField = new JPasswordField();
            
            // Add components to panel
            signupPanel.add(new JLabel("Email:"));
            signupPanel.add(emailField);
            signupPanel.add(new JLabel("Full Name:"));
            signupPanel.add(nameField);
            signupPanel.add(new JLabel("Password:"));
            signupPanel.add(signupPasswordField);
            signupPanel.add(new JLabel("Confirm Password:"));
            signupPanel.add(confirmSignupPasswordField);
            
            // Submit button
            JButton submitButton = new JButton("Create Account");
            submitButton.addActionListener(submitEvent -> {
                String email = emailField.getText();
                String name = nameField.getText();
                String password = new String(signupPasswordField.getPassword());
                String confirmPassword = new String(confirmSignupPasswordField.getPassword());
                
                if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(signupDialog,
                        "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!email.toLowerCase().endsWith("@sdsu.edu")) {
                    JOptionPane.showMessageDialog(signupDialog,
                        "Email must end with @sdsu.edu", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(signupDialog,
                        "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create directory if it doesn't exist
                File directory = new File("final_project_cs210");
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                try {
                    // Check for existing email
                    try (BufferedReader br = new BufferedReader(new FileReader(LOGIN_FILE))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] parts = line.split(",");
                            if (parts[0].equals(email)) {
                                JOptionPane.showMessageDialog(signupDialog,
                                    "Email already exists", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }

                    // Save new credentials
                    try (FileWriter fw = new FileWriter(LOGIN_FILE, true);
                         BufferedWriter bw = new BufferedWriter(fw)) {
                        Student student = new Student(Student.generateStudentId(), email, name, password);
                        bw.write(String.format("%s,%s,%s,%d,student,[],()%n", 
                            student.getEmail(), student.getPassword(), student.getName(), student.getId()));
                    }

                    JOptionPane.showMessageDialog(signupDialog,
                        "Sign up successful! You can now log in with your SDSU email.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    signupDialog.dispose();

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(signupDialog,
                        "Error saving credentials: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(submitButton);
            
            signupDialog.add(signupPanel, BorderLayout.CENTER);
            signupDialog.add(buttonPanel, BorderLayout.SOUTH);
            signupDialog.setVisible(true);
        });

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);           
    }
}
