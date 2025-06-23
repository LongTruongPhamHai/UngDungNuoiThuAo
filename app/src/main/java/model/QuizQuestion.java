package model;

public class QuizQuestion {
    private String id;
    private String textQuestion;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;

    public QuizQuestion() {}

    public QuizQuestion(String id, String textQuestion, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        this.id = id;
        this.textQuestion = textQuestion;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTextQuestion() { return textQuestion; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectAnswer() { return correctAnswer; }

    public void setTextQuestion(String textQuestion) { this.textQuestion = textQuestion; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}
