package com.example.wooisso.sunnyapplication;

/**
 * Created by masterj on 2016-12-21.
 */

public class GetHangulChosung {

    // ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ
    private static final char[] CHO =
            {0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141,
                    0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148,
                    0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};

    //ㅏㅐㅑㅒㅓㅔㅕㅖ ㅗ ㅘ ㅙ ㅚ ㅛ ㅜ ㅝ ㅞ ㅟ ㅠ ㅡ ㅢ ㅣ
    private static final char[] JUN =
            {0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155,
                    0x3156, 0x3157, 0x3158, 0x3159, 0x315a, 0x315b,
                    0x315c, 0x315d, 0x315e, 0x315f, 0x3160,    0x3161,
                    0x3162, 0x3163};

    // ㄱㄲㄳㄴㄵㄶㄷㄹㄺ ㄻ ㄼ ㄽ ㄾ ㄿ ㅀ ㅁ ㅂ ㅄ ㅅ ㅆ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ
    private static final char[] JON =
            {0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136,
                    0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d,
                    0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144,
                    0x3145, 0x3146, 0x3147, 0x3148, 0x314a, 0x314b,
                    0x314c, 0x314d, 0x314e};

    private final static char DefHangulUnicodeOffLow = 0xAC00;
    private final static char DefHangulUnicodeOffHi = 0xD7AF;


    /*
        Check a char in HangulUnicode range.
    * */
    public static boolean isHangul(final char hchar) {
        if ( hchar >= DefHangulUnicodeOffLow &&
                hchar <= DefHangulUnicodeOffHi) {
            return true;
        }
        return false;
    }

    public static char getChosung(final char hchar) {
        char temp = hchar;
        temp -= DefHangulUnicodeOffLow;

        char cho = (char)(((temp - (temp % 28)) /28) / 21);

        return CHO[cho];
    }

    public static char getJunsung(final char hchar) {
        char temp = hchar;
        temp -= DefHangulUnicodeOffLow;

        char jun = (char)(((temp - (temp % 28)) /28) % 21);

        return JUN[jun];
    }

    public static char getJonsung(final char hchar) {
        char temp = hchar;
        temp -= DefHangulUnicodeOffLow;

        char jon = (char)(temp - (temp % 28));

        return JON[jon];
    }
}
