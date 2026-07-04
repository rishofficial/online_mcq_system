package com.example.demo1.datatypes;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class User {
    private final String name;
    private final String age;
    private final String userID;
    private final String role;
    private List<Course> courses= new ArrayList<>();
    private List<User> chats= new ArrayList<>();
    private static List<User> users = new ArrayList<>();
    private static User currentUser;

    public static void setcurrentUser(User user) {
        User.currentUser = user;
    }
    public static User getCurrentUser() {
         return User.currentUser;
    }

    public User(String name, String age, String userID, String role) {
        this.name = name;
        this.age = age;
        this.userID = userID;
        this.role = role;
    }
    public static void setUsers(List<User> users) {
        User.users = users;
    }
    public void setChats(List<User> chats) {
        User.currentUser.chats = chats;
    }
    public static List<User> getUsers() {
        return users;
    }
    public List<User> getChats() {
        return chats;
    }
    public void loadCourses() {
        // get me courses from database
        if(this.role.equals("Student")){
            for(Course course : Course.getCourses()) {
                if (course.getStudents().contains(this)) {
                    courses.add(course);
                }
            }
        } else if (this.role.equals("Teacher")) {
            for(Course course : Course.getCourses()) {
                if (course.getTeachers().contains(this)) {
                    courses.add(course);
                }
            }
        }
        courses = new ArrayList<>(new HashSet<>(courses));
    }
    public List<Course> getCourses() {
        return courses;
    }
    public static User findUser(String userID) {
        for (User user : users) {
            if (user.getUserID().equals(userID)) {
                return user;
            }
        }
        System.out.println("User not found.");
        return null;
    }

    public String getName() {
        return name.replace("_", " ");
    }
    public String getUserID() {
        return userID;
    }
    public String getAge() {
        return age;
    }
    public String getRole() {
        return role;
    }
    public boolean isTeacher(){
        return role.equals("Teacher");
    }
    public boolean isStudent(){
        return role.equals("Student");
    }
    public String getUserId() {
        return userID;
    }
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
    public static void loadUsers() throws IOException {
        if(!users.isEmpty()){
            users.clear();
        }
        try (Socket socket = new Socket(Info.ip , Info.port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("LOAD_USERS");
            boolean student=true;
            while(true){
                String line = in.readLine();
                if(line.equals("TEACHER_FINISH")){
                    break;
                }
                if(line.equals("STUDENT_FINISH")){
                    student=false;
                    continue;
                }
                System.out.println(line);
                String[]  parts = line.split(" ");
                System.out.println(parts.length);
                if(student){
                    User user = new User(parts[1], parts[2], parts[0], "Student");
                    users.add(user);
                }else{
                    User user = new User(parts[1], parts[2], parts[0], "Teacher");
                    users.add(user);
                }
            }
        }
        users = new ArrayList<>(new HashSet<>(users));
    }
    public void loadChats() throws IOException {
        if(User.getCurrentUser()== null){
            return;
        }
        if(!chats.isEmpty()){
            chats.clear();
        }
        try (Socket socket = new Socket(Info.ip , Info.port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("LOAD_CHATS");
            while(true){
                String line = in.readLine();
                if(line.equals("FINISH")){
                    break;
                }
                String[]  parts = line.split("-");
                if(parts.length==2){
                    if(parts[0].equals(this.userID)){
                        chats.add(User.findUser(parts[1]));
                    }else if(parts[1].equals(this.userID)){
                        chats.add(User.findUser(parts[0]));
                    }
                }
            }
        }
        chats=new ArrayList<>(new HashSet<>(chats));
    }

    public int getQuizMarks(String quizId) {
        //File format must be: quizId_marks for this to work
        //example: quiz1_19, logic can be changed later though to fit the file
        // if not found, return 0
        try(Scanner sc=new Scanner(new File("assets/quiz/" + quizId + ".txt"))){
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] parts = line.split("_");
                if(parts[0].equals(userID)){
                    return Integer.parseInt(parts[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Default return value if not found
    }
}
