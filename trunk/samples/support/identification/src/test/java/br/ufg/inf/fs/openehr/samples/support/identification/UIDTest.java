package br.ufg.inf.fs.openehr.samples.support.identification;

import org.openehr.rm.support.identification.UID;
import org.openehr.rm.support.identification.UUID;
import org.junit.Test;
import org.openehr.rm.support.identification.ISO_OID;
import org.openehr.rm.support.identification.InternetID;

/**
 *
 * @author kyriosdata
 */
public class UIDTest {

    @Test
    public void testSimples() {
        String luid = java.util.UUID.randomUUID().toString();
        System.out.println(luid);
        UID uid = new UUID(luid);

        uid = new ISO_OID("1.2");

        uid = new InternetID("ufg.br");
    }

}
