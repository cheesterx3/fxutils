/*
 *
 *  * Copyright © 2020 Shaklein Alexander
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
