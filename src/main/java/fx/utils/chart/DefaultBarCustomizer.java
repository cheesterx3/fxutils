package fx.utils.chart;


import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.util.Objects.nonNull;

class DefaultBarCustomizer implements Customizer {
    private final BarChart<String, ?> barChart;
    private final Function<String, Optional<Color>> colorByNameSupplier;
    private final Map<XYChart.Series<String, ?>, ListChangeListener<XYChart.Data<String, ?>>> seriesListenerMap = new ConcurrentHashMap<>();
    private final Map<XYChart.Data<String, ?>, ChangeListener<Node>> dataChangeListenerMap = new ConcurrentHashMap<>();
    private boolean isCustomized;

    DefaultBarCustomizer(BarChart<String, ?> barChart, Function<String, Optional<Color>> colorByNameSupplier) {
        this.barChart = barChart;
        this.colorByNameSupplier = colorByNameSupplier;
    }

    public void customize() {
        if (!isCustomized) {
            isCustomized = true;
            barChart.getData().addListener((ListChangeListener<XYChart.Series<String, ?>>) c -> {
                while (c.next()) {
                    c.getRemoved().forEach(this::seriesRemoved);
                    c.getAddedSubList().forEach(this::seriesAdded);
                }
            });
        }
    }

    private void seriesAdded(XYChart.Series<String, ?> stringSeries) {
        final ListChangeListener<XYChart.Data<String, ?>> listener = change -> {
            while (change.next()) {
                change.getAddedSubList().forEach(this::updateColor);
                change.getRemoved().forEach(this::dataRemoved);
            }
        };
        stringSeries.getData().forEach(this::updateColor);
        stringSeries.getData().addListener(listener);
        seriesListenerMap.put(stringSeries, listener);
    }

    private void dataRemoved(XYChart.Data<String, ?> data) {
        clearDataListener(data);
    }

    private void seriesRemoved(XYChart.Series<String, ?> stringSeries) {
        final ListChangeListener<XYChart.Data<String, ?>> listener = seriesListenerMap.remove(stringSeries);
        stringSeries.getData().removeListener(listener);
    }

    private void updateColor(XYChart.Data<String, ?> data) {
        colorByNameSupplier.apply(data.getXValue()).ifPresent(color -> {
            if (nonNull(data.getNode()))
                setNodeColor(color, data.getNode());
            else {
                final ChangeListener<Node> nodeChangeListener = (observable, oldValue, newValue) -> {
                    if (nonNull(newValue)) setNodeColor(color, newValue);
                };
                clearDataListener(data);
                data.nodeProperty().addListener(nodeChangeListener);
                dataChangeListenerMap.put(data, nodeChangeListener);
            }
        });
    }

    private void clearDataListener(XYChart.Data<String, ?> data) {
        final ChangeListener<Node> listener = dataChangeListenerMap.remove(data);
        if (nonNull(listener))
            data.nodeProperty().removeListener(listener);
    }
}
