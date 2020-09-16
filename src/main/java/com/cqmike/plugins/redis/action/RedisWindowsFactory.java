package com.cqmike.plugins.redis.action;

import com.cqmike.plugins.redis.ui.DialogMainForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class RedisWindowsFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DialogMainForm dialogMainForm = new DialogMainForm(project, toolWindow);

        ContentFactory instance = ContentFactory.SERVICE.getInstance();
        Content content = instance.createContent(dialogMainForm.getMainPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
