/*
 * Copyright (C) 2014 たんらる
 */

package fourthline.mabiicco.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MabiIccoApplication extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("MainFrame.fxml"));

		Scene scene = new Scene(root);

		primaryStage.setTitle("MabiIcco");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
