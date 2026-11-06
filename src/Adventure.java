import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.List;

/** Flashcard scheduling core plus a local Swing adventure UI. */
public class Adventure {
    static class Card {
        final String front,back; int interval; double ease=2.5; LocalDate due=LocalDate.now();
        Card(String front,String back){this.front=front;this.back=back;}
        void review(boolean correct){
            if(correct){interval=interval==0?1:interval==1?3:Math.max(1,(int)Math.round(interval*ease));ease=Math.min(3.0,ease+.05);}
            else{interval=0;ease=Math.max(1.3,ease-.2);}
            due=LocalDate.now().plusDays(interval);
        }
    }
    final List<Card> cards=new ArrayList<>();
    final Properties save=new Properties();final Path savePath;
    int xp;Card current;boolean revealed;
    JFrame frame;JLabel progress;JTextArea question;JButton reveal,yes,no;
    Adventure(Path deck,Path state)throws IOException{
        savePath=state;
        for(String line:Files.readAllLines(deck)){
            if(line.isBlank()||line.startsWith("#"))continue;
            String[] pair=line.split("\t",-1);if(pair.length!=2||pair[0].isBlank()||pair[1].isBlank())throw new IOException("Each card needs front<TAB>back");
            cards.add(new Card(pair[0],pair[1]));
        }
        if(cards.isEmpty())throw new IOException("Deck has no cards");
        if(Files.exists(state))try(Reader in=Files.newBufferedReader(state)){save.load(in);}
        try{
            xp=Integer.parseInt(save.getProperty("xp","0"));
            for(Card c:cards){String id=id(c);c.interval=Integer.parseInt(save.getProperty(id+".interval","0"));c.ease=Double.parseDouble(save.getProperty(id+".ease","2.5"));c.due=LocalDate.parse(save.getProperty(id+".due",LocalDate.now().toString()));}
        }catch(RuntimeException e){throw new IOException("Invalid progress file; restore a backup or choose a new state path",e);}
    }
    static String id(Card c){return Base64.getUrlEncoder().withoutPadding().encodeToString((c.front+"\t"+c.back).getBytes(java.nio.charset.StandardCharsets.UTF_8));}
    void persist()throws IOException{
        save.setProperty("xp",Integer.toString(xp));
        for(Card c:cards){String id=id(c);save.setProperty(id+".interval",Integer.toString(c.interval));save.setProperty(id+".ease",Double.toString(c.ease));save.setProperty(id+".due",c.due.toString());}
        Path path=savePath.toAbsolutePath();Files.createDirectories(path.getParent());Path temp=Files.createTempFile(path.getParent(),"adventure-",".tmp");
        try{try(Writer out=Files.newBufferedWriter(temp)){save.store(out,"Spaced repetition adventure progress");}Files.move(temp,path,StandardCopyOption.REPLACE_EXISTING);}
        finally{Files.deleteIfExists(temp);}
    }
    void next(){
        current=cards.stream().filter(c->!c.due.isAfter(LocalDate.now())).min(Comparator.comparing(c->c.due)).orElse(null);revealed=false;
        String[] places={"Mosswood Trail","Ember Caves","Moonlit Keep","The Sky Library"};
        String gear=xp>=100?"runic staff":xp>=50?"silver compass":xp>=20?"trail boots":"wooden wand";
        progress.setText("XP "+xp+" | "+places[Math.min(3,xp/50)]+" | Gear: "+gear);
        question.setText(current==null?"You cleared today's encounters. Come back tomorrow!": "A guardian asks:\n\n"+current.front);
        reveal.setEnabled(current!=null);yes.setEnabled(false);no.setEnabled(false);
    }
    void grade(boolean correct){if(current==null||!revealed)return;current.review(correct);if(correct)xp+=10;
        try{persist();next();}catch(IOException e){JOptionPane.showMessageDialog(frame,"Save failed: "+e.getMessage());yes.setEnabled(false);no.setEnabled(false);}
    }
    void ui(){
        frame=new JFrame("Recall Quest");frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);frame.setSize(720,480);
        JPanel root=new JPanel(new BorderLayout(16,16));root.setBorder(BorderFactory.createEmptyBorder(24,24,24,24));frame.setContentPane(root);
        progress=new JLabel();root.add(progress,BorderLayout.NORTH);question=new JTextArea();question.setEditable(false);question.setLineWrap(true);question.setWrapStyleWord(true);question.setFont(new Font("SansSerif",Font.PLAIN,24));root.add(new JScrollPane(question),BorderLayout.CENTER);
        JPanel buttons=new JPanel();reveal=new JButton("Reveal answer");yes=new JButton("I remembered");no=new JButton("Try again");buttons.add(reveal);buttons.add(yes);buttons.add(no);root.add(buttons,BorderLayout.SOUTH);
        reveal.addActionListener(e->{if(current!=null){revealed=true;question.append("\n\nAnswer: "+current.back);yes.setEnabled(true);no.setEnabled(true);reveal.setEnabled(false);}});yes.addActionListener(e->grade(true));no.addActionListener(e->grade(false));next();frame.setLocationRelativeTo(null);frame.setVisible(true);
    }
    public static void main(String[] args){
        if(args.length>0&&args[0].equals("--self-test")){Card c=new Card("x","y");c.review(true);if(c.interval!=1)throw new AssertionError();c.review(true);if(c.interval!=3)throw new AssertionError();c.review(false);if(c.interval!=0||!c.due.equals(LocalDate.now()))throw new AssertionError();System.out.println("Scheduler tests passed");return;}
        try{Adventure app=new Adventure(Path.of(args.length>0?args[0]:"deck.tsv"),Path.of(args.length>1?args[1]:"progress.properties"));SwingUtilities.invokeLater(app::ui);}
        catch(IOException e){System.err.println(e.getMessage());System.exit(1);}
    }
}
