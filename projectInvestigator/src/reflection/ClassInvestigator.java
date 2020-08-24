package reflection;

import reflection.api.Investigator;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public class ClassInvestigator implements Investigator {
    private Object obj = new Object();
    private Class clazz = obj.getClass();

    public ClassInvestigator() {
    }

    @Override
    public void load(Object anInstanceOfSomething) {
        this.clazz = anInstanceOfSomething.getClass();
        this.obj = anInstanceOfSomething;
    }

    @Override
    public int getTotalNumberOfMethods() {
        return clazz.getDeclaredMethods().length;
    }

    @Override
    public int getTotalNumberOfConstructors() {
        return clazz.getDeclaredConstructors().length;
    }

    @Override
    public int getTotalNumberOfFields() {
        return clazz.getDeclaredFields().length;
    }

    @Override
    public Set<String> getAllImplementedInterfaces() {
        Class[] interfaces = clazz.getInterfaces();
        Set<String> interfacesReturn = new HashSet<String>();

        for (Class interf : interfaces) {
            interfacesReturn.add(interf.getSimpleName());
        }

        return interfacesReturn;
    }

    @Override
    public int getCountOfConstantFields() {
        Field[] fields = clazz.getDeclaredFields();
        int numberOfConstant = 0;

        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers())) {
                numberOfConstant++;
            }
        }

        return numberOfConstant;
    }

    @Override
    public int getCountOfStaticMethods() {
        Method[] methods = clazz.getDeclaredMethods();
        int numberOfStatic = 0;

        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                numberOfStatic++;
            }
        }

        return numberOfStatic;
    }

    @Override
    public boolean isExtending() {
        return !clazz.getSuperclass().getSimpleName().equals("Object");
    }

    @Override
    public String getParentClassSimpleName() {
        String parentClassName = null;
        if (this.isExtending())
            parentClassName = clazz.getSuperclass().getSimpleName();
        return parentClassName;
    }

    @Override
    public boolean isParentClassAbstract() {
        Class parentClass = clazz.getSuperclass();
        return Modifier.isAbstract(parentClass.getModifiers());
    }

    @Override
    public Set<String> getNamesOfAllFieldsIncludingInheritanceChain() {
        Set<String> fields = new HashSet<String>();
        boolean haveParent = true;
        Class current = this.clazz;

        while (haveParent) {
            for (Field field : current.getDeclaredFields()) {
                fields.add(field.getName());
            }
            if (!current.getSuperclass().getSimpleName().equals("Object"))
                current = current.getSuperclass();
            else
                haveParent = false;
        }

        return fields;
    }

    @Override
    public int invokeMethodThatReturnsInt(String methodName, Object... args) {
        Class[] params = argsToClass(args);
        Method method = null;

        try {
            method = clazz.getMethod(methodName, params);
            return (int) method.invoke(obj, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("Exception : " + e.getMessage());
        }

        return 0;
    }

    private Class[] argsToClass(Object... args) {
        Class[] params = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            params[i] = args[i].getClass();
        }

        return params;
    }

    @Override
    public Object createInstance(int numberOfArgs, Object... args) {
        for (Constructor ctor : clazz.getConstructors()) {
            if (ctor.getParameterCount() == numberOfArgs) {
                try {
                    return ctor.newInstance(args);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    System.out.println("Exception : " + e.getMessage());
                }
            }
        }

        return null;
    }

    @Override
    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args) {
        try {
            Method method = clazz.getDeclaredMethod(name, parametersTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("Exception : " + e.getMessage());
        }

        return null;
    }

    @Override
    public String getInheritanceChain(String delimiter) {
        String inheritanceChain = this.clazz.getSimpleName();
        Class current = this.clazz.getSuperclass();
        boolean haveParent = true;

        do {
            if (current != null) {
                inheritanceChain = current.getSimpleName() + delimiter + inheritanceChain;
                current = current.getSuperclass();
            }
            else
                haveParent = false;
        } while (haveParent);

        return inheritanceChain;
    }
}
