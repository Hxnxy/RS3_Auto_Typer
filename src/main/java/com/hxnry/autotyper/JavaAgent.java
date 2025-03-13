package com.hxnry.autotyper;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

public class JavaAgent {

    public static void premain(String args, Instrumentation inst) {


    }

    public static void agentmain(String args, Instrumentation instrumentation) {


        String name = null;
        for (String arg : args.split(",")) {
            if (arg.startsWith("name=")) {
                name = arg.replace("name=", "");
            } else if (arg.startsWith("server=")) {
                if (arg.startsWith("name=")) {
                    name = arg.replace("name=", "");
                }
            }
        }


        //writer.append("Java agent has been loaded! -> " + name);

        try {
            FileWriter fileWriter = new FileWriter("dump5.txt");
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write("Debug information for " + name);
            writer.append(System.lineSeparator());
            writer.append("Loaded class size: ").append(String.valueOf(instrumentation.getAllLoadedClasses().length));
            writer.append(System.lineSeparator());
            for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
                if (clazz != null) {
                    if(clazz.getName().equalsIgnoreCase("java.awt.Canvas")) {

                        Method getBounds = clazz.getMethod("getBounds");
                        Method getLocation = clazz.getMethod("getLocation");

                        Object instance = clazz.getClassLoader().loadClass(clazz.getName());

                        Rectangle bounds = (Rectangle) getBounds.invoke(instance);
                        Point location = (Point) getLocation.invoke(instance);

                        System.out.println("bounds: " + bounds);
                        System.out.println("location: " + location);


                        writer.append("Class: ").append(clazz.getName());
                        writer.append(System.lineSeparator());

                        int classModifiers = clazz.getModifiers();
                        writer.append(String.valueOf(Modifier.isPublic(classModifiers))).append("\n");
                        writer.append(System.lineSeparator());

                        Class<?> classSuper = clazz.getSuperclass();
                        writer.append(classSuper.getName());

                        Method[] classMethods = clazz.getMethods();

                        for(Method method : classMethods){

                            writer.append(System.lineSeparator());
                            writer.append("Method Name: ").append(method.getName());
                            writer.append(System.lineSeparator());
                            if(method.getName().startsWith("get")) {
                                writer.append(System.lineSeparator());
                                writer.append("Getter Method");
                                writer.append(System.lineSeparator());
                            } else if(method.getName().startsWith("set")) {
                                writer.append(System.lineSeparator());
                                writer.append("Setter Method");
                                writer.append(System.lineSeparator());
                            }
                            writer.append("Return Type: ").append(String.valueOf(method.getReturnType()));
                            writer.append(System.lineSeparator());
                            Class<?>[] parameterType = method.getParameterTypes();
                            writer.append("Parameters");
                            writer.append(System.lineSeparator());
                            for(Class<?> parameter : parameterType){
                                writer.append(parameter.getName());
                                writer.append(System.lineSeparator());
                            }
                            writer.append(System.lineSeparator());
                        }

                    }
                }
            }
            writer.append("Class files have been dumped to -> ").append(new File("dump2.txt").getAbsolutePath());
            writer.close();

        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        if (name == null) {
            throw new IllegalArgumentException("The name to use for the rmi server was not provided as an argument to the java agent.");
        }
    }
}
