package com.nombreGrupo.util;

import java.text.Normalizer;

public class StringUtil {

    public static String toKebabCase(String input) {
        String temp = input.toLowerCase();
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD);
        temp = temp.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        temp = temp.replace(" ", "-");
        return temp;
    }
    
}
