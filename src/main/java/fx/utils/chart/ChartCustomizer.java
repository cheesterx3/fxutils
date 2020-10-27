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
