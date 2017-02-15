package org.simpleproject.beans;

@StringOne
public class StringOneGenarator implements StringGenerator {
    @Override
    public String generateString() {
        return "one";
    }
}