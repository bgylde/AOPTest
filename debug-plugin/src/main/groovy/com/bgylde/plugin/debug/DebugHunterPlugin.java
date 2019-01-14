package com.bgylde.plugin.debug;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Collections;

/**
 * Created by wangyan on 2019/1/12
 */
public class DebugHunterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        AppExtension appExtension = (AppExtension) project.getProperties().get("android");
        appExtension.registerTransform(new DebugHunterTransform(project), Collections.EMPTY_LIST);
    }
}
