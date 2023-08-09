package com.daysmatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @description 提示窗口
 * @author tianma
 * @date 2022/11/14 16:00
 **/
@Component
@Slf4j
public class DaysMatterJDialog extends JDialog {
    @Value("${server.port}")
    private String port;
    private DaysMatterJDialog instance;
    private TrayIcon trayIcon;
    private ImageIcon icon;
    private SystemTray tray;
    private String systemName = "Days Matter";
    private JLabel topLabel;
    private JLabel contentLabel;
    private JButton configButton;
    private JButton miniButton;
    private JButton closeButton;
    private JScrollPane jp;
    private int width = 500;
    private int height = 400;
    private int x, y;
    private Dimension dim;
    private Insets screenInsets;
    private byte[] getByteFromClassPathResource(ClassPathResource classPathResource) {
        try{
            InputStream inputStream = classPathResource.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] cache = new byte[1024];
            int n;
            while ((n = inputStream.read(cache)) != -1) {
                bos.write(cache, 0, n);
            }
            inputStream.close();
            bos.close();
            // byteArray 为最终结果
            byte[] byteArray = bos.toByteArray();
            return byteArray;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public DaysMatterJDialog(){
        instance = this;
        this.tray = SystemTray.getSystemTray();
        ClassPathResource iconResource = new ClassPathResource("static/icon.png");
        if(iconResource.exists()){
            byte[] iconByte = getByteFromClassPathResource(iconResource);
            icon = new ImageIcon(iconByte);
            trayIcon = new TrayIcon(icon.getImage(), systemName);
        }
        this.initTray();
        this.setResizable(false);
        this.setSize(this.width, this.height);
        this.setTitle(systemName);
        this.setLayout(new BorderLayout());
        this.setIconImage(icon.getImage());
        this.topLabel = new JLabel("<html><body><span style=\"width:"+this.width+"px;background-color:#fefefe;\">&nbsp;</span></body></html>",SwingConstants.CENTER);
        this.topLabel.setFont(new java.awt.Font("", 1, 26));
        this.contentLabel = new JLabel();
//        try {
//            this.contentLabel.setText("<html><body style=\"font-family:Microsoft YaHei\"><p color=red align=\"center\"><img src=\""+iconResource.getURL()+"\"></p></body></html>");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.contentLabel.setFont(new java.awt.Font("", 1, 13));
        this.jp=new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.contentLabel.setVerticalAlignment(SwingConstants.TOP);
        jp.setViewportView(this.contentLabel);
        jp.getVerticalScrollBar().setUnitIncrement(16);
        jp.setBorder(null);
        this.configButton = new JButton("配置");
        this.configButton.addActionListener(e -> {
            instance.openConfig();
        });
        this.miniButton = new JButton("确定");
        this.miniButton.addActionListener(e -> {
            instance.doHide();
        });
        this.closeButton = new JButton("关闭");
        this.closeButton.addActionListener(e -> {
            System.exit(0);
        });
        Panel northPanel = new Panel();
        northPanel.setLayout(new BorderLayout());

        northPanel.add(this.topLabel,BorderLayout.CENTER);
        this.add(northPanel, BorderLayout.NORTH);

        this.add(jp, BorderLayout.CENTER);

        Panel southPanel = new Panel();
        southPanel.add(this.miniButton);
        southPanel.add(this.configButton);
        southPanel.add(this.closeButton);
        this.add(southPanel, BorderLayout.SOUTH);

        this.setVisible(false);
        dim = Toolkit.getDefaultToolkit().getScreenSize();
        screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
                this.getGraphicsConfiguration());
        trayIcon.setImageAutoSize(true);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {
                    tray.remove(trayIcon);
                    instance.doShow();
                }//if

            }//mouseClicked
        });//addMouseListener
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            };//windowClosing
        });//addWindowListener
        log.info("----Days Matter客户端工具初始化成功----");
    }
    public void setClientSize(int width,int height){
        this.setSize(width,height);
        this.width = width;
        this.height = height;
    }
    public void setContent(String content){
        this.contentLabel.setText(content);
    }
    public void setTopContent(String content){
        this.topLabel.setText(content);
    }

    public void openConfig() {
        try{
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI("http://localhost:"+port);
                desktop.browse(uri);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void doShow() {
        this.jp.getViewport().setViewPosition(new Point(0, 0));
        this.setAlwaysOnTop(true);
        tray.remove(trayIcon);
        this.setTitle(this.systemName+" - "+new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
        x = (int) (dim.getWidth() - this.width - 3);
        y = (int) (dim.getHeight() - screenInsets.bottom - 3);
        for (int i = 0; i <= this.height; i += 10) {
            try {
                this.setLocation(x, y - i);
                Thread.sleep(5);
                if(i == 0){
                    this.setVisible(true);
                }
            } catch (InterruptedException ex) {
            }
        }
    }
    public void doHide(){
//        int ybottom = (int) dim.getHeight() - screenInsets.bottom;
//        for (int i = 0; i <= ybottom - y; i += 10) {
//            try {
//                this.setLocation(x, y + i);
//                Thread.sleep(5);
//            } catch (InterruptedException ex) {
//            }
//        }
        this.setVisible(false);
        this.miniTray();
    }
    private void initTray(){
        PopupMenu pop = new PopupMenu();
        MenuItem show = new MenuItem("显  示");
        MenuItem config = new MenuItem("配  置");
        MenuItem exit = new MenuItem("退  出");

        //actionPerformed
        show.addActionListener(e -> {
            instance.doShow();
        });//addActionListener

        //actionPerformed
        exit.addActionListener(e -> {
            tray.remove(trayIcon);
            System.exit(0);
        });//addActionListener

        config.addActionListener(e -> {
            instance.openConfig();
        });
        pop.add(show);
        pop.add(config);
        pop.add(exit);
        trayIcon.setPopupMenu(pop);
    }
    private void miniTray() {
        try {
            tray.add(trayIcon);
        } catch (AWTException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }//try

    }//miniTray

    public static void main(String[] args) {
    }//main
}//ApplicationJFrame