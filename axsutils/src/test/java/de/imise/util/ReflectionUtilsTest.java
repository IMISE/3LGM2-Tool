package de.imise.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;
import org.testng.collections.Lists;
import org.testng.collections.Sets;

import de.imise.util.ReflectionUtils.CurrentClassGetter;

public class ReflectionUtilsTest {

    private interface I {
    };
    private interface I1 extends I {
    }
    private interface I2 extends I {
    }
    private interface J {
    };
    private interface J1 extends J {
    }
    private interface J2 extends J {
    }

    private class A implements I, J {
    };
    @SuppressWarnings("unused")
    private class A1 extends A implements I1, J1 {
        public static final String VAR = "A1";
        public static final int INT = 1;
    };
    private class A2 extends A implements I2, J2 {
    };
    private class A11 extends A1 {
        @SuppressWarnings("unused")
        public void doNothing() {
        }
    };
    private class A12 extends A1 {
    };
    private class A21 extends A2 {
        @SuppressWarnings("unused")
        public void doNothing() {
        }
    };
    @SuppressWarnings("unused")
    private class A111 extends A11 {
        public static final String VAR = "A111";
        public static final int INT = 111;
    };
    private class A121 extends A12 {
    };
    private class A211 extends A21 {
    };

    private final A a = new A();
    private final A1 a1 = new A1();
    private final A2 a2 = new A2();
    private final A11 a11 = new A11();
    private final A12 a12 = new A12();
    private final A21 a21 = new A21();
    private final A111 a111 = new A111();
    private final A121 a121 = new A121();
    private final A211 a211 = new A211();

    //    @BeforeClass
    //    final private void initObjectCollctions() {
    //        aObjectsNull = Lists.newArrayList((A) null, (A) null, (A) null);
    //        aObjects = Lists.newArrayList(a, a1, a2, a11, a12, a21, a111, a121, a211);
    //        aObjects2 = Lists.newArrayList(a211, a121, a111, a21, a12, a11, a2, a1, a);
    //        aObjects3 = Lists.newArrayList(a, a1, a2, a11, a12, a21, a111, a121, a211, null);
    //        aObjects4 = Lists.newArrayList(a, a1, a2, a11, a12, a21, a111, a121, a211, a211, a121, a111, a21, a12, a11, a2, a1, a, null);
    //        aObjects5 = Lists.newArrayList(a11, a12, a21, a111, a121, a211);
    //        aObjects6 = Lists.newArrayList(a12, a12);
    //        aObjects7 = Lists.newArrayList(a21, a21);
    //        aObjects8 = Lists.newArrayList(a12, a21);
    //        aObjects9 = Lists.newArrayList(a111, a211);
    //        aObjects10 = Lists.newArrayList(a1, a111);
    //        aObjects11 = Lists.newArrayList(a1, a211);
    //        aObjects12 = Lists.newArrayList(a1);
    //    }
    //
    //    @SuppressWarnings("unchecked")
    //    @BeforeClass
    //    final private void initClassCollections() {
    //        aClassesNull = Lists.newArrayList(null, null, null);
    //        aClasses = Lists.newArrayList(A.class, A1.class, A2.class, A11.class, A12.class, A21.class, A111.class, A121.class, A211.class);
    //        aClasses2 = Lists.newArrayList(A211.class, A121.class, A111.class, A21.class, A12.class, A11.class, A2.class, A1.class, A.class);
    //        aClasses3 = Lists.newArrayList(A.class, A1.class, A2.class, A11.class, A12.class, A21.class, A111.class, A121.class, A211.class, null);
    //        aClasses4 = Lists.newArrayList(A.class, A1.class, A2.class, A11.class, A12.class, A21.class, A111.class, A121.class, A211.class, A211.class, A121.class, A111.class, A21.class, A12.class, A11.class, A2.class, A1.class, A.class, null);
    //        aClasses5 = Lists.newArrayList(A11.class, A12.class, A21.class, A111.class, A121.class, A211.class);
    //        aClasses6 = Lists.newArrayList(A12.class, A12.class);
    //        aClasses7 = Lists.newArrayList(A21.class, A21.class);
    //        aClasses8 = Lists.newArrayList(A12.class, A21.class);
    //        aClasses9 = Lists.newArrayList(A111.class, A211.class);
    //        aClasses10 = Lists.newArrayList(A1.class, A111.class);
    //        aClasses11 = Lists.newArrayList(A1.class, A211.class);
    //        aClasses12 = Lists.newArrayList(A1.class);
    //    }
    //
    @Test
    public void getCommonSuperClassTestTT() {
        Object o = new Object();
        String s = new String();
        Class<? extends Object> commonSuperClassO;
        commonSuperClassO = ReflectionUtils.getCommonSuperClass(o, s);
        assertEquals(commonSuperClassO, Object.class);
        commonSuperClassO = ReflectionUtils.getCommonSuperClass(s, o);
        assertEquals(commonSuperClassO, Object.class);
        commonSuperClassO = ReflectionUtils.getCommonSuperClass(s, s);
        assertEquals(commonSuperClassO, String.class);

        Class<? extends A> commonSuperClassA;
        commonSuperClassA = ReflectionUtils.getCommonSuperClass(a1, a2);
        assertEquals(commonSuperClassA, A.class);
        commonSuperClassA = ReflectionUtils.getCommonSuperClass(a, a211);
        assertEquals(commonSuperClassA, A.class);
        commonSuperClassA = ReflectionUtils.getCommonSuperClass(a111, a121);
        assertEquals(commonSuperClassA, A1.class);

        //null checks
        commonSuperClassA = ReflectionUtils.getCommonSuperClass(a111, null);
        assertEquals(commonSuperClassA, A111.class);
        commonSuperClassA = ReflectionUtils.getCommonSuperClass(null, a111);
        assertEquals(commonSuperClassA, A111.class);
        commonSuperClassA = ReflectionUtils.getCommonSuperClass(null, null);
        assertEquals(commonSuperClassA, null);
    }

