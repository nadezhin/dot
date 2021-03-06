package dot

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TestDotPrettyPrinter extends FunSuite with DotPrettyPrint with DotNominalSyntax with DotParsing {
  def ok(in: String) = phrase(Parser.term)(new lexical.Scanner(in)) match {
    case Success(tm, _) => expectResult(in.trim){tm.pp}
    case r@_ => sys.error("parsing failed: " + r)
  }

  test("1") { ok("val x = new ⊤; x") }
  test("2") { ok("val x = new ⊤; val y = new ⊤; val z = new ⊤; z") }
  test("3") { ok("val x = new ⊤ { z ⇒ }; x") }
  test("4") { ok("val x = new ⊤ { z ⇒ L: ⊥ .. ⊤ }; x") }

  test("5") { ok("""
val x = new ⊤ { z ⇒ L: ⊥ .. ⊤ } ∧
            ⊤ { z ⇒ L: ⊥ .. ⊤ }; x""") }

  test("6") { ok("""
val x = new ⊤ { x ⇒ !L: ⊥ .. ⊤; L: ⊥ .. ⊤ };
val y = new x.!L;
val id = new ⊤ { id ⇒ app: ⊤ → ⊤ } ( app(x) = x );
id.app(y)
""") }

  test("7") { ok(
"""
val sugar = new ⊤ { s ⇒
   !Arrow: ⊥ .. ⊤ { f ⇒ app: ⊥ → ⊤ };
   !Unit: ⊥ .. ⊤
};
val unit = new sugar.!Unit;
val id = new sugar.!Arrow { f ⇒
   app: ⊤ → ⊤
} ( app(x) = x );
val error = new sugar.!Arrow { f ⇒
   app: ⊤ → ⊥
} ( app(x) = error.app(x) );
val root = new ⊤ { r ⇒
   !Nat2Nat: sugar.!Arrow { f ⇒
                app: r.!Nat → r.!Nat
             } ..
             sugar.!Arrow { f ⇒
                app: r.!Nat → r.!Nat
             };
   !Boolean: ⊥ .. ⊤ { b ⇒
      ifNat: r.!Nat → r.!Nat2Nat
   };
   !Nat: ⊥ .. ⊤ { n ⇒
      isZero: sugar.!Unit → r.!Boolean;
      pred: sugar.!Unit → r.!Nat;
      succ: sugar.!Unit → r.!Nat
   };
   false: sugar.!Unit → r.!Boolean;
   true: sugar.!Unit → r.!Boolean;
   zero: sugar.!Unit → r.!Nat;
   successor: r.!Nat → r.!Nat
} (
   false(u) = val ff = new root.!Boolean (
      ifNat(a) = val f = new root.!Nat2Nat (
         app(b) = b
      ); f
   ); ff;
   true(u) = val tt = new root.!Boolean (
      ifNat(a) = val f = new root.!Nat2Nat (
         app(b) = a
      ); f
   ); tt;
   zero(u) = val zz = new root.!Nat (
      isZero(u) = root.true(u);
      succ(u) = root.successor(zz);
      pred(u) = error.app(u)
   ); zz;
   successor(n) = val ss = new root.!Nat (
      isZero(u) = root.false(u);
      succ(u) = root.successor(ss);
      pred(u) = n
   ); ss
);
id.app(root.zero(unit).succ(unit).pred(unit).isZero(unit))
""") }

  test("Grouping1") { ok("val x = new ⊤ { z ⇒ L: ⊤ .. ⊤ ∧ ⊤ ∨ ⊤ }; x") }
  test("Grouping2") { ok("val x = new ⊤ { z ⇒ L: ⊤ .. ⊤ ∧ (⊤ ∨ ⊤) }; x") }
  test("Grouping3") { ok("val x = new ⊤ { z ⇒ L: ⊤ .. ⊤ ∧ ⊤ ∨ ⊤ ∧ ⊤ ∨ ⊤ }; x") }
  test("Grouping4") { ok("""
val x = new ⊤ { z ⇒
   L: ⊤ .. (⊤ ∨ ⊤) ∧ (⊤ ∨ ⊤) ∧ (⊤ ∨ ⊤)
}; x
""") }

  test("Grouping5") { ok("""
val x = new ⊤ { z ⇒
   L: ⊤ .. (⊤ ∨ ⊤) ∧
           (⊤ ∨ ⊤) ∧
           (⊤ ∨ ⊤) ∧
           (⊤ ∨ ⊤) ∧
           (⊤ ∨ ⊤) ∧
           (⊤ ∨ ⊤) ∧
           (⊤ ∨ ⊤) ∧
           (⊤ ∨ ⊤) ∧
           (⊤ ∨ ⊤)
}; x
""") }
}