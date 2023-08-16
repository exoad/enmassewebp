// Software created by Jack Meng (AKA exoad). Licensed by the included "LICENSE" file. If this file is not found, the project is fully copyrighted.

package pkg.exoad.enmassewebp.ux;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.ImageIcon;

import com.jackmeng.stl.stl_Chrono;
import com.jackmeng.stl.stl_Colors;

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

  private static JEditorPane process_output;
  static
  {
    process_output = new JEditorPane();
    process_output.setContentType("text/html");
    process_output.setText("<html><body>");
  }
  private JFileChooser jfc;
  private JProgressBar p1, p2, p3;
  private final String FILES_LOAD = "Files Buffer [Inactive]", BUFF_HEALTH = "Buffer Health [Inactive]",
      MEM_USAGE = "Memory Usage";

  private ExecutorService worker = Executors.newWorkStealingPool();

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

    p1 = new JProgressBar(SwingConstants.HORIZONTAL);
    p1.setValue(0);
    p1.setStringPainted(true);
    p1.setIndeterminate(true);
    p1.setForeground(stl_Colors.hexToRGB("#8ed15a"));
    p1.setToolTipText(FILES_LOAD);
    p1.setMaximum(100);
    p1.setMinimum(0);

    p2 = new JProgressBar(SwingConstants.HORIZONTAL);
    p2.setValue(0);
    p2.setStringPainted(true);
    p2.setIndeterminate(true);
    p2.setToolTipText(BUFF_HEALTH);
    p2.setForeground(stl_Colors.hexToRGB("#5aa2d1"));
    p2.setMaximum(100);
    p2.setMinimum(0);

    p3 = new JProgressBar(SwingConstants.HORIZONTAL);
    p3.setValue(0);
    p3.setStringPainted(true);
    p3.setIndeterminate(true);
    p3.setToolTipText(MEM_USAGE);
    p3.setForeground(stl_Colors.hexToRGB("#d1685a"));
    p3.setMaximum(100);
    p3.setMinimum(0);

    JPanel progress_pane = new JPanel();
    progress_pane.setLayout(new FlowLayout(FlowLayout.CENTER));

    progress_pane.add(p1);
    progress_pane.add(p2);
    progress_pane.add(p3);
    progress_pane.setMaximumSize(progress_pane.getPreferredSize());

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
        for (Component r : controls_naming.getComponents())
          if (r instanceof JComponent jc)
            jc.setEnabled(false);
      });
    });

    JPanel action_controls = new JPanel();
    action_controls.setLayout(new ux_WrapLayout(FlowLayout.CENTER, 4, 0));
    action_controls.add(start_action);
    action_controls.add(kill_action);
    action_controls.setMaximumSize(action_controls.getPreferredSize());
    action_controls.setAlignmentX(Component.CENTER_ALIGNMENT);

    JButton runbg_btn = new JButton("<html><p style=\"text-align:center\"><strong>Scan background</strong></p></html>");
    runbg_btn.setToolTipText(
        "Runs a continuous check on the user's home directory for any webp pictures and converts them.");
    runbg_btn
        .setMaximumSize(new Dimension(runbg_btn.getPreferredSize().width + 40, runbg_btn.getPreferredSize().height));
    runbg_btn.setAlignmentX(Component.CENTER_ALIGNMENT);

    JButton select_btn = new JButton(
        "<html><p style=\"text-align:center\"><strong>Select folder/file(s)</strong><br /><em>Or drag and drop them here</em></p></html>");
    select_btn.addActionListener(ev -> {
      if (jfc == null)
      {
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
      }
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
        p1.setToolTipText("Files Buffer");
        p1.setIndeterminate(false);
        files.clear();
        if (!jcb_deepscan.isSelected())
        {
          System.out.println("[FILE I/O]: Using I/O without DEEP_SCAN");
          for (File file : jfc.getSelectedFiles()) // lmfao prob couldve have done this part with better recursion
          {
            if (file.isFile())
            {
              System.out.println("[FILE I/O]: Loaded FILE: " + file.getAbsolutePath());
              files.add(file);
              p1.setValue(files.size() / jfc.getSelectedFiles().length);
              p1.setToolTipText(Double.toString(files.size() / (double) jfc.getSelectedFiles().length));
            }
            else if (file.isDirectory())
            {
              System.out.println("[FILE I/O]: Expanding FOLDER: " + file.getAbsolutePath());
              for (File file_expanded : file.listFiles())
              {
                if (stx_Helper.has_perms(file_expanded))
                {
                  System.out.println("[FILE I/O]: Loaded FILE_EXPANDED: " + file_expanded.getAbsolutePath());
                  files.add(file);
                  p1.setValue(files.size() / jfc.getSelectedFiles().length);
                  p1.setToolTipText(Double.toString(files.size() / (double) jfc.getSelectedFiles().length));
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
              System.out.println("[FILE I/O]: Loaded (DEEP_SCAN) FILE: " + file.getAbsolutePath());
              files.add(file);
              p1.setValue(files.size() / jfc.getSelectedFiles().length);
              p1.setToolTipText(Double.toString(files.size() / (double) jfc.getSelectedFiles().length));
            }
            else if (file.isDirectory())
            {
              System.out.println("[FILE I/O]: Expanding (DEEP_SCAN) FOLDER: " + file.getAbsolutePath());
              try (Stream< Path > stream = Files.walk(Paths.get(file.getPath()), Integer.MAX_VALUE))
              {
                stream.filter(p -> !Files.isDirectory(p)).map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith(".webp")).collect(Collectors.toList()).forEach(x -> {
                      System.out.println("[FILE I/O]: DEEP_SCAN saw: " + x);
                      File file_expanded = new File(x);
                      System.out.println(
                          "[FILE I/O]: Loaded (DEEP_SCAN) FILE_EXPANDED: " + file_expanded.getAbsolutePath());
                      files.add(file_expanded);
                      p1.setValue(files.size() / jfc.getSelectedFiles().length);
                      p1.setToolTipText(Double.toString(files.size() / (double) jfc.getSelectedFiles().length));
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
          p1.setValue(100); // %
          p1.setToolTipText("Loaded: " + files.size());
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
    jsp_output_text.setViewportView(process_output);

    add(app_title);
    add(Box.createVerticalStrut(5));
    add(new ui_Socials());
    add(jcb_deepscan);
    add(select_btn);
    add(runbg_btn);
    add(Box.createVerticalStrut(10));
    add(controls);
    add(Box.createVerticalStrut(5));
    add(action_controls);
    add(jsp_output_text);
    add(progress_pane);

    setMinimumSize(new Dimension(340, 480));
  }

  @Override public void run()
  {
    pack();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
    p3.setIndeterminate(false);
    p3.addMouseListener(new MouseAdapter() {
      @Override public void mouseClicked(MouseEvent event)
      {
        Runtime.getRuntime().gc();
        System.out.println("[SYS]: Ran an invoked GCEvent");
      }
    });
    _1const.RUN_Q_1.scheduleAtFixedRate(new TimerTask() {
      @Override public void run()
      {
        p3.setValue((int) ((((double) Runtime.getRuntime().totalMemory() - (double) Runtime.getRuntime().freeMemory())
            / (double) Runtime.getRuntime().totalMemory()) * 100));
        p3.setToolTipText("Memory Usage: "
            + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)) + "Mb / "
            + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + "Mb [" + p3.getValue() + "%]");
      }
    }, 400L, 350L);

    revalidate();
    System.out.println("START");
  }

}
