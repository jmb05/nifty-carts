package net.jmb19905.niftycarts.entity.util;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author bytemaniak
 * Originally licensed under MIT <a href="https://git.bytemaniak.net/byteManiak/mecha">https://git.bytemaniak.net/byteManiak/mecha</a>
 */
public interface MultiPartEntity {
    @NotNull
    SubEntity<?>[] getSubEntities();
}
