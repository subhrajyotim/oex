package br.ufg.inf.fs.openehr.samples.measurement;

import org.openehr.rm.support.measurement.MeasurementService;

/**
 * MesaurmentService oferece apenas dois métodos. Um uso correto para uma
 * implementação inválida é fornecida abaixo.
 *
 */
public class MeasurementSample implements MeasurementService {

    public boolean isValidUnitsString(String units) {
        return true;
    }

    public boolean unitsEquivalent(String units1, String units2) {
        return units1.equalsIgnoreCase(units2);
    }

    public static void main(String[] args) {
        MeasurementService ms = new MeasurementSample();

        boolean valida = ms.isValidUnitsString("w$x");
        System.out.println("w$x é uma medida válida: " + valida);

        valida = ms.unitsEquivalent("km", "Km");
        System.out.println("km é equivalente a Km: " + valida);
    }
}
