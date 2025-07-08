package com.example.demo1.datatypes;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Course {
    private List<User> students = new ArrayList<>();
    private List<User> teachers = new ArrayList<>();
    static private List<Course> courses = new ArrayList<>();
    private String courseName;
    private String CourseID;

    public Course(String courseName, String CourseID) {
        this.courseName = courseName;
        this.CourseID = CourseID;
    }

    public void initCourse(String CourseId){
        String path= "assets/course/" + CourseId + "/Students.txt";
        try (Scanner scan = new Scanner(new File(path))) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                User user = User.findUser(line);
                students.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        path = "assets/course/" + CourseId + "/Teachers.txt";
        try (Scanner scan = new Scanner(new File(path))) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                User user = User.findUser(line);
                teachers.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCourses() {
        try (Scanner scan = new Scanner(new File("assets/course/lists.txt"))) {
            while( scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] parts = line.split(" ");
                Course course = new Course(parts[0], parts[1]);
                course.initCourse(parts[0]);
                courses.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseID() {
        return CourseID;
    }

    public static List<Course> getCourses() {
        return courses;
    }

    public List<User> getStudents() {
        return students;
    }

    public List<User> getTeachers() {
        return teachers;
    }

    public String getCourseId() {
        return CourseID;
    }
}
