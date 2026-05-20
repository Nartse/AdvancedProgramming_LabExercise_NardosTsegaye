import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;

public class App extends Application {

    Deck deck;

    Player player = new Player("Player");
    Player computer = new Player("Computer");

    Button[] playerCards = new Button[5];

    Label[] computerCards = new Label[5];

    boolean[] heldCards = new boolean[5];

    Label titleLabel = new Label("♠ 5-CARD DRAW POKER ♠");

    Label instructionLabel =
            new Label("Click cards to HOLD them, then press DRAW");

    Label resultLabel = new Label("");

    boolean roundFinished = false;

    @Override
    public void start(Stage stage) {

        startGame();

        // ---------- TITLE ----------
        titleLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        32
                )
        );

        titleLabel.setTextFill(Color.GOLD);

        // ---------- POKER INTRO & HOW TO PLAY PANEL ----------
        VBox introPanel = new VBox(5);
        introPanel.setAlignment(Pos.CENTER);
        introPanel.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.4);" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 12;" +
                "-fx-max-width: 700;"
        );

        Label gameTypeLabel = new Label("Variant: 5-Card Draw");
        gameTypeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gameTypeLabel.setTextFill(Color.LIGHTBLUE);

        Label rulesLabel = new Label(
                "How to Play:\n" +
                "1. You and the computer are both dealt 5 private cards.\n" +
                "2. Strategy: Select which cards to keep by clicking them (they will turn GOLD).\n" +
                "3. Click 'DRAW' to trade in your discarded cards for brand new ones from the deck.\n" +
                "4. Highest traditional 5-card poker hand wins the round!"
        );
        rulesLabel.setFont(Font.font("Arial", 13));
        rulesLabel.setTextFill(Color.LIGHTGRAY);
        rulesLabel.setWrapText(true);
        
        introPanel.getChildren().addAll(gameTypeLabel, rulesLabel);

        // ---------- INSTRUCTIONS ----------
        instructionLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        instructionLabel.setTextFill(Color.WHITE);

        // ---------- RESULT ----------
        resultLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.EXTRA_BOLD,
                        24
                )
        );

        resultLabel.setTextFill(Color.YELLOW);

        // ---------- COMPUTER LABEL ----------
        Label computerLabel =
                new Label("COMPUTER");

        computerLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        22
                )
        );

        computerLabel.setTextFill(Color.WHITE);

        // ---------- PLAYER LABEL ----------
        Label playerLabel =
                new Label("PLAYER");

        playerLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        22
                )
        );

        playerLabel.setTextFill(Color.WHITE);

        // ---------- COMPUTER CARDS ----------
        HBox computerBox = new HBox(10);

        computerBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < 5; i++) {

            Label card = new Label("🂠");

            card.setPrefSize(80, 120);

            card.setStyle(
                    "-fx-border-color: black;" +
                    "-fx-background-color: white;" +
                    "-fx-font-size: 28px;" +
                    "-fx-alignment:center;" +
                    "-fx-border-radius:10;" +
                    "-fx-background-radius:10;"
            );

            computerCards[i] = card;

            computerBox.getChildren().add(card);
        }

        // ---------- PLAYER CARDS ----------
        HBox playerBox = new HBox(10);

        playerBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < 5; i++) {

            Button btn = new Button();

            btn.setPrefSize(100, 140);

            btn.setFont(
                    Font.font(
                            "Arial",
                            FontWeight.BOLD,
                            14
                    )
            );

            int index = i;

            btn.setOnAction(e -> {

                if (roundFinished)
                    return;

                heldCards[index] = !heldCards[index];

                if (heldCards[index]) {

                    btn.setStyle(
                            "-fx-background-color: gold;" +
                            "-fx-font-size:14;" +
                            "-fx-font-weight:bold;" +
                            "-fx-border-color:black;"
                    );
                }
                else {

                    btn.setStyle(
                            "-fx-font-size:14;" +
                            "-fx-font-weight:bold;"
                    );
                }
            });

            playerCards[i] = btn;

            playerBox.getChildren().add(btn);
        }

        updatePlayerCards();

        // ---------- DRAW BUTTON ----------
        Button drawBtn = new Button("DRAW");

        drawBtn.setPrefWidth(180);

        drawBtn.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        drawBtn.setStyle(
                "-fx-background-color:black;" +
                "-fx-text-fill:white;"
        );

        drawBtn.setOnAction(e -> {

            if (roundFinished)
                return;

            // Replace unheld cards
            for (int i = 0; i < 5; i++) {

                if (!heldCards[i]) {

                    player.replaceCard(
                            i,
                            deck.dealCard()
                    );
                }
            }

            updatePlayerCards();

            // Computer move
            Random random = new Random();

            for (int i = 0; i < 5; i++) {

                if (random.nextBoolean()) {

                    computer.replaceCard(
                            i,
                            deck.dealCard()
                    );
                }
            }

            revealComputerCards();

            showWinner();

            roundFinished = true;

            instructionLabel.setText(
                    "Press NEW GAME to play again"
            );
        });

        // ---------- NEW GAME ----------
        Button newGameBtn = new Button("NEW GAME");

        newGameBtn.setPrefWidth(180);

        newGameBtn.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        newGameBtn.setStyle(
                "-fx-background-color:darkred;" +
                "-fx-text-fill:white;"
        );

        newGameBtn.setOnAction(e -> {

            roundFinished = false;

            Arrays.fill(heldCards,false);

            startGame();

            updatePlayerCards();

            hideComputerCards();

            resultLabel.setText("");

            instructionLabel.setText(
                    "Click cards to HOLD them, then press DRAW"
            );

            for (Button b : playerCards) {

                b.setStyle(
                        "-fx-font-size:14;" +
                        "-fx-font-weight:bold;"
                );
            }
        });

        // ---------- BUTTON AREA ----------
        VBox controls = new VBox(
                15,
                drawBtn,
                newGameBtn,
                resultLabel
        );

        controls.setAlignment(Pos.CENTER);

        // ---------- CENTER AREA ----------
        VBox centerArea = new VBox(
                20,
                computerLabel,
                computerBox,
                playerLabel,
                playerBox
        );

        centerArea.setAlignment(Pos.CENTER);

        // ---------- ROOT ----------
        BorderPane root = new BorderPane();

        // Added the introPanel into the topArea Layout Flow
        VBox topArea = new VBox(
                12,
                titleLabel,
                introPanel,
                instructionLabel
        );

        topArea.setAlignment(Pos.CENTER);

        root.setTop(topArea);

        root.setCenter(centerArea);

        root.setBottom(controls);

        root.setStyle(
                "-fx-background-color:darkgreen;" +
                "-fx-padding:20;"
        );

        // ---------- SCENE ----------
        // Expanded window size slightly to fit the rules layout elegantly
        Scene scene = new Scene(root, 980, 750);

        stage.setTitle("Poker Game");

        stage.setScene(scene);

        stage.show();
    }

    // ---------- START GAME ----------
    void startGame() {

        deck = new Deck();

        deck.shuffle();

        player.clearHand();

        computer.clearHand();

        for (int i = 0; i < 5; i++) {

            player.addCard(deck.dealCard());

            computer.addCard(deck.dealCard());
        }
    }

    // ---------- UPDATE PLAYER ----------
    void updatePlayerCards() {

        for (int i = 0; i < 5; i++) {

            playerCards[i].setText(
                    player.hand.get(i).toString()
            );
        }
    }

    // ---------- HIDE COMPUTER ----------
    void hideComputerCards() {

        for (Label card : computerCards) {

            card.setText("🂠");
        }
    }

    // ---------- REVEAL COMPUTER ----------
    void revealComputerCards() {

        for (int i = 0; i < 5; i++) {

            computerCards[i].setText(
                    computer.hand.get(i).toString()
            );
        }
    }

    // ---------- SHOW WINNER ----------
    void showWinner() {

        int playerScore =
                HandEvaluator.evaluate(player.hand);

        int computerScore =
                HandEvaluator.evaluate(computer.hand);

        String result =
                "PLAYER: "
                        + HandEvaluator.getHandName(playerScore)
                        + "\nCOMPUTER: "
                        + HandEvaluator.getHandName(computerScore);

        if (playerScore > computerScore) {

            result += "\n\nYOU WIN!";
        }
        else if (computerScore > playerScore) {

            result += "\n\nCOMPUTER WINS!";
        }
        else {

            result += "\n\nDRAW!";
        }

        resultLabel.setText(result);
    }

    public static void main(String[] args) {

        launch();
    }
}