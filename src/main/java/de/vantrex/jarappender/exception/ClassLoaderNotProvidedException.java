package de.vantrex.jarappender.exception;

public class ClassLoaderNotProvidedException extends ClassNotFoundException {

    public ClassLoaderNotProvidedException(String s) {
        super(s);
    }
}
