package TPE.back;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;

public class MyLine extends Line {
	private int draw = 0;
	//Inicializa la linea
	public MyLine(boolean horizontal) {
		if(horizontal) {
			setStartX(0);
			setEndX(20);
		} else {
			setStartY(0);
			setEndY(20);
		}
		setStrokeWidth(7);
		setOpacity(0);
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(getOpacity() != 1) {
					setOpacity(0.25);
				}
			}
		});
		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(getOpacity() != 1) {
					setOpacity(0);
				}
			}
		});
	}
	public void drawLine() {
		draw = 1;
		setOpacity(1);
	}
	public void unDrawLine() {
		draw = 0;
		setOpacity(0);
	}
	public boolean isDraw() {
		return (draw == 1);
	}
	public int draw() {
		return draw;
	}
}
