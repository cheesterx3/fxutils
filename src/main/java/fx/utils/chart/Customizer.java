package fx.utils.chart;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public interface Customizer {
    void customize();

    default void setNodeColor(Color color, Node node) {
        node.setStyle(String.format("-fx-background-color: RGB(%d,%d,%d);",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)));
    }
}
