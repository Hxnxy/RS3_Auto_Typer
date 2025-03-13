package com.hxnry.autotyper.io.profiling.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hxnry.autotyper.io.profiling.profiles.SettingsProfile;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsProfileManager {

    private static int currentSetting = 1;
    private static String BASE =  "";
    private final static String AUTHOR =  "Hxnry";
    private final static String FOLDER_NAME =  "Auto Typer";
    private final static String BASE_FOLDER = String.format("\\%s\\%s\\",AUTHOR, FOLDER_NAME);

    private static String getSavePath(){
        return  BASE + BASE_FOLDER + "\\ui\\";
    }

    public static void createProfile(SettingsProfile profile) {
        if(SettingsProfileManager.nameStartsWith("Settings_")) return;
        profile.setName(profile.getName());
        saveJson(profile);
        System.out.println("Created profile: " + profile.name + " @ " +  getSavePath() + profile.name + ".json");
    }

    public static void saveJson(SettingsProfile profile ) {
        profile.setName("Settings_" + getCurrentSetting());
        try (Writer writer = new FileWriter(getSavePath()  + profile.name + ".json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(profile, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SettingsProfile loadProfile(Type type, String fileName){
        try (Reader reader = new FileReader(getSavePath() + fileName)) {
            Gson gson = new GsonBuilder().setLenient().create();
            return gson.fromJson(reader,type);
        }catch (IOException e) {
            System.out.println(fileName + " profile not found. @ "+ getSavePath()  + fileName + ".json");
            return null;
        }
    }

    public static SettingsProfile loadProfile(String fileName){
        try (Reader reader = new FileReader(getSavePath()  + fileName)) {
            System.out.println("Attempting to load from -> " + getSavePath()  + fileName);
            Gson gson = new GsonBuilder().setLenient().create();
            return gson.fromJson(reader,PROFILE_TYPE);
        }catch (IOException e) {
            System.out.println(fileName + " profile not found. @ "+ getSavePath()  + fileName + ".json");
            return null;
        }
    }

    private static boolean isJson(File file, String name){
        FilenameFilter filter = (dir, name1) -> name1.toLowerCase().endsWith(".json");
        return filter.accept(file,name);
    }

    public static boolean nameExists(String profileName){
        String savePath = getSavePath();
        try {

            List<Path> paths = Files.walk(Paths.get(savePath))
                    .filter(f -> isJson(f.getFileName().toFile(),f.getFileName().toString()))
                    .collect(Collectors.toList());
            for(Path path : paths){
                String name = FilenameUtils.getBaseName(path.getFileName() + "");
                if(name.equalsIgnoreCase((profileName))){
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean nameStartsWith(String startsWith){
        String savePath = getSavePath();
        try {

            List<Path> paths = Files.walk(Paths.get(savePath))
                    .filter(f -> isJson(f.getFileName().toFile(),f.getFileName().toString()))
                    .collect(Collectors.toList());
            for(Path path : paths){
                String name = FilenameUtils.getBaseName(path.getFileName() + "");
                if(name.startsWith((startsWith))){
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static final Type PROFILE_TYPE = new TypeToken<SettingsProfile>() {
    }.getType();

    public static boolean createBaseIfNotExists(){
        String savePath = getSavePath();
        Path path = Paths.get(savePath);
        if(!Files.exists(path)){
            try {
                Files.createDirectories(path);
                System.out.println("Data stored @ -> " + path);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static SettingsProfile getProfileByName(String name) {
        String savePath = getSavePath();
        List<Path> paths = null;
        try {
            paths = Files.walk(Paths.get(savePath))
                    .filter(f -> isJson(f.getFileName().toFile(),f.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path found = paths.stream().filter(p -> p.getFileName().toString().equalsIgnoreCase(name + ".json")).findFirst().orElse(null);
        if(found != null) {
            return loadProfile(PROFILE_TYPE, found.getFileName().toString());
        }
        return null;
    }

    public static int count() {
        String savePath = getSavePath();
        List<Path> paths = null;
        try {
            paths = Files.walk(Paths.get(savePath))
                    .filter(f -> isJson(f.getFileName().toFile(),f.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long found = paths.stream().filter(p -> p.getFileName().toString().startsWith("Settings_")).count();
        return (int) found;
    }

    public static SettingsProfile getProfileByName() {
        String savePath = getSavePath();
        List<Path> paths = null;
        try {
            paths = Files.walk(Paths.get(savePath))
                    .filter(f -> isJson(f.getFileName().toFile(),f.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path found = paths.stream().filter(p -> p.getFileName().toString().equalsIgnoreCase("Settings_" + currentSetting + ".json")).findFirst().orElse(null);
        if(found != null) {
            return loadProfile(PROFILE_TYPE, found.getFileName().toString());
        }
        return null;
    }

    public static void setBase(String settings_dir) {
        BASE =  settings_dir + "\\";
    }

    public static int getCurrentSetting() {
        return currentSetting;
    }

    public static void setCurrentSetting(int index) {
        currentSetting = index;
    };

}



