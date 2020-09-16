package com.cqmike.plugins.redis.ui;

import com.cqmike.plugins.redis.data.RedisDataBase;
import com.cqmike.plugins.redis.data.RedisKeyData;
import com.cqmike.plugins.redis.data.RedisServerData;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.net.URL;

/**
 * TODO
 *
 * @author chen qi
 * @date 2020-09-03 14:27
 **/
public class TreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * ID
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        setText(value.toString());

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(value);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getUserObject();
        final Object userObject = node.getUserObject();

        URL redis = TreeCellRenderer.class.getResource("/icon/redis.png");
        URL database = TreeCellRenderer.class.getResource("/icon/database.png");
        if (userObject instanceof RedisServerData) {
            this.setIcon(new ImageIcon(redis));
        } else if (userObject instanceof RedisDataBase) {
            this.setIcon(new ImageIcon(database));
        } else if (userObject instanceof RedisKeyData) {
        }

        return this;
    }
}
