import java.awt.*;
import java.util.List;
import javax.swing.*;

class StudentPanel extends JFrame {
    private Student currentStudent; // Add this field
    private DefaultListModel<String> currentListModel;
    private DefaultListModel<String> pastListModel;

    // Creates the student panel
    public StudentPanel(Student student) {
        this.currentStudent = student;
        setTitle("Welcome, " + student.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a panel for buttons on the left
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create buttons
        JButton addClassButton = new JButton("Add Class");
        JButton dropClassButton = new JButton("Drop Class"); 
        JButton checkPrereqButton = new JButton("Check Prerequisites");
        JButton viewInfoButton = new JButton("View Student Information");
        JButton waitlistInfoButton = new JButton("View Waitlist Info");
        JButton searchButton = new JButton("Search Course");

        // Set button sizes
        Dimension buttonSize = new Dimension(200, 50);  // Make buttons wider and taller
        addClassButton.setPreferredSize(buttonSize);
        dropClassButton.setPreferredSize(buttonSize);
        checkPrereqButton.setPreferredSize(buttonSize);
        viewInfoButton.setPreferredSize(buttonSize);
        waitlistInfoButton.setPreferredSize(buttonSize);
        searchButton.setPreferredSize(buttonSize);

        // Set minimum sizes to maintain button size
        addClassButton.setMinimumSize(buttonSize);
        dropClassButton.setMinimumSize(buttonSize);
        checkPrereqButton.setMinimumSize(buttonSize);
        viewInfoButton.setMinimumSize(buttonSize);
        waitlistInfoButton.setMinimumSize(buttonSize);
        searchButton.setMinimumSize(buttonSize);

        // Set maximum sizes to maintain button size
        addClassButton.setMaximumSize(buttonSize);
        dropClassButton.setMaximumSize(buttonSize);
        checkPrereqButton.setMaximumSize(buttonSize);
        viewInfoButton.setMaximumSize(buttonSize);
        waitlistInfoButton.setMaximumSize(buttonSize);
        searchButton.setMaximumSize(buttonSize);

        // Add spacing between buttons
        addClassButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropClassButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkPrereqButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewInfoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        waitlistInfoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add buttons to panel with spacing
        buttonPanel.add(addClassButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(dropClassButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(checkPrereqButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(viewInfoButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(waitlistInfoButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(searchButton);

        // Create enrolled courses panel for right side
        JPanel coursesPanel = new JPanel(new GridLayout(1, 2, 10, 0));  // 1 row, 2 columns, 10px gap
        coursesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Left table - Current Enrollments
        JPanel currentPanel = new JPanel(new BorderLayout());
        JLabel currentTitle = new JLabel("Current Enrollments");
        currentTitle.setFont(new Font("Arial", Font.BOLD, 16));
        currentTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        currentListModel = new DefaultListModel<>();
        JList<String> currentList = new JList<>(currentListModel);
        currentList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane currentScrollPane = new JScrollPane(currentList);
        
        currentPanel.add(currentTitle, BorderLayout.NORTH);
        currentPanel.add(currentScrollPane, BorderLayout.CENTER);
        
        // Right table - Past Courses
        JPanel pastPanel = new JPanel(new BorderLayout());
        JLabel pastTitle = new JLabel("Past Courses");
        pastTitle.setFont(new Font("Arial", Font.BOLD, 16));
        pastTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        pastListModel = new DefaultListModel<>();
        JList<String> pastList = new JList<>(pastListModel);
        pastList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane pastScrollPane = new JScrollPane(pastList);
        
        pastPanel.add(pastTitle, BorderLayout.NORTH);
        pastPanel.add(pastScrollPane, BorderLayout.CENTER);
        
        // Add both panels to courses panel
        coursesPanel.add(currentPanel);
        coursesPanel.add(pastPanel);

        // Update the lists
        updateCourseLists(currentListModel, pastListModel);

        // Add action listeners
        addClassButton.addActionListener(e -> {
            // Create course selection dialog
            JDialog addCourseDialog = new JDialog(this, "Add Course", true);
            addCourseDialog.setSize(400, 300);
            addCourseDialog.setLocationRelativeTo(this);

            // Create course list
            DefaultListModel<String> addCourseListModel = new DefaultListModel<>();
            for (Course course : Course.getAllCourses()) {
                if (!currentStudent.isEnrolledIn(course)) {
                    addCourseListModel.addElement(course.getClassName());
                }
            }
            JList<String> addCourseList = new JList<>(addCourseListModel);
            JScrollPane addScrollPane = new JScrollPane(addCourseList);

            JButton addButton = new JButton("Add Selected Course");
            addButton.addActionListener(event -> {
                String selectedCourse = addCourseList.getSelectedValue();
                if (selectedCourse != null) {
                    Course course = Course.getCourse(selectedCourse);
                    if (course.isFull()) {
                        JOptionPane.showMessageDialog(addCourseDialog,
                            "Course is full: " + selectedCourse,
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (course.hasCompletedPrerequisites(currentStudent)) {
                        try {
                            currentStudent.enrollInCourse(course);
                            updateLoginFile(currentStudent);
                            updateCourseLists(currentListModel, pastListModel);
                            JOptionPane.showMessageDialog(addCourseDialog,
                                "Successfully enrolled in " + selectedCourse,
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                            addCourseDialog.dispose();
                        } catch (IllegalStateException ex) {
                            JOptionPane.showMessageDialog(addCourseDialog,
                                ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(addCourseDialog,
                            "Prerequisites not met for " + selectedCourse,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            addCourseDialog.setLayout(new BorderLayout());
            addCourseDialog.add(addScrollPane, BorderLayout.CENTER);
            addCourseDialog.add(addButton, BorderLayout.SOUTH);
            addCourseDialog.setVisible(true);
        });

        dropClassButton.addActionListener(e -> {
            // Create course selection dialog
            JDialog dropCourseDialog = new JDialog(this, "Drop Course", true);
            dropCourseDialog.setSize(400, 300);
            dropCourseDialog.setLocationRelativeTo(this);

            // Create course list
            DefaultListModel<String> dropCourseListModel = new DefaultListModel<>();
            for (Course course : currentStudent.getEnrolledCourses()) {
                dropCourseListModel.addElement(course.getClassName());
            }
            JList<String> dropCourseList = new JList<>(dropCourseListModel);
            JScrollPane dropScrollPane = new JScrollPane(dropCourseList);

            JButton dropButton = new JButton("Drop Selected Course");
            dropButton.addActionListener(event -> {
                String selectedCourse = dropCourseList.getSelectedValue();
                if (selectedCourse != null) {
                    Course course = Course.getCourse(selectedCourse);
                    if (course != null) {
                        // Remove from student's enrolled courses
                        currentStudent.dropCourse(course);
                        
                        // Update the file
                        updateLoginFile(currentStudent);
                        
                        // Reload data
                        Course.initializeCourses();
                        SDSU_App.loadStudentsIntoCourses();
                        
                        // Update UI
                        updateCourseLists(currentListModel, pastListModel);
                        dropCourseListModel.removeElement(selectedCourse);
                        
                        JOptionPane.showMessageDialog(dropCourseDialog,
                            "Successfully dropped " + selectedCourse,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                            
                        if (dropCourseListModel.isEmpty()) {
                            dropCourseDialog.dispose();
                        }
                    }
                }
            });

            dropCourseDialog.setLayout(new BorderLayout());
            dropCourseDialog.add(dropScrollPane, BorderLayout.CENTER);
            dropCourseDialog.add(dropButton, BorderLayout.SOUTH);
            dropCourseDialog.setVisible(true);
        });

        checkPrereqButton.addActionListener(e -> {
            new PrerequisiteVisualizer();
        });

        waitlistInfoButton.addActionListener(e -> {
            // Create course selection dialog for waitlist
            DefaultListModel<String> waitlistListModel = new DefaultListModel<>();
            for (Course course : Course.getAllCourses()) {
                if (course.getWaitlistSize() > 0) {
                    waitlistListModel.addElement(course.getClassName() + 
                        " (Waitlist: " + course.getWaitlistSize() + ")");
                }
            }

            JList<String> waitlistList = new JList<>(waitlistListModel);
            JScrollPane waitlistScrollPane = new JScrollPane(waitlistList);

            waitlistList.addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting()) {
                    String selected = waitlistList.getSelectedValue();
                    if (selected != null) {
                        String courseName = selected.split(" \\(")[0];
                        Course course = Course.getCourse(courseName);
                        showWaitlistInfo(course);
                    }
                }
            });

            JDialog waitlistDialog = new JDialog(this, "View Waitlist Info", true);
            waitlistDialog.setSize(400, 300);
            waitlistDialog.setLocationRelativeTo(this);

            waitlistDialog.add(waitlistScrollPane);
            waitlistDialog.setVisible(true);
        });

        viewInfoButton.addActionListener(e -> {
            // Create a formatted string with basic student information
            StringBuilder info = new StringBuilder();
            info.append("Student Information:\n\n");
            info.append("ID: ").append(currentStudent.getId()).append("\n");
            info.append("Name: ").append(currentStudent.getName()).append("\n");
            info.append("Email: ").append(currentStudent.getEmail()).append("\n");
            info.append("GPA: ").append(String.format("%.2f", currentStudent.calculateGPA())).append("\n");

            // Show the information in a dialog
            JOptionPane.showMessageDialog(StudentPanel.this,
                info.toString(),
                "Student Information",
                JOptionPane.INFORMATION_MESSAGE);
        });

        searchButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                this,
                "Enter Course ID:",
                "Search Course",
                JOptionPane.QUESTION_MESSAGE
            );
            
            try {
                int courseId = Integer.parseInt(input);
                Course found = Course.searchById(courseId);
                
                if (found != null) {
                    JOptionPane.showMessageDialog(
                        this,
                        String.format("Found: %s\nID: %d\nUnits: %d",
                            found.getClassName(),
                            found.getCourseID(),
                            found.getUnits()),
                        "Course Found",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Course not found",
                        "Search Result",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid course ID",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // Create prerequisite visualizer panel
        JPanel visualizerPanel = new JPanel();
        visualizerPanel.setPreferredSize(new Dimension(800, 800));

        // Create logo panel for bottom
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ImageIcon logoIcon = new ImageIcon("map.jpg");
        Image scaledImage = logoIcon.getImage().getScaledInstance(900, 600, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoPanel.add(logoLabel);
        
        // Add panels to frame
        add(buttonPanel, BorderLayout.WEST);
        add(visualizerPanel, BorderLayout.CENTER);
        add(coursesPanel, BorderLayout.EAST);
        add(logoPanel, BorderLayout.SOUTH);  // Add logo panel to bottom

        setVisible(true);
    }

    // Helper method to update course list
    private void updateCourseList(DefaultListModel<String> model) {
        model.clear();
        double gpa = currentStudent.calculateGPA();
        
        // Show GPA at the top
        model.addElement(String.format("Current GPA: %.2f", gpa));
        model.addElement("");
        
        // Show completed courses first
        List<Course> completedCourses = currentStudent.getCoursesTaken();
        if (!completedCourses.isEmpty()) {
            model.addElement("Completed Courses:");
            model.addElement("------------------------");
            for (Course course : completedCourses) {
                String grade = currentStudent.getGrade(course);
                model.addElement(String.format("%s - Final Grade: %s", 
                    course.getClassName(), grade));
            }
        }
        
        // Add spacing between sections
        model.addElement("");
        
        // Show current enrollments
        List<Course> currentCourses = currentStudent.getEnrolledCourses();
        if (!currentCourses.isEmpty()) {
            model.addElement("Currently Enrolled Courses:");
            model.addElement("------------------------");
            for (Course course : currentCourses) {
                model.addElement(String.format("%s - In Progress", 
                    course.getClassName()));
            }
        }
        
        if (currentCourses.isEmpty() && completedCourses.isEmpty()) {
            model.addElement("No courses enrolled or completed");
        }
    }

    private void updateLoginFile(Student student) {
        student.writeToFile();
    }

    // Add method to show waitlist information
    private void showWaitlistInfo(Course course) {
        StringBuilder info = new StringBuilder();
        info.append("Course: ").append(course.getClassName()).append("\n");
        info.append("Current Waitlist Size: ").append(course.getWaitlistSize()).append("\n");
        
        if (course.isOnWaitlist(currentStudent)) {
            info.append("Your Position: ").append(course.getWaitlistPosition(currentStudent));
        }

        JOptionPane.showMessageDialog(this,
            info.toString(),
            "Waitlist Information",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Converts the grade to a letter
    private String numberToLetter(double grade) {
        if (grade >= 4.0) return "A";
        if (grade >= 3.7) return "A-";
        if (grade >= 3.3) return "B+";
        if (grade >= 3.0) return "B";
        if (grade >= 2.7) return "B-";
        if (grade >= 2.3) return "C+";
        if (grade >= 2.0) return "C";
        if (grade >= 1.7) return "C-";
        if (grade >= 1.3) return "D+";
        if (grade >= 1.0) return "D";
        if (grade >= 0.7) return "D-";
        return "F";
    }

    private void updateCourseLists(DefaultListModel<String> currentModel, DefaultListModel<String> pastModel) {
        currentModel.clear();
        pastModel.clear();
        
        // Add completed courses (from brackets)
        System.out.println("Displaying completed courses:");  // Debug print
        List<Course> completedCourses = currentStudent.getCoursesTaken();
        if (!completedCourses.isEmpty()) {
            for (Course course : completedCourses) {
                String grade = currentStudent.getGrade(course);
                String display = String.format("%s - Grade: %s", course.getClassName(), grade);
                System.out.println(display);  // Debug print
                pastModel.addElement(display);
            }
        } else {
            pastModel.addElement("No completed courses");
        }
        
        // Add current enrollments (from parentheses)
        System.out.println("Displaying current enrollments:");  // Debug print
        List<Course> currentCourses = currentStudent.getEnrolledCourses();
        if (!currentCourses.isEmpty()) {
            for (Course course : currentCourses) {
                String display = String.format("%s - In Progress", course.getClassName());
                System.out.println(display);  // Debug print
                currentModel.addElement(display);
            }
        } else {
            currentModel.addElement("No current enrollments");
        }
    }
}