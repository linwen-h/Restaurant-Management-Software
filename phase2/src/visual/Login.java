package visual;

import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Login extends Application {
	private static String CSS;
	public static Logger logger = Logger.getLogger("MyLog");//TODO: fix this
	public static FileHandler fh; //TODO: fix this

	@Override
	public void start(Stage primaryStage) throws Exception {
		CSS = getClass().getResource("/css/stylecomp.css").toExternalForm();
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

		primaryStage.setResizable(false);
		JFXDecorator decorator = new JFXDecorator(primaryStage, root);

		Scene sc = new Scene(decorator, 250, 300);
		sc.getStylesheets().add(getCSS());
		primaryStage.setScene(sc);
		primaryStage.show();

		try {
			// This block configure the logger with handler and formatter
			fh = new FileHandler("resources/data/log.txt", true);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	public static String getCSS() {
		return CSS;
	}
}
