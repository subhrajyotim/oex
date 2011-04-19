package br.ufg.inf.fs.openehr.samples.measurement;

import org.junit.Test;
import static org.junit.Assert.*;

import org.openehr.rm.support.measurement.MeasurementService;

/**
 * MesaurmentService oferece apenas dois métodos. Um uso correto para uma
 * implementação inválida é fornecida abaixo.
 *
 */
public class MeasurementTest implements MeasurementService {

    public boolean isValidUnitsString(String units) {
        return "w$x".equals(units);
    }

    public boolean unitsEquivalent(String units1, String units2) {
        return units1.equalsIgnoreCase(units2);
    }

    @Test
    public void simpleImplementationPossible() {
        MeasurementService ms = new MeasurementTest();

        assertTrue(ms.isValidUnitsString("w$x"));
        assertFalse(ms.isValidUnitsString("cm"));
        assertTrue(ms.unitsEquivalent("km", "Km"));
    }
}
