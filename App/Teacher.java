import java.util.ArrayList;
import java.util.List;

class Teacher extends Account {
    private List<Course> coursesTeaching;
    
    public Teacher(int id, String email, String name, String password) {
        super(id, email, name, password);
        this.coursesTeaching = new ArrayList<>();
    }

    // Adds a course to the teacher's list of courses
    public void addCourseTeaching(Course course) {
        coursesTeaching.add(course);
    }

    // Returns the list of courses the teacher is teaching
    public List<Course> getCoursesTeaching() {
        return new ArrayList<>(coursesTeaching);
    }
}