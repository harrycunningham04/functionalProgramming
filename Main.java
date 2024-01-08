import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


interface Enrollable {
    String getName();
}

record Module(String name, int grade) implements Enrollable {
    public String getName() {
        return name;
    }

}

record Course(String name, List<Module> modules) implements Enrollable {

    public String getName() {
        return name;
    }

    public double calculateAverageGrade() {
        if (modules.isEmpty()) {
            return 0.0;
        }
        double totalGrades = modules.stream().mapToInt(Module::grade).sum();
        return totalGrades / modules.size();
    }
}


record Student(int studentNumber, String firstName, String surname, List<Course> enrolledCourses) {
    public String getSurname() {
        return surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public Student updateGrades(String courseName, List<Module> updatedModules) {
        List<Course> updatedCourses = enrolledCourses.stream()
                .map(course -> course.getName().equalsIgnoreCase(courseName) ? new Course(courseName, updatedModules) : course)
                .toList();
        return new Student(studentNumber, firstName, surname, updatedCourses);
    }

    public void displayEnrolledCourses() {
        System.out.println("Student Number: " + studentNumber);
        System.out.println(firstName + " " + surname + "'s enrolled courses:");
        enrolledCourses.forEach(course -> {
            System.out.println("Course: " + course.getName());
            System.out.println("Average Grade: " + course.calculateAverageGrade());
            System.out.println("Modules and Grades:");
            course.modules().forEach(module -> System.out.println("- " + module.getName() + " (Grade: " + module.grade() + ")"));
            System.out.println();
        });
    }

    public double getAverageGradeForCourse(String courseName) {
        return enrolledCourses.stream()
                .filter(course -> course.getName().equalsIgnoreCase(courseName))
                .findFirst()
                .map(course -> Double.parseDouble(String.format("%.2f", course.calculateAverageGrade())))
                .orElse(Double.NaN);
    }

}


class UniversityRegister {
    private static final List<Student> students = new ArrayList<>();
    private static int studentCounter = 1;
    private static final Scanner scanner = new Scanner(System.in);
    private static final String[] FIRST_NAMES = {
            "Alice", "Bob", "Charlie", "David", "Emma", "Frank", "Grace", "Henry", "Ivy", "Jack",
            "Katherine", "Leo", "Mia", "Nathan", "Olivia", "Peter", "Quinn", "Rachel", "Samuel", "Taylor",
            "Ursula", "Victor", "Wendy", "Xander", "Yvonne", "Zane", "Sophia", "Liam", "Ava", "Noah"
    };
    private static final String[] SURNAMES = {
            "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor",
            "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson",
            "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall", "Allen", "Young", "Hernandez", "King"
    };

