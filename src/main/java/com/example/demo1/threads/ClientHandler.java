package com.example.demo1.threads;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final socketWrap clientSocket;
    public ClientHandler(socketWrap clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try{
            String command = clientSocket.readLine();
            if (command.equals("CREATE")) {
                synchronized (this) {
                    String name = clientSocket.readLine();
                    name = name.replace(" ", "_");
                    String age = clientSocket.readLine();
                    String userId = clientSocket.readLine();
                    String password = clientSocket.readLine();
                    String confirmPassword = clientSocket.readLine();
                    String role = clientSocket.readLine();
                    System.out.println("Received from client: " + userId + ", " + password + ", " + role);

                    String resourceFile = role.equals("Teacher") ? "assets/accounts/Teacher.txt" : "assets/accounts/Student.txt";
                    System.out.println(resourceFile);
                    try (Scanner scanner = new Scanner(new File(resourceFile))) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            String[] parts = line.split(" ");
                            if (parts[0].equals(userId)) {
                                System.out.println("Duplicate for " + role + ": " + userId);
                                clientSocket.writeLine("NO");
                                return;
                            }
                        }
                    }
                    File file = new File(resourceFile);
                    try (FileWriter writer = new FileWriter(file, true)) {
                        writer.write("\n" + userId + " " + name + " " + age + " " + password);
                    } catch (IOException e) {
                        clientSocket.writeLine("Error writing to file. Please try again.");
                    }
                    clientSocket.writeLine("YES");
                    System.out.println("Account created successfully for " + role + ": " + userId);
                }
            }
            else if (command.equals("LOGIN")) {
                String userId = clientSocket.readLine();
                String password = clientSocket.readLine();
                String role = clientSocket.readLine();
                System.out.println("Received from client: " + userId + ", " + password + ", " + role);

                String resourceFile = role.equals("Teacher") ? "assets/accounts/Teacher.txt" : "assets/accounts/Student.txt";
                System.out.println("Resource file: " + new File(resourceFile).exists());
                try (Scanner scanner = new Scanner(new File(resourceFile))) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(" ");
                        if (parts[0].equals(userId) && parts[3].equals(password)) {
                            System.out.println("Login successful for " + role + ": " + userId);
                            clientSocket.writeLine("YES");
                            return;
                        }
                    }
                }
                System.out.println("Login failed for " + role + ": " + userId);
                clientSocket.writeLine("NO");
            }
            else if (command.equals("REFRESH")) {
                String fileName = clientSocket.readLine();
                String lastMessage = clientSocket.readLine();
                //boolean found = false;
                File file = new File("assets/inbox/" + fileName);
                List<String> allMessages = new ArrayList<>();
                try (Scanner scan = new Scanner(file)) {
                    while (scan.hasNextLine()) {
                        allMessages.add(scan.nextLine());
                    }
                }
                int lastIndex = -1;
                for(int i=allMessages.size()-1;i>=0;i--){
                    if (allMessages.get(i).equals(lastMessage)){
                        lastIndex = i;
                        break;
                    }
                }
                if(lastIndex!=allMessages.size()-1){
                    for (int i = lastIndex + 1; i < allMessages.size(); i++) {
                        clientSocket.writeLine(allMessages.get(i));
                    }
                }
                clientSocket.writeLine("END");
            }
            else if (command.equals("LOAD_USERS")) {
                try (Scanner scan = new Scanner(new File("assets/accounts/Student.txt"))) {
                    while (scan.hasNextLine()) {
                        String line = scan.nextLine();
                        clientSocket.writeLine(line);
                    }
                } catch (Exception e) {
                    System.out.println("Error loading students");
                }
                clientSocket.writeLine("STUDENT_FINISH");
                try (Scanner scan = new Scanner(new File("assets/accounts/Teacher.txt"))) {
                    while (scan.hasNextLine()) {
                        String line = scan.nextLine();
                        clientSocket.writeLine(line);
                    }
                } catch (Exception e) {
                    System.out.println("error loading teachers");
                }
                clientSocket.writeLine("TEACHER_FINISH");
                System.out.println("Users loaded successfully.");
            }
            else if (command.equals("LOAD_CHATS")) {
                try (Scanner scan = new Scanner(new File("assets/inbox/chats.txt"))) {
                    while (scan.hasNextLine()) {
                        String line = scan.nextLine();
                        clientSocket.writeLine(line);
                    }
                } catch (Exception e) {
                    System.out.println("error loading chats");
                }
                clientSocket.writeLine("FINISH");
            }
            else if(command.equals("CREATE_INBOX_FILE")){
                String fileName = clientSocket.readLine();
                File file = new File("assets/inbox/" + fileName + ".txt");
                try {
                    if (file.createNewFile()) {
                        System.out.println("Inbox file created: " + fileName);
                        try(FileWriter writer = new FileWriter(file, true)) {
                            //writer.write("WELCOME" + "\n");
                        }
                        File chatFile = new File("assets/inbox/chats.txt");
                        try (FileWriter writer = new FileWriter(chatFile, true)) {
                            writer.write(fileName + "\n");
                        }
                    } else {
                        System.out.println("Inbox file already exists: " + fileName);
                    }
                } catch (IOException e) {
                    System.out.println("Error creating inbox file: " + e.getMessage());
                }
            }
            else if (command.equals("INIT_COURSE")) {
                String name = clientSocket.readLine();
                String path = "assets/course/" + name + "/Students.txt";
                try (Scanner scan = new Scanner(new File(path))) {
                    while (scan.hasNextLine()) {
                        String line = scan.nextLine();
                        clientSocket.writeLine(line);
                    }
                }
                clientSocket.writeLine("STUDENTS_FINISH");
                path = "assets/course/" + name + "/Teachers.txt";
                try (Scanner scan = new Scanner(new File(path))) {
                    while (scan.hasNextLine()) {
                        String line = scan.nextLine();
                        clientSocket.writeLine(line);
                    }
                }
                clientSocket.writeLine("TEACHER_FINISH");
            }
            else if (command.equals("LOAD_COURSES")) {
                try (Scanner scan = new Scanner(new File("assets/course/lists.txt"))) {
                    while (scan.hasNextLine()) {
                        String line = scan.nextLine();
                        clientSocket.writeLine(line);
                    }
                }
                clientSocket.writeLine("FINISH");
            }
            else if(command.equals("SEND_MESSAGE")){
                String txt = clientSocket.readLine();
                String filename = "assets/inbox/"+ clientSocket.readLine();
                String userId = clientSocket.readLine();
                if (!filename.endsWith(".txt")) {
                    filename = filename + ".txt";
                }
                File file = new File(filename);
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.write( userId + "_" + txt + "\n");
                } catch (IOException e) {
                    clientSocket.writeLine("Error writing to file. Please try again.");
                }
                System.out.println("send message successful " + txt);
            }
            else if(command.equals("SHOW_COURSES")){
                String userId=clientSocket.readLine();
                String courseLine=clientSocket.readLine();
                String role= clientSocket.readLine();
                System.out.println(userId+"_"+courseLine+"_"+role);
                List<String> coursesSelected=List.of(courseLine.split(","));
                File coursesDirectory=new File("assets/course");
                File[] courseFolders= coursesDirectory.listFiles(File::isDirectory);

                if(courseFolders!=null){
                    for(File courseFolder: courseFolders){
                        String courseId=courseFolder.getName();
                        String resource=role.equals("Teacher") ? "Teachers.txt" : "Students.txt";
                        File studentsFile=new File(courseFolder, resource);
                        List<String> students=new ArrayList<>();
                        if(studentsFile.exists()){
                            try(Scanner sc=new Scanner(studentsFile)){
                                while(sc.hasNextLine()){
                                    students.add(sc.nextLine());
                                }
                            }
                        }
                        if(coursesSelected.contains(courseId)){
                            if(!students.contains(userId)){
                                try(FileWriter writer=new FileWriter(studentsFile,true)){
                                    writer.write(userId+ "\n");
                                    System.out.println("Added " + userId + " to " + courseId);
                                }
                            }
                        }
                        else{
                            if(students.contains(userId)){
                                students.remove(userId);
                                System.out.println("Removed " + userId + " from " + courseId);
                                try(PrintWriter writer=new PrintWriter(studentsFile)){
                                    for(int i=0;i<students.size();i++){
                                        if(i>0){
                                            writer.write("\n");
                                        }
                                        writer.write(students.get(i));
                                        /*writer.print(students.get(i));
                            if (i < students.size() - 1) writer.print("\n");*/
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if(command.equals("FIND_INBOX_FILE")){
                String currentUserId = clientSocket.readLine();
                String anotherUserId = clientSocket.readLine();
                String fileName="null";
                try(Scanner scan=new Scanner(new File("assets/inbox/chats.txt"))){
                    while (scan.hasNextLine()){
                        String line = scan.nextLine();
                        String[] parts = line.split("-");
                        if (parts[0].equals(currentUserId) && parts[1].equals(anotherUserId)) {
                            fileName= currentUserId + "-" + anotherUserId + ".txt";
                        }else if(parts[0].equals(anotherUserId) && parts[1].equals(currentUserId)) {
                            fileName= anotherUserId + "-" + currentUserId+ ".txt";
                        }
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if(!fileName.equals("null")) {
                    clientSocket.writeLine(fileName);
                    System.out.println("Found inbox file: " + fileName);
                } else {
                    clientSocket.writeLine(fileName);
                }
            }
            else if(command.equals("LOAD_MESSAGES")){
                String inboxFile = clientSocket.readLine();
                if(!inboxFile.endsWith(".txt")){
                    inboxFile=inboxFile+".txt";
                }
                File file = new File("assets/inbox/" + inboxFile);
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        clientSocket.writeLine(line);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Error loading messages from " + inboxFile);
                }
                clientSocket.writeLine("END");
            }
            else if(command.equals("ADD_QUESTION")){
                String path=clientSocket.readLine();
                System.out.println(path);
                String question = clientSocket.readLine();
                System.out.println("Question received: " + question);
                String option1 = clientSocket.readLine();
                System.out.println("Option 1 received: " + option1);
                String option2 = clientSocket.readLine();
                System.out.println("Option 2 received: " + option2);
                String option3 = clientSocket.readLine();
                System.out.println("Option 3 received: " + option3);
                String option4 = clientSocket.readLine();
                System.out.println("Option 4 received: " + option4);
                try (FileWriter writer = new FileWriter(path, true)) {
                    writer.write(question + "\n");
                    writer.write(option1 + "\n");
                    writer.write(option2 + "\n");
                    writer.write(option3 + "\n");
                    writer.write(option4 + "\n");
                } catch (IOException e) {
                    System.out.println("Error writing to quiz file: " + e.getMessage());
                }
            }
            else if(command.equals("CREATE_QUIZ")){
                String path=clientSocket.readLine();
                //System.out.println("Creating quiz at path: " + path);
                String quizDetails = clientSocket.readLine();
                //System.out.println("Quiz details: " + quizDetails);
                String chatpath = clientSocket.readLine();
                File file = new File(path);
                if (!file.exists()) {
                    boolean created = file.mkdirs();
                    System.out.println("Directory created: " + created);
                }
                File quizFile = new File(path + "/quiz.txt");
                File resultsFile = new File(path + "/result.txt");
                if(!quizFile.exists()) {
                    try {
                        boolean created = quizFile.createNewFile();
                        System.out.println("Quiz file created: " + created);
                    } catch (Exception e) {
                        System.out.println("Error creating quiz file: " + e.getMessage());
                    }
                }
                if(!resultsFile.exists()) {
                    try {
                        boolean created = resultsFile.createNewFile();
                        System.out.println("Results file created: " + created);
                    } catch (Exception e) {
                        System.out.println("Error creating results file: " + e.getMessage());
                    }
                }
                //System.out.println("Chat path: " + chatpath);
                File file2 = new File(chatpath);
                try (FileWriter writer = new FileWriter(file2, true)) {
                    writer.write("\n" + quizDetails);
                } catch (IOException e) {
                    System.out.println("Error writing to quiz file: " + e.getMessage() + "  " + chatpath);
                }
            }
            else if(command.equals("LOAD_QUIZ_QUESTIONS")){
                String path=clientSocket.readLine();
                File file = new File(path);
                if(!file.exists()){
                    return;
                }
                List<String> questions = Files.readAllLines(Paths.get(path));;
                for(String question : questions) {
                    if (!question.trim().isEmpty()) {
                        clientSocket.writeLine(question);
                    }
                }
                clientSocket.writeLine("END");
            }
            else if(command.equals("INIT_QUIZ_RESULT")){
                String path=clientSocket.readLine();
                List<String> resultStrings = new ArrayList<>();
                while (true) {
                    String line = clientSocket.readLine();
                    if(line==null){
                        break;
                    }
                    if (line.equals("END")) {
                        break;
                    }
                    resultStrings.add(line);
                }
                resultStrings=new ArrayList<>(new HashSet<>(resultStrings));
                try(FileWriter writer = new FileWriter(path, true)) {
                    for(String result : resultStrings) {
                        writer.write(result + "\n");
                    }
                }
            }
            else if (command.equals("READ_QUIZ_RESULT")) {
                String path = clientSocket.readLine();
                File file = new File(path);
                if (file.exists()) {
                    try (Scanner scanner = new Scanner(file)) {
                        while (scanner.hasNextLine()) {
                            clientSocket.writeLine(scanner.nextLine());
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading file");
                    }
                }
                clientSocket.writeLine("RESULT_READ_FINISH");
            }
            else if (command.equals("WRITE_QUIZ_RESULT")) {
                String path = clientSocket.readLine();
                String update= clientSocket.readLine();
                String mark= clientSocket.readLine();
                List<String> lines= new ArrayList<>();
                boolean found=false;
                try (Scanner scan = new Scanner(new File(path))) {
                    while (scan.hasNextLine()) {
                        String line = scan.nextLine();
                        String[] parts = line.split("_");
                        if(parts[0].equals(update)){
                            lines.add(update+"_"+mark);
                            found=true;
                            System.out.println("Updated result for " + update + " to " + mark);
                        }else{
                            lines.add(line);
                        }
                    }
                    if(!found){
                        lines.add(update+"_"+mark);
                        System.out.println("Added result for " + update + " with mark " + mark);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading file");
                }
                lines=new ArrayList<>(new HashSet<>(lines));
                try(FileWriter writer= new FileWriter(path, false)){
                    for(String line: lines) {
                        writer.write(line+"\n");
                    }
                }
            }
            else if(command.equals("LOAD_COURSE_QUIZ")){
                String path=clientSocket.readLine();
                File file = new File(path);
                if(file.exists()) {
                    try (Scanner scanner = new Scanner(file)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            clientSocket.writeLine(line);
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading file");
                    }
                }
                clientSocket.writeLine("END");
            }
            else if (command.equals("LOGOUT")) {
            } else {
                System.out.println("Unknown command: " + command);
                clientSocket.writeLine("UNKNOWN_COMMAND");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
