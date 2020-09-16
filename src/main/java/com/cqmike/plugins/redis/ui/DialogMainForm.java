package com.cqmike.plugins.redis.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cqmike.plugins.redis.data.*;
import com.cqmike.plugins.redis.exception.ServerException;
import com.cqmike.plugins.redis.util.RedisClientUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.concurrency.SwingWorker;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;

@Data
@Slf4j
public class DialogMainForm {
    private JTree tree;
    private JButton plus;
    private JButton miuns;
    private JScrollPane scrollPanel;
    private JPanel topPanel;
    private JPanel mainPanel;
    private JToolBar toolBar;

    private AddRedisDialog addRedisDialog;

    private void init() {
        tree.setModel(DataCenter.ROOT_MODE);
        tree.setRootVisible(false);
        tree.setEnabled(true);
        tree.setCellRenderer(new TreeCellRenderer());
        mainPanel.setEnabled(true);
        mainPanel.setSize(300, 600);

    }

    public DialogMainForm(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        init();
        plus.addActionListener(e -> {
            addRedisDialog = new AddRedisDialog();
            addRedisDialog.getCardLayout().show(addRedisDialog.getMiddlePanel(), "connectionPanel");
            addRedisDialog.show();
        });
        miuns.addActionListener(e -> {
            DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectNode == null) {
                return;
            }
            final Object userObject = selectNode.getUserObject();
            //  判断是否是服务器节点    数据库节点不允许删除
            if (!(userObject instanceof RedisServerData)) {
                return;
            }
            DataCenter.ROOT_MODE.removeNodeFromParent(selectNode);
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    return;
                }

                DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectNode == null) {
                    return;
                }
                final int childCount = selectNode.getChildCount();
                if (childCount != 0) {
                    return;
                }

                final Object userObject = selectNode.getUserObject();
                if (userObject instanceof RedisServerData) {
                    RedisServerData redisServerData = (RedisServerData) userObject;
                    final String msg = RedisClientUtil.connectionRedis(selectNode, redisServerData.getHost(),
                            redisServerData.getAuth(), redisServerData.getPort());
                    if (StrUtil.isNotEmpty(msg)) {
                        Messages.showErrorDialog("连接失败: " + msg, "连接redis");
                    }

                } else if (userObject instanceof RedisDataBase) {
                    SwingWorker<String> worker = new SwingWorker<String>() {
                        @Override
                        public String construct() {
                            RedisDataBase dataBase = (RedisDataBase) userObject;
                            final String serverKey = dataBase.getServerKey();
                            final GenericObjectPool<StatefulRedisConnection<String, String>> pool = RedisDataCenter.REDIS_MAP.get(serverKey);
                            try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
                                final RedisCommands<String, String> sync = connection.sync();
                                final ScanArgs match = ScanArgs.Builder.limit(100);

                                // TODO 目前不做展示数量限制
                                Set<String> dirKeySet = CollUtil.newHashSet();
                                Set<String> keySet = CollUtil.newHashSet();
                                ScanCursor of = ScanCursor.INITIAL;
                                while (!of.equals(ScanCursor.FINISHED)) {
                                    KeyScanCursor<String> scan = sync.scan(of, match);
                                    final String cursor = scan.getCursor();
                                    of = ScanCursor.of(cursor);
                                    if ("0".equals(cursor)) {
                                        of = ScanCursor.FINISHED;
                                    }
                                    final List<String> keys = scan.getKeys();
                                    for (String key : keys) {
                                        if (key.contains(StrUtil.COLON)) {
                                            dirKeySet.add(key);
                                        } else {
                                            keySet.add(key);
                                        }
                                    }
                                }

                                // 没有冒号的key
                                for (String key : keySet) {
                                    RedisKeyData keyData = new RedisKeyData();
                                    keyData.setDataType(RedisKeyData.DataType.BASE);
                                    keyData.setName(key);
                                    keyData.setType(RedisKeyData.DataDirType.DATA);
                                    DefaultMutableTreeNode databaseNode = new DefaultMutableTreeNode(keyData);
                                    DataCenter.ROOT_MODE.insertNodeInto(databaseNode, selectNode, selectNode.getChildCount());
                                }
                                return null;
                            } catch (Exception e1) {
                                log.error("redis异常: ", e1);
                                throw new ServerException(e1);
                            }
                        }
                    };

                    try {
                        worker.start();
                    } catch (Exception exception) {
                        Messages.showErrorDialog(exception.getMessage(), "异常信息");
                    }

                }
            }
        });
    }
}
