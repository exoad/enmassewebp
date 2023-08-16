// Software created by Jack Meng (AKA exoad). Licensed by the included "LICENSE" file. If this file is not found, the project is fully copyrighted.

package pkg.exoad.enmassewebp;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jackmeng.stl.stl_AssetFetcher;
import com.jackmeng.stl.stl_AssetFetcher.assetfetcher_FetcherStyle;

import pkg.exoad.enmassewebp.ux.ux_WrapLayout;

public final class _1const
{
  public static final Timer RUN_Q_1 = new Timer("pkg-exoad-enmassewebp-runthread#1");
  public static final stl_AssetFetcher assets = new stl_AssetFetcher(assetfetcher_FetcherStyle.WEAK);

  public static JLabel getApp_title()
  {
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
    return app_title;
  }

  public static JCheckBox getJcb_deepscan()
  {
    JCheckBox jcb_deepscan = new JCheckBox("Deep scan", false);
    jcb_deepscan.setToolTipText("Look through all sub folders under a folder");
    jcb_deepscan.setAlignmentX(Component.CENTER_ALIGNMENT);
    return jcb_deepscan;
  }

  public static JPanel getAction_controls(JButton kill_action, JButton start_action)
  {
    JPanel action_controls = new JPanel();
    action_controls.setLayout(new ux_WrapLayout(FlowLayout.CENTER, 4, 0));
    action_controls.add(start_action);
    action_controls.add(kill_action);
    action_controls.setMaximumSize(action_controls.getPreferredSize());
    action_controls.setAlignmentX(Component.CENTER_ALIGNMENT);
    return action_controls;
  }

  public static JEditorPane process_output;

  static
  {
    _1const.process_output = new JEditorPane();
    _1const.process_output.setContentType("text/html");
    _1const.process_output.setText("<html><body>");
  }
}
