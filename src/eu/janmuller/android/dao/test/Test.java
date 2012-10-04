package eu.janmuller.android.dao.test;

import eu.janmuller.android.dao.api.Foo;
import eu.janmuller.android.dao.api.TestEnum;
import eu.janmuller.android.dao.api.UUIDId;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Coder: Jan Müller
 * Date: 03.10.12
 * Time: 13:53
 */
public class Test {

    public static void test() {

        Foo foo = new Foo();

        Foo.findObjectById(foo, new UUIDId("dsfjkladsjf"));

        int count = Foo.getCount();
        //String d = s.email;
        //s.delete();
        //s.delete();


        Foo t = new Foo();
        t.age = 10;
        t.name = "Honza";
        t.birthday = new Date();
        t.tEnum = TestEnum.TEST1;

        //t.creationDate
        Foo f = t.save();

        t.getId();





    }

    public static void main(String[] args) {

        test();
    }



}
