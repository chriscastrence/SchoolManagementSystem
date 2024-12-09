import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class TeacherPanel extends JFrame {
    private Teacher currentTeacher;
    private DefaultListModel<String> courseListModel;
    private JList<String> courseList;

    // Creates the teacher panel
    public TeacherPanel(Teacher teacher) {
        this.currentTeacher = teacher;
        setTitle("Welcome, " + teacher.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create left panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create buttons
        JButton viewCoursesButton = new JButton("View My Courses");
        JButton manageStudentsButton = new JButton("Manage Students");
        JButton assignGradesButton = new JButton("Assign Grades");

        // Set button sizes
        Dimension buttonSize = new Dimension(200, 50);
        viewCoursesButton.setPreferredSize(buttonSize);
        manageStudentsButton.setPreferredSize(buttonSize);
        assignGradesButton.setPreferredSize(buttonSize);

        // Set button alignments
        viewCoursesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        manageStudentsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        assignGradesButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add buttons to panel
        buttonPanel.add(viewCoursesButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(manageStudentsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(assignGradesButton);

        // Create right panel for displaying information
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create course list
        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(courseList);

        infoPanel.add(new JLabel("Courses Teaching:"), BorderLayout.NORTH);
        infoPanel.add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        viewCoursesButton.addActionListener(e -> {
            updateCourseList();
        });

        manageStudentsButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            if (selectedCourse != null) {
                showStudentList(Course.getCourse(selectedCourse));
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a course first", 
                    "No Course Selected", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        assignGradesButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            if (selectedCourse != null) {
                assignGrades(Course.getCourse(selectedCourse));
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a course first", 
                    "No Course Selected", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add panels to frame
        add(buttonPanel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void updateCourseList() {
        courseListModel.clear();
        for (Course course : currentTeacher.getCoursesTeaching()) {
            courseListModel.addElement(course.getClassName());
        }
    }

    private void showStudentList(Course course) {
        JDialog dialog = new JDialog(this, "Students in " + course.getClassName(), true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        DefaultListModel<String> studentListModel = new DefaultListModel<>();
        Map<Integer, Student> enrolledStudents = course.getEnrolledStudents();
        
        if (enrolledStudents.isEmpty()) {
            studentListModel.addElement("No students enrolled in this course");
        } else {
            for (Student student : enrolledStudents.values()) {
                String grade = student.getGrade(course);
                String gradeStr = grade != null ? grade : "No grade";
                studentListModel.addElement(String.format("%s - ID: %d (Grade: %s)", 
                    student.getName(), student.getId(), gradeStr));
            }
        }

        JList<String> studentList = new JList<>(studentListModel);
        studentList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(studentList);
        
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    // Assigns grades to students in a course
    private void assignGrades(Course course) {
        HashMap<Integer, Student> students = course.getEnrolledStudents();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students enrolled in this course.");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));
        HashMap<Student, JComboBox<String>> gradeSelections = new HashMap<>();
        String[] grades = {"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F"};

        for (Student student : students.values()) {
            panel.add(new JLabel(student.getName()));
            JComboBox<String> gradeBox = new JComboBox<>(grades);
            panel.add(gradeBox);
            gradeSelections.put(student, gradeBox);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Assign Grades", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            for (Map.Entry<Student, JComboBox<String>> entry : gradeSelections.entrySet()) {
                Student student = entry.getKey();
                String grade = (String) entry.getValue().getSelectedItem();
                
                // Remove from current enrollment
                student.removeFromCurrentEnrollment(course);
                
                // Add to completed courses with grade
                student.addCompletedCourse(course, grade);
                
                // Update the data file
                student.writeToFile();
            }
            
            JOptionPane.showMessageDialog(this, "Grades have been assigned successfully.");
            showStudentList(course);  // Refresh the student list
        }
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

    // Converts the grade to a number
    private double letterToNumber(String letter) {
        return switch (letter.toUpperCase()) {
            case "A+" -> 4.0;
            case "A" -> 4.0;
            case "A-" -> 3.7;
            case "B+" -> 3.3;
            case "B" -> 3.0;
            case "B-" -> 2.7;
            case "C+" -> 2.3;
            case "C" -> 2.0;
            case "C-" -> 1.7;
            case "D+" -> 1.3;
            case "D" -> 1.0;
            case "D-" -> 0.7;
            case "F" -> 0.0;
            default -> throw new IllegalArgumentException("Invalid grade: " + letter);
        };
    }

    private void updateDataFile() {
        File file = new File("final_project_cs210/Data.txt");
        java.util.List<String> lines = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    int studentId = Integer.parseInt(parts[3]);
                    Student student = null;
                    
                    // Find student in any of teacher's courses
                    for (Course course : currentTeacher.getCoursesTeaching()) {
                        if (course.getEnrolledStudents().containsKey(studentId)) {
                            student = course.getEnrolledStudents().get(studentId);
                            break;
                        }
                    }
                    
                    if (student != null) {
                        // Parse existing courses and grades
                        String coursesStr = parts[5].substring(1, parts[5].length() - 1);
                        Map<String, String> courseGrades = new HashMap<>();
                        
                        // Store existing courses and grades
                        for (String entry : coursesStr.split(";")) {
                            String[] courseData = entry.split(":");
                            courseGrades.put(courseData[0], courseData[1]);
                        }
                        
                        // Update grades for teacher's courses
                        for (Course course : currentTeacher.getCoursesTeaching()) {
                            String courseId = String.valueOf(course.getCourseID());
                            String grade = student.getGrade(course);
                            if (grade != null) {
                                courseGrades.put(courseId, grade);
                            }
                        }
                        
                        // Build the new courses string
                        StringBuilder newCoursesStr = new StringBuilder("[");
                        boolean first = true;
                        for (Map.Entry<String, String> entry : courseGrades.entrySet()) {
                            if (!first) newCoursesStr.append(";");
                            newCoursesStr.append(entry.getKey()).append(":").append(entry.getValue());
                            first = false;
                        }
                        newCoursesStr.append("]");
                        
                        // Create updated line
                        line = String.format("%s,%s,%s,%d,%s,%s",
                            parts[0], parts[1], parts[2], studentId, parts[4], newCoursesStr);
                    }
                }
                lines.add(line);
            }
            
            // Write back to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}