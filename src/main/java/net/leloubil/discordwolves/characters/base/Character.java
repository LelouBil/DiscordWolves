package net.leloubil.discordwolves.characters.base;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.Player;

import java.io.File;
import java.io.InputStream;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public abstract class Character {
    String displayName;

    @Setter
    @NonFinal Player player;

    Game game;

    public abstract boolean hasWon();

    public InputStream getImage(){
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream("characters/" + this.getClass().getSimpleName() + ".jpg");
    }
}
