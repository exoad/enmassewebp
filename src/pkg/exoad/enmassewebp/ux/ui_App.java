// Software created by Jack Meng (AKA exoad). Licensed by the included "LICENSE" file. If this file is not found, the project is fully copyrighted.

package pkg.exoad.enmassewebp.ux;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.ImageIcon;

import com.jackmeng.stl.stl_Colors;

import pkg.exoad.enmassewebp._1const;

public final class ui_App
    extends
    JFrame
    implements
    Runnable
{
  private JFileChooser jfc;
  private JProgressBar p1, p2, p3;
  private final String FILES_LOAD = "Files Buffer [Inactive]", BUFF_HEALTH = "Buffer Health [Inactive]",
      MEM_USAGE = "Memory Usage";

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
                      made by exoad
                    </em>
              </p>
            </html>
              """);
    app_title.setHorizontalAlignment(SwingConstants.CENTER);
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

    JPanel controls = new JPanel();
    controls.setEnabled(false);
    controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

    JPanel controls_naming = new JPanel();
    controls_naming.setLayout(new ux_WrapLayout(FlowLayout.CENTER, 8, 0));
    controls_naming.add(new JCheckBox("Delete original", false));
    controls_naming.add(new JCheckBox("Same name", true));
    controls_naming.add(new JCheckBox("Same metadata", true));
    controls_naming.add(new JCheckBox("Prefer PNG over JPG", true));

    for (Component r : controls_naming.getComponents())
    {
      if (r instanceof JComponent jc)
        jc.setEnabled(false);
    }

    controls.add(controls_naming);

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
        System.out.println("[FILE I/O]: Total: " + files.size());
        for (Component r : controls_naming.getComponents())
        {
          if (r instanceof JComponent jc)
            jc.setEnabled(false);
        }

      }
    });
    select_btn.setBackground(stl_Colors.hexToRGB("#8ed15a"));
    select_btn.setForeground(Color.black);
    select_btn.setAlignmentX(Component.CENTER_ALIGNMENT);

    add(app_title);
    add(new ui_Socials());
    add(select_btn);
    add(Box.createVerticalStrut(15));
    add(controls);
    add(progress_pane);
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
  }

}
