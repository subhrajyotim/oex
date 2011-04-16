package br.ufg.inf.fs.openehr.samples.support;

import java.util.List;
import java.util.Map;
import org.openehr.rm.support.ExternalEnvironment;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.CodeSetAccess;
import org.openehr.rm.support.terminology.OpenEHRCodeSetIdentifiers;
import org.openehr.rm.support.terminology.TerminologyAccess;
import org.openehr.rm.support.terminology.TerminologyService;

/**
 * Just use Netbeans to generate empty implementations for the interfaces
 * needed by interface ExternalEnvironment.
 *
 * Open the projects checked out by subversion (openehr repository for java
 * implementation). Create this project (maven based) and create a dependency
 * to the openEHR Reference Model Core. It should be enough to get this
 * running.
 * 
 */
public class ExternalEnvironmentSample
{
    public static void main( String[] args )
    {
        final TerminologyService ts = new TerminologyService() {

            public TerminologyAccess terminology(String name) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public CodeSetAccess codeSet(String name) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public CodeSetAccess codeSetForId(OpenEHRCodeSetIdentifiers id) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean hasTerminology(String name) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean hasCodeSet(String name) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public List<String> terminologyIdentifiers() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public List<String> codeSetIdentifiers() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public Map<String, String> openehrCodeSets() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        final MeasurementService ms = new MeasurementService() {

            public boolean isValidUnitsString(String units) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean unitsEquivalent(String units1, String units2) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        ExternalEnvironment ee = new ExternalEnvironment() {

            public TerminologyService terminologyService() {
                return ts;
            }

            public MeasurementService measurementService() {
                return ms;
            }
        };

        System.out.println(ee.terminologyService() != null);
        System.out.println(ee.measurementService() != null);
    }
}