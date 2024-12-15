import java.io.*;
import java.util.*;

class Student extends Account {
    private List<Course> coursesEnrolled;
    private List<Course> coursesTaken;
    private Map<Course, String> courseGrades;
    
    public Student(int id, String email, String name, String password) {
        super(id, email, name, password);
        this.coursesEnrolled = new ArrayList<>();
        this.coursesTaken = new ArrayList<>();
        this.courseGrades = new HashMap<>();
    }
    
    // Add static method for ID generation
    public static int generateStudentId() {
        return 1000000 + (int)(Math.random() * 9000000);
    }
    
    public void enrollInCourse(Course course) {
        if (!coursesEnrolled.contains(course)) {
            coursesEnrolled.add(course);
            course.addStudent(this);
        }
    }
    
    public void removeFromCurrentEnrollment(Course course) {
        coursesEnrolled.remove(course);
        course.removeStudent(getId());
    }
    
    public void addCompletedCourse(Course course, String grade) {
        if (!coursesTaken.contains(course)) {
            coursesTaken.add(course);
            courseGrades.put(course, grade);
        }
    }
    
    public List<Course> getEnrolledCourses() {
        return new ArrayList<>(coursesEnrolled);  // Currently enrolled courses
    }
    
    public List<Course> getCoursesTaken() {
        return new ArrayList<>(coursesTaken);     // Completed courses
    }
    
    // Add method to set a grade for a course
    public void setGrade(Course course, String grade) {
        courseGrades.put(course, grade);
    }
    
    // Add method to get grade for a specific course
    public String getGrade(Course course) {
        return courseGrades.get(course);
    }
    
    // Modify calculateGPA to include both current and past courses
    public double calculateGPA() {
        if (courseGrades.isEmpty()) {
            return 0.0;
        }
        
        double totalPoints = 0.0;
        int totalCourses = 0;
        
        for (String grade : courseGrades.values()) {
            totalPoints += letterToNumber(grade);
            totalCourses++;
        }
        
        return Math.round((totalPoints / totalCourses) * 100.0) / 100.0;
    }

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
            default -> throw new IllegalArgumentException("Invalid grade");
        }
        ;
    }

    public boolean isEnrolledIn(Course course) {
        return coursesEnrolled.contains(course);
    }

    public void dropCourse(Course course) {
        coursesEnrolled.remove(course);
        course.removeStudent(getId());
    }

    // Read student data from file
    public static Student readFromFile(String email) {
        System.out.println("Attempting to read student: " + email);
        try (BufferedReader br = new BufferedReader(new FileReader("final_project_cs210/Data.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Reading line: " + line);
                String[] parts = line.split(",");
                if (parts[0].equals(email)) {
                    System.out.println("Found matching email");
                    int id = Integer.parseInt(parts[3]);
                    Student student = new Student(id, parts[0], parts[2], parts[1]);
                    
                    // tokenizes the completed courses
                    String completedStr = parts[5].substring(1, parts[5].length() - 1);
                    System.out.println("Completed courses string: " + completedStr);
                    
                    if (!completedStr.isEmpty()) {
                        for (String courseInfo : completedStr.split(";")) {
                            String[] courseData = courseInfo.split(":");
                            String courseId = courseData[0].trim();
                            Course course;
                            if (courseId.startsWith("8")) {
                                String baseId = courseId.substring(1);  // Remove the 8
                                course = Course.getCourse("CS " + baseId + "L");
                            } else {
                                course = Course.getCourse("CS " + courseId);
                            }
                            if (course != null) {
                                student.addCompletedCourse(course, courseData[1].trim());
                            }
                        }
                    }
                    
                    // tokenizes the current enrollments
                    if (parts.length > 6) {
                        String currentStr = parts[6].substring(1, parts[6].length() - 1);
                        System.out.println("Current enrollment string: " + currentStr);
                        if (!currentStr.isEmpty()) {
                            // Split by semicolon for multiple courses
                            String[] currentCourses = currentStr.split(";");
                            for (String courseId : currentCourses) {
                                courseId = courseId.trim();
                                Course course;
                                if (courseId.startsWith("8")) {
                                    String baseId = courseId.substring(1);
                                    course = Course.getCourse("CS " + baseId + "L");
                                } else {
                                    course = Course.getCourse("CS " + courseId);
                                }
                                if (course != null) {
                                    student.enrollInCourse(course);
                                }
                            }
                        }
                    }
                    return student;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Student not found");
        return null;
    }

    // Write student data to file
    public void writeToFile() {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("final_project_cs210/Data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(this.getEmail())) {
                    // Build completed courses string
                    StringBuilder completedStr = new StringBuilder("[");
                    List<Course> completedCourses = this.getCoursesTaken();
                    for (int i = 0; i < completedCourses.size(); i++) {
                        if (i > 0) completedStr.append(";");
                        Course course = completedCourses.get(i);
                        String courseId;
                        if (course.getClassName().contains("L")) {
                            String baseId = String.valueOf(course.getCourseID());
                            if (baseId.length() <= 3 && !baseId.startsWith("8")) {
                                courseId = "8" + baseId;
                            } else {
                                courseId = baseId;
                            }
                        } else {
                            courseId = String.valueOf(course.getCourseID());
                        }
                        completedStr.append(courseId)
                                  .append(":")
                                  .append(this.getGrade(course));
                    }
                    completedStr.append("]");
                    
                    // Build current enrollments string
                    StringBuilder currentStr = new StringBuilder("(");
                    List<Course> currentCourses = this.getEnrolledCourses();
                    if (!currentCourses.isEmpty()) {
                        for (int i = 0; i < currentCourses.size(); i++) {
                            if (i > 0) currentStr.append(";");
                            currentStr.append(currentCourses.get(i).getCourseID());
                        }
                    }
                    currentStr.append(")");
                    
                    // Create updated line with both brackets and parentheses
                    line = String.format("%s,%s,%s,%d,%s,%s,%s",
                        this.getEmail(), this.getPassword(), this.getName(), 
                        this.getId(), "student", completedStr, currentStr);
                }
                lines.add(line);
            }

            // Write back to file
            BufferedWriter writer = new BufferedWriter(new FileWriter("final_project_cs210/Data.txt"));
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
