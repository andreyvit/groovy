import java.math.BigDecimal;
import java.math.BigInteger;

class BigDecimalOperationTest extends GroovyTestCase {

    property x
    property y

    void testPlus() {
        x = .1 + 1.1
        assert x == 1.2
        assert x instanceof BigDecimal;

        x = 3 + 2.2
        assert x == 5.2

        x = 2.2 + 4
        assert x == 6.2

        y = x + 1
        assert y == 7.2

        z = y + x + 1 + 2
        assert z == 16.4
    }

    void testMinus() {
        x = 1.1-0.01
        assert x == 1.09

        x = 6 - 2.2
        assert x == 3.8

        x = 5.8 - 2
        assert x == 3.8

        y = x - 1
        assert y == 2.8
    }

    void testMultiply() {
        x = 3 * 2.0
        assert x == 6.0

        x = 3.0 * 2
        assert x == 6.0

        x = 3.0 * 2.0
        assert x == 6.0

        y = x * 2
        assert y == 12.0

        y = 11 * 3.333
        assert y == 36.663 : "y = " + y

        y = 3.333 * 11
        assert y == 36.663 : "y = " + y
    }

    void testDivide() {
        x = 80.0 / 4
        assert x == 20.0 : "x = " + x

        x = 80 / 4.0
        assert x == 20.0 : "x = " + x

        y = x / 2
        assert y == 10.0 : "y = " + y
        assert y == 10 : "y = " + y

        y = 34 / 3.000
        assert y == 11.3333333333 : "y = " + y

        y = 34.00000000000 / 3
        assert y == 11.33333333333 : "y = " + y
    }
}
