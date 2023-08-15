// Software created by Jack Meng (AKA exoad). Licensed by the included "LICENSE" file. If this file is not found, the project is fully copyrighted.

package pkg.exoad.enmassewebp.ux;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pkg.exoad.enmassewebp._1const;

public final class ui_App
    extends
    JFrame
    implements
    Runnable
{

  public ui_App()
  {
    setIconImage(_1const.assets.image("assets/icon.png"));
    setTitle("En Masse WebP");
    setPreferredSize(new Dimension(_2const.WIDTH, _2const.HEIGHT));
    JPanel contentPane_new = new JPanel();
    contentPane_new.setLayout(new BoxLayout(contentPane_new, BoxLayout.Y_AXIS));
    contentPane_new.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
    setContentPane(contentPane_new);

    JPanel logoNTitle = new JPanel();
    logoNTitle.setLayout(new FlowLayout(FlowLayout.LEFT));

    add(new JLabel(
        """
            <html>
                    <strong style="font-size: 24px;">
                      EnMasse
                      <strong style="color: #ccd13f;">
                        WebP
                      </strong>
                    </strong>
                    <br />
                    <em style="font-size: 8.5px; color: #828282;">
                      made by exoad
                    </em>
            </html>
              """));
    add(new ui_Line(Color.gray, 50, true, 2));
  }

  @Override public void run()
  {
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

}
