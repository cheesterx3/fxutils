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

package fx.utils.samples.cell;

import fx.utils.helper.cell.StyleableTableCell;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class StyleableCellSample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        final BorderPane pane = new BorderPane();
        final Scene scene = new Scene(pane);

        final TableView<SomeData> tableView = new TableView<>();
        final TableColumn<SomeData, Integer> column1 = new TableColumn<>("amount");
        final TableColumn<SomeData, String> column2 = new TableColumn<>("field2");
        final TableColumn<SomeData, String> column3 = new TableColumn<>("field3");
        tableView.getColumns().addAll(column1, column2, column3);

        tableView.setItems(FXCollections.observableArrayList(
                new SomeData(10, "Sam", "hello"),
                new SomeData(5, "Max", "world"),
                new SomeData(56, "Joe", "anyone"),
                new SomeData(2, "Sam", null)
        ));

        column1.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getAmount()));
        column2.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().field2));
        column3.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().field3));

        column1.setCellFactory(StyleableTableCell.forColumn(column1)
                .withCellSize(36D)
                .withStyleDataSupplier(amount -> amount >= 10 ? "-fx-text-fill:#f00" : "-fx-text-fill:#555")
                .build());
        column2.setCellFactory(StyleableTableCell.forColumn(column2)
                .withStyleObjectSupplier(someData -> "Sam".equals(someData.field2) && someData.field3 != null ? "-fx-font-weight:bold" : null)
                .withStyleDataSupplier(field2 -> "Joe".equals(field2) ? "-fx-text-fill:#0f0" : null)
                .build());
        column3.setCellFactory(StyleableTableCell.forColumn(column3).build());

        pane.setCenter(tableView);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private static class SomeData {
        private final int amount;
        private final String field2;
        private final String field3;

        private SomeData(int amount, String field2, String field3) {
            this.amount = amount;
            this.field2 = field2;
            this.field3 = field3;
        }

        public int getAmount() {
            return amount;
        }

        public String getField2() {
            return field2;
        }

        public String getField3() {
            return field3;
        }
    }
}
