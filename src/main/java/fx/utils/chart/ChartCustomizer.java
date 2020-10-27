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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ChartCustomizer implements Customizer {
    private final Logger logger = Logger.getLogger(ChartCustomizer.class.getName());
    private final Collection<Customizer> customizers;

    ChartCustomizer(Collection<Customizer> customizers) {
        this.customizers = new ArrayList<>(customizers);
    }

    ChartCustomizer(Customizer... customizers) {
        this(Arrays.asList(customizers));
    }

    public void customize() {
        customizers.forEach(this::doCustomize);
    }

    private void doCustomize(Customizer customizer) {
        try {
            customizer.customize();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "", e);
        }
    }

}
