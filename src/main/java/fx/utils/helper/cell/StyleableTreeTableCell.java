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

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class StyleableTreeTableCell<S, T> extends TreeTableCell<S, T> {
    private final Set<String> styleClassCache = new HashSet<>();
    private final Label label = new Label();
    private final Function<S, String> styleSupplier;
    private final StringConverter<T> converter;
    private final Function<TreeItem<S>, Node> graphicSupplier;
    private final Function<TreeItem<S>, String> styleClassSupplier;
    private final Function<TreeItem<S>, ContextMenu> contextMenuSupplier;

    StyleableTreeTableCell(Function<S, String> styleSupplier,
                           StringConverter<T> converter,
                           Function<TreeItem<S>, Node> graphicSupplier,
                           Function<TreeItem<S>, String> styleClassSupplier,
                           Function<TreeItem<S>, ContextMenu> contextMenuSupplier) {
        this.graphicSupplier = graphicSupplier;
        this.styleClassSupplier = styleClassSupplier;
        this.getStyleClass().add("text-field-table-cell");
        this.styleSupplier = styleSupplier;
        this.converter = converter == null
                ? new SimpleStringConverter<>()
                : converter;
        this.contextMenuSupplier = contextMenuSupplier;
        label.getStyleClass().add("table-cell-label");
        label.setWrapText(true);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            final TreeTableRow<S> tableRow = getTreeTableRow();
            if (tableRow != null) {
                final S object = tableRow.getItem();
                final TreeItem<S> treeItem = tableRow.getTreeItem();
                if (object != null && styleSupplier != null) {
                    final String style = styleSupplier.apply(object);
                    if (style != null) {
                        label.setStyle(style);
                    }
                }
                if (graphicSupplier == null) {
                    label.setText(converter.toString(item));
                    setGraphic(label);
                    updateStyleClass(label);
                } else {
                    setText(converter.toString(item));

                    if (treeItem != null) {
                        setGraphic(graphicSupplier.apply(treeItem));
                    }
                    updateStyleClass(this);
                }
                if (contextMenuSupplier != null && treeItem != null) {
                    Optional.ofNullable(contextMenuSupplier.apply(treeItem))
                            .ifPresent(this::setContextMenu);
                }
            }
        }
    }

    private void updateStyleClass(Node node) {
        if (styleClassSupplier != null) {
            node.getStyleClass().removeAll(styleClassCache);
            final TreeItem<S> treeItem = getTreeTableRow().getTreeItem();
            if (treeItem != null) {
                final String styleClass = styleClassSupplier.apply(treeItem);
                if (styleClass != null && !styleClass.isEmpty()) {
                    styleClassCache.add(styleClass);
                    node.getStyleClass().add(styleClass);
                }
            }
        }
    }

    /**
     * Generic tree table cell builder
     *
     * @param column   for column
     * @param <S>        type of data
     * @param <T>        type of representation
     * @return new cell builder
     */
    public static <S, T> StyleableTreeTableCellBuilder<S, T> forColumn(TreeTableColumn<S, T> column) {
        return new StyleableTreeTableCellBuilder<>(column);
    }


    public static class StyleableTreeTableCellBuilder<S, T> {
        private Function<S, String> styleSupplier;
        private StringConverter<T> converter;
        private Function<TreeItem<S>, String> styleClassSupplier;
        private Function<TreeItem<S>, Node> graphicSupplier;
        private Function<TreeItem<S>, ContextMenu> contextMenuSupplier;


        StyleableTreeTableCellBuilder(TreeTableColumn<S, T> column) {
        }

        public StyleableTreeTableCellBuilder<S, T> withStyleSupplier(Function<S, String> styleSupplier) {
            this.styleSupplier = styleSupplier;
            return this;
        }

        public StyleableTreeTableCellBuilder<S, T> withConverter(StringConverter<T> converter) {
            this.converter = converter;
            return this;
        }

        public StyleableTreeTableCellBuilder<S, T> withStyleClassSupplier(Function<TreeItem<S>, String> styleClassSupplier) {
            this.styleClassSupplier = styleClassSupplier;
            return this;
        }

        public StyleableTreeTableCellBuilder<S, T> withGraphicSupplier(Function<TreeItem<S>, Node> graphicSupplier) {
            this.graphicSupplier = graphicSupplier;
            return this;
        }

        public StyleableTreeTableCellBuilder<S, T> withContextMenuSupplier(Function<TreeItem<S>, ContextMenu> contextMenuSupplier) {
            this.contextMenuSupplier = contextMenuSupplier;
            return this;
        }

        public Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> build() {
            return (column) -> new StyleableTreeTableCell<>(styleSupplier, converter, graphicSupplier, styleClassSupplier, contextMenuSupplier);
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
