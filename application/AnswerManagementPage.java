package application;

import databasePart1.DatabaseHelper;
import application.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.util.Callback;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AnswerManagementPage {
	private final DatabaseHelper db;
	private final QuestionManager qMgr;
	private final AnswerManager aMgr;
	private final UserManager uMgr;
	private final User user;
	private final Question questionFilter;

	private TableView<Answer> answerTable;

	public AnswerManagementPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, User user,
			Question questionFilter, UserManager uMgr) {
		this.db = db;
		this.qMgr = qMgr;
		this.aMgr = aMgr;
		this.uMgr = uMgr;
		this.user = user;
		this.questionFilter = questionFilter;
	}
	
	public AnswerManagementPage(DatabaseHelper db, QuestionManager qMgr,
            AnswerManager aMgr, User user, UserManager uMgr) {
		this.db = db;
		this.qMgr = qMgr;
		this.aMgr = aMgr;
		this.user = user;
		this.questionFilter = null; // means "show all answers"
		this.uMgr = uMgr;
	}

	public void show(Stage stage) {
		Label title;
		if (questionFilter != null) {
			title = new Label("Manage Answers for Question: " + questionFilter.getTitle());
		} else {
			title = new Label("Manage All Answers");
		}
		title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		// Answer Table
		answerTable = new TableView<>();
		TableColumn<Answer, Integer> aIdCol = new TableColumn<>("ID");
		aIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<Answer, Integer> qRefCol = new TableColumn<>("Question ID");
		qRefCol.setCellValueFactory(new PropertyValueFactory<>("questionId"));

		TableColumn<Answer, String> aTextCol = new TableColumn<>("Answer");
		aTextCol.setCellValueFactory(new PropertyValueFactory<>("answerText"));
		
		TableColumn<Answer, String> createdByCol = new TableColumn<>("Created By");
		createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));

		answerTable.getColumns().addAll(aIdCol, qRefCol, aTextCol, createdByCol);
		answerTable.setPrefHeight(300);
		refreshAnswers();

		// Add Answer button
		Button addBtn = new Button("Add Answer");
		addBtn.setOnAction(e -> {
			
			if (questionFilter == null) {
                new Alert(Alert.AlertType.WARNING,
                          "You must select a specific question to add an answer.")
                          .showAndWait();
                return;
            }
			
			Dialog<String> dialog = new Dialog<>();
			dialog.setTitle("Add Answer");
			dialog.setHeaderText("Enter a new answer:");

			dialog.getDialogPane().setPrefHeight(300);
			dialog.getDialogPane().setPrefWidth(400);

			// Multi-line text area
			TextArea answerField = new TextArea();
			answerField.setWrapText(true);
			answerField.setPrefRowCount(6);
			answerField.setPrefHeight(200);

			VBox content = new VBox(10, new Label("Answer:"), answerField);
			dialog.getDialogPane().setContent(content);

			// Add OK/Cancel buttons
			ButtonType okButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == okButtonType) {
					return answerField.getText();
				}
				return null;
			});

			dialog.showAndWait().ifPresent(newText -> {
				try {
					// Save to DB
					aMgr.addAnswer(questionFilter.getId(), newText, user.getName());
					// Refresh table
					refreshAnswers();
				} catch (Exception ex) {
					ex.printStackTrace();
					new Alert(Alert.AlertType.ERROR, "Failed to add answer.").showAndWait();
				}
			});
		});

		// Update Answer button
		Button updateBtn = new Button("Update Answer");
		updateBtn.setOnAction(e -> {
			Answer selected = answerTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				new Alert(Alert.AlertType.WARNING, "Please select an answer to update.").showAndWait();
				return;
			}

			Dialog<String> dialog = new Dialog<>();
			dialog.setTitle("Update Answer");
			dialog.setHeaderText("Edit the answer below:");

			// Make the dialog taller
			dialog.getDialogPane().setPrefHeight(300);
			dialog.getDialogPane().setPrefWidth(400);

			// Multi-line text area
			TextArea updateField = new TextArea(selected.getAnswerText());
			updateField.setWrapText(true);
			updateField.setPrefRowCount(6);
			updateField.setPrefHeight(200);

			VBox content = new VBox(10, new Label("Answer:"), updateField);
			dialog.getDialogPane().setContent(content);

			ButtonType okButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == okButtonType) {
					return updateField.getText();
				}
				return null;
			});

			dialog.showAndWait().ifPresent(updatedText -> {
				try {
					// Update in DB
					aMgr.updateAnswer(selected.getId(), updatedText);
					// Update in table
					selected.setAnswerText(updatedText);
					answerTable.refresh();
				} catch (Exception ex) {
					ex.printStackTrace();
					new Alert(Alert.AlertType.ERROR, "Failed to update answer.").showAndWait();
				}
			});
		});

		Button deleteBtn = new Button("Delete Answer");
		deleteBtn.setOnAction(e -> {
			Answer selected = answerTable.getSelectionModel().getSelectedItem();
			if (selected != null) {
				try {
					aMgr.deleteAnswer(selected.getId());
					refreshAnswers();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		HBox controls = new HBox(10, addBtn, updateBtn, deleteBtn);

		// Back Button
		Button backBtn = new Button("Back to Dashboard");
		backBtn.setOnAction(e -> new AdminDashboardPage(db, qMgr, aMgr, user, uMgr).show(stage));

		VBox root = new VBox(15, title, answerTable, controls, backBtn);
		root.setPadding(new Insets(15));
		root.setAlignment(Pos.CENTER);

		stage.setScene(new Scene(root, 700, 500));
		stage.setTitle("Answer Management");
		stage.show();
	}

	private void refreshAnswers() {
		try {
			if (questionFilter != null) {
				// Only answers for the selected question
				ObservableList<Answer> data = FXCollections
						.observableArrayList(aMgr.getAnswersByQuestionId(questionFilter.getId()));
				answerTable.setItems(data);
			} else {
				// Show all answers
				ObservableList<Answer> data = FXCollections.observableArrayList(aMgr.getAllAnswers());
				answerTable.setItems(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
