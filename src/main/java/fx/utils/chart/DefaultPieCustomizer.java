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
            pieChart.getData().forEach(this::dataAdded);
        }
    }

    private void dataAdded(PieChart.Data data) {
        colorByNameSupplier.apply(data.getName()).ifPresent(color -> {
            if (nonNull(data.getNode()))
                setNodeColor(color, data.getNode());
        });
    }
}
