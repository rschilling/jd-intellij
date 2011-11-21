package jd.ide.eclipse.editors;


import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.SystemInfo;
import jd.ide.idea.config.JDPluginComponent;

import java.io.File;

/**
 * Java Decompiler native libs are coupled to their equivalent so they need
 * this class in an "eclipse" package to work properly.
 */
public class JDSourceMapper {

    public JDSourceMapper() {
        File pluginPath = PluginManager.getPlugin(PluginId.getId(JDPluginComponent.JD_INTELLIJ_ID)).getPath();
        String libPath = new StringBuilder()
                .append(pluginPath).append("/lib/")
                .append("nativelib/")
                .append(osIdentifier()).append('/')
                .append(architecture()).append('/')
                .append(libFileName())
                .toString();

        try {
            System.load(libPath);
        } catch (Exception e) {
            throw new IllegalStateException("Something is wrong when loading the Java Decompiler native lib, " +
                    "\nlookup path : " + libPath +
                    "\nplugin path : " + pluginPath, e);
        }
    }

    private String libFileName() {
        if (SystemInfo.isMac) {
            return "libjd.jnilib";
        } else if (SystemInfo.isWindows) {
            return "jd.dll";
        } else if(SystemInfo.isLinux) {
            return "libjd.so";
        }
        throw new IllegalStateException("OS not supported");
    }

    private String architecture() {
        if (SystemInfo.is32Bit) {
            return "x86";
        } else if (SystemInfo.is64Bit) {
            return "x86_64";
        }
        throw new IllegalStateException("Unsupported architecture, only x86 and x86_64 architectures are supported.");
    }

    private String osIdentifier() {
        if (SystemInfo.isMac) {
            return "macosx";
        } else if (SystemInfo.isWindows) {
            return "win32";
        } else if(SystemInfo.isLinux) {
            return "linux";
        }
        throw new IllegalStateException("Unsupported OS, only windows, linux and mac OSes are supported.");
    }

    public static final boolean loaded = true;
    public final static String JAVA_CLASS_SUFFIX         = ".class";
    public final static String JAVA_SOURCE_SUFFIX        = ".java";
    public final static int    JAVA_SOURCE_SUFFIX_LENGTH = 5;
    public final static String JAR_SUFFIX                = ".jar";
    public final static String ZIP_SUFFIX                = ".zip";

    /**
     * Actual call to the native lib.
     *
     * @param baseName Path to the rooth of the classpath, either a path to a directory or a path to a jar file.
     * @param qualifiedName Qualified path name of the class.
     * @return Decompiled class text.
     */
    public native String decompile(String baseName, String qualifiedName);
}