    @Test
    public void getCommonSuperClassOfClassesTestClassextendsTClassextendsT() {
        Class<? extends A> commonSuperClassOfClasses;
        commonSuperClassOfClasses = ReflectionUtils.getCommonSuperClassOfClasses(A1.class, A1.class);
        assertEquals(commonSuperClassOfClasses, A1.class);
        commonSuperClassOfClasses = ReflectionUtils.getCommonSuperClassOfClasses(A1.class, A2.class);
        assertEquals(commonSuperClassOfClasses, A.class);
        commonSuperClassOfClasses = ReflectionUtils.getCommonSuperClassOfClasses(A1.class, A111.class);
        assertEquals(commonSuperClassOfClasses, A1.class);
        commonSuperClassOfClasses = ReflectionUtils.getCommonSuperClassOfClasses(A111.class, A211.class);
        assertEquals(commonSuperClassOfClasses, A.class);

        //null checks
        commonSuperClassOfClasses = ReflectionUtils.getCommonSuperClassOfClasses(A111.class, null);
        assertEquals(commonSuperClassOfClasses, A111.class);
        commonSuperClassOfClasses = ReflectionUtils.getCommonSuperClassOfClasses(null, A111.class);
        assertEquals(commonSuperClassOfClasses, A111.class);
        commonSuperClassOfClasses = ReflectionUtils.getCommonSuperClassOfClasses(null, null);
        assertEquals(commonSuperClassOfClasses, null);
    }

