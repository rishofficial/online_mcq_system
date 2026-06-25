package com.example.demo1.datatypes;

import com.example.demo1.threads.socketWrap;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Course {
    private List<User> students = new ArrayList<>();
    private List<User> teachers = new ArrayList<>();
    static private List<Course> courses = new ArrayList<>();
    private String courseName;
    private String CourseID;
    private List<Quiz> quizzes = new ArrayList<>();
    public Course(String courseName, String CourseID) {
        this.courseName = courseName;
        this.CourseID = CourseID;
    }
    public static void setCourses(List<Course> courses) {
        Course.courses = courses;
    }
    public String getCourseName() {
        return courseName;
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
    public void setStudents(List<User> students) {
        this.students = students;
    }
    public void setTeachers(List<User> teachers) {
        this.teachers = teachers;
    }
    public static void loadCourses() throws IOException {
        if(!courses.isEmpty()){
            courses.clear();
        }
        try (Socket socket = new Socket(Info.ip , Info.port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("LOAD_COURSES");
            while(true){
                String line = in.readLine();
                if(line.equals("FINISH")){
                    break;
                }
                String[]  parts = line.split(" ", 2);
                if(parts.length==2){
                    Course course = new Course(parts[1], parts[0]);
                    courses.add(course);
                }
            }
        }
        courses=new ArrayList<>(new HashSet<>(courses));
        for(Course course:courses){
            Course.initCourse(course);
        }
    }
    public static void initCourse(Course course) throws IOException {
        try (Socket socket = new Socket(Info.ip , Info.port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("INIT_COURSE");
            out.println(course.getCourseId());
            while (true){
                String line = in.readLine();
                if(line.equals("STUDENTS_FINISH")){
                    break;
                }
                User student= User.findUser(line);
                course.getStudents().add(student);
            }
            while (true){
                String line = in.readLine();
                if(line.equals("TEACHER_FINISH")) {
                    break;
                }
                User teacher= User.findUser(line);
                course.getTeachers().add(teacher);
            }
            course.setStudents(new ArrayList<>(new HashSet<>(course.getStudents())));
            course.setTeachers(new ArrayList<>(new HashSet<>(course.getTeachers())));
        }
    }
    public void loadQuizzes() {
        String path = "assets/course/" + this.CourseID + "/chats.txt";//
        if(!quizzes.isEmpty()){
            quizzes.clear();
        }
        try(socketWrap socket = new socketWrap(Info.ip, Info.port)) {
            socket.writeLine("LOAD_COURSE_QUIZ");
            socket.writeLine(path);
            while (true) {
                String line = socket.readLine();
                if (line == null || line.equals("END")) {
                    break;
                }
                Quiz quiz = Quiz.fromString(line, this); // Assuming Quiz has a static fromString method
                if (quiz != null) {
                    quizzes.add(quiz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        quizzes=new ArrayList<>(new HashSet<>(quizzes));
    }

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }
    public static Course findCourse(String courseId) {
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                return course;
            }
        }
        System.out.println("Course not found.");
        return null;
    }
}