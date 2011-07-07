package org.openehr.rm;

/**
 * Classe abstrata que define chave "padrão" para persistência via JPA.
 *
 */
public abstract class KeyID {

    private long key_id;

    public long getKey_id() {
        return key_id;
    }
}
