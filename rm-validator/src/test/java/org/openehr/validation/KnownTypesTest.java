package org.openehr.validation;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openehr.rm.RMObject;

public class KnownTypesTest {

    @Test
    public void getUidBasedIdsTest(){
        Map<String,Class> map = KnownTypes.getUidBasedIds();
        assertNotNull("Map de Ids baseados em UID é nulo", map);
        assertFalse("Map de Ids baseados em UID é vazio", map.entrySet().isEmpty());
    }

    @Test
    public void getBasicTypesTest(){
        Map<String,Class> map = KnownTypes.getBasicTypes();
        assertNotNull("Map de tipos básicos é nulo", map);
        assertFalse("Map de tipos básicos é vazio", map.entrySet().isEmpty());
    }

    @Test
    public void getAbstractTypesTest(){
        Map<String,Class> map = KnownTypes.getAbstractTypes();
        assertNotNull("Map de tipos abstratos é nulo", map);
        assertFalse("Map de tipos abstratos é vazio", map.entrySet().isEmpty());
    }

    @Test
    public void getUidIdentifiersTest() {
        Map<String,Class> map = KnownTypes.getUidIdentifiers();
        assertNotNull("Map de identificadores UID é nulo", map);
        assertFalse("Map de identificadores UID é vazio", map.entrySet().isEmpty());
    }

    @Test
    public void getInterfacesTypesTest() {
        Map<String,Class> map = KnownTypes.getInterfacesTypes();
        assertNotNull("Map de interfaces é nulo", map);
        assertFalse("Map de interfaces é vazio", map.entrySet().isEmpty());
    }

    @Test
    public void getContainerTypesTest(){
        Map<String,Class> map = KnownTypes.getContainerTypes();
        assertNotNull("Map de containers é nulo", map);
        assertFalse("Map de containers é vazio", map.entrySet().isEmpty());
    }

    @Test
    public void getRmTypesTest() {
        Map<String, Class<RMObject>> map = KnownTypes.getRmTypes();
        assertNotNull("Map de RMObjects é nulo", map);
        assertFalse("Map de RMObjects é vazio", map.entrySet().isEmpty());
    }

    @Test
    public void getAllTypesTest() {
        Map<String, Class> map = KnownTypes.getAllTypes();
        assertNotNull("Map de tipos é nulo", map);
        assertFalse("Map de tipos é vazio", map.entrySet().isEmpty());
    }

}