    @Test
    public void getCommonSuperClassTestCollectionextendsT() {
        Class<? extends A> commonSuperClass;
        Collection<? extends A> aObjects = null;
        commonSuperClass = ReflectionUtils.getCommonSuperClass(aObjects);
        assertEquals(commonSuperClass, null);

        aObjects = Sets.newHashSet();
        commonSuperClass = ReflectionUtils.getCommonSuperClass(aObjects);
        assertEquals(commonSuperClass, null);

        aObjects = Sets.newHashSet((A) null, (A) null, (A) null);
        commonSuperClass = ReflectionUtils.getCommonSuperClass(aObjects);
        assertEquals(commonSuperClass, null);

        aObjects = Sets.newHashSet(a, a1, null); //null as value with valid values is ignored
        commonSuperClass = ReflectionUtils.getCommonSuperClass(aObjects);
        assertEquals(commonSuperClass, A.class);

        aObjects = Sets.newHashSet(a, a1, a2, a11, a12, a21, a111, a121, a211);
        commonSuperClass = ReflectionUtils.getCommonSuperClass(aObjects);
        assertEquals(commonSuperClass, A.class);
        aObjects = Sets.newHashSet(a11, a12, a111, a121);
        commonSuperClass = ReflectionUtils.getCommonSuperClass(aObjects);
        assertEquals(commonSuperClass, A1.class);

        aObjects = Sets.newHashSet(a12, a12); //same
        commonSuperClass = ReflectionUtils.getCommonSuperClass(aObjects);
        assertEquals(commonSuperClass, A12.class);
        aObjects = Sets.newHashSet(a111, a211);
        commonSuperClass = ReflectionUtils.getCommonSuperClass(aObjects);
        assertEquals(commonSuperClass, A.class);
        aObjects = Sets.newHashSet(a1, a211);
        commonSuperClass = ReflectionUtils.getCommonSuperClass(aObjects);
        assertEquals(commonSuperClass, A.class);
        aObjects = Sets.newHashSet(a1);
        commonSuperClass = ReflectionUtils.getCommonSuperClass(aObjects);
        assertEquals(commonSuperClass, A1.class);
    }

    @Test
    public void getCommonSuperClassOfClassesTestCollectionClassextendsT() {
        Class<? extends A> commonSuperClass;
        Collection<Class<? extends A>> aClasses = null;
        commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(aClasses);
        assertEquals(commonSuperClass, null);

        aClasses = Sets.newHashSet();
        commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(aClasses);
        assertEquals(commonSuperClass, null);

        aClasses = Sets.newHashSet(null, null, null);
        commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(aClasses);
        assertEquals(commonSuperClass, null);

        aClasses = Sets.newHashSet(A.class, A1.class, null); //null as value with valid values is ignored
        commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(aClasses);
        assertEquals(commonSuperClass, A.class);

        aClasses = Sets.newHashSet(A.class, A1.class, A2.class, A11.class, A12.class, A21.class, A111.class, A121.class, A211.class);
        commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(aClasses);
        assertEquals(commonSuperClass, A.class);
        aClasses = Sets.newHashSet(A11.class, A12.class, A111.class, A121.class);
        commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(aClasses);
        assertEquals(commonSuperClass, A1.class);

        aClasses = Sets.newHashSet(A12.class, A12.class); //same
        commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(aClasses);
        assertEquals(commonSuperClass, A12.class);
        aClasses = Sets.newHashSet(A111.class, A211.class);
        commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(aClasses);
        assertEquals(commonSuperClass, A.class);
        aClasses = Sets.newHashSet(A1.class, A211.class);
        commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(aClasses);
        assertEquals(commonSuperClass, A.class);
        aClasses = Sets.newHashSet(A1.class);
        commonSuperClass = ReflectionUtils.getCommonSuperClassOfClasses(aClasses);
        assertEquals(commonSuperClass, A1.class);

    }

    @Test
    public void getMostSpecialClassTestObjectObject() {
        Class<? extends A> mostSpecialClass;
        mostSpecialClass = ReflectionUtils.getMostSpecialClass((A) null, (A) null);
        assertEquals(mostSpecialClass, null);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(a1, a2);
        assertEquals(mostSpecialClass, null);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(a2, a1);
        assertEquals(mostSpecialClass, null);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(a1, a11);
        assertEquals(mostSpecialClass, A11.class);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(a11, a1);
        assertEquals(mostSpecialClass, A11.class);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(a, a111);
        assertEquals(mostSpecialClass, A111.class);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(a111, a);
        assertEquals(mostSpecialClass, A111.class);
    }

