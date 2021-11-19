package com.ogefest.filehunter.search;
/*
Because there is the problem with lucene + native-build using graalvm
when you have working code and after build binary using native-build and got exception similar to

Caused by: java.lang.IllegalArgumentException: Cannot find implementing class for: org.apache.lucene.search.BoostAttribute
	at org.apache.lucene.util.AttributeFactory$DefaultAttributeFactory.findImplClass(AttributeFactory.java:87)
	at org.apache.lucene.util.AttributeFactory$DefaultAttributeFactory.access$000(AttributeFactory.java:62)
	at org.apache.lucene.util.AttributeFactory$DefaultAttributeFactory$1.computeValue(AttributeFactory.java:66)
	at org.apache.lucene.util.AttributeFactory$DefaultAttributeFactory$1.computeValue(AttributeFactory.java:63)

then this class is solution for you. Class copied from: https://www.morling.dev/blog/how-i-built-a-serverless-search-for-my-blog/

Don't forget to add (you need this for @TargetClass and @Substitute annotations)

    <dependency>
      <groupId>org.graalvm.nativeimage</groupId>
      <artifactId>svm</artifactId>
      <version>21.2.0</version>
      <scope>provided</scope>
    </dependency>

into your pom.xml file

 */


import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.search.BoostAttribute;
import org.apache.lucene.search.BoostAttributeImpl;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;

@TargetClass(className = "org.apache.lucene.util.AttributeFactory$DefaultAttributeFactory")
public final class DefaultAttributeFactorySubstitution {

    public DefaultAttributeFactorySubstitution() {}

    @Substitute
    public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass) {
        if (attClass == BoostAttribute.class) {
            return new BoostAttributeImpl();
        }
        else if (attClass == CharTermAttribute.class) {
            return new CharTermAttributeImpl();
        }
//        else if (...) {
//      ...
//        }

        throw new UnsupportedOperationException("Unknown attribute class: " + attClass);
    }
}


