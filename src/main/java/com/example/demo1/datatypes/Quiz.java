package com.example.demo1.datatypes;
import com.example.demo1.threads.socketWrap;
import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class Quiz {
    private final String topicName;
    private List<Question> questions=new ArrayList<>();
    private String courseId;
    private final String quizId;
    private List<String> quizResults;
    private int questionCount=0;
    private int timeLimit=0;
    public Quiz(String topicName, String courseId, String quizId) {
        this.topicName = topicName;
        this.courseId = courseId;
        this.quizId = quizId;
    }
    public Quiz(String quizId, String topicName, int questionCount) {
        this.quizId = quizId;
        this.topicName = topicName;
        this.questionCount = questionCount;
    }
    public Quiz(String quizId, String courseId, String topicName , int questionCount) {
        this.topicName = topicName;
        this.courseId = courseId;
        this.quizId = quizId;
        this.questionCount = questionCount;
    }
    public Quiz(String topicName, String courseId, String quizId, int questionCount, int timeLimit) {
        this.topicName = topicName;
        this.courseId = courseId;
        this.quizId = quizId;
        this.questionCount = questionCount;
        this.timeLimit = timeLimit;
    }
    public void loadQuestions(String CourseID, String quizId) throws Exception {
        try(socketWrap socket = new socketWrap(Info.ip, Info.port)){
            socket.writeLine("LOAD_QUIZ_QUESTIONS");
            socket.writeLine("assets/course/"+courseId+"/"+quizId+"/quiz.txt");
            int count=0;
            List<Option> options = new ArrayList<>();
            String questionText = "";
            while (true){
                String line= socket.readLine();
                if(line==null || line.equals("END")){
                    //socket.writeLine("END_OF_QUIZ");
                    System.out.println("End of quiz questions");
                    break;
                }
                if(count==0){
                    String[] parts = line.split("_", 2);
                    questionText = parts[1];
                }else if(count>0 && count < 5){
                    String[] parts = line.split("_", 3);
                    String optiontext = parts[2];
                    boolean correct= Boolean.parseBoolean(parts[1]);
                    Option option= new Option(optiontext, correct);
                    options.add(option);
                }
                count++;
                if(count==5){
                    count=0;
                    questionCount++;
                    Question question = new Question(questionText, options);
                    questions.add(question);
                    options=new ArrayList<>();
                }
                questions=new ArrayList<>(new HashSet<>(questions));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void initResult(String courseId, String quizId) {
        Course currentCourse = Course.findCourse(courseId);
        // find out the Users of the course of the quiz
        try(socketWrap socket=new socketWrap(Info.ip, Info.port)){
            socket.writeLine("INIT_QUIZ_RESULT");
            socket.writeLine("assets/course/" + courseId + "/" + quizId + "/result.txt");
            assert currentCourse != null;
            List<User>users=currentCourse.getStudents();
            for(User user: users){
                socket.writeLine(user.getUserID()+"_0");
            }
            socket.writeLine("END");
        }catch(Exception e){
            System.out.println("Error initializing quiz results: " + e.getMessage());
        }
    }
    public static Quiz fromString(String data, Course course) {
        // Example format: quizId ,quizTopic,questionCount
        String[] parts = data.split("_", 4);
        if (parts.length < 3) return null;
        String quizId = parts[0].trim();
        String quizTitle = parts[1].trim() ;
        int questionCount = Integer.parseInt(parts[2].trim());
        int time=Integer.parseInt(parts[3].trim());
        return new Quiz(quizTitle, course.getCourseId(), quizId, questionCount, time);
    }
    public String getTopicName() {
        return topicName;
    }
    public String getQuizId() {
        return quizId;
    }
    public String getQuestionCount() {
        return String.valueOf(questionCount);
    }
    public int getTimeLimit() {
        return this.timeLimit;
    }
    public List<Question> getQuestions() {
        return this.questions;
    }
    public void writeResult(User user, Course course, String quizId, int marks) {
        String courseId= course.getCourseId();
        try(socketWrap socket = new socketWrap(Info.ip, Info.port)){
            socket.writeLine("WRITE_QUIZ_RESULT");
            socket.writeLine("assets/course/" + courseId + "/" + quizId + "/result.txt");
            socket.writeLine(user.getUserID());
            socket.writeLine(String.valueOf(marks));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public List<String> getResult(Course course, String quizId) {
        String courseId = course.getCourseId();
        File resultFile=new File("assets/course/" + courseId + "/" + quizId + "/result.txt");
        List<String> results = new ArrayList<>();
        try(socketWrap socket = new socketWrap(Info.ip, Info.port)){
            socket.writeLine("READ_QUIZ_RESULT");
            socket.writeLine("assets/course/" + courseId + "/" + quizId + "/result.txt");
            while (true) {
                String line = socket.readLine();
                if(line == null || line.equals("RESULT_READ_FINISH")){
                    break;
                }
                results.add(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        mergeSort(results,0,results.size()-1);
        return results;
    }
    private void mergeSort(List<String> list, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(list, left, mid);
            mergeSort(list, mid + 1, right);
            merge(list, left, mid, right);
        }
    }
    private void merge(List<String> list, int left, int mid, int right) {
        List<String> leftList = new ArrayList<>(list.subList(left, mid + 1));
        List<String> rightList = new ArrayList<>(list.subList(mid + 1, right + 1));
        int i = 0, j = 0, k = left;
        while (i < leftList.size() && j < rightList.size()) {
            int marksLeft = Integer.parseInt(leftList.get(i).substring(leftList.get(i).indexOf('_') + 1));
            int marksRight = Integer.parseInt(rightList.get(j).substring(rightList.get(j).indexOf('_') + 1));
            if (marksLeft >= marksRight) {
                list.set(k, leftList.get(i));
                k++;
                i++;
            } else {
                list.set(k, rightList.get(j));
                k++;
                j++;
            }
        }
        while (i < leftList.size()) {
            list.set(k, leftList.get(i));
            k++;
            i++;
        }
        while (j < rightList.size()) {
            list.set(k, rightList.get(j));
            k++;
            j++;
        }
    }
}