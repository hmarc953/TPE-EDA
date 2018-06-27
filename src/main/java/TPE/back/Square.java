package TPE.back;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Square extends Text {
	private Player player;
	public Square(Player p) {
		player = p;
		setText(p.name());
		setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
	}
	public Player player() {
		return player;
	}
}
