package com.cqmike.plugins.redis.ui;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class FocusAdapterWithReminderTextAdapter extends FocusAdapter {
    JTextField txt;
    String msg;

    public FocusAdapterWithReminderTextAdapter(JTextField txt_, String reminderString_) {
        txt = txt_;
        msg = reminderString_;
        if (txt_.getText().equals("")) {
            txt.setForeground(JBColor.GRAY);
            txt.setText(msg);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        String tempString = txt.getText();
        if (tempString.equals(msg)) {
            txt.setText("");
            txt.setForeground(JBColor.BLACK);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        String tempString = txt.getText();
        if (tempString.equals("")) {
            txt.setForeground(JBColor.GRAY);
            txt.setText(msg);
        }
    }
}