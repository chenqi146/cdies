package com.cqmike.plugins.redis.ui;

import lombok.Data;

import javax.swing.*;
import java.awt.*;

@Data
public class AddRedisForm {
    private JPanel mainPanel;
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

    private void ini() {
        middlePanel.setEnabled(true);
    }

    public AddRedisForm() {
        connectionBtn.addActionListener(e -> {
            middlePanel.add(new JButton("1111"), BorderLayout.SOUTH);
        });
        sslBtn.addActionListener(e -> {

        });
        sshBtn.addActionListener(e -> {

        });
        settingBtn.addActionListener(e -> {

        });
        testBtn.addActionListener(e -> {

        });
        cancelBtn.addActionListener(e -> {

        });
        okBtn.addActionListener(e -> {

        });
    }
}
