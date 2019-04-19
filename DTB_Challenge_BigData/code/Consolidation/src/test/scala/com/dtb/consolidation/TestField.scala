package com.dtb.consolidation

import org.scalatest.FunSuite



/**
  * @author CGodinho
  *
  * Test class for object Field.
  * Used as an example to check ScalaTest.
  */
class TestField extends FunSuite {

    test("3 simple fields") {

        assert(Field.count(',', '"', "Hello, world,!") == 3)
    }

    test("4 simple fields") {

      assert(Field.count(',', '"', "11221,Hello, world,!") == 4)
    }

    test("A field quoted withot separator") {

      assert(Field.count(',', '"', "11221,\"Hello world\",!") == 3)
    }

    test("A fields quoted with a separator") {

      assert(Field.count(',', '"', "11221,\"Hello, world\",!") == 3)
    }

    test("A field quoted with many separators") {

        assert(Field.count(',', '"', "abc,11221,\"Hello, world, here , I have ,,, many separators\",!") == 4)
    }

    test("Fields quoted with separators") {

        assert(Field.count(',', '"', "begin,11221,\"Hello, world!\",abc, eeeeee,\"www\",111,\",a,b,c,d\",end") == 9)
    }

    test("Fields quoted with separators simple") {

        assert(  Field.tokens(',', '"',  "Hello, world,!") == List(-1, 5, 12, 14))
    }

    test("Fields quoted with separators complex") {

        assert(Field.tokens(',', '"', "Hello, \"w,o,r,l,d\",!") == List(-1, 5, 18, 20))
    }
}