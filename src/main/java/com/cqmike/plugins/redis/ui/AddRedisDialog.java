package com.cqmike.plugins.redis.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cqmike.plugins.redis.data.DataCenter;
import com.cqmike.plugins.redis.data.RedisDataBase;
import com.cqmike.plugins.redis.data.RedisDataCenter;
import com.cqmike.plugins.redis.data.RedisServerData;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.concurrency.SwingWorker;
import com.intellij.util.ui.JBUI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
public class AddRedisDialog extends DialogWrapper {

    private JButton connectionBtn;
    private JButton sslBtn;
    private JButton sshBtn;
    private JButton settingBtn;
    private JButton testBtn;
    private JButton okBtn;
    private JButton cancelBtn;
    private JToolBar toolBar;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel middlePanel;

    private JPanel connectionPanel;
    private JPanel sslPanel;
    private JPanel sshPanel;
    private JPanel settingsPanel;

    private JTextField nameField;
    private JTextField hostField;
    private JTextField portField;
    private JTextField authField;

    private final CardLayout cardLayout = new CardLayout();


    protected AddRedisDialog() {
        super(true);
        this.getRootPane().setBounds(500, 500, 500, 500);
        init();

        setTitle("创建Redis连接");
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        middlePanel = new JPanel(cardLayout);
        sslPanel = new JPanel();
        sshPanel = new JPanel();
        settingsPanel = new JPanel();


        initConnectionPanel();
        initSSlPanel();
        initSSHPanel();
        initSettingsPanel();

        middlePanel.add(connectionPanel, "connectionPanel");
        middlePanel.add(sslPanel, "sslPanel");
        middlePanel.add(sshPanel, "sshPanel");
        middlePanel.add(settingsPanel, "settingsPanel");
        return middlePanel;
    }

    private void initSettingsPanel() {

    }

    private void initSSHPanel() {
    }

    private void initSSlPanel() {
    }

    private void initConnectionPanel() {
        JLabel nameLabel = new JLabel("Name:", SwingConstants.LEFT);
        Font consolas = new Font("Consolas", Font.PLAIN, 14);
        nameField = new JTextField(30);
        nameField.addFocusListener(new FocusAdapterWithReminderTextAdapter(nameField, "Connection Name"));
        JLabel hostLabel = new JLabel("Host:", SwingConstants.LEFT);
        hostField = new JTextField(30);
        hostField.addFocusListener(new FocusAdapterWithReminderTextAdapter(hostField, "redis-server Host"));
        JLabel portLabel = new JLabel("Port:", SwingConstants.LEFT);
        portField = new JTextField(30);
        portField.addFocusListener(new FocusAdapterWithReminderTextAdapter(portField, "port"));
        JLabel authLabel = new JLabel("Auth:", SwingConstants.LEFT);
        authField = new JTextField(30);
        authField.addFocusListener(new FocusAdapterWithReminderTextAdapter(authField, "(Optional)"));

        nameLabel.setFont(consolas);
        hostLabel.setFont(consolas);
        portLabel.setFont(consolas);
        authLabel.setFont(consolas);

        connectionPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPanel.add(nameLabel);
        jPanel.add(nameField);
        JPanel jPane2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPane2.add(hostLabel);
        jPane2.add(hostField);
        JPanel jPane3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPane3.add(portLabel);
        jPane3.add(portField);
        JPanel jPane4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPane4.add(authLabel);
        jPane4.add(authField);
        connectionPanel.add(jPanel);
        connectionPanel.add(jPane2);
        connectionPanel.add(jPane3);
        connectionPanel.add(jPane4);
    }

    @Override
    protected JComponent createSouthPanel() {
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        testBtn = new JButton("Test Connection");
        okBtn = new JButton("ok");
        cancelBtn = new JButton("cancel");
        bottomPanel.add(testBtn);
        bottomPanel.add(okBtn);
        bottomPanel.add(cancelBtn);

        testBtn.addActionListener(e -> {
            // 在另外的线程中连接redis
            SwingWorker<String> worker = new SwingWorker<String>() {
                @Override
                public String construct() {
                    try {
                        final String host = hostField.getText();
                        final String auth = authField.getText();
                        final String port = portField.getText();

                        GenericObjectPool<StatefulRedisConnection<String, String>> pool = getStatefulRedisConnectionGenericObjectPool(host, auth, port);

                        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
                            // 将redis连接保存起来
                            if (connection == null) {
                                return "连接为空";
                            }
                            return null;
                        } catch (Exception exception) {
                            log.error("redis测试连接异常", exception);
                            return exception.getMessage();
                        }
                    } catch (Exception e1) {
                        log.error("redis测试连接配置异常", e1);
                        return e1.getMessage();
                    }
                }
            };
            worker.start();

            // 这里还是会阻塞UI  因为需要获取值
            final String msg = worker.get();
            if (StrUtil.isEmpty(msg)) {
                Messages.showInfoMessage("连接成功!", "测试连接");
            } else {
                Messages.showErrorDialog("连接失败: " + msg, "测试连接");
            }

        });

        okBtn.addActionListener(e -> {
            final String host = hostField.getText();
            final String name = nameField.getText();
            final String auth = authField.getText();
            final String port = portField.getText();
            RedisServerData data = new RedisServerData(name, auth, host, port);
            data.setStatus(RedisServerData.ServerStatus.OFFLINE);

            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(data);
            DataCenter.ROOT_MODE.insertNodeInto(treeNode, DataCenter.ROOT_NODE, DataCenter.ROOT_NODE.getChildCount());

            // 新增只做连接  双击节点 拉取信息
            final GenericObjectPool<StatefulRedisConnection<String, String>> pool = getStatefulRedisConnectionGenericObjectPool(host, auth, port);
            try (final StatefulRedisConnection<String, String> connection = pool.borrowObject()) {

                if (connection == null) {
                    Messages.showErrorDialog("连接失败: 连接为空", "测试连接");
                }

            } catch (Exception exception) {
                log.error("redis连接失败: ", exception);
                Messages.showErrorDialog("连接失败: " + exception.getMessage(), "测试连接");
            }
            this.close(200);
        });
        cancelBtn.addActionListener(e -> this.close(200));

        return bottomPanel;
    }

    @NotNull
    private GenericObjectPool<StatefulRedisConnection<String, String>> getStatefulRedisConnectionGenericObjectPool(String host, String auth, String port) {
        final RedisURI build = RedisURI.builder()
                .withHost(host)
                .withDatabase(0)
                .withPort(Integer.parseInt(port))
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        if (StrUtil.isNotEmpty(auth) && !"(Optional)".equals(auth)) {
            build.setPassword(auth);
        }
        RedisClient redisClient = RedisClient.create(build);
        return ConnectionPoolSupport.createGenericObjectPool(redisClient::connect, DataCenter.poolConfig);
    }

    @Override
    protected @Nullable JComponent createNorthPanel() {
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolBar = new JToolBar();
        toolBar.setBorderPainted(false);
        toolBar.setFloatable(false);
        connectionBtn = new JButton("Connection");

        sslBtn = new JButton("SSL");
        sshBtn = new JButton("SSH Tunnel");
        settingBtn = new JButton("Advanced Settings");

        toolBar.setBorder(JBUI.Borders.empty());

        toolBar.add(connectionBtn);
        toolBar.add(sslBtn);
        toolBar.add(sshBtn);
        toolBar.add(settingBtn);
        topPanel.add(toolBar);
        return topPanel;
    }
}
