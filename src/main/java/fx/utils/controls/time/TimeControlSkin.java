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

package fx.utils.controls.time;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

class TimeControlSkin extends SkinBase<TimeControl> {
    private final Text hourText;
    private final Text minuteText;
    private final Text secondText;

    private final MaxRestrictedIntegerProperty hour = new MaxRestrictedIntegerProperty(23);
    private final MaxRestrictedIntegerProperty minute = new MaxRestrictedIntegerProperty(59);
    private final MaxRestrictedIntegerProperty second = new MaxRestrictedIntegerProperty(59);

    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
    private final Map<Text, MaxRestrictedIntegerProperty> fieldMapping = new LinkedHashMap<>();
    private MaxRestrictedIntegerProperty selectedProperty;
    private boolean isJustFocused;
    private String currentValue;

    protected TimeControlSkin(TimeControl control) {
        super(control);

        time.bindBidirectional(control.timeProperty());
        time.addListener((observableValue, localTime, newTime) -> updateFields(newTime));
        updateFields(time.get());

        fieldMapping.put(hourText = new Text(), hour);
        fieldMapping.put(minuteText = new Text(), minute);
        fieldMapping.put(secondText = new Text(), second);
        secondText.managedProperty().bind(secondText.visibleProperty());
        secondText.visibleProperty().bind(getSkinnable().showSecondsProperty());

        final HBox container = new HBox(elementContainer(hourText),
                createSeparator(minuteText),
                elementContainer(minuteText),
                createSeparator(secondText),
                elementContainer(secondText));
        container.getStyleClass().add("text-input");
        container.setPadding(new Insets(4));
        container.setAlignment(Pos.CENTER_LEFT);
        container.maxWidthProperty().bind(container.widthProperty());
        getChildren().add(container);

        Stream.of(hourText, minuteText, secondText).forEach(text -> {
            final MaxRestrictedIntegerProperty prop = fieldMapping.get(text);
            text.textProperty().bind(prop.asString("%02d"));
            text.fontProperty().bind(getSkinnable().fontProperty());
            text.setFocusTraversable(true);
            text.setOnMouseClicked(mouseEvent -> text.requestFocus());
            text.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
                text.getParent().setStyle(t1 ? "-fx-background-color: -fx-accent; -fx-background-radius: 2" : "");
                text.setStyle(t1 ? "-fx-fill:#fff" : "");
                isJustFocused = true;
            });
            text.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode().isDigitKey()) {
                    if (isJustFocused) {
                        isJustFocused = false;
                        currentValue = keyEvent.getText();
                        prop.set(Integer.parseInt(currentValue));
                    } else {
                        currentValue += keyEvent.getText();
                        int i = Integer.parseInt(currentValue);
                        int value = Math.min(i, prop.getMaxValue());
                        prop.set(value);
                        isJustFocused = true;
                        if (value == i) {
                            selectNext(text);
                        }
                    }
                } else if (keyEvent.getCode() == KeyCode.UP) {
                    prop.set(prop.get() + 1);
                    keyEvent.consume();
                } else if (keyEvent.getCode() == KeyCode.DOWN) {
                    prop.set(prop.get() - 1);
                    keyEvent.consume();
                }
            });
        });

        Stream.of(hour, minute, second).forEach(property -> property.addListener((observableValue, number, t1) -> updateTime()));
        getSkinnable().focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                hourText.requestFocus();
            }
        });
    }

    private void selectNext(Text text) {
        if (text == hourText) {
            minuteText.requestFocus();
        } else if (text == minuteText && secondText.isVisible()) {
            secondText.requestFocus();
        }

    }

    private VBox elementContainer(Text text) {
        final VBox container = new VBox(text);
        container.setPadding(new Insets(2D));
        return container;
    }

    private void updateFields(LocalTime time) {
        if (time != null) {
            hour.set(time.getHour());
            minute.set(time.getMinute());
            second.set(time.getSecond());
        }
    }

    private void updateTime() {
        int h = hour.get();
        int m = minute.get();
        int s = getSkinnable().isShowSeconds() ? second.get() : 0;
        time.set(LocalTime.of(h, m, s));
    }

    private Text createSeparator(Text beforeText) {
        final Text label = new Text(":");
        label.fontProperty().bind(getSkinnable().fontProperty());
        label.managedProperty().bind(label.visibleProperty());
        label.visibleProperty().bind(beforeText.visibleProperty());
        return label;
    }

    private static class MaxRestrictedIntegerProperty extends SimpleIntegerProperty {
        private final int maxValue;

        private MaxRestrictedIntegerProperty(int maxValue) {
            this.maxValue = maxValue;
        }

        @Override
        public void set(int newValue) {
            final int value = newValue < 0 ? maxValue : newValue;
            super.set(value > maxValue ? 0 : value);
        }

        int getMaxValue() {
            return maxValue;
        }
    }
}