    @Test
    public void getMostSpecialClassTestClassextendsTClassextendsT() {
        Class<? extends A> mostSpecialClass;
        mostSpecialClass = ReflectionUtils.getMostSpecialClass((Class<? extends A>) null, (Class<? extends A>) null);
        assertEquals(mostSpecialClass, null);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(A1.class, (Class<? extends A>) null);
        assertEquals(mostSpecialClass, null);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass((Class<? extends A>) null, A2.class);
        assertEquals(mostSpecialClass, null);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(A1.class, A2.class);
        assertEquals(mostSpecialClass, null);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(A2.class, A1.class);
        assertEquals(mostSpecialClass, null);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(A1.class, A11.class);
        assertEquals(mostSpecialClass, A11.class);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(A11.class, A1.class);
        assertEquals(mostSpecialClass, A11.class);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(A.class, A111.class);
        assertEquals(mostSpecialClass, A111.class);
        mostSpecialClass = ReflectionUtils.getMostSpecialClass(A111.class, A.class);
        assertEquals(mostSpecialClass, A111.class);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void removeSubClassesTestNull() {
        ReflectionUtils.removeSubClasses(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void removeSubClassesTestNull2() {
        Collection<Class<? extends A>> aClasses = Sets.newHashSet((Class<? extends A>) null);
        ReflectionUtils.removeSubClasses(aClasses);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void removeSubClassesTestNull3() {
        Collection<Class<? extends A>> aClasses = Sets.newHashSet((Class<? extends A>) null, (Class<? extends A>) null);
        ReflectionUtils.removeSubClasses(aClasses);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void removeSubClassesTestNull4() {
        Collection<Class<? extends A>> aClasses = Sets.newHashSet(A.class, null);
        ReflectionUtils.removeSubClasses(aClasses);
    }

    @Test
    public void removeSubClassesTest() {
        Collection<Class<? extends A>> aClasses = null;

        aClasses = Sets.newHashSet(A.class, A1.class, A2.class, A11.class, A12.class, A21.class, A111.class, A121.class, A211.class);
        ReflectionUtils.removeSubClasses(aClasses);
        assertEquals(aClasses.size(), 1);
        assertTrue(aClasses.contains(A.class));
        ReflectionUtils.removeSubClasses(aClasses); //second run doesn't change the result anymore
        assertEquals(aClasses.size(), 1);
        assertTrue(aClasses.contains(A.class));

        //same test with List
        aClasses = Lists.newArrayList(A.class, A1.class, A2.class, A11.class, A12.class, A21.class, A111.class, A121.class, A211.class);
        ReflectionUtils.removeSubClasses(aClasses);
        assertEquals(aClasses.size(), 1);
        assertTrue(aClasses.contains(A.class));
        ReflectionUtils.removeSubClasses(aClasses); //second run doesn't change the result anymore
        assertEquals(aClasses.size(), 1);
        assertTrue(aClasses.contains(A.class));

        aClasses = Sets.newHashSet(A1.class, A2.class, A11.class, A12.class, A21.class, A111.class, A121.class, A211.class);
        ReflectionUtils.removeSubClasses(aClasses);
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A1.class));
        assertTrue(aClasses.contains(A2.class));

        aClasses = Sets.newHashSet(A1.class, A2.class);
        ReflectionUtils.removeSubClasses(aClasses);
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A1.class));
        assertTrue(aClasses.contains(A2.class));

        aClasses = Sets.newHashSet(A111.class, A211.class);
        ReflectionUtils.removeSubClasses(aClasses);
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A111.class));
        assertTrue(aClasses.contains(A211.class));

