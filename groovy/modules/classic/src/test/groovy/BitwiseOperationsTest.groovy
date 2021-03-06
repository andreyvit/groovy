/** 
 * Test Bitwise Operations in Classic/New Groovy
 * 
 * @author Pilho Kim
 * @version $Revision$
 */
class BitwiseOperationsTest extends GroovyTestCase {

    void testBitwiseShift() {
        a = 4
        b = -4
        assert a << 1 == 8
        assert a << 2 == 16
        assert a >> 1 == 2
        assert a >> 2 == 1
        assert a >>> 1 == 2
        assert a >>> 2 == 1
        assert b << 1 == -8
        assert b << 2 == -16
        assert b >> 1 == -2
        assert b >> 2 == -1
        assert b >>> 1 == 0x7FFFFFFE
        assert b >>> 2 == 0x3FFFFFFF
    }

    void testBitwiseShiftEQUAL() {
        a = 4
        a <<= 1
        assert a == 8
        a <<= 2
        assert a == 32
        a >>= 1
        assert a == 16
        a >>= 2
        assert a == 4

        b = -4
        b <<= 1
        assert b == -8
        b <<= 2
        assert b == -32
        b >>= 1
        assert b == -16
        b >>= 2
        assert b == -4

        b = -4
        b >>>= 1
        assert b == 0x7FFFFFFE
        b = -8
        b >>>= 2
        assert b == 0x3FFFFFFE
    }

    void testBitwiseAnd() {
       /*
        // Oprator Precedence Problem
        // ^, &, | should be prior to ==, <, >, <=, >=
        a = 13
        assert a & 3 == 1    // 0x0000000D & 0x00000003
        assert a & 7 == 5    // 0x0000000D & 0x00000007
        b = -13
        assert b & 3 == 3    // 0xFFFFFFF3 & 0x00000003
        assert b & 7 == 3    // 0xFFFFFFF3 & 0x00000007
       */

        a = 13
        assert (a & 3) == 1    // 0x0000000D & 0x00000003
        assert (a & 7) == 5    // 0x0000000D & 0x00000007
        b = -13
        assert (b & 3) == 3    // 0xFFFFFFF3 & 0x00000003
        assert (b & 7) == 3    // 0xFFFFFFF3 & 0x00000007
    }

    void testBitwiseAndEqual() {
        a = 13
        a &= 3
        assert a == 1    // 0x0000000D & 0x00000003
        a &= 4
        assert a == 0    // 0x00000001 & 0x00000004
        b = -13
        b &= 3
        assert b == 3    // 0xFFFFFFF3 & 0x00000003
        b &= 7
        assert b == 3    // 0x00000003 & 0x00000007
    }

    void testBitwiseOr() {
       /*
        // Oprator Precedence Problem
        // ^, &, | should be prior to ==, <, >, <=, >=
        a = 13
        assert a | 8 == 13      // 0x0000000D | 0x00000008
        assert a | 16 == 29     // 0x0000000D | 0x00000010
        b = -13
        assert b | 8 == -5      // 0xFFFFFFF3 | 0x00000008
        assert b | 16 == -13    // 0xFFFFFFF3 | 0x00000010
       */

        a = 13
        assert (a | 8) == 13      // 0x0000000D | 0x00000008
        assert (a | 16) == 29     // 0x0000000D | 0x00000010
        b = -13
        assert (b | 8) == -5      // 0xFFFFFFF3 | 0x00000008
        assert (b | 16) == -13    // 0xFFFFFFF3 | 0x00000010
    }

    void testBitwiseOrEqual() {
        a = 13
        a |= 2
        assert a == 15     // 0x0000000D | 0x00000002
        a |= 16
        assert a == 31     // 0x0000000F | 0x0000001F
        b = -13
        b |= 8
        assert b == -5     // 0xFFFFFFF3 | 0x00000008
        b |= 1
        assert b == -5     // 0xFFFFFFFB | 0x00000001
    }

    void testBitwiseXor() {
       /*
        // Oprator Precedence Problem
        // ^, &, | should be prior to ==, <, >, <=, >=
        a = 13
        assert a ^ 10 == 7     // 0x0000000D ^ 0x0000000A = 0x000000007
        assert a ^ 15 == 2     // 0x0000000D ^ 0x0000000F = 0x000000002
        b = -13
        assert b ^ 10 == -7    // 0xFFFFFFF3 ^ 0x0000000A = 0xFFFFFFF9
        assert b ^ 15 == -4    // 0xFFFFFFF3 ^ 0x0000000F = 0xFFFFFFFC
       */

        a = 13
        assert (a ^ 10) == 7     // 0x0000000D ^ 0x0000000A = 0x000000007
        assert (a ^ 15) == 2     // 0x0000000D ^ 0x0000000F = 0x000000002
        b = -13
        assert (b ^ 10) == -7    // 0xFFFFFFF3 ^ 0x0000000A = 0xFFFFFFF9
        assert (b ^ 15) == -4    // 0xFFFFFFF3 ^ 0x0000000F = 0xFFFFFFFC
    }

