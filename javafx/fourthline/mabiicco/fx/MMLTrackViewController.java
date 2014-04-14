/*
 * Copyright (C) 2014 たんらる
 */

package fourthline.mabiicco.fx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import fourthline.mabiicco.midi.InstClass;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class MMLTrackViewController implements Initializable {

	public ComboBox<InstClass> instComboBox;
	public ComboBox<InstClass> songComboBox;
	public Button muteButton;
	public Button soloButton;
	public Button allButton;
	public Label rankView;
	public ToggleButton melodyButton;
	public ToggleButton chord1Button;
	public ToggleButton chord2Button;
	public ToggleButton songButton;
	public TextField melodyText;
	public TextField chord1Text;
	public TextField chord2Text;
	public TextField songText;

	public static Node createMMLTrackView() {
		try {
			return FXMLLoader.load(MMLTrackViewController.class.getResource("MMLTrackView.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new AssertionError("", e);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
}
