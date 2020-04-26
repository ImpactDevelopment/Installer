/*
 * This file is part of Impact Installer.
 *
 * Copyright (C) 2019  ImpactDevelopment and contributors
 *
 * See the CONTRIBUTORS.md file for a list of copyright holders
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package io.github.ImpactDevelopment.installer.optifine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This is a compatibility class that allows the installer to "upgrade" an existing install without needing the OptiFine installer jar
 */
@Deprecated
public class OptiFineExisting extends OptiFine {

    public OptiFineExisting(Path libraries, String version) throws RuntimeException {
        // Surprisingly, passing the "mod" jar to super actually works, since it still includes the installer classes and the launchwrapper jar
        super(libraries.resolve("optifine").resolve("OptiFine").resolve(version).resolve(String.format("OptiFine-%s.jar", version)));
    }


    @Override
    protected void installOptiFine(Path destination, Path vanilla) throws IOException, InvocationTargetException, IllegalAccessException {
        // No-op: this class was built from the installed OptiFine, "installing" it over itself is paradoxical
    }

    @Override
    protected void installLaunchwrapper(Path destination) throws IOException {
        // It can happen that this OptiFine wants its own launchwrapper but Impact was previously installed without it, so install it if it is missing
        if (!Files.exists(destination)) {
            super.installLaunchwrapper(destination);
        }
    }
}
