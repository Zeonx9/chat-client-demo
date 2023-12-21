package com.ade.chatclient.application;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;

public class SettingsManager {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String appDirectoryName = System.getenv("USERPROFILE") + "\\AppData\\Local\\InTouch";

    private static void checkAppDirectory() {
        try {
            Files.createDirectories(Path.of(appDirectoryName));
        } catch (IOException e) {
            throw new RuntimeException("Cannot create directory: " + appDirectoryName, e);
        }
    }

    private static File checkSettingsFile() {
        checkAppDirectory();
        File file = Paths.get(appDirectoryName + "\\settings.json").toFile();
        try {
            if (file.createNewFile()) {
                mapper.writeValue(file, new Settings());
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create settings file: " + file.getAbsolutePath(), e);
        }
        return file;
    }

    public static Settings getSettings() {
        File file = checkSettingsFile();
        try {
            return mapper.readValue(file, Settings.class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot read settings from json file", e);
        }
    }

    public static void saveSettings(Settings newSettings) {
        File file = checkSettingsFile();
        try {
            mapper.writeValue(file, newSettings);
        } catch (IOException e) {
            throw new RuntimeException("Cannot save settings to json file", e);
        }
    }

    public static <T> void changeSettings(BiConsumer<Settings, T> settingSetter, T newValue) {
        var settings = getSettings();
        settingSetter.accept(settings, newValue);
        saveSettings(settings);
    }
}
