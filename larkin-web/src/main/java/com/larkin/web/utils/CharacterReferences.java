package com.larkin.web.utils;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

class CharacterReferences {
    static final char REFERENCE_START = 38;
    static final String DECIMAL_REFERENCE_START = "&#";
    static final String HEX_REFERENCE_START = "&#x";
    static final char REFERENCE_END = 59;
    static final char CHAR_NULL = 65535;
    private static final String PROPERTIES_FILE = "config/CharacterReferences.properties";
    private final String[] characterToEntityReferenceMap = new String[3000];
    private final Map<String, Character> characterReferenceMap = Maps.newHashMapWithExpectedSize(252);

    public CharacterReferences() {
        Properties entityReferences = new Properties();
        InputStream is = CharacterReferences.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (is == null) {
            throw new IllegalStateException("Cannot find [CharacterReferences.properties] as class path resource");
        }
        try {
            try {
                entityReferences.load(is);
            } finally {
                is.close();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to parse reference definition file [CharacterReferences.properties]: " + ex.getMessage());
        }

        Enumeration<?> keys = entityReferences.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            int referredChar = Integer.parseInt(key);

            int index = (referredChar < 1000) ? referredChar : referredChar - 7000;
            String reference = entityReferences.getProperty(key);
            this.characterToEntityReferenceMap[index] = '&' + reference + ';';
            this.characterReferenceMap.put(reference, new Character((char) referredChar));
        }
    }

    public int getSupportedReferenceCount() {
        return this.characterReferenceMap.size();
    }

    public boolean isMappedToReference(char character) {
        return (convertToReference(character) != null);
    }

    public String convertToReference(char character) {
        if ((character < 1000) || ((character >= 8000) && (character < 10000))) {
            int index = (character < 1000) ? character : character - 7000;
            String entityReference = this.characterToEntityReferenceMap[index];
            if (entityReference != null) {
                return entityReference;
            }
        }
        return null;
    }

    public char convertToCharacter(String entityReference) {
        Character referredCharacter = (Character) this.characterReferenceMap.get(entityReference);
        if (referredCharacter != null) {
            return referredCharacter.charValue();
        }
        return 65535;
    }
}
