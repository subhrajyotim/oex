package org.openehr.rm.common.resource;

import java.util.HashMap;
import org.openehr.rm.common.generic.RevisionHistory;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.support.terminology.TerminologyService;

/**
 * Esta classe tem como principal fun��o auxiliar na persist�ncia
 * dos dados das classes que herdar�o de AuthoredResource.
 * Recomenda-se herdar esta classe e sobrescrever os m�todos toByteArray()
 * e fromByteArray(byte[] bytes), para que, assim, a persist�ncia seja realizada.
 * 
 * @author Geovane Pazine Filho
 */
public class AuthoredResourcePersiste extends AuthoredResource {

    public AuthoredResourcePersiste(CodePhrase originalLanguage, HashMap<String,
            TranslationDetails> translations, ResourceDescription description,
            RevisionHistory revisionHistory, boolean isControlled,
            TerminologyService terminologyService) {
        super(originalLanguage, translations, description, revisionHistory,
                isControlled, terminologyService);
    }

    AuthoredResourcePersiste() {
        super();
    }

    /*
     * Deve ser sobrescrito para auxiliar na persist�ncia das classes
     * que herdam de AuthoredResource. Quando for chamado, deve retornar um
     * array de byte que contenha todos os atributos da classe criada.
     *
     * @return Array de byte se a classe possuir novos atributos ou null
     * se n�o possuir.
     */
    public byte[] toByteArray() {
        return null;
    }

    /*
     * Deve ser sobrescrito para auxilar na persist�ncia das classes
     * que herdam de AuthoredResource. Este m�todo deve retornar os estados dos
     * objetos que foram persistidos anteriormente. O mesmo tem como par�metro
     * um array de bytes que cont�m o estado dos objetos.
     *
     * @param bytes Array de byte que cont�m o estado dos objetos
     */
    public void fromByteArrya(byte[] bytes) {
    }
}
