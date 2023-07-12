
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
import java.util.Set;
import java.util.function.Function;

public class StyleableTreeCell<S> extends TreeCell<S> {
    private final Set<String> styleClassCache = new HashSet<>();
    private final Function<TreeItem<S>, String> styleSupplier;
    private final StringConverter<S> converter;
    private final Function<TreeItem<S>, Node> graphicSupplier;
    private final Function<TreeItem<S>, String> styleClassSupplier;

    StyleableTreeCell(Function<TreeItem<S>, String> styleSupplier,
                      StringConverter<S> converter,
                      Function<TreeItem<S>, Node> graphicSupplier,
                      Function<TreeItem<S>, String> styleClassSupplier) {
        this.styleSupplier = styleSupplier;
        this.converter = converter == null
                ? new SimpleStringConverter<>()
                : converter;
        this.graphicSupplier = graphicSupplier;
        this.styleClassSupplier = styleClassSupplier;
    }

    @Override
    protected void updateItem(S item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            final S s = getItem();
            final TreeItem<S> treeItem = getTreeItem();
            if (s != null && treeItem != null) {
                setText(converter.toString(item));
                if (styleSupplier != null) {
                    final String style = styleSupplier.apply(treeItem);
                    if (style != null) {
                        setStyle(style);
                    }
                }
                if (graphicSupplier != null) {
                    setGraphic(graphicSupplier.apply(treeItem));
                }
                if (styleClassSupplier != null) {
                    getStyleClass().removeAll(styleClassCache);
                    final String styleClass = styleClassSupplier.apply(treeItem);
                    if (styleClass != null && !styleClass.isEmpty()) {
                        styleClassCache.add(styleClass);
                        getStyleClass().add(styleClass);
                    }
                }
            }
        }
    }

    /**
     * Styleable tree table cell builder for String cell-representation
     *
     * @param treeView  for tree view
     * @param <S>       type of data
     * @return new cell builder
     */
    public static <S> StyleableTreeCellBuilder<S> forTree(TreeView<S> treeView) {
        return new StyleableTreeCellBuilder<>(treeView);
    }

    public static class StyleableTreeCellBuilder<S> {
        private Function<TreeItem<S>, String> styleSupplier;
        private Function<TreeItem<S>, String> styleClassSupplier;
        private StringConverter<S> converter;
        private Function<TreeItem<S>, Node> graphicSupplier;

        StyleableTreeCellBuilder(TreeView<S> treeView) {
        }

        public StyleableTreeCellBuilder<S> withStyleSupplier(Function<TreeItem<S>, String> styleSupplier) {
            this.styleSupplier = styleSupplier;
            return this;
        }

        public StyleableTreeCellBuilder<S> withConverter(StringConverter<S> converter) {
            this.converter = converter;
            return this;
        }

        public StyleableTreeCellBuilder<S> withStyleClassSupplier(Function<TreeItem<S>, String> styleClassSupplier) {
            this.styleClassSupplier = styleClassSupplier;
            return this;
        }

        public StyleableTreeCellBuilder<S> withGraphicSupplier(Function<TreeItem<S>, Node> graphicSupplier) {
            this.graphicSupplier = graphicSupplier;
            return this;
        }

        public Callback<TreeView<S>, TreeCell<S>> build() {
            return (column) -> new StyleableTreeCell<>(styleSupplier, converter, graphicSupplier, styleClassSupplier);
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
