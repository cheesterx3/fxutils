package fx.utils.chart;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.paint.Color;

import java.util.Optional;
import java.util.function.Function;

/**
 * Simple utility class.
 * This class helps to customize colors of chart pies and bars with legend according to names.
 * <br/>
 * Usage:
 * <pre><code>
 *
 *     final static String austria = "Austria";
 *     final static String brazil = "Brazil";
 *     final static String france = "France";
 *     final Map<String, Color> colorMapping = Map.of(austria, Color.RED, brazil, Color.BLUE, france, COLOR.GREEN);
 *
 *     final PieChart chart = new PieChart();
 *     ChartCustomizers.forPieChart(chart, name -> colorMapping.getOrDefault(name, Color.BLACK)).customize();
 *
 * </code></pre>
 */
public final class ChartCustomizers {
    private ChartCustomizers() {
    }

    /**
     * Constructs customizer for bar chart
     *
     * @param barChart            instance of bar Chart
     * @param colorByNameSupplier color supplier according to data name
     * @return instance of customizer
     */
    public static Customizer forBarChart(BarChart<String, ?> barChart, Function<String, Optional<Color>> colorByNameSupplier) {
        return new ChartCustomizer(new DefaultChartLegendCustomizer(barChart, colorByNameSupplier),
                new DefaultBarCustomizer(barChart, colorByNameSupplier));
    }

    /**
     * Constructs customizer for pie chart
     *
     * @param chart               instance of pie Chart
     * @param colorByNameSupplier color supplier according to data name
     * @return instance of customizer
     */
    public static Customizer forPieChart(PieChart chart, Function<String, Optional<Color>> colorByNameSupplier) {
        return new ChartCustomizer(new DefaultChartLegendCustomizer(chart, colorByNameSupplier),
                new DefaultPieCustomizer(chart, colorByNameSupplier));
    }
}
