package br.ufg.inf.fs.openehr.samples.support.identification;

import org.openehr.rm.support.identification.UID;
import org.openehr.rm.support.identification.UUID;
import org.junit.Test;
import org.openehr.rm.support.identification.ISO_OID;
import org.openehr.rm.support.identification.InternetID;

/**
 * Some tests just to learn UID.
 * @author kyriosdata
 */
public class UIDTest {

    @Test
    public void minimalUID() {
        UID uid = new UUID(getUUID());
        uid = new ISO_OID("1.2");
        uid = new InternetID("ufg.br");
    }

    private String getUUID() {
        return java.util.UUID.randomUUID().toString();
    }

}
