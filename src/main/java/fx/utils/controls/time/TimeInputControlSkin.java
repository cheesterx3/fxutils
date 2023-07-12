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
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.function.Supplier;
import java.util.stream.Stream;

class TimeInputControlSkin extends SkinBase<TimeInputControl> {
    private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
    private final ElementText hourText, minuteText, secondText;

    private final ElementText yearText, monthText, dayText;

    private final RestrictedIntegerProperty hour = new RestrictedIntegerProperty(23);
    private final RestrictedIntegerProperty minute = new RestrictedIntegerProperty(59);
    private final RestrictedIntegerProperty second = new RestrictedIntegerProperty(59);

    private final RestrictedIntegerProperty year = new RestrictedIntegerProperty(9999, 0);
    private final RestrictedIntegerProperty month = new RestrictedIntegerProperty(12, 1);
    private final RestrictedIntegerProperty day = new RestrictedIntegerProperty(31, 1);


    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private boolean isJustFocused;
    private String currentValue;

    protected TimeInputControlSkin(TimeInputControl control) {
        super(control);
        date.bindBidirectional(control.dateProperty());
        date.addListener((observableValue, date1, newDate) -> updateDateFields(newDate));
        time.bindBidirectional(control.timeProperty());
        time.addListener((observableValue, localTime, newTime) -> updateFields(newTime));
        day.setMaxValueSupplier(this::calcMaxDay);
        updateFields(time.get());
        updateDateFields(date.get());

        hourText = new ElementText(hour);
        minuteText = new ElementText(minute);
        secondText = new ElementText(second);
        yearText = new ElementText(year);
        monthText = new ElementText(month);
        dayText = new ElementText(day);


        secondText.managedProperty().bind(secondText.visibleProperty());
        secondText.visibleProperty().bind(getSkinnable().showSecondsProperty());

        final Node datePicker = createDatePicker();
        final Node timePicker = createTimePicker();
        HBox container = new HBox(datePicker, timePicker);
        container.getStyleClass().addAll("text-input", "time-input-control");
        container.setAlignment(Pos.CENTER_LEFT);
        container.maxWidthProperty().bind(container.widthProperty());
        getChildren().add(container);

        Stream.of(hour, minute, second).forEach(property -> property.addListener((observableValue, number, t1) -> updateTime()));
        Stream.of(year, month, day).forEach(property -> property.addListener((observableValue, number, t1) -> updateDate()));
        getSkinnable().focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                if (getSkinnable().isShowDate()) {
                    dayText.requestFocus();
                } else {
                    hourText.requestFocus();
                }
            }
        });
    }

    private Node createTimePicker() {
        final HBox timeContainer = new HBox(elementContainer(hourText),
                createSeparator(minuteText, ":"),
                elementContainer(minuteText),
                createSeparator(secondText, ":"),
                elementContainer(secondText));
        timeContainer.setAlignment(Pos.CENTER_LEFT);
        timeContainer.visibleProperty().bind(getSkinnable().showTimeProperty());
        timeContainer.managedProperty().bind(timeContainer.visibleProperty());
        return timeContainer;
    }

    private int calcMaxDay() {
        return Month.of(month.get()).maxLength();
    }

    private Node createDatePicker() {
        final DatePicker datePicker = new DatePicker();
        date.bindBidirectional(datePicker.valueProperty());
        datePicker.setVisible(false);
        datePicker.setManaged(false);
        final Button button = new Button();
        button.setFocusTraversable(false);
        button.getStyleClass().add("time-input-control-button");
        button.setOnAction(event -> datePicker.show());
        final HBox dateContainer = new HBox(elementContainer(dayText),
                createSeparator(monthText, "."),
                elementContainer(monthText),
                createSeparator(yearText, "."),
                elementContainer(yearText),
                button,
                datePicker);
        HBox.setMargin(button, new Insets(0, 8, 0, 8));
        dateContainer.setAlignment(Pos.CENTER_LEFT);
        dateContainer.visibleProperty().bind(getSkinnable().showDateProperty());
        dateContainer.managedProperty().bind(dateContainer.visibleProperty());
        return dateContainer;
    }

    private void selectNext(Text text) {
        if (text == hourText) {
            minuteText.requestFocus();
        } else if (text == minuteText && getSkinnable().isShowSeconds()) {
            secondText.requestFocus();
        } else if (text == dayText) {
            monthText.requestFocus();
        } else if (text == monthText) {
            yearText.requestFocus();
        } else if (text == yearText && getSkinnable().isShowTime()) {
            hourText.requestFocus();
        }
    }

    private VBox elementContainer(Text text) {
        final VBox container = new VBox(text);
        container.getStyleClass().add("time-input-control-element");
        container.setAlignment(Pos.CENTER);
        return container;
    }

    private void updateDateFields(LocalDate newDate) {
        if (newDate != null) {
            year.set(newDate.getYear());
            month.set(newDate.getMonthValue());
            day.set(newDate.getDayOfMonth());
        }
    }

    private void updateDate() {
        int y = year.get();
        int m = month.get();
        int d = day.get();
        date.set(LocalDate.of(y, m, d));
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

    private Text createSeparator(Text beforeText, String value) {
        final Text label = new Text(value);
        label.fontProperty().bind(getSkinnable().fontProperty());
        label.managedProperty().bind(label.visibleProperty());
        label.visibleProperty().bind(beforeText.visibleProperty());
        return label;
    }

    private class ElementText extends Text {
        private final RestrictedIntegerProperty property;

        private ElementText(RestrictedIntegerProperty property) {
            this.property = property;
            getStyleClass().add("time-input-control-text");
            setTextOrigin(VPos.CENTER);
            textProperty().bind(property.asString("%0" + property.getMaxDigitsCount() + "d"));
            fontProperty().bind(getSkinnable().fontProperty());
            setFocusTraversable(true);
            setOnMouseClicked(mouseEvent -> requestFocus());
            focusedProperty().addListener((observableValue, aBoolean, t1) -> {
                getParent().pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, t1);
                isJustFocused = true;
            });
            setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode().isDigitKey()) {
                    if (isJustFocused) {
                        isJustFocused = false;
                        currentValue = keyEvent.getText();
                        property.set(Integer.parseInt(currentValue));
                    } else {
                        currentValue += keyEvent.getText();
                        final int i = Integer.parseInt(currentValue);
                        final int value = Math.min(i, property.getMaxValue());
                        property.set(value);
                        isJustFocused = property.getMaxDigitsCount() == currentValue.length();
                        if (value == i && isJustFocused) {
                            selectNext(this);
                        }
                    }
                } else if (keyEvent.getCode() == KeyCode.UP) {
                    property.set(property.get() + 1);
                    keyEvent.consume();
                } else if (keyEvent.getCode() == KeyCode.DOWN) {
                    property.set(property.get() - 1);
                    keyEvent.consume();
                }
            });
        }
    }

    private static class RestrictedIntegerProperty extends SimpleIntegerProperty {
        private final int maxValue;
        private final int minValue;
        private Supplier<Integer> maxValueSupplier;

        private RestrictedIntegerProperty(int maxValue) {
            this(maxValue, 0);
        }

        private RestrictedIntegerProperty(int maxValue, int minValue) {
            super(minValue);
            this.maxValue = maxValue;
            this.minValue = minValue;
        }

        @Override
        public void set(int newValue) {
            final int value = newValue < minValue ? calcMaxValue() : newValue;
            super.set(value > calcMaxValue() ? minValue : value);
        }

        @Override
        public int get() {
            int value = super.get();
            return Math.min(value, calcMaxValue());
        }

        private int calcMaxValue() {
            return maxValueSupplier == null ? maxValue : maxValueSupplier.get();
        }

        int getMaxValue() {
            return maxValue;
        }

        public void setMaxValueSupplier(Supplier<Integer> maxValueSupplier) {
            this.maxValueSupplier = maxValueSupplier;
        }

        int getMaxDigitsCount() {
            return String.valueOf(maxValue).length();
        }
    }
}
