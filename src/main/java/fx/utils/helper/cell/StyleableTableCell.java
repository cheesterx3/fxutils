/*
 *
 *  * Copyright © 2023 Shaklein Alexander
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

package fx.utils.helper.cell;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is a helper class for managing table cell representation
 * It has a simple usage
 * <pre>
 *     column.setCellFactory(StyleableTableCell
 *               .forColumn(column)
 *               .withCellSize(36D)
 *               .withStyleDataSupplier(amount -> amount >= 10 ? "-fx-text-fill:#f00" : "-fx-text-fill:#555")
 *               .build());
 * </pre>
 *
 * It provides possibilities for changing:
 *  <ul>
 *      <li>style of cell</li>
 *      <li>style class of cell</li>
 *      <li>height of cell</li>
 *      <li>custom string converter of cell</li>
 *  </ul>
 *
 *
 * @param <S> the type of elements maintained by table view
 * @param <T> the type of elements maintained by target column
 */
public class StyleableTableCell<S, T> extends TableCell<S, T> {
    private final Label label = new Label();

    private final Function<S, String> styleSupplier;
    private final Function<T, String> styleValueSupplier;
    private final StringConverter<T> converter;
    private double cellSize = 32.0;

    StyleableTableCell(
            Function<S, String> styleSupplier,
            Function<T, String> styleValueSupplier,

            Supplier<Collection<String>> styleClassSupplier,
            StringConverter<T> converter) {
        this.getStyleClass().add("text-field-table-cell");
        this.styleSupplier = styleSupplier;
        this.styleValueSupplier = styleValueSupplier;
        this.converter = converter == null
                ? new SimpleStringConverter<>()
                : converter;
        label.getStyleClass().add("table-cell-label");
        if (styleClassSupplier != null) {
            this.getStyleClass().addAll(styleClassSupplier.get());
            label.getStyleClass().addAll(styleClassSupplier.get());
        }
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            @SuppressWarnings("unchecked") final TableRow<S> tableRow = getTableRow();
            if (tableRow != null) {
                tableRow.setStyle(String.format("-fx-cell-size: %fpx", cellSize));
                label.setText(converter.toString(item));
                final S object = tableRow.getItem();
                if (object != null) {

                    String style = styleSupplier != null ? styleSupplier.apply(object) : null;
                    if (style != null) {
                        label.setStyle(style);
                    } else if ((style = styleValueSupplier != null ? styleValueSupplier.apply(item) : null) != null) {
                        label.setStyle(style);
                    }
                }
                setGraphic(label);
            }
        }
    }

    /**
     * Generic table cell builder
     *
     * @param column for column
     * @param <S>    type of data
     * @param <T>    type of representation
     * @return new cell builder
     */
    public static <S, T> StyleableTableCellBuilder<S, T> forColumn(TableColumn<S, T> column) {
        return new StyleableTableCellBuilder<>(column);
    }

    void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }

    public static class StyleableTableCellBuilder<S, T> {
        private Function<S, String> styleSupplier;
        private Function<T, String> styleValueSupplier;
        private StringConverter<T> converter;
        private Supplier<Collection<String>> styleClassSupplier;
        private double cellSize = 32.0;

        StyleableTableCellBuilder(TableColumn<S, T> column) {
        }

        public StyleableTableCellBuilder<S, T> withStyleObjectSupplier(Function<S, String> styleSupplier) {
            this.styleSupplier = styleSupplier;
            return this;
        }

        public StyleableTableCellBuilder<S, T> withStyleDataSupplier(Function<T, String> styleSupplier) {
            this.styleValueSupplier = styleSupplier;
            return this;
        }

        public StyleableTableCellBuilder<S, T> withStyleClassSupplier(Supplier<Collection<String>> styleClassSupplier) {
            this.styleClassSupplier = styleClassSupplier;
            return this;
        }

        public StyleableTableCellBuilder<S, T> withConverter(StringConverter<T> converter) {
            this.converter = converter;
            return this;
        }

        public StyleableTableCellBuilder<S, T> withCellSize(double size) {
            this.cellSize = size;
            return this;
        }

        public Callback<TableColumn<S, T>, TableCell<S, T>> build() {
            return (column) -> {
                final StyleableTableCell<S, T> tableCell = new StyleableTableCell<>(styleSupplier,
                        styleValueSupplier,
                        styleClassSupplier,
                        converter);
                tableCell.setCellSize(cellSize);
                return tableCell;
            };
        }

    }

    private static class SimpleStringConverter<T> extends StringConverter<T> {

        @Override
        public String toString(T object) {
            return object != null ? object.toString() : "";
        }

        @Override
        public T fromString(String string) {
            return null;
        }
    }
}
