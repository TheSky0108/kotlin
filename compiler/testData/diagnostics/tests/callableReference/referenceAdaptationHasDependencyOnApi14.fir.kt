// !API_VERSION: 1.3
// !DIAGNOSTICS: -UNUSED_PARAMETER

class A {
    fun foo(s: String = "", vararg xs: Long): String = "foo"
}

fun coercionToUnit(f: (A, String, LongArray) -> Unit): Any = f
fun varargToElement(f: (A, String, Long, Long) -> String): Any = f
fun defaultAndVararg(f: (A) -> String): Any = f
fun allOfTheAbove(f: (A) -> Unit): Any = f

fun test() {
    coercionToUnit(A::foo)
    <!INAPPLICABLE_CANDIDATE!>varargToElement<!>(A::foo)
    <!INAPPLICABLE_CANDIDATE!>defaultAndVararg<!>(A::foo)
    <!INAPPLICABLE_CANDIDATE!>allOfTheAbove<!>(A::foo)
}