import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.HashMap;
import java.util.Map;


//JavaFX calculator class.
public class JavaFXCalculator extends Application {
  
  //creating application template
	private static final String[][] template = {
	{ "CE", "c", "Del", "/" },
	{ "7", "8", "9", "*" },
	{ "4", "5", "6", "-" },
	{ "1", "2", "3", "+" },
	{ "", "0", ".", "=" }
};


private final Map<String, Button> accelerators = new HashMap<>();

// Create stack value
private DoubleProperty stackValue = new SimpleDoubleProperty();
private DoubleProperty value = new SimpleDoubleProperty();

private enum Op { Calculator, ADD, SUBTRACT, MULTIPLY, DIVIDE }

private Op currentOp = Op.Calculator;

private Op stackOp = Op.Calculator;


public static void main(String[] args) { launch(args); }

@Override // Override the start method in the Application class

public void start(Stage primaryStage) {
final TextField screen = screen();
final TilePane buttons = buttons();
primaryStage.setTitle(" CALCULATOR");
primaryStage.initStyle(StageStyle.UTILITY);

primaryStage.setResizable(false);// making Resizing impossible
primaryStage.setScene(new Scene(createLayout(screen, buttons)));
primaryStage.show();
}


private VBox createLayout(TextField screen, TilePane buttons) {

final VBox layout = new VBox(35); 

//aligning and style 
layout.setAlignment(Pos.CENTER);
layout.setStyle(" -fx-padding: 20; -fx-font-size: 25;");
layout.getChildren().setAll(screen, buttons);
handleAccelerators(layout);
screen.prefWidthProperty().bind(buttons.widthProperty());
return layout;
}

private void handleAccelerators(VBox layout) {
layout.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

@Override

public void handle(KeyEvent keyEvent) {
Button activated = accelerators.get(keyEvent.getText());
if (activated != null) {
activated.fire();
}
}
});
}


private TextField screen() {
final TextField screen = new TextField();
screen.setStyle("-fx-background-color: BLUE;");
screen.setAlignment(Pos.CENTER_RIGHT);
screen.setEditable(false); //setting the screen uneditable
screen.textProperty().bind(Bindings.format("%.0f", value));
return screen;
}

//creating the buttons 
private TilePane buttons() {
TilePane buttons = new TilePane();
buttons.setVgap(10);
buttons.setHgap(10);
buttons.setPrefColumns(template[0].length);
for (String[] r: template) {
for (String string: r) {
	//placing the buttons in the scene
buttons.getChildren().add(createButton(string)); 
}
}
return buttons;
}


private Button createButton(final String string) {
Button button = makeStandardButton(string);
if (string.matches("[0-9]")) { 
makeNumericButton(string, button);
} else {
final ObjectProperty<Op> startOp = determineOperand(string);
if (startOp.get() != Op.Calculator) {
makeOperandButton(button, startOp);
} else if ("c".equals(string)) { 
makeClearButton(button);
} else if ("=".equals(string)) { 
makeEqualsButton(button);
}
}
return button;
}


private ObjectProperty<Op> determineOperand(String string) {
final ObjectProperty<Op> startOp = new SimpleObjectProperty<>(Op.Calculator);

//switch statements
switch (string) {
case "+": startOp.set(Op.ADD); break;
case "-": startOp.set(Op.SUBTRACT); break;
case "*": startOp.set(Op.MULTIPLY); break;
case "/": startOp.set(Op.DIVIDE); break;
}
return startOp;
}


private void makeOperandButton(Button button, final ObjectProperty<Op> startOp) {
button.setStyle("-fx-base: lightblue;");
button.setOnAction(new EventHandler<ActionEvent>() {

@Override

public void handle(ActionEvent e) {
currentOp = startOp.get();
}
});
}


private Button makeStandardButton(String string) {
Button button = new Button(string);
button.setStyle("-fx-base: beige;");
accelerators.put(string, button);
button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
return button;
}
private void makeNumericButton(final String string, Button button) {
button.setOnAction(new EventHandler<ActionEvent>() {
@Override
public void handle(ActionEvent e) {
if (currentOp == Op.Calculator) {
value.set(value.get() * 10 + Integer.parseInt(string));
} else {
stackValue.set(value.get());
value.set(Integer.parseInt(string));
stackOp = currentOp;
currentOp = Op.Calculator;
}
}
});
}

	//creating a clear button
private void makeClearButton(Button button) {
//setting an action EventHandler
button.setOnAction(new EventHandler<ActionEvent>() { 

@Override

public void handle(ActionEvent e) {
value.set(0);
}
});
}

//creating = button
private void makeEqualsButton(Button button) {
button.setOnAction(new EventHandler<ActionEvent>() {


@Override
public void handle(ActionEvent e) {

	//Switch statements
switch (stackOp) {
case ADD: value.set(stackValue.get() + value.get()); break;
case SUBTRACT: value.set(stackValue.get() - value.get()); break;
case MULTIPLY: value.set(stackValue.get() * value.get()); break;
case DIVIDE: value.set(stackValue.get() / value.get()); break;
}
}
});
}
}