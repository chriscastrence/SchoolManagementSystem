import java.io.*;
import javax.swing.*;

public class SDSU_App {
    public static void main(String[] args) {
        Course.initializeCourses();  
        System.out.println("Courses initialized"); 
        loadStudentsIntoCourses();   
        System.out.println("Students loaded"); 
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }

    // Loads the students into the courses
    public static void loadStudentsIntoCourses() {
        File file = new File("final_project_cs210/Data.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7 && parts[4].equals("student")) {
                    int id = Integer.parseInt(parts[3]);
                    Student student = new Student(id, parts[0], parts[2], parts[1]);
                    
                    // tokenizes the completed courses
                    String completedStr = parts[5].substring(1, parts[5].length() - 1);
                    if (!completedStr.isEmpty()) {
                        for (String courseInfo : completedStr.split(";")) {
                            String[] courseData = courseInfo.split(":");
                            String courseId = courseData[0].trim();
                            Course course;
                            if (courseId.startsWith("8")) {
                                String baseId = courseId.substring(1);
                                course = Course.getCourse("CS " + baseId + "L");
                            } else {
                                course = Course.getCourse("CS " + courseId);
                            }
                            if (course != null && courseData.length > 1) {
                                student.addCompletedCourse(course, courseData[1].trim());
                            }
                        }
                    }
                    
                    // tokenizes the current enrollments
                    String currentStr = parts[6].substring(1, parts[6].length() - 1);
                    if (!currentStr.isEmpty()) {
                        String[] currentCourses = currentStr.split(";");
                        for (String courseId : currentCourses) {
                            Course course = Course.getCourse("CS " + courseId.trim());
                            if (course != null) {
                                student.enrollInCourse(course);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}








