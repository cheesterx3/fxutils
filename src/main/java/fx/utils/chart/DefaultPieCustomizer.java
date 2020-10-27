package fx.utils.chart;

import javafx.collections.ListChangeListener;
import javafx.scene.chart.PieChart;
import javafx.scene.paint.Color;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.nonNull;

class DefaultPieCustomizer implements Customizer {
    private final PieChart pieChart;
    private final Function<String, Optional<Color>> colorByNameSupplier;
    private boolean isCustomized;

    DefaultPieCustomizer(PieChart pieChart, Function<String, Optional<Color>> colorByNameSupplier) {
        this.pieChart = pieChart;
        this.colorByNameSupplier = colorByNameSupplier;
    }

    @Override
    public void customize() {
        if (!isCustomized) {
            isCustomized = true;
            pieChart.getData().addListener((ListChangeListener<PieChart.Data>) c -> {
                while (c.next())
                    c.getAddedSubList().forEach(this::dataAdded);
            });
        }
    }

    private void dataAdded(PieChart.Data data) {
        colorByNameSupplier.apply(data.getName()).ifPresent(color -> {
            if (nonNull(data.getNode()))
                setNodeColor(color, data.getNode());
        });
    }
}
