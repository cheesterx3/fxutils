package fx.utils.chart;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

class DefaultChartLegendCustomizer implements Customizer {
    private final Map<Label, ListChangeListener<Node>> changeListenerMap = new HashMap<>();
    private final Chart chart;
    private final Function<String, Optional<Color>> colorByNameSupplier;
    private boolean isCustomized;

    DefaultChartLegendCustomizer(Chart chart, Function<String, Optional<Color>> colorByNameSupplier) {
        this.chart = chart;
        this.colorByNameSupplier = colorByNameSupplier;
    }

    public void customize() {
        if (!isCustomized) {
            isCustomized = true;
            final Node legend = chart.lookup(".chart-legend");
            if (legend instanceof Region) {
                final Region region = (Region) legend;
                region.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) c -> {
                    while (c.next()) {
                        c.getRemoved().forEach(this::legendChildRemoved);
                        c.getAddedSubList().forEach(this::legendChildAdded);
                    }
                });
            }
        }
    }

    private void legendChildAdded(Node node) {
        if (node instanceof Label) {
            final Label label = (Label) node;
            final Optional<Color> color = getColor(label.getText());
            final ListChangeListener<Node> listener = c1 -> {
                while (c1.next())
                    c1.getAddedSubList().forEach(node1 -> color.ifPresent(cl -> setNodeColor(cl, node1)));
            };
            label.getChildrenUnmodifiable().addListener(listener);
            changeListenerMap.put(label, listener);
        }
    }

    private Optional<Color> getColor(String text) {
        return colorByNameSupplier.apply(text);
    }

    private void legendChildRemoved(Node node) {
        if (node instanceof Label) {
            final Label label = (Label) node;
            final ListChangeListener<Node> listener = changeListenerMap.remove(label);
            label.getChildrenUnmodifiable().removeListener(listener);
        }
    }

}