    void testBitwiseXorEqual() {
        a = 13
        a ^= 8
        assert a == 5      // 0x0000000D ^ 0x00000008 = 0x000000005
        a ^= 16
        assert a == 21     // 0x00000005 ^ 0x00000010 = 0x000000015
        b = -13
        b ^= 8
        assert b == -5     // 0xFFFFFFF3 ^ 0x00000008 = 0xFFFFFFFB
        b ^= 16
        assert b == -21    // 0xFFFFFFFB ^ 0x00000010 = 0xFFFFFFEB
    }

    void testBitwiseOrInClosure() {
        c1 = { x, y | x | y }
        assert c1(14, 5) == 15          // 0x0000000E | 0x00000005 = 0x0000000F
        assert c1(0x0D, 0xFE) == 255    // 0x0000000D | 0x000000FE = 0x000000FF

        c2 = { |x, y| x | y }
        assert c2(14, 5) == 15          // 0x0000000E | 0x00000005 = 0x0000000F
        assert c2(0x0D, 0xFE) == 255    // 0x0000000D | 0x000000FE = 0x000000FF
    }

    void testAmbiguityOfBitwiseOr() {
        c1 = { x, y | x | y }
        assert c1(14, 5) == 15          // 0x0000000E | 0x00000005 = 0x0000000F
        assert c1(0x0D, 0xFE) == 255    // 0x0000000D | 0x000000FE = 0x000000FF

        c2 = { |x, y| x | y }
        assert c2(14, 5) == 15          // 0x0000000E | 0x00000005 = 0x0000000F
        assert c2(0x0D, 0xFE) == 255    // 0x0000000D | 0x000000FE = 0x000000FF

        x = 3
        y = 5
        c1 = { x | y }      // | is a pipe
        c2 = { x & y }      // & is a bitAnd
        c3 = { x ^ y }      // & is a bitXor
        c11 = {
             x | y          // | is a pipe
        }
        c12 = {
             (x | y)        // | is a bitOr
        }
        c13 = {| x | y      // two |'s are pipes
        }
        c14 = {|| x | y     // last | is a bitOr
        }

        assert c1() == 5
        assert c2() == 1
        assert c3() == 6
        assert c11() == 5
        assert c12() == 7
        assert c13() == 5
        assert c14() == 7

        x = 0x03

        d1 = { x | x }      // | is a pipe
        d2 = { x & x }      // & is a bitAnd
        d3 = { x ^ x }      // & is a bitXor
        d11 = {
             x | x          // | is a pipe
        }
        d12 = {
             (x | x)        // | is a bitOr
        }
        d13 = {| x | x      // two |'s are pipes
        }
        d14 = {|| x | x     // last | is a bitOr
        }
        assert d1(0xF0) == 0xF0
        assert d2(0xF0) == 0x03
        assert d3(0xF0) == 0
        assert d11(0xF0) == 0xF0
        assert d12(0xF0) == 0x03
        assert d13(0xF0) == 0xF0
        assert d14(0xF0) == 0x03
    }

    void testBitwiseNegation() {
        assert ~1 == -2     // ~0x00000001 = 0xFFFFFFFE
        assert ~-1 == 0     // ~0xFFFFFFFF = 0x00000000
        assert ~~5 == 5     // ~~0x00000005 = ~0xFFFFFFFA = 0xFFFFFFF5
        a = 13
        assert ~a  == -14     // ~0x0000000D = 0xFFFFFFF2
        assert ~~a  == 13     // ~~0x0000000D = ~0xFFFFFFF2 = 0x0000000D
        assert -~a  == 14     // -~0x0000000D = -0xFFFFFFF2 = 0x0000000E
    }

    void testBitwiseNegationType() {
        x = ~7
        assert x.class == java.lang.Integer

        y = ~"foo"
        assert y.class == java.util.regex.Pattern

        z = ~"${x}"
        assert z.class == java.util.regex.Pattern
    }

    void testBitwiseNegationTypeCallFunction() {
        // integer test
        assert neg(2).class == java.lang.Integer
        assert neg(2) instanceof java.lang.Integer
        assert neg(2) == ~2

        // long test
        assert neg(2L).class == java.lang.Long
        assert neg(2L) instanceof java.lang.Long
        assert neg(2L) == ~2

        // BigInteger test
        assert neg(new java.math.BigInteger("2")).class == java.math.BigInteger
        assert neg(new java.math.BigInteger("2")) instanceof java.math.BigInteger
        assert neg(new java.math.BigInteger("2")) == ~2

        // BigInteger test
        assert neg(2G).class == java.math.BigInteger
        assert neg(2G) instanceof java.math.BigInteger
        assert neg(2G) == ~2

        assert neg("foo").class == java.util.regex.Pattern
        assert neg("foo") instanceof java.util.regex.Pattern
    }

    Object neg(n) {
        if (n instanceof java.lang.Integer) {
            return ~n
        }
        else if (n instanceof java.lang.Long) {
            return ~n
        }
        else if (n instanceof java.math.BigInteger) {
            return ~n
        }
        else {
             return ~n.toString()
        }
    }
}