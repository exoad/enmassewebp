// Software created by Jack Meng (AKA exoad). Licensed by the included "LICENSE" file. If this file is not found, the project is fully copyrighted.

package pkg.exoad.enmassewebp.ux;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
      if (res == JFileChooser.APPROVE_OPTION)
      {
        files.clear();
        for (File file : jfc.getSelectedFiles()) // lmfao prob couldve have done this part with better recursion
        {
          if (file.isFile())
          {
            System.out.println("[FILE I/O]: Loaded FILE: " + file.getAbsolutePath());
            files.add(file);
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
              }
            }
          }
        }
        System.out.println("[FILE I/O]: Total: " + files.size());
      }
    });
    select_btn.setBackground(stl_Colors.hexToRGB("#8ed15a"));
    select_btn.setForeground(Color.black);
    select_btn.setAlignmentX(Component.CENTER_ALIGNMENT);

    add(app_title);
    add(new ui_Socials());
    add(select_btn);
    add(Box.createVerticalStrut(30));
  }

  @Override public void run()
  {
    pack();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
  }

}
