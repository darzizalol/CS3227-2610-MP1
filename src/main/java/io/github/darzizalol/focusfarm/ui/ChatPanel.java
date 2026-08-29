package io.github.darzizalol.focusfarm.ui;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Right-side chat transcript and command entry panel. */
public final class ChatPanel extends VBox {
    private final TextArea transcript;
    private final TextField commandField;

    /** Creates a chat panel that sends submitted commands to the callback. */
    public ChatPanel(Consumer<String> commandHandler) {
        Objects.requireNonNull(commandHandler);
        getStyleClass().add("chat-panel");
        setSpacing(10);
        setPadding(new Insets(18));

        Label title = new Label("FARM TERMINAL");
        title.getStyleClass().add("panel-title");

        Label hint = new Label("Type /help for commands");
        hint.getStyleClass().add("panel-hint");

        transcript = new TextArea();
        transcript.setEditable(false);
        transcript.setWrapText(true);
        transcript.getStyleClass().add("chat-transcript");
        VBox.setVgrow(transcript, Priority.ALWAYS);

        commandField = new TextField();
        commandField.setPromptText("/plant 1 carrot 10s");
        commandField.getStyleClass().add("command-field");
        HBox.setHgrow(commandField, Priority.ALWAYS);

        Button sendButton = new Button("SEND");
        sendButton.getStyleClass().add("send-button");

        Runnable submit = () -> {
            String command = commandField.getText();
            if (!command.isBlank()) {
                commandField.clear();
                commandHandler.accept(command);
            }
            commandField.requestFocus();
        };
        commandField.setOnAction(event -> submit.run());
        sendButton.setOnAction(event -> submit.run());

        HBox inputRow = new HBox(8, commandField, sendButton);
        getChildren().addAll(title, hint, transcript, inputRow);
    }

    /** Appends a command entered by the user. */
    public void appendUser(String message) {
        append("YOU", message);
    }

    /** Appends a normal response from Focus Farm. */
    public void appendFarm(String message) {
        append("FARM", message);
    }

    /** Appends a highlighted error response. */
    public void appendError(String message) {
        append("ERROR", message);
    }

    /** Places keyboard focus in the command field. */
    public void focusInput() {
        commandField.requestFocus();
    }

    private void append(String sender, String message) {
        if (!transcript.getText().isEmpty()) {
            transcript.appendText(System.lineSeparator() + System.lineSeparator());
        }
        transcript.appendText("[" + sender + "] " + message);
        transcript.positionCaret(transcript.getLength());
    }
}
