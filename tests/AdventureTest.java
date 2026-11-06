import java.nio.file.*;
import java.time.*;
public class AdventureTest {
    public static void main(String[] args)throws Exception {
        Path dir=Files.createTempDirectory("recall-quest-test-");
        Path deck=dir.resolve("deck.tsv"),save=dir.resolve("state.properties");
        try {
            Files.writeString(deck,"Question\tAnswer\n");
            Adventure a=new Adventure(deck,save);a.cards.get(0).review(true);a.xp=20;a.persist();
            Adventure b=new Adventure(deck,save);
            if(b.xp!=20||b.cards.get(0).interval!=1||!b.cards.get(0).due.equals(LocalDate.now().plusDays(1)))throw new AssertionError("progress round trip failed");
            Files.writeString(deck,"malformed line\n");
            try{new Adventure(deck,save);throw new AssertionError("invalid deck accepted");}catch(java.io.IOException expected){}
            System.out.println("Persistence and deck validation tests passed");
        } finally {Files.deleteIfExists(deck);Files.deleteIfExists(save);Files.deleteIfExists(dir);}
    }
}
