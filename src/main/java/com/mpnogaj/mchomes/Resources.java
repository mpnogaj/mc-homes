package com.mpnogaj.mchomes;

import net.fabricmc.loader.impl.util.StringUtil;

public class Resources {
    public static String WAYPOINT = "waypoint";
    public static String HOME = "home";

    private static String OBJECT_NOT_FOUND = "%s \"%s\" doesn't exists";
    private static String OBJECT_SUCCESSFULLY_ADDED = "%s - \"%s\", successfully added";
    private static String OBJECT_SUCCESSFULLY_REMOVED = "%s - \"%s\", successfully removed";
    private static String DEFAULT_OBJECT_NOT_SET = "Default %s is not set (maybe it's been removed)";
    private static String NAME_ALREADY_TAKEN = "This name (\"%s\") is already taken";

    public static String getObjectNotFound(String object, String name) {
        return StringUtil.capitalize(OBJECT_NOT_FOUND.formatted(object, name));
    }

    public static String getObjectSuccessfullyAdded(String object, String name) {
        return StringUtil.capitalize(OBJECT_SUCCESSFULLY_ADDED.formatted(object, name));
    }

    public static String getObjectSuccessfullyRemoved(String object, String name) {
        return StringUtil.capitalize(OBJECT_SUCCESSFULLY_REMOVED.formatted(object, name));
    }

    public static String getDefaultObjectNotSet(String object) {
        return StringUtil.capitalize(DEFAULT_OBJECT_NOT_SET.formatted(object));
    }

    public static String getNameAlreadyTaken(String name) {
        return StringUtil.capitalize(NAME_ALREADY_TAKEN.formatted(name));
    }
}
