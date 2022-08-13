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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;

import java.time.LocalTime;

public class TimeControl extends Control {
    private final ObjectProperty<Font> font = new SimpleObjectProperty<>(Font.font(13));
    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
    private final BooleanProperty showSeconds = new SimpleBooleanProperty(true);

    public TimeControl() {
        this(LocalTime.now());
    }

    public TimeControl(LocalTime time) {
        this.time.set(time);
        this.setAccessibleRole(AccessibleRole.TEXT_FIELD);
        this.setFocusTraversable(true);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TimeControlSkin(this);
    }

    public ObjectProperty<Font> fontProperty() {
        return font;
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

    public void setShowSeconds(boolean value){
        showSeconds.set(value);
    }

    public void setFont(Font font){
        this.font.set(font);
    }
}
