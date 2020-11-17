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
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.nonNull;

class DefaultChartLegendCustomizer implements Customizer {
    public static final String LEGEND_STYLE_CLASS = ".chart-legend";
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
            final Node legend = chart.lookup(LEGEND_STYLE_CLASS);
            if (legend instanceof Region) {
                final Region region = (Region) legend;
                region.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) c -> {
                    while (c.next()) {
                        c.getRemoved().forEach(this::legendChildRemoved);
                        c.getAddedSubList().forEach(this::legendChildAdded);
                    }
                });
                region.getChildrenUnmodifiable().forEach(this::legendChildAdded);
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
            if (nonNull(listener))
                label.getChildrenUnmodifiable().removeListener(listener);
        }
    }

}
