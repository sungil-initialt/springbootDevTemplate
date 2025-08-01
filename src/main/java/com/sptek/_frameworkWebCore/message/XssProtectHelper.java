package com.sptek._frameworkWebCore.message;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
Xss 방지 적용을 위한 클레스로 버그가 있지않는 한 수정할 부분은 없다.
 */
public class XssProtectHelper extends CharacterEscapes {
    private final int[] asciiEscapes;
    private final Map<Integer, SerializedString> escapeCache = new ConcurrentHashMap<>();

    // todo: 성능 측면 고려 필요
    public XssProtectHelper() {
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['&'] = CharacterEscapes.ESCAPE_CUSTOM;

        /*
        // 추가 적용 고려 가능
        asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['/'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['='] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['+'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[';'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['%'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\\'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[':'] = CharacterEscapes.ESCAPE_CUSTOM;
        */
    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

//    @Override
//    public SerializableString getEscapeSequence(int ch) {
//        return new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString((char) ch)));
//    }

    // 성능 개선을 위해 케시 기능을 추가함
    @Override
    public SerializableString getEscapeSequence(int ch) {
        return escapeCache.computeIfAbsent(ch, key ->
                new SerializedString(
                        StringEscapeUtils.escapeHtml4(Character.toString((char) key.intValue()))
                )
        );
    }
}


