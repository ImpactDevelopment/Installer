package io.github.ImpactDevelopment.installer.versions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.ImpactDevelopment.installer.libraries.ILibrary;
import io.github.ImpactDevelopment.installer.libraries.MavenResolver;

/**
 * A library entry in the final MC launcher version json
 */
public class Library {

    public static void populate(ILibrary lib, JsonArray libraries) {
        // too much nesting for
        JsonObject library = new JsonObject();
        library.addProperty("name", lib.getName());
        libraries.add(library);
        downloads: {
            JsonObject downloads = new JsonObject();
            library.add("downloads", downloads);
            artifact: {
                JsonObject artifact = new JsonObject();
                downloads.add("artifact", artifact);
                artifact.addProperty("path", MavenResolver.partsToPath(lib.getName().split(":")));
                artifact.addProperty("sha1", lib.getSHA1());
                artifact.addProperty("size", lib.getSize());
                artifact.addProperty("url", lib.getURL());
            }
        }
    }
}
