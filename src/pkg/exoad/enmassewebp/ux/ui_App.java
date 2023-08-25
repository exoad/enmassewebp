// Software created by Jack Meng (AKA exoad). Licensed by the included "LICENSE" file. If this file is not found, the project is fully copyrighted.

package pkg.exoad.enmassewebp.ux;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.ImageIcon;

import com.jackmeng.stl.stl_Callback;
import com.jackmeng.stl.stl_Chrono;
import com.jackmeng.stl.stl_Colors;
import com.jackmeng.stl.stl_Struct;

import pkg.exoad.enmassewebp.EnMasseWebp;
import pkg.exoad.enmassewebp._1const;

public final class ui_App
    extends
    JFrame
    implements
    Runnable
{

  public static class use_TextOutStream
      extends OutputStream
  {
    @Override public void write(byte[] buffer, int offset, int length)
    {
      String content = new String(buffer, offset, length);
      System.err.print(content.replaceAll("<[^>]*>", ""));
      print(content);
    }

    @Override public void write(int b) throws IOException
    {
      write(new byte[] { (byte) b }, 0, 1);
    }
  }

  public static void print(String str)
  {
    try
    {
      ((HTMLEditorKit) process_output.getEditorKit()).insertHTML((HTMLDocument) process_output.getDocument(),
          ((HTMLDocument) process_output.getDocument()).getLength(),
          "<strong style=\"color:#8ed15a\">" + stl_Chrono.format_millis("HH:mm:ss") + "</strong> | " + str, 0,
          0, null);
      process_output.setCaretPosition(process_output.getDocument().getLength());
    } catch (BadLocationException | IOException e)
    {
      e.printStackTrace();
    }
  }

  static Random rnd = new Random();
  static String generate_str(int len)
  {
    String E = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";
    StringBuilder sb = new StringBuilder();
    while (sb.length() < len)
    {
      int index = (int) (rnd.nextFloat() * E.length());
      sb.append(E.charAt(index));
    }
    return sb.toString();
  }

  private static JEditorPane process_output;
  static
  {
    process_output = new JEditorPane();
    process_output.setContentType("text/html");
    process_output.setEditable(false);
    process_output.setAutoscrolls(true);

    JPopupMenu popupMenu = new JPopupMenu();

    JMenuItem clearProcessOut = new JMenuItem("Clear text");
    clearProcessOut.addActionListener(ev -> process_output.setText(""));
    popupMenu.add(clearProcessOut);

    process_output.setComponentPopupMenu(popupMenu);
  }
  private JFileChooser jfc;

  private ExecutorService worker = Executors.newSingleThreadExecutor();

  private ArrayList< File > files = new ArrayList<>();

  public ui_App()
  {
    setIconImage(_1const.assets.image("assets/icon.png"));
    setTitle("En Masse WebP");
    setPreferredSize(new Dimension(_2const.WIDTH, _2const.HEIGHT));
    JPanel contentPane_new = new JPanel();
    contentPane_new.setLayout(new BoxLayout(contentPane_new, BoxLayout.Y_AXIS));
    contentPane_new.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
    setContentPane(contentPane_new);

    JLabel app_title = new JLabel(
        """
            <html>
              <p style="text-align:center">
                    <strong style="font-size: 24px;">
                      EnMasse
                      <strong style="color: #8ed15a;">
                        WebP
                      </strong>
                    </strong>
                    <br />
                    <em style="font-size: 9.5px; color: #828282;">
                      version %d | made by exoad
                    </em>
              </p>
            </html>
              """.formatted(EnMasseWebp.__VERSION__));
    app_title.setMaximumSize(app_title.getPreferredSize());
    app_title.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPanel controls = new JPanel();
    controls.setEnabled(false);
    controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

    JCheckBox delete_orig = stx_Helper.make("Delete original", "Deletes the original .webp file.", false);
    JCheckBox random_name = stx_Helper.make("Random name",
        "Renames the convertted file to have a random name. If this is false, the original file name is preserved.",
        false);
    JCheckBox prefer_png = stx_Helper.make("Prefer PNG",
        "Uses PNG instead of JPG. This option is best when dealing with transparency.", true);

    JPanel controls_naming = new JPanel();
    controls_naming.setLayout(new ux_WrapLayout(FlowLayout.CENTER, 8, 0));
    controls_naming.add(delete_orig);
    controls_naming.add(random_name);
    controls_naming.add(prefer_png);

    for (Component r : controls_naming.getComponents())
    {
      if (r instanceof JComponent jc)
        jc.setEnabled(false);
    }

    controls.add(controls_naming);
    controls.setMaximumSize(controls.getPreferredSize());

    JCheckBox jcb_deepscan = new JCheckBox("Deep scan", false);
    jcb_deepscan.setToolTipText("Look through all sub folders under a folder");
    jcb_deepscan.setAlignmentX(Component.CENTER_ALIGNMENT);
    /*-------------------------------------------------------------------------------------------------------- /
    / jcb.addActionListener(ev -> preload_Properties.put(x, stl_Struct.make_pair(y.first, jcb.isSelected()))); /
    /---------------------------------------------------------------------------------------------------------*/

    JButton start_action = new JButton("<html><strong>Start conversion</strong></html>");
    JButton kill_action = new JButton("<html><strong>Kill conversion</strong></html>");
    kill_action.setIconTextGap(5);
    kill_action.setIcon(new ImageIcon(stx_Helper.repack(_1const.assets.image("assets/warning.png"), 20, 20)));
    kill_action.setEnabled(false);
    kill_action.setToolTipText("Kill the conversion process");
    kill_action.setAlignmentX(Component.LEFT_ALIGNMENT);
    kill_action.addActionListener(ev -> {
      worker.shutdownNow();
      worker = Executors.newWorkStealingPool();
      start_action.setEnabled(false);
      kill_action.setEnabled(false);
    });

    start_action.setEnabled(false);
    start_action.setIcon(new ImageIcon(stx_Helper.repack(_1const.assets.image("assets/start.png"), 20, 20)));
    start_action.setToolTipText("Starts the conversion for the selected medium");
    start_action.setAlignmentX(Component.LEFT_ALIGNMENT);
    start_action.addActionListener(ev -> {
      kill_action.setEnabled(true);
      System.out.println("<strong>STARTING Conversion Process. Use the \"Kill Conversion\" button to stop it</strong>");
      worker.submit(() -> {
        AtomicLong converted = new AtomicLong(0);
        AtomicLong failed = new AtomicLong(0);
        for (Component r : controls_naming.getComponents())
          if (r instanceof JComponent jc)
            jc.setEnabled(false);
        stl_Callback< String, File > random = random_name.isSelected() ? e -> new File(
            e.getParentFile().getAbsolutePath() + "/" + generate_str(10) + "." + (prefer_png.isSelected() ? "png"
                : "jpg")).getAbsolutePath()
            : e -> new File(
                e.getParentFile().getAbsolutePath() + "/"
                    + e.getName().replaceAll("\\.webp$", prefer_png.isSelected() ? ".png"
                        : ".jpg")).getAbsolutePath();
        stl_Callback< ?, File > delete_original = delete_orig.isSelected() ? File::delete : e -> null;
        stl_Callback< Void, stl_Struct.struct_Pair< BufferedImage, File > > preferPng = prefer_png.isSelected()
            ? eee -> {
              try
              {
                ImageIO.write(eee.first, "png", eee.second);
              } catch (IOException e1)
              {
                e1.printStackTrace();
                failed.set(failed.get() + 1);
              }
              return (Void) null;
            }
            : eee -> {
              try
              {
                ImageIO.write(eee.first, "jpg", eee.second);
              } catch (IOException e1)
              {
                e1.printStackTrace();
                failed.set(failed.get() + 1);
              }
              return (Void) null;
            };
        files.forEach(x -> {
          File newFile = new File(random.call(x));
          System.out.println("Reading: " + x.getAbsolutePath() + " --> " + newFile.getAbsolutePath());
          try
          {
            BufferedImage webpImage = ImageIO.read(x);
            preferPng.call(stl_Struct.make_pair(webpImage, newFile));
            delete_original.call(x);
          } catch (Exception e1)
          {
            e1.printStackTrace();
            failed.set(failed.get() + 1);
          }
          converted.set(converted.get() + 1);
          System.out.println("Converted: " + x.getAbsolutePath() + " --> " + newFile.getAbsolutePath());
        });
        System.out.println("[DONE] CONVERSION COMPLETED");
        System.out.println("Converted: " + converted.get());
        System.out.println("Failed: " + failed.get());
        start_action.setEnabled(false);
        kill_action.setEnabled(false);
        files.clear();
      });
    });

    JPanel action_controls = new JPanel();
    action_controls.setLayout(new ux_WrapLayout(FlowLayout.CENTER, 4, 0));
    action_controls.add(start_action);
    action_controls.add(kill_action);
    action_controls.setMaximumSize(action_controls.getPreferredSize());
    action_controls.setAlignmentX(Component.CENTER_ALIGNMENT);

    /*-------------------------------------------------------------------------------------------------------------------- /
    / JButton runbg_btn = new JButton("<html><p style=\"text-align:center\"><strong>Scan background</strong></p></html>"); /
    / runbg_btn.setToolTipText(                                                                                            /
    /     "Runs a continuous check on the user's home directory for any webp pictures and converts them.");                /
    / runbg_btn                                                                                                            /
    /     .setMaximumSize(new Dimension(runbg_btn.getPreferredSize().width + 40, runbg_btn.getPreferredSize().height));    /
    / runbg_btn.setAlignmentX(Component.CENTER_ALIGNMENT);                                                                 /
    / runbg_btn.addActionListener(ev -> {                                                                                  /
    /   if (jfc == null)                                                                                                   /
    /     jfc = new JFileChooser(System.getProperty("user.home"));                                                         /
    /   jfc.setAcceptAllFileFilterUsed(false);                                                                             /
    /   jfc.setMultiSelectionEnabled(true);                                                                                /
    /   jfc.setFileView(new FileView() {                                                                                   /
    /     @Override public Icon getIcon(File f)                                                                            /
    /     {                                                                                                                /
    /       if (f.isDirectory())                                                                                           /
    /         return new ImageIcon(stx_Helper.repack(_1const.assets.image("assets/folder-icon.png"), 22, 22));             /
    /       return new ImageIcon(stx_Helper.repack(_1const.assets.image("assets/image-icon.png"), 22, 22)); // this line   /
    /                                                                                                       // is useless  /
    /                                                                                                       // here but oh /
    /                                                                                                       // well        /
    /     }                                                                                                                /
    /   });                                                                                                                /
    /   jfc.setFileFilter((new FileFilter() {                                                                              /
    /                                                                                                                      /
    /     @Override public String getDescription()                                                                         /
    /     {                                                                                                                /
    /       return "Folders";                                                                                              /
    /     }                                                                                                                /
    /                                                                                                                      /
    /     @Override public boolean accept(File f)                                                                          /
    /     {                                                                                                                /
    /       return f.isDirectory();                                                                                        /
    /     }                                                                                                                /
    /   }));                                                                                                               /
    /   int res = jfc.showOpenDialog(this);                                                                                /
    /   if (res == JFileChooser.APPROVE_OPTION && jfc.getSelectedFiles().length > 0)                                       /
    /   {                                                                                                                  /
    /   }                                                                                                                  /
    / });                                                                                                                  /
    /---------------------------------------------------------------------------------------------------------------------*/

    JButton select_btn = new JButton(
        "<html><p style=\"text-align:center\"><strong>Select folder/file(s)</strong><br /><em>One go conversion</em></p></html>");
    select_btn.setBorderPainted(false);
    select_btn.addActionListener(ev -> {
      if (jfc == null)
        jfc = new JFileChooser(System.getProperty("user.home"));
      jfc.setFileFilter((new FileFilter() {

        @Override public String getDescription()
        {
          return "Web Picture (*.webp)";
        }

        @Override public boolean accept(File f)
        {
          return f.isDirectory() || stx_Helper.has_perms(f);
        }
      }));
      jfc.setAcceptAllFileFilterUsed(false); // might be funky
      jfc.setPreferredSize(new Dimension(800, 650));
      jfc.setMultiSelectionEnabled(true);
      jfc.setFileView(new FileView() {
        @Override public Icon getIcon(File f)
        {
          if (f.isDirectory())
            return new ImageIcon(stx_Helper.repack(_1const.assets.image("assets/folder-icon.png"), 22, 22));
          return new ImageIcon(stx_Helper.repack(_1const.assets.image("assets/image-icon.png"), 22, 22));
        }
      });
      jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      jfc.setDialogTitle("Select folders or files");
      int res = jfc.showOpenDialog(this);
      if (res == JFileChooser.APPROVE_OPTION && jfc.getSelectedFiles().length > 0)
      {

        files.clear();
        if (!jcb_deepscan.isSelected())
        {
          System.out.println("[FILE I/O]: Using I/O without DEEP_SCAN");
          for (File file : jfc.getSelectedFiles()) // lmfao prob couldve have done this part with better recursion
          {
            if (file.isFile())
            {
              System.out.println("[FILE I/O]: Loaded FILE: <p style=\"background-color:#d1c566;color:#000\">"
                  + file.getAbsolutePath() + "</p>");
              files.add(file);

            }
            else if (file.isDirectory())
            {
              System.out.println("[FILE I/O]: Expanding FOLDER: " + file.getAbsolutePath());
              for (File file_expanded : file.listFiles())
              {
                if (stx_Helper.has_perms(file_expanded))
                {
                  System.out
                      .println("[FILE I/O]: Loaded FILE_EXPANDED: <p style=\"background-color:#d1c566;color:#000\">"
                          + file_expanded.getAbsolutePath() + "</p>");
                  files.add(file);

                }
              }
            }
          }
        }
        else
        {
          System.out.println("[FILE I/O]: Using I/O with DEEP_SCAN");
          for (File file : jfc.getSelectedFiles()) // lmfao prob couldve have done this part with better recursion
          {
            if (file.isFile())
            {
              System.out
                  .println("[FILE I/O]: Loaded (DEEP_SCAN) FILE: <p style=\"background-color:#d1c566;color:#000\">"
                      + file.getAbsolutePath() + "</p>");
              files.add(file);

            }
            else if (file.isDirectory())
            {
              System.out.println("[FILE I/O]: Expanding (DEEP_SCAN) FOLDER: " + file.getAbsolutePath());
              try (Stream< Path > stream = Files.walk(Paths.get(file.getPath()), Integer.MAX_VALUE))
              {
                stream.filter(p -> !Files.isDirectory(p)).map(Path::toString)
                    .filter(f -> f.endsWith(".webp")).collect(Collectors.toList()).forEach(x -> {
                      File file_expanded = new File(x);
                      System.out.println(
                          "[FILE I/O]: Loaded (DEEP_SCAN) FILE_EXPANDED: <p style=\"background-color:#d1c566;color:#000\">"
                              + file_expanded.getAbsolutePath() + "</p>");
                      files.add(file_expanded);
                    });
              } catch (IOException e)
              {
                e.printStackTrace();
              }
            }
          }
        }
        System.out.println("[FILE I/O]: Total: " + files.size());
        if (files.size() > 0)
        {
          for (Component r : controls_naming.getComponents())
            if (r instanceof JComponent jc)
              jc.setEnabled(true);
          start_action.setEnabled(true);
        }
        else
        {
          System.out.println("<strong>Maybe you forgot to turn on DEEP_SCAN?</strong>");
          process_output.requestFocus(true);
        }
      }
    });
    select_btn.setBackground(stl_Colors.hexToRGB("#8ed15a"));
    select_btn.setForeground(Color.black);
    select_btn.setAlignmentX(Component.CENTER_ALIGNMENT);

    JScrollPane jsp_output_text = new JScrollPane();
    jsp_output_text.setBorder(BorderFactory.createTitledBorder("<html><strong>Logger</strong></html>"));
    jsp_output_text.setViewportView(process_output);

    add(app_title);
    add(Box.createVerticalStrut(5));
    add(new ui_Socials());
    add(jcb_deepscan);
    add(select_btn);
    /*--------------- /
    / add(runbg_btn); /
    /----------------*/
    add(Box.createVerticalStrut(10));
    add(controls);
    add(Box.createVerticalStrut(5));
    add(action_controls);
    add(jsp_output_text);

    setMinimumSize(new Dimension(340, 480));
  }

  @Override public void run()
  {
    pack();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);

    revalidate();
    System.out.println("START");
  }

}
