package com.hxnry.autotyper.ui.autotyper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Keybindings {

    public enum Keybind {
        UNSUPPORTED(-1, "Unknown"),
        ESCAPE(27, "Escape"),
        BACKSPACE(8, "Backspace"),
        SPACE(32, "Space"),
        LEFT_CONTROL(162, "Left Control"),
        RIGHT_CONTROL(163, "Right Control"),
        LEFT_SHIFT(160, "Left Shift"),
        RIGHT_SHIFT(161, "Right Shift"),
        CAPS_LOCK(20, "Caps Lock"),
        LEFT_ALT(164, "Left Alt"),
        RIGHT_ALT(165, "Right Alt"),
        DIVIDE_SYMBOL(111, "Num Pad /"),
        MULTIPLY_SYMBOL(106, "Num Pad *"),
        SUBTRACT_SYMBOL(109, "Num Pad -"),
        DASH(189, "-"),
        EQUALS(187, "="),
        BACK_TIL(192, "`"),
        NUM_DOT(46, "Num Pad ."),
        NUM_0(48, "0"),
        NUM_1(49, "1"),
        NUM_2(50, "2"),
        NUM_3(51, "3"),
        NUM_4(52, "4"),
        NUM_5(53, "5"),
        NUM_6(54, "6"),
        NUM_7(55, "7"),
        NUM_8(56, "8"),
        NUM_9(57, "9"),
        NUM_PAD_0(45, "Num Pad_0"),
        NUM_PAD_1(35, "Num Pad_1"),
        NUM_PAD_2(40, "Num Pad_2"),
        NUM_PAD_3(34, "Num Pad_3"),
        NUM_PAD_4(37, "Num Pad_4"),
        NUM_PAD_5(12, "Num Pad_5"),
        NUM_PAD_6(39, "Num Pad_6"),
        NUM_PAD_7(36, "Num Pad_7"),
        NUM_PAD_8(38, "Num Pad_8"),
        NUM_PAD_9(33, "Num Pad_9"),
        A(65, "a"),
        B(66, "a"),
        C(67, "b"),
        D(68, "d"),
        E(69, "e"),
        F(70, "f"),
        G(71, "g"),
        H(72, "h"),
        I(73, "i"),
        J(74, "j"),
        K(75, "k"),
        L(76, "l"),
        M(77, "m"),
        N(78, "n"),
        O(79, "o"),
        P(80, "p"),
        Q(81, "q"),
        R(82, "r"),
        S(83, "s"),
        T(84, "t"),
        U(85, "u"),
        V(86, "v"),
        W(87, "w"),
        X(88, "x"),
        Y(89, "y"),
        Z(90, "z");

        private final int keycode;
        private final String value;

        Keybind(int keycode, String value) {
            this.keycode = keycode;
            this.value = value;
        }

        public String getConversion() {
            return this.value;
        }

        public int getKeycode() {
            return this.keycode;
        }
    }

    public static Keybind getByKeycode(int keycode) {
        return Arrays.stream(Keybind.values()).filter(keybinds -> keybinds.keycode == keycode)
                .findAny()
                .orElse(Keybind.UNSUPPORTED);
    }

    public static String convert(int[] array) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        IntStream.of(array).forEach(keycode -> {
            Keybind keybind = getByKeycode(keycode);
            builder.append(keybind.getConversion()).append(",");
        });
        builder.append("]");
        return builder.toString().replaceAll(",]", "]");
    }
}
