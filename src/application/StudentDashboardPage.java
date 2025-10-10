package application;

import databasePart1.DatabaseHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.util.List;
import java.util.Arrays;




public class StudentDashboardPage {
    private final DatabaseHelper db;
    private final QuestionManager qMgr;
    private final AnswerManager aMgr;
    private final UserManager uMgr;
    private final CommentManager cMgr;
    private final User user;
    
    private BorderPane root;
    private VBox contentArea;


    public StudentDashboardPage(DatabaseHelper db, QuestionManager qMgr, AnswerManager aMgr, UserManager uMgr, CommentManager cMgr, User user) {
        this.db = db;
        this.qMgr = qMgr;
        this.aMgr = aMgr;
        this.uMgr = uMgr;
        this.cMgr = cMgr;
        this.user = user;
    }

    public void show(Stage stage) {
        root = new BorderPane();

        // --- Sidebar ---
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(15));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #ECF0F1;");

        Button newQuestionBtn = new Button("New Question");
        Button myQuestionsBtn = new Button("My Questions");
        Button allQuestionsBtn = new Button("All Questions");

        String sidebarBtnStyle = "-fx-background-color: transparent; -fx-text-fill: #2C3E50; "
                               + "-fx-font-size: 14px; -fx-alignment: CENTER_LEFT;";
        String sidebarBtnHover = "-fx-background-color: #BDC3C7; -fx-text-fill: #2C3E50; "
                               + "-fx-font-size: 14px; -fx-alignment: CENTER_LEFT;";

        for (Button btn : new Button[]{newQuestionBtn, myQuestionsBtn, allQuestionsBtn}) {
            btn.setStyle(sidebarBtnStyle);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnMouseEntered(e -> btn.setStyle(sidebarBtnHover));
            btn.setOnMouseExited(e -> btn.setStyle(sidebarBtnStyle));
        }

        sidebar.getChildren().addAll(newQuestionBtn, myQuestionsBtn, allQuestionsBtn);

        // --- Content Area ---
        contentArea = new VBox();
        contentArea.setPadding(new Insets(10));
        contentArea.getChildren().add(new Label("Select a tab to view content"));

        // --- Top Banner ---
        HBox topBanner = new HBox(15);
        topBanner.setStyle("-fx-background-color: #2C3E50; -fx-padding: 10;");
        topBanner.setAlignment(Pos.CENTER_RIGHT);

        Button updateProfileBtn = new Button("Update Profile");
        Button logoutBtn = new Button("Logout");

        String updateStyle = "-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold;";
        String updateHover = "-fx-background-color: #2980B9; -fx-text-fill: white; -fx-font-weight: bold;";
        String logoutStyle = "-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;";
        String logoutHover = "-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-weight: bold;";

        updateProfileBtn.setStyle(updateStyle);
        updateProfileBtn.setOnMouseEntered(e -> updateProfileBtn.setStyle(updateHover));
        updateProfileBtn.setOnMouseExited(e -> updateProfileBtn.setStyle(updateStyle));
        updateProfileBtn.setOnAction(e -> {
            UpdateAccountPage updatePage = new UpdateAccountPage(db, qMgr, aMgr, uMgr, cMgr, user);
            updatePage.show(stage);
        });

