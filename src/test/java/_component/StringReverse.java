package _component;

import java.text.BreakIterator;
import java.util.Locale;

public class StringReverse {
    public static void main(String[] args) {
        String s = "\uD83D\uDC67\uD83C\uDFFFa";
        System.out.println(s);
        System.out.println(new StringBuilder(s).reverse().toString());
        System.out.println(reverse(s));
        System.out.println(s.length());
        System.out.println(s.codePointCount(0, s.length()));
        System.out.println(getLength(s));
    }

    public static int getLength(String text) {
        BreakIterator graphemeIterator = BreakIterator.getCharacterInstance(Locale.US);
        graphemeIterator.setText(text);
        int graphemes = 0;
        while (graphemeIterator.next() != BreakIterator.DONE) {
            graphemes++;
        }
        return graphemes;
    }

    private static String reverse(String s) {
        StringBuilder builder = new StringBuilder();
        boolean hasSurrogates = false;
        for (int i = s.length() -1; i >= 0; --i) {
            builder.append(s.charAt(i));
            if (Character.isSurrogate(s.charAt(i))) {
                hasSurrogates = true;
            }
        }
        if (hasSurrogates) {
            for (int i = 0; i < s.length(); ++i) {
                if (Character.isLowSurrogate(builder.charAt(i))) {
                    if ((i + 1) < s.length() && Character.isHighSurrogate(builder.charAt(i + 1))) {
                        char ch = builder.charAt(i);
                        builder.setCharAt(i, builder.charAt(i + 1));
                        builder.setCharAt(i + 1, ch);
                    }
                }
            }
        }
        return builder.toString();
    }
}