        aClasses = Sets.newHashSet(A1.class, A111.class, A211.class);
        ReflectionUtils.removeSubClasses(aClasses);
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A1.class));
        assertTrue(aClasses.contains(A211.class));

        aClasses = Sets.newHashSet(A1.class, A1.class);
        ReflectionUtils.removeSubClasses(aClasses);
        assertEquals(aClasses.size(), 1);
        assertTrue(aClasses.contains(A1.class));

        aClasses = Sets.newHashSet(A1.class, A2.class, A1.class, A2.class, A11.class, A11.class, A21.class, A21.class);
        ReflectionUtils.removeSubClasses(aClasses);
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A1.class));
        assertTrue(aClasses.contains(A2.class));

    }

    @Test(expectedExceptions = NullPointerException.class)
    public void removeSuperClassesTestNull() {
        ReflectionUtils.removeSuperClasses(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void removeSuperClassesTestNull2() {
        Collection<Class<? extends A>> aClasses = Sets.newHashSet((Class<? extends A>) null);
        ReflectionUtils.removeSuperClasses(aClasses);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void removeSuperClassesTestNull3() {
        Collection<Class<? extends A>> aClasses = Sets.newHashSet((Class<? extends A>) null, (Class<? extends A>) null);
        ReflectionUtils.removeSuperClasses(aClasses);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void removeSuperClassesTestNull4() {
        Collection<Class<? extends A>> aClasses = Sets.newHashSet(A.class, null);
        ReflectionUtils.removeSuperClasses(aClasses);
    }

    @Test
    public void removeSuperClassesTest() {
        Collection<Class<? extends A>> aClasses = null;

        aClasses = Sets.newHashSet(A.class, A1.class, A2.class, A11.class, A12.class, A21.class, A111.class, A121.class, A211.class);
        ReflectionUtils.removeSuperClasses(aClasses);
        assertEquals(aClasses.size(), 3);
        assertTrue(aClasses.contains(A111.class));
        assertTrue(aClasses.contains(A121.class));
        assertTrue(aClasses.contains(A211.class));
        ReflectionUtils.removeSuperClasses(aClasses); //second run doesn't change the result anymore
        assertEquals(aClasses.size(), 3);
        assertTrue(aClasses.contains(A111.class));
        assertTrue(aClasses.contains(A121.class));
        assertTrue(aClasses.contains(A211.class));

        //same test with List
        aClasses = Lists.newArrayList(A.class, A1.class, A2.class, A11.class, A12.class, A21.class, A111.class, A121.class, A211.class);
        ReflectionUtils.removeSuperClasses(aClasses);
        assertEquals(aClasses.size(), 3);
        assertTrue(aClasses.contains(A111.class));
        assertTrue(aClasses.contains(A121.class));
        assertTrue(aClasses.contains(A211.class));
        ReflectionUtils.removeSuperClasses(aClasses); //second run doesn't change the result anymore
        assertEquals(aClasses.size(), 3);
        assertTrue(aClasses.contains(A111.class));
        assertTrue(aClasses.contains(A121.class));
        assertTrue(aClasses.contains(A211.class));

        aClasses = Sets.newHashSet(A1.class, A2.class, A11.class, A12.class, A21.class, A111.class, A121.class, A211.class);
        ReflectionUtils.removeSuperClasses(aClasses);
        assertEquals(aClasses.size(), 3);
        assertTrue(aClasses.contains(A111.class));
        assertTrue(aClasses.contains(A121.class));
        assertTrue(aClasses.contains(A211.class));

        aClasses = Sets.newHashSet(A1.class, A2.class);
        ReflectionUtils.removeSuperClasses(aClasses);
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A1.class));
        assertTrue(aClasses.contains(A2.class));

        aClasses = Sets.newHashSet(A111.class, A211.class);
        ReflectionUtils.removeSuperClasses(aClasses);
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A111.class));
        assertTrue(aClasses.contains(A211.class));

        aClasses = Sets.newHashSet(A1.class, A211.class);
        ReflectionUtils.removeSuperClasses(aClasses);
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A1.class));
        assertTrue(aClasses.contains(A211.class));

        aClasses = Sets.newHashSet(A1.class, A111.class);
        ReflectionUtils.removeSuperClasses(aClasses);
        assertEquals(aClasses.size(), 1);
        assertTrue(aClasses.contains(A111.class));

        aClasses = Sets.newHashSet(A1.class, A1.class);
        ReflectionUtils.removeSuperClasses(aClasses);
        assertEquals(aClasses.size(), 1);
        assertTrue(aClasses.contains(A1.class));

        aClasses = Sets.newHashSet(A1.class, A2.class, A1.class, A2.class);
        ReflectionUtils.removeSuperClasses(aClasses);
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A1.class));
        assertTrue(aClasses.contains(A2.class));

    }

    @Test
    public void retainSubClassesTest() {
        List<Class<? extends A>> aClasses;
        aClasses = Lists.newArrayList();
        ReflectionUtils.retainSubClasses(aClasses, null); //second argument is irrelevant in this case
        assertTrue(aClasses.isEmpty());
        aClasses = Lists.newArrayList(A1.class, A2.class, A11.class, A12.class, A21.class);
        List<Class<? extends A>> aClassesCopy = Lists.newArrayList(aClasses);
        ReflectionUtils.retainSubClasses(aClasses, A.class); //class
        assertTrue(aClasses.equals(aClassesCopy));
        ReflectionUtils.retainSubClasses(aClasses, I.class); //interface
        assertTrue(aClasses.equals(aClassesCopy));

        aClasses = Lists.newArrayList(A1.class, A2.class, A11.class, A12.class, A21.class);
        ReflectionUtils.retainSubClasses(aClasses, A2.class); //class
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A2.class));
        assertTrue(aClasses.contains(A21.class));
        aClasses = Lists.newArrayList(A1.class, A2.class, A11.class, A12.class, A21.class);
        ReflectionUtils.retainSubClasses(aClasses, I2.class); //interface
        assertEquals(aClasses.size(), 2);
        assertTrue(aClasses.contains(A2.class));
        assertTrue(aClasses.contains(A21.class));

    }

    @Test
    public void getClassesWithSuperClassesTestCollectionClassextendsTClass() {
        Collection<Class<? extends A>> aClasses;
        Set<Class<?>> classesWithSuperClasses;

        aClasses = Sets.newHashSet(A.class);
        classesWithSuperClasses = ReflectionUtils.getClassesWithSuperClasses(aClasses, null); //no excluded super class
        assertEquals(classesWithSuperClasses.size(), 2);
        assertTrue(classesWithSuperClasses.contains(A.class));
        assertTrue(classesWithSuperClasses.contains(Object.class));

        aClasses = Sets.newHashSet(A.class);
        classesWithSuperClasses = ReflectionUtils.getClassesWithSuperClasses(aClasses, String.class); //invalid excluded super class = no stopping class
        assertEquals(classesWithSuperClasses.size(), 2);
        assertTrue(classesWithSuperClasses.contains(A.class));
        assertTrue(classesWithSuperClasses.contains(Object.class));

        aClasses = Sets.newHashSet(A.class);
        classesWithSuperClasses = ReflectionUtils.getClassesWithSuperClasses(aClasses, A1.class); //sub class as excluded super class = invalid excluded super class = no excluded super class
        assertEquals(classesWithSuperClasses.size(), 2);
        assertTrue(classesWithSuperClasses.contains(A.class));
        assertTrue(classesWithSuperClasses.contains(Object.class));

        aClasses = Sets.newHashSet(A.class);
        classesWithSuperClasses = ReflectionUtils.getClassesWithSuperClasses(aClasses, Object.class); //Object.class as excluded super class
        assertEquals(classesWithSuperClasses.size(), 1);
        assertTrue(classesWithSuperClasses.contains(A.class));

        aClasses = Sets.newHashSet(A.class);
        classesWithSuperClasses = ReflectionUtils.getClassesWithSuperClasses(aClasses, A.class); //same class as excluded super class
        assertTrue(classesWithSuperClasses.isEmpty());

        aClasses = Sets.newHashSet(A111.class);
        classesWithSuperClasses = ReflectionUtils.getClassesWithSuperClasses(aClasses, A.class); //3 hierarchy steps away excluded super class
        assertEquals(classesWithSuperClasses.size(), 3);
        assertTrue(classesWithSuperClasses.contains(A111.class));
        assertTrue(classesWithSuperClasses.contains(A11.class));
        assertTrue(classesWithSuperClasses.contains(A1.class));

        aClasses = Sets.newHashSet(A11.class, A21.class);
        classesWithSuperClasses = ReflectionUtils.getClassesWithSuperClasses(aClasses, Object.class); //2 hierarchy steps away excluded super class
        assertEquals(classesWithSuperClasses.size(), 5);
        assertTrue(classesWithSuperClasses.contains(A11.class));
        assertTrue(classesWithSuperClasses.contains(A21.class));
        assertTrue(classesWithSuperClasses.contains(A1.class));
        assertTrue(classesWithSuperClasses.contains(A2.class));
        assertTrue(classesWithSuperClasses.contains(A.class));
    }

    @Test
    public void getClassWithSuperClassesTest() {
        Set<Class<?>> classWithSuperClasses;

        classWithSuperClasses = ReflectionUtils.getClassWithSuperClasses(A.class, null); //no excluded super class
        assertEquals(classWithSuperClasses.size(), 2);
        assertTrue(classWithSuperClasses.contains(A.class));
        assertTrue(classWithSuperClasses.contains(Object.class));

        classWithSuperClasses = ReflectionUtils.getClassWithSuperClasses(A.class, String.class); //invalid excluded super class = no excluded super class
        assertEquals(classWithSuperClasses.size(), 2);
        assertTrue(classWithSuperClasses.contains(A.class));
        assertTrue(classWithSuperClasses.contains(Object.class));

        classWithSuperClasses = ReflectionUtils.getClassWithSuperClasses(A.class, A1.class); //sub class as excluded super class = invalid excluded super class = no excluded super class
        assertEquals(classWithSuperClasses.size(), 2);
        assertTrue(classWithSuperClasses.contains(A.class));
        assertTrue(classWithSuperClasses.contains(Object.class));

        classWithSuperClasses = ReflectionUtils.getClassWithSuperClasses(A.class, Object.class); //Object.class as excluded super class
        assertEquals(classWithSuperClasses.size(), 1);
        assertTrue(classWithSuperClasses.contains(A.class));

        classWithSuperClasses = ReflectionUtils.getClassWithSuperClasses(A.class, A.class); // same class as excluded super class
        assertTrue(classWithSuperClasses.isEmpty());

        classWithSuperClasses = ReflectionUtils.getClassWithSuperClasses(A111.class, A.class); //3 hierarchy steps away excluded super class
        assertEquals(classWithSuperClasses.size(), 3);
        assertTrue(classWithSuperClasses.contains(A111.class));
        assertTrue(classWithSuperClasses.contains(A11.class));
        assertTrue(classWithSuperClasses.contains(A1.class));
    }

    @Test
    public void isAssignableTestClassClass() {
        boolean assignable;

        assignable = ReflectionUtils.isAssignable(null, null);
        assertFalse(assignable);
        assignable = ReflectionUtils.isAssignable(null, A.class);
        assertFalse(assignable);
        assignable = ReflectionUtils.isAssignable(A.class, null);
        assertFalse(assignable);
        assignable = ReflectionUtils.isAssignable(A.class, A1.class);
        assertTrue(assignable);
        assignable = ReflectionUtils.isAssignable(A1.class, A.class);
        assertTrue(assignable);
        assignable = ReflectionUtils.isAssignable(A1.class, A2.class);
        assertFalse(assignable);
        assignable = ReflectionUtils.isAssignable(A2.class, A1.class);
        assertFalse(assignable);
    }

    //    @Test
    //    public void getAbsoluteDirectoryTest() {
    //      not implemented because RefelctionUtils.getAbsoluteDirectory() is actually an unused function
    //    }

    @Test
    public void getClassFileTest() {
        Class<? extends ReflectionUtilsTest> myClass = getClass();
        String myClassName = myClass.getName();
        String myClassFileNameEnd = ".target.test-classes." + myClassName;
        myClassFileNameEnd = myClassFileNameEnd.replace('.', File.separatorChar);
        myClassFileNameEnd += ".class";
        File classFile = ReflectionUtils.getClassFile(myClass);
        String fileName = classFile.toString();
        assertTrue(fileName.endsWith(myClassFileNameEnd));
        assertTrue(classFile.exists());
    }

    @Test
    public void getFieldTestClassClassStringClassT() {
        String fieldValue;
        fieldValue = ReflectionUtils.getField(A1.class, null, "VAR", String.class);
        assertEquals(fieldValue, "A1");
        fieldValue = ReflectionUtils.getField(A1.class, null, "VAR", null);
        assertEquals(fieldValue, "A1");
        fieldValue = ReflectionUtils.getField(A1.class, Object.class, "VAR", String.class);
        assertEquals(fieldValue, "A1");
        fieldValue = ReflectionUtils.getField(A1.class, null, "RAV", String.class);
        assertEquals(fieldValue, null);
        Integer fieldValueInt = ReflectionUtils.getField(A1.class, null, "VAR", Integer.class);
        assertEquals(fieldValueInt, null);

        fieldValue = ReflectionUtils.getField(A11.class, Object.class, "VAR", String.class);
        assertEquals(fieldValue, "A1");

        fieldValue = ReflectionUtils.getField(A111.class, null, "VAR", String.class);
        assertEquals(fieldValue, "A111");
        fieldValue = ReflectionUtils.getField(A1.class, null, "RAV", String.class);
        assertEquals(fieldValue, null);

        fieldValue = ReflectionUtils.getField(A211.class, null, "VAR", String.class);
        assertEquals(fieldValue, null);

        fieldValue = ReflectionUtils.getField(A211.class, A2.class, "VAR", String.class);
        assertEquals(fieldValue, null);
    }

    @Test
    public void getFieldTestClassClassString() {
        String fieldValue;
        fieldValue = ReflectionUtils.getField(A1.class, null, "VAR");
        assertEquals(fieldValue, "A1");
        int intValue;
        intValue = ReflectionUtils.getField(A1.class, null, "INT");
        assertEquals(intValue, 1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void getFieldTestClassStringClassTNull() {
        @SuppressWarnings("unused")
        int intValue = ReflectionUtils.getField(A.class, "INT", null); //can not cast null to primitive int
    }

    @Test
    public void getFieldTestClassStringClassT() {
        String fieldValue;
        fieldValue = ReflectionUtils.getField(A1.class, "VAR", String.class);
        assertEquals(fieldValue, "A1");
        fieldValue = ReflectionUtils.getField(A1.class, "VAR", null);
        assertEquals(fieldValue, "A1");
        fieldValue = ReflectionUtils.getField(A1.class, "RAV", String.class);
        assertEquals(fieldValue, null);
        Integer intValue;
        intValue = ReflectionUtils.getField(A1.class, "INT", null);
        assertEquals(intValue, Integer.valueOf(1));
        intValue = ReflectionUtils.getField(A.class, "INT", null);
        assertEquals(intValue, null);
    }

    @Test
    public void getFieldTestClassString() {
        String fieldValue;
        fieldValue = ReflectionUtils.getField(A1.class, "VAR");
        assertEquals(fieldValue, "A1");
        fieldValue = ReflectionUtils.getField(A1.class, "RAV");
        assertEquals(fieldValue, null);
        Integer intValue;
        intValue = ReflectionUtils.getField(A1.class, "INT");
        assertEquals(intValue, Integer.valueOf(1));
        intValue = ReflectionUtils.getField(A.class, "INT");
        assertEquals(intValue, null);
    }

    @Test
    public static void currentClassGetter_getClassNameTest() {
        CurrentClassGetter currentClassGetter = new ReflectionUtils.CurrentClassGetter();
        String className = currentClassGetter.getClassName();
        String thisClassName = ReflectionUtilsTest.class.getName();
        assertEquals(className, thisClassName);
    }

    @Test
    public static void currentClassGetter_getCurrentClassTest() {
        CurrentClassGetter currentClassGetter = new ReflectionUtils.CurrentClassGetter();
        Class<?> currentClass = currentClassGetter.getCurrentClass();
        assertEquals(currentClass, ReflectionUtilsTest.class);
    }

}
