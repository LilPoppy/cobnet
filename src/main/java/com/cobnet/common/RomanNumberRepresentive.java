package com.cobnet.common;

import com.cobnet.exception.RomanNumberSupportFormatException;

import java.util.Arrays;
import java.util.stream.Stream;

public enum RomanNumberRepresentive {

    I (1),
    IV(4),
    V (5),
    IX(9),
    X (10),
    XL(40),
    L (50),
    XC(90),
    C (100),
    CD(400),
    D (500),
    CM(900),
    M (1000);


    private int unit;

    private RomanNumberRepresentive(int num) {

        this.unit = num;
    }

    public int getValue() {

        return this.unit;
    }

    public static int getNumber(String text) {

        return getNumber(getRepresentives(text));
    }

    public static int getNumber(RomanNumberRepresentive[] representives) {

        for(int i = 0; i < representives.length; i++) {

            if(i + 1 < representives.length) {

                if(representives[i + 1].getValue() > representives[i].getValue()) {

                    throw new ArithmeticException("Invaild format");
                }
            }
        }

        return Arrays.stream(representives).mapToInt(RomanNumberRepresentive::getValue).sum();
    }

    public static String format(int num) throws RomanNumberSupportFormatException {

        return String.join("" , Arrays.stream(RomanNumberRepresentive.getUnits(num)).map(representive -> {

            assert representive != null;

            return representive.toString();

        }).toList());
    }

    public static RomanNumberRepresentive[] getUnits(int num) throws RomanNumberSupportFormatException {

        RomanNumberRepresentive result = RomanNumberRepresentive.getUnit(num);

        int left = num - result.getValue();

        if(left > 0) {

            return Stream.of(result , getUnits(left)).toArray(RomanNumberRepresentive[]::new);
        }

        return new RomanNumberRepresentive[] { result };
    }



    static RomanNumberRepresentive getUnit(int num) throws RomanNumberSupportFormatException {

        RomanNumberRepresentive[] enums = RomanNumberRepresentive.values();

        for(int i = enums.length - 1; i >= 0; i--) {

            RomanNumberRepresentive representive = enums[i];

            if(num >= representive.getValue()) {

                return representive;
            }
        }

        throw new RomanNumberSupportFormatException(num);
    }

    static RomanNumberRepresentive getRepresentive(String text) {

        final String letter = text.toUpperCase();

        return Arrays.stream(RomanNumberRepresentive.values()).filter(representive -> representive.toString().equals(letter)).findFirst().orElse(null);

    }

    static RomanNumberRepresentive[] getRepresentives(String text) {

        if(text.length() > 0) {

            int index = text.length() > 1 ? 2 : 1;

            RomanNumberRepresentive representive = RomanNumberRepresentive.getRepresentive(text.substring(0, index));

            if(representive == null) {

                index = 1;

                representive = RomanNumberRepresentive.getRepresentive(text.substring(0, index));

            }

            return Stream.of(representive, getRepresentives(text.substring(index))).toArray(RomanNumberRepresentive[]::new);
        }

        return new RomanNumberRepresentive[] {};
    }
}