    public static void main(String[] args) {
        System.out.print("Do you want to generate students? (y/n): ");
        String generateStudentsOption = scanner.nextLine().trim().toLowerCase();


        if (generateStudentsOption.equals("y")) {
            int numberOfStudents = promptNumberOfStudents();
            generateInitialStudents(numberOfStudents);
        }
        while (true) {
            System.out.println("University Register Menu:");
            System.out.println("1. Add a new student");
            System.out.println("2. Search for a student");
            System.out.println("3. Display students on a course");
            System.out.println("4. Remove a student");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1 -> addNewStudent();
                case 2 -> searchStudent();
                case 3 -> displayStudentsOnCourse();
                case 4 -> removeStudent();
                case 5 -> {
                    System.out.println("Exiting program. Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }

    private static int promptNumberOfStudents() {
        System.out.print("How many students would you like to generate? Enter a number: ");
        while (true) {
            try {
                int numberOfStudents = Integer.parseInt(scanner.nextLine());
                if (numberOfStudents > 0) {
                    return numberOfStudents;
                } else {
                    System.out.println("Please enter a positive whole number greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    private static void removeStudent() {
        System.out.print("Enter the student number to remove: ");
        int studentNumberToRemove = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        Iterator<Student> iterator = students.iterator();
        boolean studentFound = false;

        while (iterator.hasNext()) {
            Student student = iterator.next();
            if (student.studentNumber() == studentNumberToRemove) {
                iterator.remove();
                System.out.println("Student removed successfully!");
                studentFound = true;
                break; // Assuming there is only one student with the given student number
            }
        }

        if (!studentFound) {
            System.out.println("No student found with the given student number.");
        }
    }

    private static void generateInitialStudents(int numberOfStudents) {
        Random random = new Random();

        for (int i = 0; i < numberOfStudents; i++) {
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String surname = SURNAMES[random.nextInt(SURNAMES.length)];
            Course selectedCourse = createCourses().get(random.nextInt(4));

            List<Module> modules = selectedCourse.modules().stream()
                    .map(module -> new Module(module.getName(), random.nextInt(46) + 40))
                    .collect(Collectors.toList());

            List<Course> enrolledCourses = List.of(new Course(selectedCourse.getName(), modules));

            students.add(new Student(studentCounter, firstName, surname, enrolledCourses));
            studentCounter++;
        }
    }

    private static void addNewStudent() {
        try {
            System.out.print("Enter student's first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter student's surname: ");
            String surname = scanner.nextLine();

            List<Course> availableCourses = createCourses();

            if (availableCourses.isEmpty()) {
                System.out.println("No available courses to enroll in. Cannot add a new student.");
                return;
            }

            System.out.println("Available Courses:");
            IntStream.range(0, availableCourses.size())
                    .forEach(i -> System.out.println((i + 1) + ". " + availableCourses.get(i).getName()));

            System.out.print("Enter the number of the course to enroll in: ");
            int courseNumber;
            while (true) {
                try {
                    courseNumber = Integer.parseInt(scanner.nextLine());
                    break; // Break the loop if parsing is successful
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                }
            }

            if (courseNumber >= 1 && courseNumber <= availableCourses.size()) {
                Course selectedCourse = availableCourses.get(courseNumber - 1);
                students.add(new Student(studentCounter, firstName, surname, List.of(selectedCourse)));
                System.out.println("Student added successfully to course " + selectedCourse.getName() +
                        "! Student Number: " + studentCounter);
                studentCounter++;
            } else {
                System.out.println("Invalid course number. The student has not been enrolled in any courses.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static List<Course> createCourses() {
        // Sample courses
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Mathematics", List.of(new Module("Algebra", 0), new Module("Calculus", 0), new Module("Statistics", 0))));
        courses.add(new Course("Computer Science", List.of(new Module("Programming", 0), new Module("Data Structures", 0), new Module("Algorithms", 0))));
        courses.add(new Course("Physics", List.of(new Module("Mechanics", 0), new Module("Electromagnetism", 0), new Module("Quantum Physics", 0))));
        courses.add(new Course("Psychology", List.of(new Module("Ethics", 0), new Module("Social Sciences", 0), new Module("Critical Reasoning", 0))));
        return courses;
    }


    private static void searchStudent() {
        System.out.println("Search Options:");
        System.out.println("1. Search by first name");
        System.out.println("2. Search by last name");
        System.out.println("3. Search by student number");
        System.out.print("Enter your search option (1-3): ");

        int searchOption = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        switch (searchOption) {
            case 1 -> searchStudentByFirstName();
            case 2 -> searchStudentByLastName();
            case 3 -> searchStudentByStudentNumber();
            default -> System.out.println("Invalid search option. Please enter a number between 1 and 3.");
        }
    }

    private static void searchStudentByFirstName() {
        System.out.print("Enter student's first name to search: ");
        String searchFirstName = scanner.nextLine().toLowerCase();

        //search to see if what user entered is the name or contained in the name or a single letter that starts the name

        List<Student> foundStudents = students.stream()
                .filter(student -> student.firstName().toLowerCase().contains(searchFirstName)
                        || (searchFirstName.length() == 1 && student.firstName().toLowerCase().startsWith(searchFirstName)))
                .toList();

        displaySearchResults(foundStudents);
    }

    private static void searchStudentByLastName() {
        System.out.print("Enter student's last name to search: ");
        String searchLastName = scanner.nextLine().toLowerCase();


        //search to see if what user entered is the last name or contained in the last name or a single letter that starts the last name

        List<Student> foundStudents = students.stream()
                .filter(student -> student.surname().toLowerCase().contains(searchLastName)
                        || (searchLastName.length() == 1 && student.surname().toLowerCase().startsWith(searchLastName)))
                .toList();

        displaySearchResults(foundStudents);
    }

    private static void searchStudentByStudentNumber() {
        System.out.print("Enter student number to search: ");
        int searchStudentNumber = scanner.nextInt();

        Optional<Student> foundStudent = students.stream()
                .filter(student -> student.studentNumber() == searchStudentNumber)
                .findFirst();

        foundStudent.ifPresentOrElse(
                student -> {
                    student.displayEnrolledCourses();
                    System.out.print("Do you want to enter grades for this student? (y/n): ");
                    String input = scanner.next();
                    if (input.equalsIgnoreCase("y")) {
                        enterGradesForStudent(student);
                    }
                },
                () -> System.out.println("No student found with the given student number.")
        );
    }

    private static void displaySearchResults(List<Student> foundStudents) {
        if (foundStudents.isEmpty()) {
            System.out.println("No students found with the given criteria.");
        } else if (foundStudents.size() == 1) {
            foundStudents.forEach(student -> {
                student.displayEnrolledCourses();
                if (askUserToEnterGrades()) {
                    enterGradesForStudent(student);
                }
            });
        } else {
            System.out.println("Multiple students found. Please select a student by entering the student number.");
            foundStudents.forEach(student -> System.out.println(student.studentNumber() + ": " +
                    student.firstName() + " " + student.surname()));
            System.out.print("Enter the student number: ");
            int selectedStudentNumber = scanner.nextInt();
            Optional<Student> selectedStudent = foundStudents.stream()
                    .filter(student -> student.studentNumber() == selectedStudentNumber)
                    .findFirst();
            selectedStudent.ifPresentOrElse(
                    student -> {
                        student.displayEnrolledCourses();
                        if (askUserToEnterGrades()) {
                            enterGradesForStudent(student);
                        }
                    },
                    () -> System.out.println("Invalid student number. Grades not updated.")
            );
        }
    }
    private static boolean askUserToEnterGrades() {
        System.out.print("Would you like to enter grades for this student? (y/n): ");
        String response = scanner.next().trim().toLowerCase();
        scanner.nextLine(); // Consume the newline character
        return response.equals("y");
    }

    private static void enterGradesForStudent(Student student) {
        System.out.println("Enter grades for the student:");

        for (Course course : student.enrolledCourses()) {
            System.out.println("Course: " + course.getName());
            List<Module> updatedModules = new ArrayList<>();
            for (Module module : course.modules()) {
                System.out.print("Enter grade for module " + module.getName() + ": ");
                int grade = scanner.nextInt();
                updatedModules.add(new Module(module.getName(), grade));
            }
            student = student.updateGrades(course.getName(), updatedModules);
        }

        // Update the student in the list
        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            Student s = iterator.next();
            if (s.studentNumber() == student.studentNumber()) {
                iterator.remove();
                break; // Assuming there is only one student with the given student number
            }
        }
        students.add(new Student(student.studentNumber(), student.getFirstName(), student.getSurname(), student.enrolledCourses()));

        System.out.println("Grades updated successfully!");
    }

    private static void displayStudentsOnCourse() {
        System.out.print("Enter the name of the course to display students: ");
        String courseName = scanner.nextLine();

        System.out.println("Sort Options:");
        System.out.println("1. Display alphabetically by surname");
        System.out.println("2. Display by highest grade on course");
        System.out.println("3. Display by lowest grade on course");
        System.out.print("Enter your sort option (1-3): ");
        int sortOption = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        List<Student> studentsOnCourse = students.stream()
                .filter(student -> student.enrolledCourses().stream()
                        .anyMatch(course -> course.getName().equalsIgnoreCase(courseName)))
                .collect(Collectors.toList());

        if (studentsOnCourse.isEmpty()) {
            System.out.println("No students found on the specified course.");
        } else {
            System.out.println("Students on course " + courseName + ":");

            switch (sortOption) {
                case 1 -> displayStudentsAlphabetically(studentsOnCourse, courseName);
                case 2 -> displayStudentsByHighestGrade(studentsOnCourse, courseName);
                case 3 -> displayStudentsByLowestGrade(studentsOnCourse, courseName);
                default -> System.out.println("Invalid sort option. Please enter 1, 2, or 3.");

            }
        }
    }

    private static void displayStudentsAlphabetically(List<Student> studentsToDisplay, String courseName) {
        List<Student> sortedStudents = studentsToDisplay.stream()
                .sorted(Comparator.comparing(Student::getSurname))
                .toList();

        if (sortedStudents.isEmpty()) {
            System.out.println("No students to display.");
        } else {
            System.out.println("Students displayed alphabetically by surname:");
            sortedStudents.forEach(student -> {
                System.out.print("Name: " + student.getFirstName() + " " + student.getSurname());
                System.out.print(", Student Number: " + student.studentNumber());
                System.out.println(", Average Grade: " + formatGrade(student.getAverageGradeForCourse(courseName)));
                System.out.println();
            });
        }
    }

    private static void displayStudentsByHighestGrade(List<Student> studentsToDisplay, String courseName) {
        List<Student> sortedStudents = studentsToDisplay.stream()
                .sorted(Comparator.comparingDouble(student ->
                        -student.enrolledCourses().stream()
                                .filter(course -> course.getName().equalsIgnoreCase(courseName))
                                .mapToDouble(Course::calculateAverageGrade)
                                .max()
                                .orElse(0.0)))
                .toList();

        System.out.println("Students sorted by highest grade on course " + courseName + ":");
        sortedStudents.forEach(student -> {
            System.out.print("Name: " + student.getFirstName() + " " + student.getSurname());
            System.out.print(", Student Number: " + student.studentNumber());
            System.out.println(", Average Grade: " + formatGrade(student.getAverageGradeForCourse(courseName)));
            System.out.println();
        });
    }

    private static void displayStudentsByLowestGrade(List<Student> studentsToDisplay, String courseName) {
        List<Student> sortedStudents = studentsToDisplay.stream()
                .sorted(Comparator.comparingDouble(student ->
                        student.enrolledCourses().stream()
                                .filter(course -> course.getName().equalsIgnoreCase(courseName))
                                .mapToDouble(Course::calculateAverageGrade)
                                .min()
                                .orElse(0.0)))
                .toList();

        System.out.println("Students sorted by lowest grade on course " + courseName + ":");
        sortedStudents.forEach(student -> {
            System.out.print("Name: " + student.getFirstName() + " " + student.getSurname());
            System.out.print(", Student Number: " + student.studentNumber());
            System.out.println(", Average Grade: " + formatGrade(student.getAverageGradeForCourse(courseName)));
            System.out.println();
        });
    }

    private static String formatGrade(double grade) {
        return new DecimalFormat("#0.00").format(grade);
    }
}