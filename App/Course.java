import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Course {
    private static final int MAX_CAPACITY = 30;
    private int courseID;
    private String className;
    private HashMap<Integer, Student> enrolledStudents;
    private List<Course> prerequisites;
    private Queue<Student> waitlist;
    private int units;
    private static HashMap<String, Course> allCourses = new HashMap<>();
    private static CourseSearchTree courseTree = new CourseSearchTree();

    // Constructor
    public Course(int courseID, String className, int units) {
        this.courseID = courseID;
        this.className = className;
        this.units = units;
        this.enrolledStudents = new HashMap<>();
        this.prerequisites = new ArrayList<>();
        this.waitlist = new LinkedList<>();
    }

    public int getCourseID() {
        return courseID;
    }

    public String getClassName() {
        return className;
    }

    public List<Course> getPrerequisites() {
        return new ArrayList<>(prerequisites);
    }

    public HashMap<Integer, Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public static Collection<Course> getAllCourses() {
        return allCourses.values();
    }

    public int getUnits() {
        return units;
    }

    public int getWaitlistSize() {
        return waitlist.size();
    }

    // Get the position of a student on the waitlist
    public int getWaitlistPosition(Student student) {
        int position = 1;
        for (Student s : waitlist) {
            if (s.getId() == student.getId()) {
                return position;
            }
            position++;
        }
        return -1;
    }

    // Create course objects
    public static void initializeCourses() {
        createCourse("CS 150", 150, 3);
        createCourse("CS 150L", 8150, 1);
        createCourse("CS 160", 160, 3);
        createCourse("CS 160L", 8160, 1);
        createCourse("CS 210", 210, 3);
        createCourse("CS 240", 240, 3);
        createCourse("CS 250", 250, 3);
        createCourse("MATH 141", 141, 3);
        createCourse("MATH 150", 150, 4);
        createCourse("MATH 151", 151, 4);
        createCourse("MATH 245", 245, 3);
        createCourse("MATH 254", 254, 3);
        createCourse("PHYS 195", 195, 3);
        createCourse("PHYS 195L", 8195, 1);
        createCourse("PHYS 196", 196, 3);
        createCourse("PHYS 196L", 8196, 1);
        createCourse("STAT 250", 2501, 3);
        createCourse("STAT 550", 550, 3);
        createCourse("CS 370", 370, 3);
        createCourse("CS 450", 450, 3);
        createCourse("CS 460", 460, 3);
        createCourse("CS 480", 480, 3);
        createCourse("CS 420", 420, 3);

        // Add prerequisites
        addPrereq("CS 160", "CS 150", "CS 150L");
        addPrereq("CS 160L", "CS 150", "CS 150L");
        addPrereq("CS 210", "CS 160", "CS 160L");
        addPrereq("CS 240", "CS 160", "CS 160L");
        addPrereq("CS 250", "CS 240");
        addPrereq("MATH 150", "MATH 141");
        addPrereq("MATH 151", "MATH 150");
        addPrereq("MATH 245", "MATH 150");
        addPrereq("MATH 254", "MATH 151");
        addPrereq("PHYS 195", "MATH 150");
        addPrereq("PHYS 195L", "MATH 150");
        addPrereq("PHYS 196", "PHYS 195", "MATH 151");
        addPrereq("CS 370", "CS 240");
        addPrereq("CS 450", "CS 210", "MATH 254", "STAT 250");
        addPrereq("CS 460", "CS 210", "MATH 245");
        addPrereq("CS 480", "CS 210", "CS 370");
        addPrereq("CS 420", "CS 210");

       
        for (Course course : allCourses.values()) {
            courseTree.insert(course);
        }
    }

    // Create a course with the given name, id, and units
    private static void createCourse(String name, int id, int units) {
        allCourses.put(name, new Course(id, name, units));
    }

    // Add prerequisites to a course
    private static void addPrereq(String course, String... prereqs) {
        Course mainCourse = allCourses.get(course);
        for (String prereq : prereqs) {
            mainCourse.addPrerequisite(allCourses.get(prereq));
        }
    }

    // Add a prerequisite to a course
    public void addPrerequisite(Course course) {
        prerequisites.add(course);
    }

    // Remove a prerequisite from a course
    public void removePrerequisite(Course course) {
        prerequisites.remove(course);
    }

    // Check if a course has a prerequisite
    public boolean hasPrerequisite(Course course) {
        return prerequisites.contains(course);
    }

    // Check if a student has completed all prerequisites for a course
    public boolean hasCompletedPrerequisites(Student student) {
        for (Course prerequisite : prerequisites) {
            List<Course> completedCourses = student.getCoursesTaken();
            if (!completedCourses.contains(prerequisite)) {
                return false;
            }
        }
        return true;
    }

    // Check if a course is full
    public boolean isFull() {
        return enrolledStudents.size() >= MAX_CAPACITY;
    }

    // Add a student to a course
    public void addStudent(Student student) {
        if (enrolledStudents.size() >= MAX_CAPACITY) {
            waitlist.offer(student);
            JOptionPane.showMessageDialog(null, 
                "Course is full. You have been added to the waitlist.",
                "Waitlist", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            enrolledStudents.put(student.getId(), student);
        }
    }

    // Remove a student from a course
    public void removeStudent(int studentId) {
        enrolledStudents.remove(studentId);
        
        if (!waitlist.isEmpty() && enrolledStudents.size() < MAX_CAPACITY) {
            Student nextStudent = waitlist.poll();
            enrolledStudents.put(nextStudent.getId(), nextStudent);
        }
    }

    // Check if a student is on the waitlist
        public boolean isOnWaitlist(Student student) {
        return waitlist.contains(student);
    }

    // Get a course by its name
    public static Course getCourse(String name) {
        return allCourses.get(name);
    }

    // Search for a course by its id
    public static Course searchById(int courseId) {
        return courseTree.search(courseId);
    }

    
    // Display all courses in order
    public static void displayInOrder(JTextArea textArea) {
        courseTree.inorderTraversal(course -> 
            textArea.append(course.getClassName() + "\n"));
    }
}
