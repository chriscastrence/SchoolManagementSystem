public class CourseSearchTree {
    private class Node {
        Course course;
        Node left;
        Node right;
        
        Node(Course course) {
            this.course = course;
            left = right = null;
        }
    }
    
    private Node root;
    
    public CourseSearchTree() {
        root = null;
    }

    // Insert a course into the tree
    public void insert(Course course) {
        root = insertRec(root, course);
    }

    // Insert a course into the tree recursively
    private Node insertRec(Node root, Course course) {
        if (root == null) {
            return new Node(course);
        }
        
        if (course.getCourseID() < root.course.getCourseID()) {
            root.left = insertRec(root.left, course);
        } else if (course.getCourseID() > root.course.getCourseID()) {
            root.right = insertRec(root.right, course);
        }
        
        return root;
    }
    
    // Search for a course by its id
    public Course search(int courseId) {
        return searchRec(root, courseId);
    }

    // Search for a course by its id recursively
    private Course searchRec(Node root, int courseId) {
        if (root == null || root.course.getCourseID() == courseId) {
            return (root == null) ? null : root.course;
        }
        
        if (courseId < root.course.getCourseID()) {
            return searchRec(root.left, courseId);
        }
        
        return searchRec(root.right, courseId);
    }

    // Inorder traversal of the tree
    public void inorderTraversal(java.util.function.Consumer<Course> action) {
        inorderRec(root, action);
    }
    
    private void inorderRec(Node root, java.util.function.Consumer<Course> action) {
        if (root != null) {
            inorderRec(root.left, action);
            action.accept(root.course);
            inorderRec(root.right, action);
        }
    }
} 