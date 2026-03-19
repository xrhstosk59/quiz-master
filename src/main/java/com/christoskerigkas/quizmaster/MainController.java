package com.christoskerigkas.quizmaster;

import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController implements Initializable {
    private static final String CORRECT_SOUND = "/com/christoskerigkas/quizmaster/Correct.mp3";
    private static final String WRONG_SOUND = "/com/christoskerigkas/quizmaster/Wrong.mp3";

    private static final String[] QUESTIONS = {
        "Ερώτηση 1: Ποια είναι η πρωτεύουσα της Ελλάδας;",
        "Ερώτηση 2: Ποιος είναι ο πιο μεγάλος ποταμός στην Ευρώπη;",
        "Ερώτηση 3: Ποια είναι η πιο μεγάλη λίμνη στην Ελλάδα;",
        "Ερώτηση 4: Ποια είναι η πιο υψηλή κορυφή της Ελλάδας;",
        "Ερώτηση 5: Ποιο είναι το μεγαλύτερο ελληνικό νησί;",
        "Ερώτηση 6: Ποιος ήταν ο πρώτος Ολυμπιακός πρωταθλητής της νεότερης ιστορίας;",
        "Ερώτηση 7: Ποιος ήταν ο πρώτος διαστημοναύτης που πετάχθηκε στο διάστημα;",
        "Ερώτηση 8: Ποιο είναι το μεγαλύτερο αρχαίο θέατρο στην Ελλάδα;",
        "Ερώτηση 9: Ποιος ήταν ο αρχαίος Έλληνας φιλόσοφος που έλεγε 'Γνώθι σεαυτόν';",
        "Ερώτηση 10: Ποιο είναι το εθνικό ζώο της Ελλάδας;"
    };

    private static final String[][] ANSWER_CHOICES = {
        {"Αθήνα", "Θεσσαλονίκη", "Πάτρα"},
        {"Δούναβης", "Ρήνος", "Βόλγας"},
        {"Πρέσπες", "Τρίχωνας", "Βιστωνίδα"},
        {"Ολύμπου", "Πάρνηθας", "Ταϋγέτου"},
        {"Κρήτη", "Λήμνος", "Εύβοια"},
        {"Γιάννης Μελισσανίδης", "Γιώργος Παπανδρέου", "Παύλος Γιαννακόπουλος"},
        {"Γαγγάρης", "Ηρόδοτος", "Γκαγκάριν"},
        {"Επίδαυρος", "Δωδώνη", "Ακρόπολη"},
        {"Πλάτωνας", "Αριστοτέλης", "Σωκράτης"},
        {"Δελφίνι", "Αγριόγατα", "Μαύρος αετός"}
    };

    private static final int[] CORRECT_ANSWERS = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private final MediaPlayer correctAnswerPlayer =
            new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource(CORRECT_SOUND)).toExternalForm()));
    private final MediaPlayer wrongAnswerPlayer =
            new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource(WRONG_SOUND)).toExternalForm()));

    @FXML
    private Text questionLabel;

    @FXML
    private HBox answersContainer;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button exitButton;

    @FXML
    private Label livesLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Button answerSubmit;

    @FXML
    private HBox SubmitRestart;

    private final Random random = new Random();

    private int currentQuestionIndex;
    private int score;
    private int lives;
    private int remainingTime;
    private Timeline timer;
    private ToggleGroup toggleGroup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetGame();
    }

    @FXML
    private void handleAnswerSubmit() {
        RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
        if (selectedRadioButton == null) {
            return;
        }

        String selectedAnswer = selectedRadioButton.getText().substring(3);
        handleAnswerSelection(selectedAnswer);
    }

    @FXML
    private void handleRestartButton() {
        correctAnswerPlayer.stop();
        wrongAnswerPlayer.stop();
        resetGame();
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stopTimer();
        stage.close();
    }

    private void resetGame() {
        currentQuestionIndex = 0;
        score = 0;
        lives = 3;

        updateScore();
        updateLives();
        showQuestion();
        ensureSubmitButtonVisible();
        startTimer();
    }

    private void startTimer() {
        stopTimer();
        remainingTime = 15;
        timerLabel.setText("Time: " + remainingTime);

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingTime--;
            timerLabel.setText("Time: " + remainingTime);
            if (remainingTime <= 0) {
                handleTimeUp();
            }
        }));
        timer.setCycleCount(15);
        timer.playFromStart();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void showQuestion() {
        questionLabel.setText(QUESTIONS[currentQuestionIndex]);
        answersContainer.getChildren().clear();
        toggleGroup = new ToggleGroup();

        char questionCharacter = 'A';
        for (String answerChoice : getRandomizedAnswerChoices(currentQuestionIndex)) {
            RadioButton radioButton = new RadioButton(questionCharacter + ". " + answerChoice);
            radioButton.setToggleGroup(toggleGroup);
            answersContainer.getChildren().add(radioButton);
            questionCharacter++;
        }
    }

    private void handleAnswerSelection(String answer) {
        if (answer.equals(ANSWER_CHOICES[currentQuestionIndex][CORRECT_ANSWERS[currentQuestionIndex]])) {
            score++;
            correctAnswerPlayer.stop();
            correctAnswerPlayer.play();
        } else {
            lives--;
            wrongAnswerPlayer.stop();
            wrongAnswerPlayer.play();
        }

        updateScore();
        updateLives();
        if (lives == 0) {
            handleGameOver();
            return;
        }

        if (goToNextQuestion()) {
            return;
        }

        startTimer();
    }

    private void updateScore() {
        scoreLabel.setText("Score: " + score + "/" + QUESTIONS.length + " \uD83C\uDFC6");
    }

    private void updateLives() {
        StringBuilder livesText = new StringBuilder("Lives: ");
        for (int i = 0; i < lives; i++) {
            livesText.append("\uD83D\uDC96 ");
        }
        livesLabel.setText(livesText.toString());
    }

    private String[] getRandomizedAnswerChoices(int questionIndex) {
        String[] choices = ANSWER_CHOICES[questionIndex].clone();
        shuffleArray(choices);
        return choices;
    }

    private void shuffleArray(String[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private void handleGameCompleted() {
        questionLabel.setText("Quiz completed!");
        finishGameState();
    }

    private void handleGameOver() {
        questionLabel.setText("Game Over!");
        finishGameState();
    }

    private void finishGameState() {
        answersContainer.getChildren().clear();
        disableAnswerButtons();
        SubmitRestart.getChildren().remove(answerSubmit);
        stopTimer();
        timerLabel.setText("Time: 0");
    }

    private void disableAnswerButtons() {
        for (Node node : answersContainer.getChildren()) {
            if (node instanceof RadioButton) {
                ((RadioButton) node).setDisable(true);
            }
        }
    }

    private void handleTimeUp() {
        lives--;
        updateLives();
        wrongAnswerPlayer.stop();
        wrongAnswerPlayer.play();

        if (lives == 0) {
            handleGameOver();
            return;
        }

        if (goToNextQuestion()) {
            return;
        }

        startTimer();
    }

    private boolean goToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < QUESTIONS.length) {
            showQuestion();
            return false;
        }

        handleGameCompleted();
        return true;
    }

    private void ensureSubmitButtonVisible() {
        if (!SubmitRestart.getChildren().contains(answerSubmit)) {
            SubmitRestart.getChildren().add(0, answerSubmit);
        }
    }
}