        logoutBtn.setStyle(logoutStyle);
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(logoutHover));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(logoutStyle));
        logoutBtn.setOnAction(e -> {
            SetupLoginSelectionPage loginSelectionPage = new SetupLoginSelectionPage(db, qMgr, aMgr, uMgr, cMgr);
            loginSelectionPage.show(stage);
        });

        topBanner.getChildren().addAll(updateProfileBtn, logoutBtn);

        // --- Sub Banner Tabs ---
        HBox subBanner = new HBox(10);
        subBanner.setStyle("-fx-padding: 10;");
        subBanner.setAlignment(Pos.CENTER_LEFT);

        Button unresolvedTab = new Button("Unresolved Questions");
        Button resolvedTab   = new Button("Resolved Questions");
        Button allTab        = new Button("All Questions");

        String tabStyle = "-fx-background-color: #BDC3C7; -fx-text-fill: #2C3E50; "
                        + "-fx-font-size: 16px; -fx-font-weight: bold; "
                        + "-fx-background-radius: 6; -fx-padding: 8 16 8 16;";
        String tabHover = "-fx-background-color: #95A5A6; -fx-text-fill: white; "
                        + "-fx-font-size: 16px; -fx-font-weight: bold; "
                        + "-fx-background-radius: 6; -fx-padding: 8 16 8 16;";
        String tabActive = "-fx-background-color: #2C3E50; -fx-text-fill: white; "
                         + "-fx-font-size: 16px; -fx-font-weight: bold; "
                         + "-fx-background-radius: 6; -fx-padding: 8 16 8 16;";

        List<Button> tabs = Arrays.asList(unresolvedTab, resolvedTab, allTab);

        for (Button tab : tabs) {
            tab.setStyle(tabStyle);
            tab.setOnMouseEntered(e -> {
                if (!tab.getStyle().equals(tabActive)) tab.setStyle(tabHover);
            });
            tab.setOnMouseExited(e -> {
                if (!tab.getStyle().equals(tabActive)) tab.setStyle(tabStyle);
            });
            subBanner.getChildren().add(tab);
        }

        // Reset helper
        Runnable resetTabs = () -> {
            for (Button tab : tabs) tab.setStyle(tabStyle);
        };

        // Tab actions
        unresolvedTab.setOnAction(e -> {
            resetTabs.run();
            unresolvedTab.setStyle(tabActive);
            contentArea.getChildren().setAll(buildUnresolvedView());
        });

        resolvedTab.setOnAction(e -> {
            resetTabs.run();
            resolvedTab.setStyle(tabActive);
            contentArea.getChildren().setAll(buildResolvedView());
        });

        allTab.setOnAction(e -> {
            resetTabs.run();
            allTab.setStyle(tabActive);
            contentArea.getChildren().setAll(buildAllQuestionsView());
        });

        // --- Center Wrapper ---
        VBox centerWrapper = new VBox();
        centerWrapper.getChildren().addAll(subBanner, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // --- Root Layout ---
        root.setTop(topBanner);
        root.setLeft(sidebar);
        root.setCenter(centerWrapper);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Student Dashboard");
        stage.show();

        // Sidebar handlers
        newQuestionBtn.setOnAction(e -> contentArea.getChildren().setAll(buildNewQuestionForm()));
        myQuestionsBtn.setOnAction(e -> contentArea.getChildren().setAll(buildMyQuestionsView()));
        allQuestionsBtn.setOnAction(e -> contentArea.getChildren().setAll(buildAllQuestionsView()));
    }
    
    private VBox buildQuestionThread(Question q) {
        VBox threadBox = new VBox(10);
        threadBox.setPadding(new Insets(10));
        threadBox.setStyle("-fx-background-color: #fff; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        //  Question Header 
        Label qLabel = new Label(q.getTitle() + " — " + q.getDescription() + " (by " + q.getCreatedBy() + ")");
        qLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        threadBox.getChildren().add(qLabel);

        //  Question Comments 
        VBox qCommentsBox = new VBox(5);
        try {
            for (Comment c : cMgr.getRootCommentsForQuestion(q.getId())) {
                qCommentsBox.getChildren().add(buildCommentThread(c, q, 20));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            qCommentsBox.getChildren().add(new Label("Error loading comments."));
        }
        threadBox.getChildren().add(qCommentsBox);

        //  Answers Section 
        VBox answersBox = new VBox(8);
        answersBox.setPadding(new Insets(5, 0, 0, 20));

        try {
            for (Answer a : aMgr.getAnswersByQuestionId(q.getId())) {
                VBox aBox = new VBox(6);
                aBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 6; -fx-border-color: #ccc; -fx-border-radius: 4;");

                // Answer row
                HBox answerRow = new HBox(8);
                answerRow.setAlignment(Pos.CENTER_LEFT);

                Label aLabel = new Label(a.getAnswerText() + " (by " + a.getCreatedBy() + ")");
                if (a.isAccepted()) {
                    aLabel.setStyle("-fx-background-color: #eaffea; -fx-border-color: green; -fx-padding: 3;");
                }
                answerRow.getChildren().add(aLabel);

                // Delete only if this user wrote the answer
                if (a.getCreatedBy().equalsIgnoreCase(user.getName())) {
                    Hyperlink deleteLink = new Hyperlink("Delete");
                    deleteLink.setStyle("-fx-text-fill: red;");
                    deleteLink.setOnAction(ev -> {
                        try {
                            aMgr.deleteAnswer(a.getId());
                            contentArea.getChildren().setAll(buildSingleQuestionView(q));
                        } catch (Exception ex) { ex.printStackTrace(); }
                    });
                    answerRow.getChildren().add(deleteLink);
                }

                aBox.getChildren().add(answerRow);

                // Accept button only for question owner
                if (q.getCreatedBy().equals(user.getName())) {
                    if (a.isAccepted()) {
                        
                        Button unacceptBtn = new Button("Unaccept");
                        unacceptBtn.setStyle("-fx-background-color: orange; -fx-font-weight: bold;");
                        unacceptBtn.setOnAction(ev -> {
                            try {
                                aMgr.unacceptAnswer(q.getId()); // clears accepted + sets resolved = false
                                contentArea.getChildren().setAll(buildSingleQuestionView(q));
                            } catch (Exception ex) { ex.printStackTrace(); }
                        });
                        aBox.getChildren().add(unacceptBtn);
                    } else {
                       
                        Button acceptBtn = new Button("Mark as Accepted");
                        acceptBtn.setOnAction(ev -> {
                            try {
                                aMgr.markAcceptedAnswer(a.getId(), q.getId());
                                contentArea.getChildren().setAll(buildSingleQuestionView(q));
                            } catch (Exception ex) { ex.printStackTrace(); }
                        });
                        aBox.getChildren().add(acceptBtn);
                    }
                }

                //  Answer Comments 
                VBox commentsBox = new VBox(5);
                commentsBox.setPadding(new Insets(5, 0, 0, 20));
                try {
                    for (Comment c : cMgr.getRootCommentsForAnswer(a.getId())) {
                        commentsBox.getChildren().add(buildCommentThread(c, q, 20));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    commentsBox.getChildren().add(new Label("Error loading comments."));
                }

                // Reply link for new comment on this answer
                Hyperlink replyLink = new Hyperlink("Reply");
                replyLink.setOnAction(e -> {
                    HBox addCommentBox = new HBox(5);
                    TextField commentField = new TextField();
                    commentField.setPromptText("Write a reply...");
                    Hyperlink submit = new Hyperlink("Submit");
                    Hyperlink cancel = new Hyperlink("Cancel");

                    submit.setOnAction(ev -> {
                        try {
                            cMgr.addCommentToAnswer(a.getId(), commentField.getText(), user.getName());
                            contentArea.getChildren().setAll(buildSingleQuestionView(q));
                        } catch (Exception ex) { ex.printStackTrace(); }
                    });

                    cancel.setOnAction(ev -> commentsBox.getChildren().remove(addCommentBox));

                    addCommentBox.getChildren().addAll(commentField, submit, cancel);
                    commentsBox.getChildren().add(addCommentBox);
                    replyLink.setDisable(true);
                });

                aBox.getChildren().addAll(commentsBox, replyLink);
                answersBox.getChildren().add(aBox);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            answersBox.getChildren().add(new Label("Error loading answers."));
        }
        
        //  Add new answer input 
        VBox addAnswerBox = new VBox(5);
        addAnswerBox.setPadding(new Insets(10, 0, 0, 20));

        Label answerPrompt = new Label("Your Answer:");
        TextArea answerField = new TextArea();
        answerField.setPromptText("Write your answer here...");
        answerField.setWrapText(true);
        answerField.setPrefRowCount(3);

        Button postAnswerBtn = new Button("Post Answer");
        postAnswerBtn.setOnAction(e -> {
            try {
                aMgr.addAnswer(q.getId(), answerField.getText(), user.getName());
                contentArea.getChildren().setAll(buildSingleQuestionView(q)); // refresh
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addAnswerBox.getChildren().addAll(answerPrompt, answerField, postAnswerBtn);
        answersBox.getChildren().add(addAnswerBox);

        threadBox.getChildren().add(answersBox);
        return threadBox;
    }
    
    private VBox buildCommentThread(Comment c, Question q, int indent) {
        VBox commentBox = new VBox(4);
        commentBox.setPadding(new Insets(0, 0, 0, indent));

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Label commentLabel = new Label(c.getText() + " (by " + c.getCreatedBy() + ")");
        row.getChildren().add(commentLabel);

        // Delete link only for author
        if (c.getCreatedBy().equalsIgnoreCase(user.getName())) {
            Hyperlink deleteLink = new Hyperlink("Delete");
            deleteLink.setStyle("-fx-text-fill: red;");
            deleteLink.setOnAction(ev -> {
                try {
                    cMgr.deleteComment(c.getId());
                    contentArea.getChildren().setAll(buildSingleQuestionView(q));
                } catch (Exception ex) { ex.printStackTrace(); }
            });
            row.getChildren().add(deleteLink);
        }

        // Reply link
        Hyperlink replyLink = new Hyperlink("Reply");
        replyLink.setOnAction(ev -> {
            HBox addReplyBox = new HBox(5);
            TextField replyField = new TextField();
            replyField.setPromptText("Write a reply...");
            Hyperlink submit = new Hyperlink("Submit");
            Hyperlink cancel = new Hyperlink("Cancel");

            submit.setOnAction(subEv -> {
                try {
                    cMgr.addReplyToComment(c.getId(), replyField.getText(), user.getName());
                    contentArea.getChildren().setAll(buildSingleQuestionView(q));
                } catch (Exception ex) { ex.printStackTrace(); }
            });

            cancel.setOnAction(subEv -> commentBox.getChildren().remove(addReplyBox));

            addReplyBox.getChildren().addAll(replyField, submit, cancel);
            commentBox.getChildren().add(addReplyBox);
            replyLink.setDisable(true);
        });
        row.getChildren().add(replyLink);

        commentBox.getChildren().add(row);

        // Recursively render child comments
        try {
            for (Comment child : cMgr.getReplies(c.getId())) {
                commentBox.getChildren().add(buildCommentThread(child, q, indent + 20));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return commentBox;
    }


    
    private Node buildSingleQuestionView(Question q) {
        ScrollPane scroll = new ScrollPane(buildQuestionThread(q));
        scroll.setFitToWidth(true);
        return scroll;
    }

   
    private Node buildMyQuestionsView() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label header = new Label("My Questions with Answers & Comments");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        box.getChildren().add(header);

        try {
            List<Question> myQuestions = qMgr.getQuestionsByUser(user.getName());
            for (Question q : myQuestions) {
                VBox qThread = buildQuestionThread(q);
                box.getChildren().add(qThread);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            box.getChildren().add(new Label("Error loading questions."));
        }

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        return scroll;
    }
    
    
    private Node buildNewQuestionForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(15));

        Label header = new Label("Ask a New Question");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField titleField = new TextField();
        titleField.setPromptText("Enter question title...");

        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Describe your question...");
        bodyField.setPrefRowCount(6);

        Button submitBtn = new Button("Submit Question");
        Label status = new Label();

        submitBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String body = bodyField.getText().trim();

            if (title.isEmpty() || body.isEmpty()) {
                status.setText("Title and description are required.");
                status.setStyle("-fx-text-fill: red;");
                return;
            }

            try {
                // QuestionManager to save the question
                Question q = qMgr.addQuestion(title, body, user.getName());

                status.setText("Question submitted successfully!");
                status.setStyle("-fx-text-fill: green;");

                // Clear fields
                titleField.clear();
                bodyField.clear();
            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
                status.setStyle("-fx-text-fill: red;");
                ex.printStackTrace();
            }
        });

        form.getChildren().addAll(header, titleField, bodyField, submitBtn, status);
        return form;
    }
    
    private Node buildAllQuestionsView() {
    	VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search questions");

        ComboBox<String> filterBox = new ComboBox<>();
        filterBox.getItems().addAll("All", "Unresolved", "Resolved");
        filterBox.setValue("All");

        HBox searchBar = new HBox(10, searchField, filterBox);

        TableView<Question> questionTable = new TableView<>();

        TableColumn<Question, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Question, String> createdByCol = new TableColumn<>("Asked By");
        createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));

        TableColumn<Question, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        questionTable.getColumns().addAll(titleCol, createdByCol, statusCol);
        
        ObservableList<Question> questions = FXCollections.observableArrayList();
        try {
            questions.addAll(qMgr.getAllQuestions());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Filtering logic
        FilteredList<Question> filteredData = new FilteredList<>(questions, p -> true);

        // Search filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(q -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return q.getTitle().toLowerCase().contains(lower) ||
                       q.getDescription().toLowerCase().contains(lower);
            });
        });

        // Status filter
        filterBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(q -> {
                if ("Unresolved".equals(newVal)) return !q.isResolved();
                if ("Resolved".equals(newVal)) return q.isResolved();
                return true; // "All"
            });
        });

        SortedList<Question> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(questionTable.comparatorProperty());

        questionTable.setItems(sortedData);
        
        // Single Question View
        questionTable.setRowFactory(tv -> {
            TableRow<Question> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (!row.isEmpty() && ev.getClickCount() == 2) {
                    Question q = row.getItem();
                    contentArea.getChildren().setAll(buildSingleQuestionView(q));
                }
            });
            return row;
        });

        box.getChildren().addAll(searchBar, questionTable);
        return box;
    }
    
    private VBox buildResolvedView() {
        VBox resolvedView = new VBox(10);
        resolvedView.setPadding(new Insets(10));

        try {
            List<Question> resolved = qMgr.getResolvedQuestionsByUser(user.getName());
            if (resolved.isEmpty()) {
                resolvedView.getChildren().add(new Label("No resolved questions yet."));
            } else {
                for (Question q : resolved) {
                    Button qBtn = new Button(q.getTitle());
                    qBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E50; "
                                + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT;");
                    qBtn.setMaxWidth(Double.MAX_VALUE);

                    // Hover effect
                    qBtn.setOnMouseEntered(ev -> qBtn.setStyle("-fx-background-color: #BDC3C7; -fx-text-fill: #2C3E50; "
                                                             + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT;"));
                    qBtn.setOnMouseExited(ev -> qBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E50; "
                                                            + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT;"));

                    // Click opens full thread
                    qBtn.setOnAction(ev -> {
                        contentArea.getChildren().clear();
                        contentArea.getChildren().add(buildQuestionThread(q));
                    });

                    resolvedView.getChildren().add(qBtn);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resolvedView.getChildren().add(new Label("Error loading resolved questions."));
        }

        return resolvedView;
    } 
    
    private VBox buildUnresolvedView() {
        VBox unresolvedView = new VBox(10);
        unresolvedView.setPadding(new Insets(10));

        try {
            List<Question> unresolved = qMgr.getUnresolvedQuestionsByUser(user.getName());
            if (unresolved.isEmpty()) {
                unresolvedView.getChildren().add(new Label("No unresolved questions."));
            } else {
                for (Question q : unresolved) {
                    int unreadCount = aMgr.countUnreadAnswers(q.getId(), user.getName());

                    // Show question title + unread count
                    Button qBtn = new Button(q.getTitle() + "  (" + unreadCount + " unread)");
                    qBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E50; "
                                + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT;");
                    qBtn.setMaxWidth(Double.MAX_VALUE);

                    // Hover effect
                    qBtn.setOnMouseEntered(ev -> qBtn.setStyle("-fx-background-color: #BDC3C7; -fx-text-fill: #2C3E50; "
                                                             + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT;"));
                    qBtn.setOnMouseExited(ev -> qBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E50; "
                                                            + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: CENTER_LEFT;"));

                    // Click opens full thread
                    qBtn.setOnAction(ev -> {
                        contentArea.getChildren().clear();
                        contentArea.getChildren().add(buildQuestionThread(q));
                    });

                    unresolvedView.getChildren().add(qBtn);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            unresolvedView.getChildren().add(new Label("Error loading unresolved questions."));
        }

        return unresolvedView;
    }
   
} 







