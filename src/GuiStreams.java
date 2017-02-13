package crawlbabyGUI;

/*
  This is a lightweight swing framework made by an old camp councelor of mine, Andrew. 
  I did not make this work, but I use it to display the information
*/


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
 
import javax.swing.*;
import javax.swing.text.DefaultCaret;
 
 
/**
 * Class containing static methods used in input and output procedures
 * @author William Andrew Cahill
 */
public class GuiStreams
{
    // GUI variables
    private static JFrame frame;
    private static JTextArea inArea, outArea;
     
    // Base streams used in "real" processing
    public static final InputStream in;
    public static final PrintStream out;
     
    // StringBuilder used in bufferIning bytes written from output stream
    private static String bufferIn;         // bufferIn of characters read in
    private static StringBuilder bufferOut; // Buffer for characters being written out
    private static int ptr;                 // Current position in the bufferIn
    private static Object lock;             // Lock used
     
     
    /**
     * Initializes static-variables (aka all of them...)
     */
    static
    {
        // Instantiates wrappers for streams
        lock = new Object();
        in = new GUIInputStream();
        out = new PrintStream(new GUIOutputStream());
         
        // Empty bufferIn
        bufferIn = "";
        bufferOut = new StringBuilder();
         
        // Creates main container
        JPanel container = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        container.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = c.gridy = 0;
         
        // For rows of container
        JPanel[] rows = new JPanel[4];
        for(int i=0; i<rows.length; i++)
        {
            rows[i] = new JPanel();
            rows[i].setLayout(new BorderLayout());
            c.gridy ++;
            c.fill = GridBagConstraints.BOTH;
            if(i % 2 != 0)
                c.weightx = c.weighty = 1;
            else
                c.weightx = c.weighty = 0;
            container.add(rows[i], c);
        }
         
        // Builds JFrame
        frame = new JFrame("Web Searcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        // Sets frame's container
        frame.setContentPane(container);
        container.setPreferredSize(new Dimension(400, 700));
        frame.pack();
                 
        // Builds text-areas
        Font monoFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        inArea = new JTextArea();
        outArea = new JTextArea();
        DefaultCaret caret = (DefaultCaret)inArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        caret = (DefaultCaret)outArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        inArea.setFont(monoFont);
        outArea.setFont(monoFont);
        outArea.setEditable(false);
         
        // Creates listener
        KeyListener l = new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                // If enter key was pressed, append characters to bufferIn, and awaken input stream
                if(e.getKeyCode() == 10)
                {
                    // Places content of TextArea into the bufferIn, and notifies waiting threads
                    String text = inArea.getText();
                    if(text.length() > 0)
                    {
                        // Sets bufferIn to last line
                        int index = text.lastIndexOf('\n');
                        if(index == -1) index = 0;
                        else index ++;
                        bufferIn = text.substring(index) + "\n";
                         
                        // Alerts waiting thread
                        synchronized(lock)
                        {
                            lock.notifyAll();
                        }
                    }
                }
            }
        };      
         
        // Allows outArea to respond to enter key
        inArea.setFocusable(true);
        inArea.requestFocusInWindow();
        inArea.addKeyListener(l);
         
        // Scroll panes for areas
        Dimension scrollSize = new Dimension(500, 300);
        JScrollPane inScrollPane = new JScrollPane(inArea);
        JScrollPane outScrollPane = new JScrollPane(outArea);
         
        // Adds panes to main container
        rows[0].add(new JLabel("Input"));
        rows[1].add(inScrollPane, BorderLayout.CENTER);
        rows[2].add(new JLabel("Output"));
        rows[3].add(outScrollPane, BorderLayout.CENTER);
         
        // Centers GUI
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(sSize.width/2 - frame.getWidth()/2, sSize.height/2 - frame.getHeight()/2);
     
        // Displays frame
        frame.setVisible(true);
    }
     
     
    /**
     * Can't instantiate me!
     */
    private GuiStreams()
    {
    }
     
     
     
    /**
     * Shows the GUIPrinter for what it is
     */
    public static void show()
    {
        frame.setVisible(true);
    }
     
     
    /**
     * Hides the GUIPrinter
     */
    public static void hide()
    {
        frame.setVisible(false);
    }
     
     
    /**
     * @return True if the GUIPrinter is showing its current graphics
     */
    public static boolean isShowing()
    {
        return frame.isVisible();
    }
     
     
    /**
     * OutputStream for the GUI
     * @author William Andrew Cahill
     */
    private static class GUIOutputStream extends OutputStream
    {
        @Override
        public void write(int b)
        {
            // Appends character to buffer
            bufferOut.append((char)b);
             
            // If reached end of stream, flushes it
            if(b == 10)
                flush();
        }
         
        @Override
        public void flush()
        {
            // Appends out JTextArea
            outArea.append(bufferOut.toString());
            bufferOut.setLength(0);
        }
    }
     
     
    /**
     * InputStream fot the GUI
     * @author William Andrew Cahill
     */
    private static class GUIInputStream extends InputStream
    {
        @Override
        public int read()
        {
            // If at the end of non-empty string, empty it, and return -1
            if(bufferIn.length() != 0 && ptr == bufferIn.length())
            {
                bufferIn = "";
                ptr = 0;
                return -1;
            }
             
            // If stream is empty, block
            while(ptr == bufferIn.length())
            {
                try
                {
                    synchronized(lock)
                    {
                        ptr = 0;
                        bufferIn = "";
                        lock.wait();
                    }
                }
                catch(InterruptedException ie)
                {
                    ie.printStackTrace();
                }
            }
     
            // Reads data
            return bufferIn.charAt(ptr ++);
        }
    }
}
 
