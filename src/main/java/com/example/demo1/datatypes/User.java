package com.example.demo1.datatypes;

import java.io.File;
import java.util.ArrayList;
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

    public User(String name, String age, String userID, String role) {
        this.name = name;
        this.age = age;
        this.userID = userID;
        this.role = role;
    }

    public static void loadUsers(){
        try(Scanner scan=new Scanner(new File("assets/accounts/Student.txt"))){
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                String[] parts = line.split(" ");
                User user = new User(parts[1], parts[2], parts[0], "Student");
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try(Scanner scan=new Scanner(new File("assets/accounts/Teacher.txt"))){
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                String[] parts = line.split(" ");
                User user = new User(parts[1], parts[2], parts[0], "Teacher");
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadChats(){
        // get me courses and chats from database
        try(Scanner scan=new Scanner(new File("assets/inbox/chats.txt"))){
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                String[] parts =line.split("-");
                if(parts[0].equals(userID)){
                    User temp=findUser(parts[1]);
                    if(temp != null) {
                        chats.add(temp);
                    }
                }else if(parts[1].equals(userID)){
                    User temp=findUser(parts[0]);
                    if(temp != null) {
                        chats.add(temp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
