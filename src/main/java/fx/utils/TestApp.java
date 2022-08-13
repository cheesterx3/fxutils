/*
 *
 *  * Copyright © 2022 Shaklein Alexander
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

package fx.utils;

import fx.utils.controls.time.TimeControl;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TestApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox borderPane = new VBox();
        borderPane.setPadding(new Insets(16));
        borderPane.setSpacing(16);
        TimeControl timeControl = new TimeControl();
        timeControl.fontProperty().set(Font.font(14));

        timeControl.showSecondsProperty().set(false);
        borderPane.getChildren().add(timeControl);
        borderPane.getChildren().add(new TimeControl());
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        timeControl.timeProperty().addListener((observableValue, localTime, t1) -> System.out.println("new time is " + t1));
        timeControl.requestFocus();
        primaryStage.show();
    }
}
