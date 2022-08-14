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

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeInputControl extends Control {
    private ObjectProperty<Font> font;
    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    private final ReadOnlyObjectWrapper<LocalDateTime> dateTime = new ReadOnlyObjectWrapper<>();
    private final BooleanProperty showSeconds = new SimpleBooleanProperty(true);
    private final BooleanProperty showDate = new SimpleBooleanProperty(true);
    private final BooleanProperty showTime = new SimpleBooleanProperty(true);

    public TimeInputControl() {
        this(LocalDate.now(), LocalTime.now());
    }

    public TimeInputControl(LocalDate date, LocalTime time) {
        this.time.set(time);
        this.date.set(date);
        this.getStylesheets().add(getClass().getResource("time-input-control.css").toExternalForm());
        this.applyCss();
        this.setAccessibleRole(AccessibleRole.TEXT_FIELD);
        this.setFocusTraversable(true);
        this.dateTime.bind(Bindings.createObjectBinding(() -> this.date.get() != null && this.time.get() != null
                ? LocalDateTime.of(this.date.get(), this.time.get())
                : null, this.date, this.time));

    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TimeInputControlSkin(this);
    }

    public ObjectProperty<Font> fontProperty() {
        if (this.font == null) {
            this.font = new StyleableObjectProperty<Font>(Font.getDefault()) {
                private boolean fontSetByCss = false;

                public void applyStyle(StyleOrigin newOrigin, Font value) {
                    try {
                        this.fontSetByCss = true;
                        super.applyStyle(newOrigin, value);
                    } finally {
                        this.fontSetByCss = false;
                    }

                }

                public void set(Font value) {
                    Font oldValue = this.get();
                    if (value == null) {
                        if (oldValue == null) {
                            return;
                        }
                    } else if (value.equals(oldValue)) {
                        return;
                    }

                    super.set(value);
                }

                protected void invalidated() {
                    if (!this.fontSetByCss) {
                        TimeInputControl.this.impl_reapplyCSS();
                    }

                }

                public CssMetaData<TimeInputControl, Font> getCssMetaData() {
                    return TimeInputControl.StyleableProperties.FONT;
                }

                public Object getBean() {
                    return TimeInputControl.this;
                }

                public String getName() {
                    return "font";
                }
            };
        }
        return this.font;
    }

    public LocalTime getTime() {
        return time.get();
    }

    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }

    public boolean isShowSeconds() {
        return showSeconds.get();
    }

    public BooleanProperty showSecondsProperty() {
        return showSeconds;
    }

    public void setShowSeconds(boolean value) {
        showSeconds.set(value);
    }

    public void setFont(Font font) {
        this.font.set(font);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public boolean isShowDate() {
        return showDate.get();
    }

    public void setShowDate(boolean value){
        this.showDate.set(value);
    }

    public BooleanProperty showDateProperty() {
        return showDate;
    }

    public void setLocalDateTime(LocalDateTime value) {
        date.set(value.toLocalDate());
        time.set(value.toLocalTime());
    }

    public LocalDateTime getLocalDateTime() {
        return dateTime.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> localDateTimeProperty() {
        return dateTime.getReadOnlyProperty();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return TimeInputControl.StyleableProperties.STYLEABLES;
    }

    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public boolean isShowTime() {
        return showTime.get();
    }

    public void setShowTime(boolean value){
        this.showTime.set(value);
    }

    public BooleanProperty showTimeProperty() {
        return showTime;
    }

    private static class StyleableProperties {
        private static final FontCssMetaData<TimeInputControl> FONT = new FontCssMetaData<TimeInputControl>("-fx-font", Font.getDefault()) {
            public boolean isSettable(TimeInputControl n) {
                return n.font == null || !n.font.isBound();
            }

            public StyleableProperty<Font> getStyleableProperty(TimeInputControl n) {
                return (StyleableProperty)n.fontProperty();
            }
        };
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        private StyleableProperties() {
        }

        static {
            List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            styleables.add(FONT);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
}
