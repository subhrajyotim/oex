package org.openehr.validation;

import static org.junit.Assert.*;

import br.ufg.inf.fs.pep.archetypes.ArchetypeRepository;
import br.ufg.inf.fs.pep.archetypes.PepArchetypeRepository;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openehr.am.archetype.constraintmodel.primitive.CDateTime;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.basic.DataValue;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.DvText;

/**
 *
 * @author Danillo Guimaraes
 */
public class PrimitiveTests {

    @BeforeClass
    public static void setUp(){
        validator = new DataValidatorImpl();
        repository = new PepArchetypeRepository();
    }

    @Test
    public void testCDate() throws Exception{
//        String dateValue = "2011-08-19";
//        Element element = element(new DvDate(dateValue), DV_DATE);
//        List<ValidationError> errors = validator.validate(element);
//        assertTrue("Validation contains errors", errors.isEmpty());
    }




    private Element element(DataValue value, String archetype){
        return new Element(archetype, new DvText(archetype), value);
    }


    private final String DV_COUNT = "openEHR-EHR-ELEMENT.test_dvcount.v1";
    private final String DV_DATE = "openEHR-EHR-ELEMENT.test_dvdate.v1";
    private final String DV_DATE_TIME = "openEHR-EHR-ELEMENT.test_dvdatetime.v1";
    private final String DV_DURATION = "openEHR-EHR-ELEMENT.test_dvduration.v1";
    private final String DV_QUANTITY = "openEHR-EHR-ELEMENT.test_dvquantity.v1";
    private final String DV_TIME = "openEHR-EHR-ELEMENT.test_dvtime.v1";

    private static DataValidator validator = null;
    private static ArchetypeRepository repository = null;
}
