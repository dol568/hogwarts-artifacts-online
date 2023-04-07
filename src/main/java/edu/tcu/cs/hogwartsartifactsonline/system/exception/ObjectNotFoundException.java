package edu.tcu.cs.hogwartsartifactsonline.system.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String name, String Id) {
        super("Could not find " + name +" with Id " + Id);
    }

    public ObjectNotFoundException(String name, Integer Id) {
        super("Could not find " + name +" with Id " + Id);
    }
}